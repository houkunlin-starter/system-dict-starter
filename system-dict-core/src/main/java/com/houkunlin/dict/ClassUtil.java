package com.houkunlin.dict;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NonNull;

import java.lang.reflect.*;
import java.util.function.Function;

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
     * @param <T>   类类型
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

    // /**
    //  * 通过类名加载类对象
    //  * <p>
    //  * 该方法首先尝试使用当前类的模块加载器加载指定类名的类，
    //  * 如果加载失败，则使用传统的 Class.forName() 方法加载。
    //  * 这种方式可以处理模块系统下的类加载问题。
    //  * </p>
    //  *
    //  * @param className 类的全限定名
    //  * @return 加载的类对象
    //  * @throws ClassNotFoundException 如果找不到指定的类
    //  */
    // public static Class<?> forName(String className) throws ClassNotFoundException {
    //     Class<?> aClass = Class.forName(ClassUtil.class.getModule(), className);
    //     if (aClass != null) {
    //         return aClass;
    //     }
    //     return Class.forName(className);
    // }

    /**
     * 获取一个将字符串转换为指定值类型的解析函数
     * 根据传入的valueType参数，返回相应的字符串解析函数
     *
     * @param valueType 需要转换的目标类型
     * @return 解析函数，可以将字符串转换为指定的值类型
     */
    public static Function<@NonNull String, Object> getParseValueFunction(Class<?> valueType) {
        if (valueType == String.class) {
            // 对于字符串类型，直接返回原字符串
            return s -> s;
        } else if (valueType == Integer.class || valueType == int.class) {
            // 对于整数类型，使用Integer.parseInt方法进行解析
            return Integer::parseInt;
        } else if (valueType == Long.class || valueType == long.class) {
            // 对于长整型，使用Long.parseLong方法进行解析
            return Long::parseLong;
        } else if (valueType == Boolean.class || valueType == boolean.class) {
            // 对于布尔类型，使用Boolean.parseBoolean方法进行解析
            return Boolean::parseBoolean;
        } else if (valueType == Double.class || valueType == double.class) {
            // 对于双精度浮点型，使用Double.parseDouble方法进行解析
            return Double::parseDouble;
        } else if (valueType == Float.class || valueType == float.class) {
            // 对于单精度浮点型，使用Float.parseFloat方法进行解析
            return Float::parseFloat;
        } else if (valueType == Short.class || valueType == short.class) {
            // 对于短整型，使用Short.parseShort方法进行解析
            return Short::parseShort;
        } else if (valueType == Byte.class || valueType == byte.class) {
            // 对于字节类型，使用Byte.parseByte方法进行解析
            return Byte::parseByte;
        }
        // 其他复杂类型，尝试直接使用原始字符串
        return s -> s;
    }

    /**
     * 解析ClassEnum的泛型参数类型
     *
     * <p>通过反射获取ClassEnum接口的第一个泛型参数类型，如果无法获取则默认为String类型。</p>
     *
     * @param clazz 枚举类
     * @return 泛型参数类型
     */
    public static <T> Class<?> getInterfaceParameterFirst(Class<T> clazz) {
        Type genericInterface = clazz.getGenericInterfaces()[0];
        Class<?> parameterFirst = null;
        // 强转成 参数化类型 实体.
        if (genericInterface instanceof ParameterizedType) {
            // 获取超类的泛型类型数组. 即 <> 中的内容, 因为泛型可以有多个, 所以用数组表示
            Type[] actualTypeArguments = ((ParameterizedType) genericInterface).getActualTypeArguments();
            // 检查泛型参数数组长度是否大于指定索引，防止数组越界
            if (actualTypeArguments.length > 0) {
                Type typeArgument = actualTypeArguments[0];
                // 检查获取到的类型是否为Class类型，如果是则直接返回
                if (typeArgument instanceof Class<?>) {
                    parameterFirst = (Class<?>) typeArgument;
                }
            }
        }
        return parameterFirst;
    }

    /**
     * 查找带有@JsonCreator注解或符合特定条件的静态方法
     * 该方法用于查找枚举类中用于创建枚举实例的静态方法
     *
     * @param <T>       枚举类型
     * @param clazz     目标类
     * @param valueType 参数类型
     * @return 符合条件的方法，如果没有找到则返回null
     */
    public static <T> Method findJsonCreatorMethod(Class<T> clazz, Class<?> valueType) {
        Method findMethod = null;
        for (Method method : clazz.getDeclaredMethods()) {
            // 优先查找带有@JsonCreator注解且参数类型匹配的方法
            if (method.isAnnotationPresent(JsonCreator.class) && method.getParameters()[0].getType() == valueType) {
                return method;
            }
            // 查找符合以下条件的静态方法：
            // 1. 是静态方法
            // 2. 是公共方法
            // 3. 只有一个参数
            // 4. 参数类型与valueType匹配
            // 5. 返回类型与clazz相同
            if (Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())
                && method.getParameterCount() == 1
                && method.getParameters()[0].getType() == valueType
                && method.getReturnType() == clazz) {
                findMethod = method;
            }
        }
        return findMethod;
    }
}
