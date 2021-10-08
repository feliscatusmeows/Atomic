/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.feature.module;

public enum ModuleType {
    MOVEMENT("Movement"), RENDER("Render"), MISC("Miscellaneous"), COMBAT("Combat"), WORLD("World"), EXPLOIT("Exploit"), HIDDEN(""), FUN("Fun");


    final String name;

    ModuleType(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }
}
