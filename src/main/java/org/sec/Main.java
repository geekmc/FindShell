package org.sec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.tools.jcore.ClassDump;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("start");
        ClassDump.main(args);
    }
}
