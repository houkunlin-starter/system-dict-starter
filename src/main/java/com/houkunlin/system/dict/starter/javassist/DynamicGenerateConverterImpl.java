package com.houkunlin.system.dict.starter.javassist;

import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.json.DictConverter;
import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

/**
 * 使用 javassist 技术动态创建 {@link Converter} 转换器实现类，并把实现类注入到 Spring 中
 *
 * @author HouKunLin
 */
@Slf4j
@Component
@NoArgsConstructor
public class DynamicGenerateConverterImpl {

    public void registerBean(final DefaultListableBeanFactory factory, final Class<?> clazz, final DictConverter dicConverter) {
        try {
            final Class<?> converterClass = createConverterClass(clazz, dicConverter);
            if (converterClass != null) {
                final Constructor<?>[] constructors = converterClass.getConstructors();
                factory.registerSingleton(clazz.getName() + ".SystemDicSpringConverter", constructors[0].newInstance());
            }
        } catch (Exception e) {
            log.error("自动创建系统字典枚举的 Converter 转换器失败", e);
            throw new RuntimeException("自动创建系统字典枚举的 Converter 转换器失败", e);
        }
    }

    /**
     * 动态创建一个转换器对象
     *
     * @param clazz        枚举对象
     * @param dicConverter 枚举转换器配置参数注解
     * @return 转换器对象
     * @throws Exception 异常
     */
    public Class<?> createConverterClass(final Class<?> clazz, final DictConverter dicConverter) throws Exception {
        // 这个 Class 一定是继承一个指定的接口的
        if (!clazz.isEnum() || !DictEnum.class.isAssignableFrom(clazz)) {
            return null;
        }
        // 系统字典枚举类完全限定名
        final String dicEnumClassName = clazz.getName();

        // DicEnum 的泛型参数类型
        final Class<?> dicValueType = getDicEnumInterfaceType(clazz);

        // 第一个泛型参数一定是字符串类型；            第二个泛型参数是枚举类型，也就是当前方法的入参参数
        final Class<?> interfaceTypeClass1 = String.class;

        final ClassPool pool = ClassPool.getDefault();

        // 创建一个基础的对象信息
        final CtClass makeClass = pool.makeClass(clazz.getName() + "SystemDictSpringConverter");
        makeClass.setInterfaces(new CtClass[]{pool.getCtClass(Converter.class.getName())});

        final ClassFile classFile = makeClass.getClassFile();
        final ConstPool constPool = classFile.getConstPool();

        // 给接口增加泛型参数信息
        final String signature = String.format("Ljava/lang/Object;%s<%s;%s;>;", getClassName(Converter.class), getClassName(interfaceTypeClass1), getClassName(clazz));
        classFile.addAttribute(new SignatureAttribute(constPool, signature));

        // 创建实现方法
        final CtMethod method = new CtMethod(pool.getCtClass(dicEnumClassName),
            "convert",
            new CtClass[]{pool.getCtClass(interfaceTypeClass1.getName())},
            makeClass);
        method.setBody(getMethodBody(dicEnumClassName, dicValueType, interfaceTypeClass1, dicConverter));
        method.setModifiers(Modifier.PUBLIC);
        makeClass.addMethod(method);

        // 加个注解确实复杂，如果想一步创建 Annotation 用 new Annotation("Scope(value=\"Request\")", constPool)
        // 产生成类文件反编译后看起来也对的，但可能用反射 API 就是看不到它
        // AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        // Annotation scopeAnnotation = new Annotation(Override.class.getName(), constPool);
        // annotationsAttribute.addAnnotation(scopeAnnotation);
        // method.getMethodInfo().addAttribute(annotationsAttribute);

        // 由于泛型的类型擦除问题，javassist不会自动处理，因此必须手动增加一个桥接方法
        addBridgeMethod(pool, makeClass, interfaceTypeClass1);

        return makeClass.toClass(clazz.getClassLoader(), null);
    }

