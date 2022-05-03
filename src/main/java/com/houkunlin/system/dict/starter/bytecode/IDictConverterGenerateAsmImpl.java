package com.houkunlin.system.dict.starter.bytecode;

import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.json.DictConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static org.springframework.asm.Opcodes.*;

/**
 * 使用 ASM 技术动态创建 {@link Converter} 转换器实现类，并把实现类注入到 Spring 中
 *
 * @author HouKunLin
 * @since 1.4.8
 */
@Slf4j
@Component
public class IDictConverterGenerateAsmImpl implements IDictConverterGenerate {
    public static final String DICT_ENUM_CLASS_NAME = DictEnum.class.getName().replace(".", "/");

    public IDictConverterGenerateAsmImpl() {
        if (log.isDebugEnabled()) {
            log.debug("使用 ASM 字节码技术动态创建字典转换器实现类");
        }
    }

    /**
     * 动态创建一个转换器对象
     *
     * @param dictEnumClass 枚举对象
     * @param dictConverter 枚举转换器配置参数注解
     * @return 转换器对象
     */
    @Override
    public Class<?> getConverterClass(final Class<?> dictEnumClass, final DictConverter dictConverter) throws Exception {
        // 这个 Class 一定是继承一个指定的接口的
        if (!dictEnumClass.isEnum() || !DictEnum.class.isAssignableFrom(dictEnumClass)) {
            return null;
        }

        // {@link DictEnum} 的泛型参数类型
        final Class<?> dictValueClass = getDictEnumInterfaceType(dictEnumClass);

        // 系统字典枚举类完全限定名
        final String dictEnumClassName = dictEnumClass.getName().replace(".", "/");
        final String converterClassName = dictEnumClassName + "SystemDictSpringConverter";
        final byte[] bytecode;
        if (dictConverter.onlyDictValue()) {
            // 只使用字典值转换
            bytecode = useDictValue(converterClassName, dictEnumClassName, dictValueClass);
        } else {
            // 最大力度尝试转换字典值，优先使用字典枚举名称转换，失败后再尝试使用字典值转换
            bytecode = useTryEnumName(converterClassName, dictEnumClassName, dictValueClass);
        }
        return ReflectUtils.defineClass(dictEnumClass.getName() + "SystemDictSpringConverter", bytecode, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 尝试使用字典枚举名称转换，字典名称转换失败后再尝试使用字典值转换
     *
     * @param converterClassName 转换器类名
     * @param dictEnumClassName  枚举类名
     * @param dictValueClass     字典值类型
     * @return 转换器字节码
     */
    public byte[] useTryEnumName(final String converterClassName, final String dictEnumClassName, final Class<?> dictValueClass) {
        final String converterClassNameDescriptor = "L" + converterClassName + ";";
        final String dictEnumClassNameDescriptor = "L" + dictEnumClassName + ";";

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor methodVisitor;

        visitConstruction(classWriter, converterClassName, converterClassNameDescriptor, dictEnumClassNameDescriptor);

        // 实现 convert 方法
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "convert", "(Ljava/lang/String;)" + dictEnumClassNameDescriptor, null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(12, label0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKESTATIC, dictEnumClassName, "valueOf", "(Ljava/lang/String;)" + dictEnumClassNameDescriptor, false);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLineNumber(13, label2);
        methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
        methodVisitor.visitVarInsn(ASTORE, 2);
        Label label3 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitLineNumber(14, label3);
        methodVisitor.visitMethodInsn(INVOKESTATIC, dictEnumClassName, "values", "()[" + dictEnumClassNameDescriptor, false);
        methodVisitor.visitVarInsn(ALOAD, 1);
        if (String.class != dictValueClass) {
            final String dictValueTypeClassName = dictValueClass.getName().replace(".", "/");
            methodVisitor.visitMethodInsn(INVOKESTATIC, dictValueTypeClassName, "valueOf", "(Ljava/lang/String;)L" + dictValueTypeClassName + ";", false);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, DICT_ENUM_CLASS_NAME, "valueOf", "([Ljava/lang/Enum;Ljava/io/Serializable;)Ljava/lang/Enum;", true);
        methodVisitor.visitTypeInsn(CHECKCAST, dictEnumClassName);
        methodVisitor.visitInsn(ARETURN);
        Label label4 = new Label();
        methodVisitor.visitLabel(label4);
        methodVisitor.visitLocalVariable("var3", "Ljava/lang/Exception;", null, label3, label4, 2);
        methodVisitor.visitLocalVariable("this", converterClassNameDescriptor, null, label0, label4, 0);
        methodVisitor.visitLocalVariable("text", "Ljava/lang/String;", null, label0, label4, 1);
        methodVisitor.visitMaxs(2, 3);
        methodVisitor.visitEnd();

        addBridgeMethod(classWriter, converterClassName, converterClassNameDescriptor, dictEnumClassNameDescriptor);
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    /**
     * 只使用字典值转换枚举对象
     *
     * @param converterClassName 转换器类名
     * @param dictClassName      枚举类名
     * @param dictValueClass     字典值类型
     * @return 转换器字节码
     */
    public byte[] useDictValue(final String converterClassName, final String dictClassName, final Class<?> dictValueClass) {
        final String converterClassNameDescriptor = "L" + converterClassName + ";";
        final String dictClassNameDescriptor = "L" + dictClassName + ";";

        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        visitConstruction(classWriter, converterClassName, converterClassNameDescriptor, dictClassNameDescriptor);

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "convert", "(Ljava/lang/String;)" + dictClassNameDescriptor, null, null);
        methodVisitor.visitParameter("text", 0);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(10, label0);
        methodVisitor.visitMethodInsn(INVOKESTATIC, dictClassName, "values", "()[" + dictClassNameDescriptor, false);
        methodVisitor.visitVarInsn(ALOAD, 1);
        if (String.class != dictValueClass) {
            final String dictValueTypeClassName = dictValueClass.getName().replace(".", "/");
            methodVisitor.visitMethodInsn(INVOKESTATIC, dictValueTypeClassName, "valueOf", "(Ljava/lang/String;)L" + dictValueTypeClassName + ";", false);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, DICT_ENUM_CLASS_NAME, "valueOf", "([Ljava/lang/Enum;Ljava/io/Serializable;)Ljava/lang/Enum;", true);
        methodVisitor.visitTypeInsn(CHECKCAST, dictClassName);
        methodVisitor.visitInsn(ARETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", converterClassNameDescriptor, null, label0, label1, 0);
        methodVisitor.visitLocalVariable("text", "Ljava/lang/String;", null, label0, label1, 1);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();

        addBridgeMethod(classWriter, converterClassName, converterClassNameDescriptor, dictClassNameDescriptor);
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    /**
     * 初始化和构造方法
     *
     * @param classWriter                  类写入器
     * @param converterClassName           转换器类名
     * @param converterClassNameDescriptor 转换器类名描述符
     * @param dictEnumClassNameDescriptor  字典类名描述符
     */
    private void visitConstruction(final ClassWriter classWriter,
                                   final String converterClassName, final String converterClassNameDescriptor,
                                   final String dictEnumClassNameDescriptor) {
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, converterClassName, "Ljava/lang/Object;L" + CONVERTER_CLASS_NAME + "<Ljava/lang/String;" + dictEnumClassNameDescriptor + ">;", "java/lang/Object", new String[]{CONVERTER_CLASS_NAME});
        // 构造方法
        final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(7, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", converterClassNameDescriptor, null, label0, label1, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
    }

    /**
     * 增加实现类转换方法的桥接方法
     *
     * @param classWriter                  类写入器
     * @param converterClassName           转换器类名
     * @param converterClassNameDescriptor 转换器类名描述符
     * @param dictEnumClassNameDescriptor  字典类名描述符
     */
    private void addBridgeMethod(final ClassWriter classWriter,
                                 final String converterClassName, final String converterClassNameDescriptor,
                                 final String dictEnumClassNameDescriptor) {
        // 构建 convert 桥接方法
        final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "convert", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        methodVisitor.visitParameter("text", ACC_SYNTHETIC);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(7, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, converterClassName, "convert", "(Ljava/lang/String;)" + dictEnumClassNameDescriptor, false);
        methodVisitor.visitInsn(ARETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", converterClassNameDescriptor, null, label0, label1, 0);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }
}
