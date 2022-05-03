package com.github.mjaroslav.reflectors.test;

import com.github.mjaroslav.reflectors.test.util.TestClassLoader;
import com.github.mjaroslav.reflectors.test.util.Utils;
import com.github.mjaroslav.reflectors.v4.Reflectors;
import lombok.val;
import lombok.var;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("ConstantConditions")
public class ReflectorsTest {
    private static final String TEST_CLASS = "com.github.mjaroslav.reflectors.test.object.TestClass";
    private static final String TEST_CLASS_REFLECTOR = "com.github.mjaroslav.reflectors.test.object.TestClassReflector";
    private static final String PATCH_ERROR = "Method not patched!";

    private static Object testClassInstance;
    private static Class<?> testClass;

    @BeforeClass
    public static void resetClassLoader() throws IOException, InstantiationException, IllegalAccessException {
        val classLoader = new TestClassLoader();
        var bytes = Utils.loadClassBytes(TEST_CLASS);
        Reflectors.obfuscated = true;
        bytes = Reflectors.reflectClass(bytes, TEST_CLASS, TEST_CLASS_REFLECTOR);
        testClass = classLoader.defineClass(TEST_CLASS, bytes);
        testClassInstance = testClass.newInstance();
    }

    @Test
    public void reflectMethodInt() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, -5, (int) Utils.findAndInvokeMethod(testClass, "methodInt",
                testClassInstance));
    }

    @Test
    public void reflectMethodByte() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, 127, (byte) Utils.findAndInvokeMethod(testClass, "methodByte",
                testClassInstance));
    }

    @Test
    public void reflectMethodShort() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, -10, (short) Utils.findAndInvokeMethod(testClass, "methodShort",
                testClassInstance));
    }

    @Test
    public void reflectMethodChar() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, 'a', (char) Utils.findAndInvokeMethod(testClass, "methodChar",
                testClassInstance));
    }

    @Test
    public void reflectMethodBoolean() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertFalse(PATCH_ERROR, Utils.findAndInvokeMethod(testClass, "methodBoolean", testClassInstance));
    }

    @Test
    public void reflectMethodObject() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, "TEST TEST", Utils.findAndInvokeMethod(testClass, "methodObject",
                testClassInstance));
    }

    @Test
    public void reflectMethodLong() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, 1000000000, (long) Utils.findAndInvokeMethod(testClass, "methodLong",
                testClassInstance));
    }

    @Test
    public void reflectMethodFloat() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, 40.5f, Utils.findAndInvokeMethod(testClass, "methodFloat", testClassInstance),
                0);
    }

    @Test
    public void reflectMethodDouble() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, -25.5, Utils.findAndInvokeMethod(testClass, "methodDouble", testClassInstance),
                0);
    }

    @Test
    public void reflectMethodVoid() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Utils.findAndInvokeMethod(testClass, "methodVoid", testClassInstance);
    }

    @Test
    public void reflectMethodStatic() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, -300, (int) Utils.findAndInvokeMethod(testClass, "methodStatic",
                testClassInstance));
    }

    @Test
    public void reflectMethodManyArgs() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, 1, (int) Utils.findAndInvokeMethod(testClass, "methodManyArgs",
                testClassInstance, -1, 1));
    }

    @Test
    public void reflectMethodStaticArgs() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Assert.assertEquals(PATCH_ERROR, -1, (int) Utils.findAndInvokeMethod(testClass, "methodStaticArgs",
                testClassInstance, 1));
    }

    @Test
    public void reflectMethodMapped() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Utils.findAndInvokeMethod(testClass, "methodMapped", testClassInstance);
    }
}
