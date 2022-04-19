package com.github.mjaroslav.reflectors.test.object;

public class TestClass {
    public int methodInt() {
        return -5;
    }

    public void methodVoid() {
        throw new RuntimeException();
    }

    public short methodShort() {
        return 10;
    }

    public byte methodByte() {
        return -127;
    }

    public boolean methodBoolean() {
        return true;
    }

    public long methodLong() {
        return -1000000000;
    }

    public double methodDouble() {
        return 25.5;
    }

    public float methodFloat() {
        return -40.5f;
    }

    public char methodChar() {
        return 'A';
    }

    public String methodObject() {
        return "test";
    }

    public void methodMapped() {
        throw new RuntimeException();
    }

    public int methodManyArgs(int first, int second) {
        return first;
    }

    public static int methodStatic() {
        return 300;
    }

    public static int methodStaticArgs(int arg) {
        return arg;
    }
}
