package org.sec;

import com.sun.source.tree.BreakTree;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.sec.asm.DefineClassVisitor;
import org.sec.asm.ShellClassVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Analysis {
    private static final Logger logger = LoggerFactory.getLogger(Analysis.class);

    public static List<Result> doAnalysis(List<String> files) throws Exception {
        logger.info("start analysis");
        List<Result> results = new ArrayList<>();
        int api = Opcodes.ASM9;
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        for (String fileName : files) {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            if (bytes.length == 0) {
                continue;
            }
            ClassReader cr;
            ClassVisitor cv;
            try {
                // runtime exec analysis
                cr = new ClassReader(bytes);
                cv = new ShellClassVisitor(api, results);
                cr.accept(cv, parsingOptions);
                // classloader defineClass analysis
                cr = new ClassReader(bytes);
                cv = new DefineClassVisitor(api, results);
                cr.accept(cv, parsingOptions);
            } catch (Exception ignored) {
            }
        }
        for (Result r : results) {
            logger.info(r.getKey() + " -> " + r.getTypeWord());
        }
        return results;
    }
}
