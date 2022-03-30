package com.github.mjaroslav.reflectors.example.reflector;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.Random;

// All original methods in source class will be replaced with public static methods of reflectors (this) class.
// But this work only with declared methods.
public class BlockStoneReflector {
    // For reflect non-static methods, you should add source class object as first argument and use this as
    // "this" in method.
    public static Item getItemDropped(BlockStone instance, int meta, Random rand, int fortune) {
        // Just replace stone block drop from cobblestone to diamond
        return Items.diamond;
    }
}
