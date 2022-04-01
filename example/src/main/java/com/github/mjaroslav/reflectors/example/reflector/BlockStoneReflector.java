package com.github.mjaroslav.reflectors.example.reflector;

import com.google.common.collect.BiMap;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.Random;

// Все оригинальные методы в целевом классе будут заменены одноименными
// публичными и статичными из класса-отражателя. Если отражается не статичных метод,
// нужно добавить в качестве первого аргумента метода-отражателя объект целевого класса.
// Отражать можно только декларированные методы.
public class BlockStoneReflector {
    // В данным примере мы заменяем метод getItemDropped(int, java.util.Random, int) в классе
    // net.minecraft.block.BlockStone. Заметьте, что в целевом классе этот метод не является
    // статичным и объект целевого класса добавлен в качестве первого аргумента метода отражателя.
    public static Item getItemDropped(BlockStone instance, int meta, Random rand, int fortune) {
        // Для примера просто заменим дроп блока камня (и всех, кто его наследует и не переопределяет
        // этот метод) с булыжника на алмаз.
        return Items.diamond;
    }
}
