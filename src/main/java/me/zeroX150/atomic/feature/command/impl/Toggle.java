/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.Utils;

public class Toggle extends Command {
    public Toggle() {
        super("Toggle", "toggles a module", "toggle", "t");
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            Utils.Client.sendMessage("ima need the module name");
            return;
        }
        Module m = ModuleRegistry.getByName(args[0].toLowerCase());
        if (m == null) {
            Utils.Client.sendMessage("Module not found bruh");
        } else m.toggle();
    }
}
