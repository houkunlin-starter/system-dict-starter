package com.houkunlin.system.dict.starter.bytecode;

import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.json.DictConverter;
import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 使用 javassist 技术动态创建 {@link Converter} 转换器实现类，并把实现类注入到 Spring 中
 *
 * @author HouKunLin
 */
@ConditionalOnProperty(prefix = "system.dict", name = "bytecode", havingValue = "JAVASSIST", matchIfMissing = true)
@ConditionalOnClass(ClassPool.class)
@Slf4j
@Component
public class IDictConverterGenerateJavassistImpl implements IDictConverterGenerate {
    private final ClassPool pool = ClassPool.getDefault();

    public IDictConverterGenerateJavassistImpl() {
        if (log.isDebugEnabled()) {
            log.debug("使用 javassist 字节码技术动态创建字典转换器实现类");
        }
        if (javassist.bytecode.ClassFile.MAJOR_VERSION < javassist.bytecode.ClassFile.JAVA_9) {
            // 修复 Java 8 环境下 SpringBoot 打包后使用 java -jar 启动异常问题
            pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        }
    }

    /**
     * 动态创建一个转换器对象
     *
     * @param dictEnumClass 枚举对象
     * @param dictConverter 枚举转换器配置参数注解
     * @return 转换器对象
     * @throws NotFoundException      找不到 Class 异常
     * @throws CannotCompileException 修改 Class 异常
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
        final String dictEnumClassName = dictEnumClass.getName();
        final String converterClassName = dictEnumClassName + "SystemDictSpringConverter";
        final String dictEnumClassNameDescriptor = "L" + dictEnumClassName.replace(".", "/") + ";";

        // 创建一个基础的对象信息
        try {
            final CtClass ctClass = pool.getCtClass(converterClassName);
            // https://stackoverflow.com/questions/52763279/getting-class-is-frozen-runtime-exception-while-invoking-webservice-call-and-por
            // https://stackoverflow.com/a/52833444
            // https://www.javassist.org/tutorial/tutorial.html
            // ctClass.defrost();// 新 class 创建后无法进行修改，会提示 frozen class (cannot edit) 错误，调用此方法解除限制
            if (log.isDebugEnabled()) {
                log.debug("已找到 {} 转换器对象，延用之前的对象", converterClassName);
            }
            return toClass(ctClass, dictEnumClass);
        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("首次创建 {} 转换器对象", converterClassName);
            }
        }
        final CtClass makeClass = pool.makeClass(converterClassName);
        makeClass.setInterfaces(new CtClass[]{pool.getCtClass(Converter.class.getName())});

        final ClassFile classFile = makeClass.getClassFile();
        final ConstPool constPool = classFile.getConstPool();

        // 给接口增加泛型参数信息
        classFile.addAttribute(new SignatureAttribute(constPool,
            "Ljava/lang/Object;L" + CONVERTER_CLASS_NAME + "<Ljava/lang/String;" + dictEnumClassNameDescriptor + ">;"));

        // 创建实现方法
        final CtMethod method = new CtMethod(pool.getCtClass(dictEnumClassName),
            "convert",
            new CtClass[]{pool.getCtClass(String.class.getName())},
            makeClass);
        method.setBody(getMethodBody(dictEnumClassName, dictValueClass, dictConverter));
        method.setModifiers(Modifier.PUBLIC);
        makeClass.addMethod(method);

        // 由于泛型的类型擦除问题，javassist不会自动处理，因此必须手动增加一个桥接方法
        addBridgeMethod(pool, makeClass);

        return toClass(makeClass, dictEnumClass);
    }

    /**
     * CtClass 转换成 Class 对象
     *
     * @param makeClass     CtClass
     * @param dictEnumClass 字典枚举 Class
     * @return 转换器 Class
     * @throws CannotCompileException 异常
     */
    private Class<?> toClass(final CtClass makeClass, final Class<?> dictEnumClass) throws CannotCompileException {
        if (javassist.bytecode.ClassFile.MAJOR_VERSION < javassist.bytecode.ClassFile.JAVA_9) {
            return makeClass.toClass(dictEnumClass.getClassLoader(), null);
        }
        return makeClass.toClass(dictEnumClass);
    }

    /**
     * 获取方法体内容
     *
     * @param dictEnumClassName 字典枚举对象类全称
     * @param dictValueClass    字典枚举对象值类型
     * @param dictConverter     字典枚举注解信息
     * @return 方法体内存
     */
    private String getMethodBody(final String dictEnumClassName, final Class<?> dictValueClass, DictConverter dictConverter) {
        if (String.class == dictValueClass) {
            if (dictConverter.onlyDictValue()) {
                return String.format("{return (%s) %s.valueOf(%s.values(),$1);}", dictEnumClassName, DictEnum.class.getName(), dictEnumClassName);
            } else {
                // 优先尝试使用字符串转换，转换失败再次尝试使用枚举字典的值类型去转换获取
                return String.format("{ try{ return %s.valueOf($1); }catch(%s e){ return (%s) %s.valueOf(%s.values(),$1);} }",
                    dictEnumClassName, Exception.class.getName(), dictEnumClassName, DictEnum.class.getName(), dictEnumClassName
                );
            }
        } else {
            if (dictConverter.onlyDictValue()) {
                return String.format("{return (%s) %s.valueOf(%s.values(), %s.valueOf($1));}", dictEnumClassName, DictEnum.class.getName(), dictEnumClassName, dictValueClass.getName());
            } else {
                // 参数类型不同，优先尝试使用字符串转换，转换失败再次尝试使用枚举字典的值类型去转换获取
                return String.format("{ try{ return %s.valueOf($1); }catch(%s e){ return (%s) %s.valueOf(%s.values(), %s.valueOf($1));} }",
                    dictEnumClassName, Exception.class.getName(), dictEnumClassName, DictEnum.class.getName(), dictEnumClassName, dictValueClass.getName()
                );
            }
        }
    }

    /**
     * 增加实现类转换方法的桥接方法
     *
     * @param pool      pool
     * @param makeClass makeClass
     * @throws NotFoundException      异常信息
     * @throws CannotCompileException 异常信息
     */
    private void addBridgeMethod(ClassPool pool, final CtClass makeClass) throws NotFoundException, CannotCompileException {
        // // 必须设置一个桥接方法，否则调用方法的时候会报 AbstractMethodError 异常，这个据说是编译器的类型擦除的问题，并且 javassist 不会自动设置桥接方法，因此需要手动构建一个桥接方法
        final CtClass objectCtClass = pool.getCtClass(Object.class.getName());
        final CtMethod method = new CtMethod(objectCtClass, "convert", new CtClass[]{objectCtClass}, makeClass);
        method.setBody("{return this.convert((java.lang.String)$1);}");
        method.setModifiers(Modifier.PUBLIC);
        makeClass.addMethod(method);
    }
}
