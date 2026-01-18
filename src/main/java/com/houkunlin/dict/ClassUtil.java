package com.houkunlin.dict;

import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
     * 通过默认构造方法创建类的新实例
     * <p>
     * 该方法会查找并调用类的无参构造方法来创建对象实例。
     * 如果类没有默认构造方法，将抛出 NoSuchMethodException 异常。
     * </p>
     *
     * @param <T>   要创建的实例类型
     * @param clazz 要实例化的类对象
     * @return 类的新实例
     * @throws InstantiationException    如果类是一个抽象类、接口、数组类、基本类型或void
     * @throws IllegalAccessException    如果构造方法不可访问
     * @throws IllegalArgumentException  如果构造方法的参数数量或类型不匹配
     * @throws InvocationTargetException 如果底层构造方法抛出异常
     * @throws NoSuchMethodException     如果类没有默认构造方法
     * @throws SecurityException         如果安全管理器拒绝访问构造方法
     */
    public static <T> T newInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Constructor<T> constructor = getDefaultConstructor(clazz);
        if (constructor == null) {
            throw new NoSuchMethodException("Class " + clazz.getName() + " has no default constructor");
        }
        return constructor.newInstance();
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
        Class<?> aClass = Class.forName(ClassUtil.class.getModule(), className);
        if (aClass != null) {
            return aClass;
        }
        return Class.forName(className);
    }
}
