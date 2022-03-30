package com.github.mjaroslav.reflectors.v0;

import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Reflectors {
    /**
     * Set true for debug logging.
     */
    public boolean enabledLogs;
    /**
     * Set true if game running in not dev environment.
     */
    public boolean obfuscated;

    /**
     * Reflect target class by reflector class. All methods from reflector class will replace all methods in
     * target class. But only if was found. For all non-static methods reflectors you should add source class object as
     * first parameter and use this as "this" in method. You also should add your mappings to root of mod jar with
     * methods.csv or methods.txt name (for txt format use lines: "srg,map" for every method).
     *
     * @param data           bytes of class for convert.
     * @param target         name of source class.
     * @param reflectorClass name of reflector class, you can use {@link Class#getName()} for this without problems,
     *                       I think.
     * @return bytes with converted class.
     */
    public byte[] reflectClass(byte[] data, @NotNull String target, @NotNull String reflectorClass) {
        log("Trying reflect target class " + target + " with " + reflectorClass + " reflector class...");
        val classNode = readClassFromBytes(data);
        val reflectorClassNode = readClassFromBytes(readClassBytes(reflectorClass));
        for (var method : reflectorClassNode.methods) {
            if ((method.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC &&
                    (method.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
                log("Found method " + method.name + method.desc + " trying to replace in target class...");
                replaceMethod(classNode, reflectorClassNode, method);
            }
        }
        log("Reflection of " + target + " done!");
        return writeClassToBytes(classNode, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
    }

    // ====================
    // Public utility
    // ====================

    /**
     * Get class bytes without loading.
     *
     * @param clazz class name.
     * @return byte array of class.
     */
    public byte[] readClassBytes(@NotNull String clazz) {
        val is = Reflectors.class.getResourceAsStream("/" + clazz.replace(".", "/") + ".class");
        if (is != null)
            try {
                val result = new byte[is.available()];
                is.read(result);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return new byte[]{};
    }

    /**
     * Convert byte array to class node.
     *
     * @param bytes source.
     * @return class node from byte array.
     */
    public @NotNull ClassNode readClassFromBytes(byte[] bytes) {
        val classNode = new ClassNode();
        val classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        return classNode;
    }

    /**
     * Convert class node to byte array.
     *
     * @param classNode source.
     * @param flags     see {@link ClassWriter#COMPUTE_FRAMES} and {@link ClassWriter#COMPUTE_MAXS}.
     * @return byte array of class.
     */
    public byte[] writeClassToBytes(@NotNull ClassNode classNode, int flags) {
        val writer = new ClassWriter(flags);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    /**
     * Find method by name and descriptor.
     *
     * @param classNode  target class.
     * @param methodName method name for search.
     * @param methodDesc method descriptor for search.
     * @return method or null if not found.
     */
    public @Nullable MethodNode findMethodNode(@NotNull ClassNode classNode, @NotNull String methodName,
                                               @NotNull String methodDesc) {
        for (var method : classNode.methods)
            if ((method.name.equals(methodName) || method.name.equals(unmapMethod(methodName))) &&
                    method.desc.equals(methodDesc))
                return method;
        return null;
    }

    /**
     * Remove first argument from method and get their descriptor.
     *
     * @param method method.
     * @return method descriptor without first argument.
     */
    public @NotNull String getMethodDescWithoutFirstArgument(@NotNull MethodNode method) {
        var reflectorParams = Type.getArgumentTypes(method.desc);
        if (reflectorParams.length == 0)
            return method.desc;
        val result = new StringBuilder("(");
        reflectorParams = Arrays.copyOfRange(reflectorParams, 1, reflectorParams.length);
        for (var param : reflectorParams)
            result.append(param.getDescriptor());
        return result.append(")").append(Type.getReturnType(method.desc).getDescriptor()).toString();
    }

    /***
     * Find source method node by reflector method node.
     *
     * @param classNode source class.
     * @param reflector reflector method.
     * @return source method or null if not found.
     */
    public @Nullable MethodNode findMethodNodeByReflector(@NotNull ClassNode classNode, @NotNull MethodNode reflector) {
        val reflectorParams = Type.getArgumentTypes(reflector.desc);
        val flag = reflectorParams.length > 0 && classNode.name.equals(reflectorParams[0].getDescriptor());
        var result = findMethodNode(classNode, reflector.name, reflector.desc);
        if (result != null && (result.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC && flag)
            return result;
        result = findMethodNode(classNode, reflector.name, getMethodDescWithoutFirstArgument(reflector));
        if (result != null && (result.access & Opcodes.ACC_STATIC) == 0)
            return result;
        return null;
    }

    /**
     * Get first instruction of method body.
     *
     * @param method method.
     * @return first body instruction.
     */
    @UnknownNullability
    public AbstractInsnNode findFirstInstruction(MethodNode method) {
        for (var instruction = method.instructions.getFirst(); instruction != null;
             instruction = instruction.getNext())
            if (instruction.getType() != AbstractInsnNode.LABEL && instruction.getType() != AbstractInsnNode.LINE)
                return instruction;
        return null;
    }

    /***
     * Replace method in classNode by reflectorMethod from reflectorClassNode.
     * @param classNode target class.
     * @param reflectorClassNode source class.
     * @param reflectorMethod method used for replacing.
     */
    public void replaceMethod(@NotNull ClassNode classNode, @NotNull ClassNode reflectorClassNode,
                              @NotNull MethodNode reflectorMethod) {
        val method = findMethodNodeByReflector(classNode, reflectorMethod);

        if (method == null) {
            log("Can't find target method!");
            return;
        }

        val replaceData = new InsnList();
        val isStatic = (method.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;

        loadMethodArguments(replaceData, Type.getArgumentTypes(method.desc), isStatic);

        replaceData.add(new MethodInsnNode(Opcodes.INVOKESTATIC, reflectorClassNode.name, reflectorMethod.name,
                reflectorMethod.desc, false));

        replaceData.add(new InsnNode(getReturnOpcodeFromType(Type.getReturnType(method.desc))));
        method.instructions.insertBefore(findFirstInstruction(method), replaceData);
        log("Method replaced");
    }

    /**
     * Add instruction for loading arguments.
     *
     * @param insnList  list with instructions.
     * @param arguments list of arguments.
     * @param isStatic  should not load "this" argument?
     */
    public void loadMethodArguments(@NotNull InsnList insnList, Type[] arguments, boolean isStatic) {
        if (!isStatic) // Add this for non-static method reflections
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));

        for (var i = 0; i < arguments.length; i++)
            insnList.add(new VarInsnNode(getLoadOpcodeForType(arguments[i]), i + 1));
    }

    /**
     * Get load opcode for type.
     *
     * @param type type.
     * @return opcode for loading parameter with this type.
     * @see Opcodes
     */
    public int getLoadOpcodeForType(Type type) {
        switch (type.getDescriptor()) {
            case "I":
            case "S":
            case "B":
            case "Z":
                return Opcodes.ILOAD;
            case "J":
                return Opcodes.LLOAD;
            case "D":
                return Opcodes.DLOAD;
            case "F":
                return Opcodes.FLOAD;
            default:
                return Opcodes.ALOAD;
        }
    }

    /**
     * Get return opcode for type.
     *
     * @param type type.
     * @return opcode for return with this type.
     * @see Opcodes
     */
    public int getReturnOpcodeFromType(Type type) {
        switch (type.getDescriptor()) {
            case "I":
            case "S":
            case "B":
            case "Z":
                return Opcodes.IRETURN;
            case "J":
                return Opcodes.LRETURN;
            case "D":
                return Opcodes.DRETURN;
            case "F":
                return Opcodes.FRETURN;
            case "V":
                return Opcodes.RETURN;
            default:
                return Opcodes.ARETURN;
        }
    }

    // ====================
    // Obfuscation helper
    // ====================

    /***
     * Map method name from SRG.
     * @param name method name.
     * @return unmapped name or itself if mappings not found.
     */
    public @NotNull String unmapMethod(@NotNull String name) {
        return obfuscated ? METHODS.getOrDefault(name, name) : name;
    }

    // TODO: Fields getters and setters
//    public @NotNull String unmapField(@NotNull String name) {
//        return obfuscated ? FIELDS.getOrDefault(name, name) : name;
//    }

    /**
     * Map method name to SRG.
     *
     * @param name method name.
     * @return mapped name or itself if mappings not found.
     */
    public @NotNull String mapMethod(@NotNull String name) {
        return obfuscated ? METHODS_REVERSE.getOrDefault(name, name) : name;
    }

    // TODO: Fields getters and setters
//    public @NotNull String mapField(@NotNull String name) {
//        return obfuscated ? FIELDS_REVERSE.getOrDefault(name, name) : name;
//    }

    // ====================
    // FMLLoadingPlugin adapter
    // ====================

    /***
     * Just adapter for FMLLoadingPlugin interface with auto setting {@link Reflectors#obfuscated} field.
     */
    public abstract static class FMLLoadingPluginAdapter {
        public abstract String[] getASMTransformerClass();

        public String getModContainerClass() {
            return null;
        }

        public String getSetupClass() {

            return null;
        }

        public void injectData(Map<String, Object> data) {
            obfuscated = ((Boolean) data.get("runtimeDeobfuscationEnabled"));
            if (obfuscated)
                log("Obfuscated environment");
        }

        public String getAccessTransformerClass() {
            return null;
        }
    }

    // ====================
    // Private utility
    // ====================
    private final Map<String, String> METHODS;
    private final Map<String, String> METHODS_REVERSE;
    // TODO: Fields getters and setters
//    private final Map<String, String> FIELDS;
//    private final Map<String, String> FIELDS_REVERSE;

    static {
        METHODS = new HashMap<>();
        METHODS_REVERSE = new HashMap<>();
        // TODO: Fields getters and setters
//        FIELDS = new HashMap<>();
//        FIELDS_REVERSE = new HashMap<>();

        // From FG mappers
        loadMappings(METHODS, METHODS_REVERSE, "/methods.csv");
        // TODO: Fields getters and setters
//        loadMappings(FIELDS, FIELDS_REVERSE, "/fields.csv");

        // Just UTF-8 file with "src,mapped" lines
        loadMappings(METHODS, METHODS_REVERSE, "/methods.txt");
        // TODO: Fields getters and setters
//        loadMappings(FIELDS, FIELDS_REVERSE, "/fields.txt");
    }

    private void log(@Nullable Object message) {
        if (enabledLogs)
            System.out.println("[Reflectors] " + message);
    }

    private void loadMappings(@NotNull Map<String, String> target, @NotNull Map<String, String> targetReverse,
                              @NotNull String resource) {
        try {
            var stream = Reflectors.class.getResourceAsStream(resource);
            if (stream == null)
                return;
            var reader = new BufferedReader(new InputStreamReader(stream));
            String[] splitted;
            var line = reader.readLine();
            while (line != null) {
                splitted = line.split(",");
                target.put(splitted[1], splitted[0]);
                targetReverse.put(splitted[0], splitted[1]);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
