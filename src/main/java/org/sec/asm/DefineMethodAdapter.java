package org.sec.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.sec.Result;
import org.sec.jvm.CoreMethodAdapter;

import java.util.List;

public class DefineMethodAdapter extends CoreMethodAdapter<String> {
    private static final Integer TOP = 0;
    private final List<Result> results;

    public DefineMethodAdapter(int api, MethodVisitor mv, String owner, int access,
                               String name, String desc, List<Result> results) {
        super(api, mv, owner, access, name, desc);
        this.results = results;
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (value instanceof String) {
            if (value.equals("defineClass")) {
                super.visitLdcInsn(value);
                this.operandStack.set(0, "LDC_STRING");
                return;
            }
        } else {
            if (value.equals(Type.getType("Ljava/lang/ClassLoader;"))) {
                super.visitLdcInsn(value);
                this.operandStack.set(0, "LDC_CL");
                return;
            }
        }
        super.visitLdcInsn(value);
    }

    @Override
    @SuppressWarnings("all")
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        Type[] argTypes = Type.getArgumentTypes(descriptor);
        if (opcode != Opcodes.INVOKESTATIC) {
            Type[] extendedArgTypes = new Type[argTypes.length + 1];
            System.arraycopy(argTypes, 0, extendedArgTypes, 1, argTypes.length);
            extendedArgTypes[0] = Type.getObjectType(owner);
            argTypes = extendedArgTypes;
        }
        boolean reflectionMethod = owner.equals("java/lang/Class") &&
                opcode == Opcodes.INVOKEVIRTUAL && name.equals("getDeclaredMethod");
        boolean methodInvoke = owner.equals("java/lang/reflect/Method") &&
                opcode == Opcodes.INVOKEVIRTUAL && name.equals("invoke");
        if (reflectionMethod) {
            int targetIndex = 0;
            for (int i = 0; i < argTypes.length; i++) {
                if (argTypes[i].getClassName().equals("java.lang.String")) {
                    targetIndex = i;
                    break;
                }
            }
            if (operandStack.get(argTypes.length - targetIndex - 1).contains("LDC_STRING")) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                operandStack.set(TOP, "REFLECTION_METHOD");
                return;
            }
        }
        if (methodInvoke) {
            int targetIndex = 0;
            for (int i = 0; i < argTypes.length; i++) {
                if (argTypes[i].getClassName().equals("java.lang.reflect.Method")) {
                    targetIndex = i;
                    break;
                }
            }
            if (operandStack.get(argTypes.length - targetIndex - 1).contains("REFLECTION_METHOD")) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                Result result = new Result();
                result.setKey(owner);
                result.setType(Result.CLASSLOADER_DEFINE);
                results.add(result);
                return;
            }
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
