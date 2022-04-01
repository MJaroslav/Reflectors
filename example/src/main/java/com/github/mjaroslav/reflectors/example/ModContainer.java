package com.github.mjaroslav.reflectors.example;

import cpw.mods.fml.common.DummyModContainer;

public class ModContainer extends DummyModContainer {
    @Override
    public String getModId() {
        return "reflectorsexample";
    }

    @Override
    public String getName() {
        return "ReflectorsExample";
    }

    @Override
    public String getVersion() {
        return "@VERSION@";
    }
}
