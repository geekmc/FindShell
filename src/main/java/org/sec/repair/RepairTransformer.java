package org.sec.repair;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;


public class RepairTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        className = className.replace("/", ".");
        ClassPool pool = ClassPool.getDefault();
        if (className.equals("org.apache.catalina.core.ApplicationFilterChain")) {
            try {
                CtClass c = pool.getCtClass(className);
                CtMethod m = c.getDeclaredMethod("doFilter");
                m.setBody("{internalDoFilter($1,$2);}");
                byte[] bytes = c.toBytecode();
                c.detach();
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (className.equals("javax.servlet.http.HttpServlet")) {
            try {
                CtClass c = pool.getCtClass(className);
                CtClass[] params = new CtClass[]{
                        pool.getCtClass("javax.servlet.ServletRequest"),
                        pool.getCtClass("javax.servlet.ServletResponse"),
                };
                CtMethod m = c.getDeclaredMethod("service", params);
                m.setBody("{" +
                        "        javax.servlet.http.HttpServletRequest  request;\n" +
                        "        javax.servlet.http.HttpServletResponse response;\n" +
                        "\n" +
                        "        try {\n" +
                        "            request = (javax.servlet.http.HttpServletRequest) $1;\n" +
                        "            response = (javax.servlet.http.HttpServletResponse) $2;\n" +
                        "        } catch (ClassCastException e) {\n" +
                        "            throw new javax.servlet.ServletException(lStrings.getString(\"http.non_http\"));\n" +
                        "        }\n" +
                        "        service(request, response);" +
                        "}");

                CtClass[] paramsProtected = new CtClass[]{
                        pool.getCtClass("javax.servlet.http.HttpServletRequest"),
                        pool.getCtClass("javax.servlet.http.HttpServletResponse"),
                };
                CtMethod mProtected = c.getDeclaredMethod("service", paramsProtected);
                mProtected.setBody("{" +
                        "String method = $1.getMethod();\n" +
                        "\n" +
                        "        if (method.equals(METHOD_GET)) {\n" +
                        "            long lastModified = getLastModified($1);\n" +
                        "            if (lastModified == -1) {\n" +
                        "                doGet($1, $2);\n" +
                        "            } else {\n" +
                        "                long ifModifiedSince;\n" +
                        "                try {\n" +
                        "                    ifModifiedSince = $1.getDateHeader(HEADER_IFMODSINCE);\n" +
                        "                } catch (IllegalArgumentException iae) {\n" +
                        "                    ifModifiedSince = -1;\n" +
                        "                }\n" +
                        "                if (ifModifiedSince < (lastModified / 1000 * 1000)) {\n" +
                        "                    maybeSetLastModified($2, lastModified);\n" +
                        "                    doGet($1, $2);\n" +
                        "                } else {\n" +
                        "                    $2.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED);\n" +
                        "                }\n" +
                        "            }\n" +
                        "\n" +
                        "        } else if (method.equals(METHOD_HEAD)) {\n" +
                        "            long lastModified = getLastModified($1);\n" +
                        "            maybeSetLastModified($2, lastModified);\n" +
                        "            doHead($1, $2);\n" +
                        "\n" +
                        "        } else if (method.equals(METHOD_POST)) {\n" +
                        "            doPost($1, $2);\n" +
                        "\n" +
                        "        } else if (method.equals(METHOD_PUT)) {\n" +
                        "            doPut($1, $2);\n" +
                        "\n" +
                        "        } else if (method.equals(METHOD_DELETE)) {\n" +
                        "            doDelete($1, $2);\n" +
                        "\n" +
                        "        } else if (method.equals(METHOD_OPTIONS)) {\n" +
                        "            doOptions($1, $2);\n" +
                        "\n" +
                        "        } else if (method.equals(METHOD_TRACE)) {\n" +
                        "            doTrace($1, $2);\n" +
                        "\n" +
                        "        } else {\n" +
                        "            String errMsg = lStrings.getString(\"http.method_not_implemented\");\n" +
                        "            Object[] errArgs = new Object[1];\n" +
                        "            errArgs[0] = method;\n" +
                        "            errMsg = java.text.MessageFormat.format(errMsg, errArgs);\n" +
                        "\n" +
                        "            $2.sendError(javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);\n" +
                        "        }"
                        + "}");

                byte[] bytes = c.toBytecode();
                c.detach();
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }
}