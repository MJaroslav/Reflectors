package com.github.mjaroslav.reflectors.example;

import com.github.mjaroslav.reflectors.example.reflector.BlockStoneReflector;
import com.github.mjaroslav.reflectors.v2.Reflectors;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * You can extend you class by {@link Reflectors.FMLLoadingPluginAdapter}
 *
 * @see Reflectors.FMLLoadingPluginAdapter
 */
@IFMLLoadingPlugin.MCVersion("1.7.10")
// You should use this index (> 1000) for SRG names while patching
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.Name("ReflectorsExamplePlugin")
public class ReflectorsExamplePlugin extends Reflectors.FMLLoadingPluginAdapter
        implements IFMLLoadingPlugin, IClassTransformer {
    public ReflectorsExamplePlugin() {
        Reflectors.enabledLogs = true;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{getClass().getName()};
    }

    @Override
    public String getModContainerClass() {
        return ModContainer.class.getName();
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // Let's use reflectors for Stone block class
        if (transformedName.equals("net.minecraft.block.BlockStone"))
            // Just call this method with your reflectors class name
            return Reflectors.reflectClass(basicClass, transformedName, BlockStoneReflector.class.getName());
        return basicClass;
    }
}
