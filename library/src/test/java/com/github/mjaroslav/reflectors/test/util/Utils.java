package com.github.mjaroslav.reflectors.test.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@UtilityClass
public class Utils {
    public byte[] loadClassBytes(@NotNull String clazz) throws IOException {
        val classReader = new ClassReader(clazz);
        return classReader.b;
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T findAndInvokeMethod(@NotNull Class<?> clazz, @NotNull String methodName,
                                               @Nullable Object instance, @NotNull Object... args)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for (var method : clazz.getMethods())
            if (method.getName().equals(methodName))
                return (T) method.invoke(instance, args);
        throw new NoSuchMethodException(methodName);
    }
}
