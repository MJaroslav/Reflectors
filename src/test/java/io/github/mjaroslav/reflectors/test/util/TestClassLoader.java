package io.github.mjaroslav.reflectors.test.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestClassLoader extends ClassLoader {
    public @NotNull Class<?> defineClass(@Nullable String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
