package org.sec;

import com.beust.jcommander.JCommander;
import org.sec.repair.RepairService;
import org.sec.util.DirUtil;
import org.sec.util.NameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.HotSpotAgent;
import sun.jvm.hotspot.tools.jcore.ClassDump;
import sun.jvm.hotspot.tools.jcore.ClassFilter;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

// 基于静态分析动态，打破规则之道   - Java King
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Command command = new Command();
        JCommander jc = JCommander.newBuilder().addObject(command).build();
        jc.parse(args);
        if (command.help || command.pid == null || command.pid.equals("")) {
            jc.usage();
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!command.debug) {
                DirUtil.removeDir(new File("out"));
                logger.info("delete temp file");
            }
        }));
        PrintStream noPrint = new PrintStream(new OutputStream() {
            @Override
            public void write(int arg) {
            }
        });
        System.setOut(noPrint);
        System.setErr(noPrint);
        try {
            logger.info("start find shell");
            int pid = Integer.parseInt(command.pid);
            ClassFilter filter = new NameFilter();
            start(pid, filter);
            List<String> files = DirUtil.getFiles("out");
            List<Result> results = Analysis.doAnalysis(files);
            if (command.repair) {
                RepairService.start(results, pid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("unknown error");
        }
    }

    private static void start(int pid, ClassFilter filter) throws Exception {
        ClassDump classDump = new ClassDump();
        classDump.setClassFilter(filter);
        classDump.setOutputDirectory("out");
        Class<?> toolClass = Class.forName("sun.jvm.hotspot.tools.Tool");
        Method method = toolClass.getDeclaredMethod("start", String[].class);
        method.setAccessible(true);
        String[] params = new String[]{String.valueOf(pid)};
        try {
            method.invoke(classDump, (Object) params);
        } catch (Exception ignored) {
            logger.error("unknown error");
            return;
        }
        logger.info("dump class finish");
        Field field = toolClass.getDeclaredField("agent");
        field.setAccessible(true);
        HotSpotAgent agent = (HotSpotAgent) field.get(classDump);
        agent.detach();
    }
}
