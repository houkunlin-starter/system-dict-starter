package com.houkunlin.system.dict.starter.bytecode;

/**
 * 自定义字节码类加载器
 *
 * @author HouKunLin
 */
public class BytecodeClassLoader extends ClassLoader {
    public BytecodeClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> define(String name, byte[] b) {
        return defineClass(name, b, 0, b.length, getClass().getProtectionDomain());
    }
}
