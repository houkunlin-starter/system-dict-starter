package com.houkunlin.dict.bytecode;

/**
 * 自定义字节码类加载器
 *
 * @author HouKunLin
 */
public class BytecodeClassLoader extends ClassLoader {
    /**
     * 构造方法
     *
     * @param parent 上级类加载器对象
     */
    public BytecodeClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * 动态加载类信息
     *
     * @param name 类名
     * @param b    类字节码
     * @return 类对象
     */
    public Class<?> define(String name, byte[] b) {
        return defineClass(name, b, 0, b.length, getClass().getProtectionDomain());
    }
}
