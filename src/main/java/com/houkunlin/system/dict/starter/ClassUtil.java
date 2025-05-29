package com.houkunlin.system.dict.starter;

import java.lang.reflect.Constructor;

public class ClassUtil {
    private ClassUtil() {
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
}
