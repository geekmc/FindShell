package org.sec.asm;

import org.objectweb.asm.MethodVisitor;
import org.sec.Result;

import java.util.List;

public class ShellMethodAdapter extends MethodVisitor {
    private final List<Result> results;
    private final String owner;

    public ShellMethodAdapter(int api, String owner, List<Result> results) {
        super(api);
        this.results = results;
        this.owner = owner;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        boolean runtimeCondition = owner.equals("java/lang/Runtime") && name.equals("exec") &&
                descriptor.equals("(Ljava/lang/String;)Ljava/lang/Process;");
        if (runtimeCondition) {
            Result result = new Result();
            result.setKey(this.owner);
            result.setType(Result.RUNTIME_EXEC_TIME);
            results.add(result);
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
