package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bytecode.BytecodeClassLoader;

import java.lang.reflect.Constructor;

public class ClassUtil {
    private ClassUtil() {
    }

    public static final BytecodeClassLoader CLASS_LOADER;

    static {
        CLASS_LOADER = new BytecodeClassLoader(Thread.currentThread().getContextClassLoader());
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

    public static Class<?> define(String name, byte[] b) {
        return CLASS_LOADER.define(name, b);
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className, true, CLASS_LOADER);
    }
}
