package org.sec.repair;

import org.sec.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RepairService {
    private static final Logger logger = LoggerFactory.getLogger(RepairService.class);

    public static void start(List<Result> resultList, int pid) {
        logger.info("try repair agent memshell");
        resultList.forEach(result -> {
            String className = result.getKey().replace("/", ".");
            if (className.equals("org.apache.catalina.core.ApplicationFilterChain")) {
                try {
                    ApplicationFilterChainRepair.start(pid);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
