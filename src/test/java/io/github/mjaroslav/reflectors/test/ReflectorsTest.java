package io.github.mjaroslav.reflectors.test;

import io.github.mjaroslav.reflectors.test.util.TestClassLoader;
import io.github.mjaroslav.reflectors.test.util.Utils;
import io.github.mjaroslav.reflectors.v5.Reflectors;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("ConstantConditions")
class ReflectorsTest {
    static final String TEST_CLASS = "io.github.mjaroslav.reflectors.test.TestClass";
    static final String TEST_CLASS_REFLECTOR = "io.github.mjaroslav.reflectors.test.TestClassReflector";
    static final String PATCH_ERROR = "Method not patched!";

    static Object testClassInstance;
    static Class<?> testClass;

    @BeforeAll
    static void setup() throws Exception {
        val classLoader = new TestClassLoader();
        var bytes = Utils.loadClassBytes(TEST_CLASS);
        Reflectors.obfuscated = true;
        bytes = Reflectors.reflectClass(bytes, TEST_CLASS, TEST_CLASS_REFLECTOR);
        testClass = classLoader.defineClass(TEST_CLASS, bytes);
        testClassInstance = testClass.getConstructor().newInstance();
    }

    @Test
    void test$reflectMethodInt() throws Exception {
        assertEquals(-5, (int) Utils.findAndInvokeMethod(testClass, "methodInt",
            testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodByte() throws Exception {
        assertEquals(127, (byte) Utils.findAndInvokeMethod(testClass, "methodByte",
            testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodShort() throws Exception {
        assertEquals(-10, (short) Utils.findAndInvokeMethod(testClass, "methodShort",
            testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodChar() throws Exception {
        assertEquals('a', (char) Utils.findAndInvokeMethod(testClass, "methodChar",
            testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodBoolean() throws Exception {
        assertFalse(Utils.findAndInvokeMethod(testClass, "methodBoolean", testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodObject() throws Exception {
        assertEquals("TEST TEST", Utils.findAndInvokeMethod(testClass, "methodObject",
            testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodLong() throws Exception {
        assertEquals(1000000000, (long) Utils.findAndInvokeMethod(testClass, "methodLong",
            testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodFloat() throws Exception {
        assertEquals(40.5f, Utils.findAndInvokeMethod(testClass, "methodFloat", testClassInstance),
            0, PATCH_ERROR);
    }

    @Test
    void test$reflectMethodDouble() throws Exception {
        assertEquals(-25.5, Utils.findAndInvokeMethod(testClass, "methodDouble", testClassInstance),
            0, PATCH_ERROR);
    }

    @Test
    void test$reflectMethodVoid() throws Exception {
        Utils.findAndInvokeMethod(testClass, "methodVoid", testClassInstance);
    }

    @Test
    void test$reflectMethodStatic() throws Exception {
        assertEquals(-300, (int) Utils.findAndInvokeMethod(testClass, "methodStatic",
            testClassInstance), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodManyArgs() throws Exception {
        assertEquals(1, (int) Utils.findAndInvokeMethod(testClass, "methodManyArgs",
            testClassInstance, -1, 1), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodStaticArgs() throws Exception {
        assertEquals(-1, (int) Utils.findAndInvokeMethod(testClass, "methodStaticArgs",
            testClassInstance, 1), PATCH_ERROR);
    }

    @Test
    void test$reflectMethodMapped() throws Exception {
        Utils.findAndInvokeMethod(testClass, "methodMapped", testClassInstance);
    }
}
