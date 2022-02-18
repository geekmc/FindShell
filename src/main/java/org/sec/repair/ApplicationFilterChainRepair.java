package org.sec.repair;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class ApplicationFilterChainRepair {
    private static final Logger logger = LoggerFactory.getLogger(RepairService.class);

    public static void start(int pid) throws Exception {
        logger.info("repair application filter chain");
        String agent = Paths.get("RepairAgent.jar").toAbsolutePath().toString();
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
        logger.info("load agent...");
        vm.loadAgent(agent);
        logger.info("repair...");
        vm.detach();
        logger.info("detach agent...");
    }
}