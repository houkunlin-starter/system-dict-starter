package com.houkunlin.system.dict.starter.bytecode;

import lombok.RequiredArgsConstructor;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.springframework.asm.Opcodes.*;

/**
 * 子类生成
 *
 * @author HouKunLin
 */
@Component
@RequiredArgsConstructor
public class DictChildrenObjectGenerate {

    public static Class<?> newClass(final Class<?> supperClazz, final String... fieldNames) throws Exception {
        final String supperClazzName = supperClazz.getName().replace(".", "/");
        final String className = supperClazzName + "$DictChildren";
        final String classNameDescriptor = "L" + className + ";";
        final byte[] classBytes = getClassBytes(className, classNameDescriptor, supperClazzName, fieldNames);
        return ReflectUtils.defineClass(supperClazz.getName() + "$DictChildren", classBytes, Thread.currentThread().getContextClassLoader());
    }

    public static Class<?> newClass(final Class<?> supperClazz, final Set<String> fieldNames) throws Exception {
        return newClass(supperClazz, fieldNames.toArray(new String[0]));
    }

    private static byte[] getClassBytes(final String className, final String classNameDescriptor, final String supperClazzName, final String... fieldNames) throws Exception {
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, supperClazzName, null);

        final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(9, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, supperClazzName, "<init>", "()V", false);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLineNumber(10, label1);
        methodVisitor.visitInsn(RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable("this", classNameDescriptor, null, label0, label2, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        for (final String fieldName : fieldNames) {
            newField(classWriter, fieldName, className, classNameDescriptor);
        }

        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    private static void newField(final ClassWriter classWriter, final String fieldName, final String className, final String classNameDescriptor) {
        final FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE, fieldName, "Ljava/lang/Object;", null, null);
        fieldVisitor.visitEnd();
        final String method = upperCaseFirst(fieldName);
        newFieldGetter(classWriter, fieldName, "get" + method, className, classNameDescriptor);
        newFieldSetter(classWriter, fieldName, "set" + method, className, classNameDescriptor);
    }

    private static void newFieldGetter(final ClassWriter classWriter, final String fieldName, final String methodName, final String className, final String classNameDescriptor) {
        final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, methodName, "()Ljava/lang/Object;", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, className, fieldName, "Ljava/lang/Object;");
        methodVisitor.visitInsn(ARETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", classNameDescriptor, null, label0, label1, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
    }

    private static void newFieldSetter(final ClassWriter classWriter, final String fieldName, final String methodName, final String className, final String classNameDescriptor) {
        final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, methodName, "(Ljava/lang/Object;)V", null, null);
        methodVisitor.visitParameter(fieldName, ACC_FINAL);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitFieldInsn(PUTFIELD, className, fieldName, "Ljava/lang/Object;");
        methodVisitor.visitInsn(RETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", classNameDescriptor, null, label0, label1, 0);
        methodVisitor.visitLocalVariable(fieldName, "Ljava/lang/Object;", null, label0, label1, 1);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    public static String upperCaseFirst(String val) {
        char[] arr = val.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }
}
