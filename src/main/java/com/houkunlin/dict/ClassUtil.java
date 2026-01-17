package com.houkunlin.dict;

import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

/**
 * 自定义类工具
 *
 * @author HouKunLin
 */
@Slf4j
public class ClassUtil {

    /**
     * 私有构造方法
     */
    private ClassUtil() {
    }

    /**
     * 获取一个类的默认构造方法
     *
     * @param clazz 类对象
     * @param <T> 类
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

    /**
     * 动态加载字节码类
     *
     * @param neighbor 新类的邻居类对象
     * @param name     类名
     * @param b        字节码
     * @return 类对象
     */
    public static Class<?> define(Class<?> neighbor, String name, byte[] b) throws IllegalAccessException {
        ClassUtil.class.getModule().addReads(neighbor.getModule());
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandles.Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
        return prvlookup.defineClass(b);
    }

    /**
     * 通过类名加载类对象
     *
     * @param className 类名
     * @return 类对象
     * @throws ClassNotFoundException 加载类失败异常
     */
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
