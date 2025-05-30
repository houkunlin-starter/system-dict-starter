package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bytecode.BytecodeClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

@Slf4j
public class ClassUtil {
    private ClassUtil() {
    }

    public static final BytecodeClassLoader CLASS_LOADER;
    /**
     * 运行环境版本是否小于等于 Java8
     */
    public static final boolean lessEqJava8;

    static {
        String version = System.getProperty("java.version");
        CLASS_LOADER = new BytecodeClassLoader(Thread.currentThread().getContextClassLoader());
        // 1.8.0_452 or 11.0.24
        lessEqJava8 = version != null && version.startsWith("1.");
    }

    /**
     * 获取一个类的默认构造方法
     *
     * @param clazz 类对象
     * @return 默认构造方法
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    public static Class<?> define(Class<?> neighbor, String name, byte[] b) {
        Class<?> re = null;
        if (lessEqJava8) {
            re = ClassUtilJava8.define(neighbor, name, b);
        }
        if (re != null) {
            return re;
        }
        return CLASS_LOADER.define(name, b);
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignore) {
        }
        return Class.forName(className, true, CLASS_LOADER);
    }
}
