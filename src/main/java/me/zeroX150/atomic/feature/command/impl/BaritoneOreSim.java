/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.oreSim.OreSim;
import me.zeroX150.atomic.helper.Utils;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

import java.util.Objects;

public class BaritoneOreSim extends Command {

    public BaritoneOreSim() {
        super("OreSimAutomine", "Start and stop baritone", "automine");
    }

    @Override
    public void onExecute(String[] args) {
        if (FabricLoader.getInstance().getModContainer("baritone").isEmpty()) {
            Utils.Client.sendMessage("Baritone is not installed ");
            return;
        }
        OreSim oreSim = ModuleRegistry.getByClass(OreSim.class);
        if (!Objects.requireNonNull(oreSim).isEnabled()) {
            Utils.Client.sendMessage("You need to have oresim enabled for this");
            return;
        }
        if (args.length == 1) {
            String command = args[0].toLowerCase();
            if (command.equals("start")) {
                Atomic.log(Level.INFO, "Starting baritone oresim");
                oreSim.automine = true;
                return;
            } else if (command.equals("stop")) {
                Atomic.log(Level.INFO, "Stopping baritone oresim");
                oreSim.automine = false;
                return;
            }
        }
        Utils.Client.sendMessage("Syntax: .automine <start/stop>");
    }
}
