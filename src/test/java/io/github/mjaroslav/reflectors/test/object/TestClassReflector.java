package io.github.mjaroslav.reflectors.test.object;

import org.jetbrains.annotations.NotNull;

public class TestClassReflector {
    public static int methodInt(@NotNull TestClass instance) {
        return -5;
    }

    public static void methodVoid(@NotNull TestClass instance) {
    }

    public static short methodShort(@NotNull TestClass instance) {
        return -10;
    }

    public static byte methodByte(@NotNull TestClass instance) {
        return 127;
    }

    public static boolean methodBoolean(@NotNull TestClass instance) {
        return false;
    }

    public static long methodLong(@NotNull TestClass instance) {
        return 1000000000;
    }

    public static double methodDouble(@NotNull TestClass instance) {
        return -25.5;
    }

    public static float methodFloat(@NotNull TestClass instance) {
        return 40.5f;
    }

    public static char methodChar(@NotNull TestClass instance) {
        return 'a';
    }

    public static String methodObject(@NotNull TestClass instance) {
        return "TEST TEST";
    }

    public static void methodUnmapped(@NotNull TestClass instance) {
    }

    public static int methodManyArgs(@NotNull TestClass instance, int first, int second) {
        return second;
    }

    public static int methodStatic() {
        return -300;
    }

    public static int methodStaticArgs(int arg) {
        return -arg;
    }
}
