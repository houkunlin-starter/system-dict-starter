package com.houkunlin.dict;

import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 类工具类
 * <p>
 * 该类提供了一些实用的类操作工具方法，包括获取类的默认构造方法、创建类实例、
 * 动态加载字节码类以及通过类名加载类等功能。
 * 这些工具方法在数据字典系统的运行时操作中起到了重要作用。
 * </p>
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
     * <p>
     * 该方法会查找类的所有构造方法，返回无参构造方法。
     * 如果类没有无参构造方法，则返回 {@code null}。
     * </p>
     *
     * @param clazz 类对象
     * @param <T> 类类型
     * @return 默认构造方法，如果没有无参构造方法则返回 {@code null}
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
     * 首先调用 {@link #getDefaultConstructor(Class)} 方法获取类的无参构造方法，
     * 然后调用该构造方法创建实例。
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
     * <p>
     * 该方法使用 Java 7 引入的 MethodHandles API 来动态加载字节码类。
     * 首先，确保当前模块可以读取邻居类所在的模块，然后通过 MethodHandles.Lookup
     * 获取对邻居类的私有访问权限，最后使用该访问权限定义新类。
     * 该方法在数据字典系统中用于动态生成字典转换器类。
     * </p>
     *
     * @param neighbor 新类的邻居类对象，用于获取模块信息和访问权限
     * @param name     类名
     * @param b        字节码数组
     * @return 动态加载的类对象
     * @throws IllegalAccessException 如果无法获取访问权限
     */
    public static Class<?> define(Class<?> neighbor, String name, byte[] b) throws IllegalAccessException {
        ClassUtil.class.getModule().addReads(neighbor.getModule());
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandles.Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
        return prvlookup.defineClass(b);
    }

    /**
     * 通过类名加载类对象
     * <p>
     * 该方法首先尝试使用当前类的模块加载器加载指定类名的类，
     * 如果加载失败，则使用传统的 Class.forName() 方法加载。
     * 这种方式可以处理模块系统下的类加载问题。
     * </p>
     *
     * @param className 类的全限定名
     * @return 加载的类对象
     * @throws ClassNotFoundException 如果找不到指定的类
     */
    public static Class<?> forName(String className) throws ClassNotFoundException {
        Class<?> aClass = Class.forName(ClassUtil.class.getModule(), className);
        if (aClass != null) {
            return aClass;
        }
        return Class.forName(className);
    }
}
