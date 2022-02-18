package org.sec;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.sec.asm.DefineClassVisitor;
import org.sec.asm.ShellClassVisitor;
import org.sec.repair.ApplicationFilterChainRepair;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 基于静态分析动态，打破规则之道   - Java King
public class Analysis {
    public static void doAnalysis(List<String> files) throws Exception {

        List<Result> results = new ArrayList<>();
        Map<String, String> data = new HashMap<>();
        Constant.blackList.forEach(s -> {
            String[] splits = s.split("#");
            data.put(splits[0], splits[1]);
        });
        for (String fileName : files) {
            byte[] bytes = Files.readAllBytes(Paths.get(fileName));
            if (bytes.length == 0) {
                continue;
            }
            ClassReader cr = new ClassReader(bytes);
            int api = Opcodes.ASM9;
            ClassVisitor cv = new ShellClassVisitor(api, data, results);
            int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
            cr.accept(cv, parsingOptions);

            cr = new ClassReader(bytes);
            cv = new DefineClassVisitor(api, data, results);
            cr.accept(cv, parsingOptions);

        }
        for (Result r : results) {
            System.out.println(r.getKey()+r.getType());
        }
    }
}
