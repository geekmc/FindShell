package org.sec.repair;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class RepairAgent {
    public static void agentmain(String agentArgs, Instrumentation ins) {
        ClassFileTransformer transformer = new RepairTransformer();
        ins.addTransformer(transformer, true);
        Class<?>[] classes = ins.getAllLoadedClasses();
        for (Class<?> clas : classes) {
            if (clas.getName().equals("org.apache.catalina.core.ApplicationFilterChain")
                    || clas.getName().equals("javax.servlet.http.HttpServlet")) {
                try {
                    ins.retransformClasses(clas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
