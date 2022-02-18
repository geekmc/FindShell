package org.sec.repair;

import com.sun.tools.attach.VirtualMachine;
import org.sec.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;

public class RepairService {
    private static final Logger logger = LoggerFactory.getLogger(RepairService.class);

    public static void start(List<Result> resultList, int pid) {
        logger.info("try repair agent memshell");
        for (Result result : resultList) {
            String className = result.getKey().replace("/", ".");
            if (className.equals("org.apache.catalina.core.ApplicationFilterChain") ||
                    className.equals("javax/servlet/http/HttpServlet")) {
                try {
                    start(pid);
                    return;
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void start(int pid) {
        try {
            String agent = Paths.get("RepairAgent.jar").toAbsolutePath().toString();
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
            logger.info("load agent...");
            vm.loadAgent(agent);
            logger.info("repair...");
            vm.detach();
            logger.info("detach agent...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
