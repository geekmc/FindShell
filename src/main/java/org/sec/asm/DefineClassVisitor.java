package org.sec.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sec.Result;

import java.util.List;

public class DefineClassVisitor extends ClassVisitor {
    private String owner;
    private final List<Result> results;

    public DefineClassVisitor(int api, List<Result> results) {
        super(api);
        this.results = results;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new DefineMethodAdapter(this.api, mv, this.owner, access, name, descriptor, results);
    }
}
