package org.sec.repair;

import com.sun.tools.attach.VirtualMachine;

import java.nio.file.Paths;

public class ApplicationFilterChainRepair {
    public static void start(int pid) throws Exception {
        String agent = Paths.get("RepairAgent-jar-with-dependencies.jar").toAbsolutePath().toString();
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
        System.out.println("Load Agent");
        vm.loadAgent(agent);
        System.out.println("Detach");
        vm.detach();
    }
}