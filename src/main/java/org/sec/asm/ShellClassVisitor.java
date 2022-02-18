package org.sec.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sec.Result;

import java.util.List;
import java.util.Map;

public class ShellClassVisitor extends ClassVisitor {
    private String owner;
    private final List<Result> results;
    private final Map<String, String> blackList;

    public ShellClassVisitor(int api, Map<String, String> blackList, List<Result> results) {
        super(api);
        this.results = results;
        this.blackList = blackList;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        for (Map.Entry<String, String> entry : blackList.entrySet()) {
            if (entry.getKey().equals(this.owner) && entry.getValue().equals(name)) {
                return new ShellMethodAdapter(this.api, this.owner, results);
            }
        }
        return mv;
    }
}
