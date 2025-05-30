package com.houkunlin.system.dict.starter;

import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

@Slf4j
public class ClassUtilJava8 {
    public static final Method defineClass;
    public static final MethodHandle defineClassHandle;

    static {
        MethodHandle defineClassHandle1 = null;
        try {
            defineClassHandle1 = AccessController.doPrivileged(
                new PrivilegedExceptionAction<MethodHandle>() {
                    public MethodHandle run() throws IllegalAccessException,
                        NoSuchMethodException, SecurityException {
                        Method rmet = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class,
                            ProtectionDomain.class);
                        rmet.setAccessible(true);
                        MethodHandle meth = MethodHandles.lookup().unreflect(rmet);
                        rmet.setAccessible(false);
                        return meth;
                    }
                });
        } catch (Throwable ignore) {
        }
        // defineClassHandle1.invokeWithArguments(loader, name, b, off, len, protectionDomain);
        defineClassHandle = defineClassHandle1;

        Method defineClass1 = null;
        if (System.getSecurityManager() == null) {
            try {
                defineClass1 = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
            } catch (Throwable ignore) {
            }
        } else {
            try {
                defineClass1 = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Method>() {
                        public Method run() throws Exception {
                            return ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
                        }
                    });
            } catch (Throwable ignore) {
            }
        }
        // defineClass1.invoke(loader, name, b, off, len, protectionDomain)
        if (defineClass1 != null) {
            try {
                defineClass1.setAccessible(true);
            } catch (Throwable throwable) {
                log.warn("set ClassLoader.defineClass accessible true error: {}", throwable.getMessage());
                defineClass1 = null;
            }
        }
        defineClass = defineClass1;
    }

    private ClassUtilJava8() {
    }

    public static Class<?> define(Class<?> neighbor, String name, byte[] b) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (defineClass != null) {
            try {
                return (Class<?>) defineClass.invoke(contextClassLoader, name, b, 0, b.length, null);
            } catch (IllegalAccessException | InvocationTargetException ignore) {
            }
        }
        if (defineClassHandle != null) {
            try {
                return (Class<?>) defineClassHandle.invokeWithArguments(contextClassLoader, name, b, 0, b.length, null);
            } catch (Throwable ignore) {
            }
        }
        return null;
    }
}
