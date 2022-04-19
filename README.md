# Reflectors
Библиотека, позволяющая легко заменять методы классов.

Сама по себе библиотека состоит из одного класса, что
позволяет просто встраивать её в свой __небольшой__ мод.

[![](https://jitpack.io/v/MJaroslav/Reflectors.svg)](https://jitpack.io/#MJaroslav/Reflectors)

## Как пользоваться?

Для начала, скачиваем класс (вся библиотека в одном 
небольшом классе) и помещаем его в свой проект.

Также можно воспользоваться shade'дингом с репозитория 
[jitpack.io](https://jitpack.io).

**build.gradle:**
```groovy
// Добавляем репозиторий jitpack в свои
repositories {
    maven {
        name 'jitpack'
        url 'https://jitpack.io'
    }
}

// Простая реализация shade'динга (включения зависимостей с
// указанной конфигурацией (shade) в jar мода 
configurations {
    shade
    
    compile.extendsFrom(shade)
}

jar {
    configurations.shade.each { dep ->
        from project.zipTree(dep)
    }
}

// Shade'им библиотеку
dependencies {
    // Подставляем выбранную версию (начиная от v1 включительно)
    shade "com.github.MJaroslav.Reflectors:library:%VERSION%"
}
```

Также для работы в обфусцированной среде требуется
добавить ваши маппинги методов в корень ресурсов проекта
(`methods.csv` или `methods.txt`, во втором случае формат
строк маппингов такой: `srg,map` для каждого метода).

Создаем свой `FMLLoadingPlugin`:

```java
package com.github.mjaroslav.reflectors.example;

import com.github.mjaroslav.reflectors.example.reflector.BlockStoneReflector;
import com.github.mjaroslav.reflectors.v2.Reflectors;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;

@IFMLLoadingPlugin.MCVersion("1.7.10")
// Эта аннотация требуется для работы плагина в среде SRG имён
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.Name("ReflectorsExamplePlugin")
// Можно воспользоваться встроенным адаптером, который также
// выставляет поле Reflections.obfuscated.
// В противном случае вы должны определять его сами
public class ReflectorsExamplePlugin extends Reflectors.FMLLoadingPluginAdapter
        implements IFMLLoadingPlugin, IClassTransformer {
    public ReflectorsExamplePlugin() {
        // Включаем логирование библиотеки (необязательно)
        Reflectors.enabledLogs = true;
    }

    @Override
    public String[] getASMTransformerClass() {
        // Для удобства, будем использовать в качестве трансформера
        // этот же класс
        return new String[]{getClass().getName()};
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // Давайте применим рефлекторы к блоку камня.
        if (transformedName.equals("net.minecraft.block.BlockStone"))
            // Достаточно просто вызвать этот метод и указать класс-рефлектор.
            return Reflectors.reflectClass(basicClass, transformedName, BlockStoneReflector.class.getName());
        return basicClass;
    }
}
```

Создаем сам рефлектор:

```java
package com.github.mjaroslav.reflectors.example.reflector;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.Random;

// Все оригинальные методы будут заменены соответствующими методами из
// этого класса-рефлектора. Но заменить можно (на данный момент) только
// декларированные методы целевого класса
// Все методы-рефлекторы должны быть public static
public class BlockStoneReflector {
    // У всех методов-рефлекторов нестатичных методов должен быть объект
    // целевого класса в качестве первого аргумента
    public static Item getItemDropped(BlockStone instance, int meta, Random rand, int fortune) {
        // В качестве примера, заменим дроп камня с булыжника на алмаз
        return Items.diamond;
    }
}
```
Вот и всё, не забудьте добавить ваш `FMLLoadingPlugin` в манифест или аргументы запуска:

Добавление в манифест:
```groovy
jar {
    manifest {
        attributes "FMLCorePlugin": "com.github.mjaroslav.reflectors.example.ReflectorsExamplePlugin"
        // Требуется для загрузки вместе с обычными модами
        attributes "FMLCorePluginContainsFMLMod": "true"
    }
}
```

Добавление в аргументы запуска:
`-Dfml.coreMods.load=com.github.mjaroslav.reflectors.example.ReflectorsExamplePlugin`

_Этот же пример можно найти в подпроекте `example`_