    /**
     * 获取方法体内容
     *
     * @param dicEnumClassName    字典枚举对象类全称
     * @param dicValueType        字典枚举对象值类型
     * @param methodArgumentClazz 转换方法参数对象（字符串对象）
     * @param dicConverter        字典枚举注解信息
     * @return 方法体内存
     */
    private String getMethodBody(final String dicEnumClassName, final Class<?> dicValueType, final Class<?> methodArgumentClazz, DictConverter dicConverter) {
        if (methodArgumentClazz == dicValueType) {
            if (dicConverter.onlyDicValue()) {
                return String.format("{return (%s) %s.valueOf(%s.values(),$1);}", dicEnumClassName, DictEnum.class.getName(), dicEnumClassName);
            } else {
                // 优先尝试使用字符串转换，转换失败再次尝试使用枚举字典的值类型去转换获取
                return String.format("{ try{ return %s.valueOf($1); }catch(%s e){ return (%s) %s.valueOf(%s.values(),$1);} }",
                    dicEnumClassName, Exception.class.getName(), dicEnumClassName, DictEnum.class.getName(), dicEnumClassName
                );
            }
        } else {
            if (dicConverter.onlyDicValue()) {
                return String.format("{return (%s) %s.valueOf(%s.values(), %s.valueOf($1));}", dicEnumClassName, DictEnum.class.getName(), dicEnumClassName, dicValueType.getName());
            } else {
                // 参数类型不同，优先尝试使用字符串转换，转换失败再次尝试使用枚举字典的值类型去转换获取
                return String.format("{ try{ return %s.valueOf($1); }catch(%s e){ return (%s) %s.valueOf(%s.values(), %s.valueOf($1));} }",
                    dicEnumClassName, Exception.class.getName(), dicEnumClassName, DictEnum.class.getName(), dicEnumClassName, dicValueType.getName()
                );
            }
        }
    }

    /**
     * 增加实现类转换方法的桥接方法
     *
     * @param pool                pool
     * @param makeClass           makeClass
     * @param methodArgumentClazz 方法参数类对象
     * @throws NotFoundException      异常信息
     * @throws CannotCompileException 异常信息
     */
    private void addBridgeMethod(ClassPool pool, final CtClass makeClass, final Class<?> methodArgumentClazz) throws NotFoundException, CannotCompileException {
        // // 必须设置一个桥接方法，否则调用方法的时候会报 AbstractMethodError 异常，这个据说是编译器的类型擦除的问题，并且 javassist 不会自动设置桥接方法，因此需要手动构建一个桥接方法
        final CtClass objectCtClass = pool.getCtClass(Object.class.getName());
        final CtMethod method = new CtMethod(objectCtClass, "convert", new CtClass[]{objectCtClass}, makeClass);
        method.setBody("{return this.convert((" + methodArgumentClazz.getName() + ")$1);}");
        method.setModifiers(Modifier.PUBLIC);
        makeClass.addMethod(method);
    }

    /**
     * 获取枚举接口的泛型参数对象
     *
     * @param clazz 字典枚举对象
     * @return 泛型对象
     * @see GenericConversionService#getRequiredTypeInfo(java.lang.Class, java.lang.Class)
     */
    private Class<?> getDicEnumInterfaceType(final Class<?> clazz) {
        final ResolvableType resolvableType = ResolvableType.forClass(clazz).as(DictEnum.class);
        final ResolvableType[] interfaces = resolvableType.getGenerics();

        // DicEnum 的泛型参数类型
        final Class<?> dicValueType = interfaces[0].resolve();
        assert dicValueType != null;
        return dicValueType;
    }

    /**
     * 获取对象签名信息
     *
     * @param clazz 对象信息
     * @return 签名信息
     */
    private String getClassName(final Class<?> clazz) {
        return "L" + clazz.getName().replace(".", "/");
    }
}
