package com.houkunlin.dict;

import javassist.util.proxy.DefineClassHelper;

import java.lang.invoke.MethodHandles;

/**
 * 类工具：Java11 及以上
 *
 * @author HouKunLin
 */
public class ClassUtilJava11 {
    /**
     * 私有构造方法
     */
    private ClassUtilJava11() {
    }

    /**
     * 动态加载字节码类
     *
     * @param neighbor 新类的邻居类对象
     * @param name     类名
     * @param b        字节码
     * @return 类对象
     */
    public static Class<?> define(Class<?> neighbor, String name, byte[] b) {
        try {
            DefineClassHelper.class.getModule().addReads(neighbor.getModule());
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
            return prvlookup.defineClass(b);
        } catch (IllegalAccessException | IllegalArgumentException ignore) {
        }
        return null;
    }
}
