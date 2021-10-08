/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class MoonGravity extends Module {
    public MoonGravity() {
        super("MoonGravity", "what would you do if you'd be on the moon?", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        if (Atomic.client.player == null || Atomic.client.getNetworkHandler() == null) return;
        Atomic.client.player.addVelocity(0, 0.0568000030517578, 0);
        // yea thats literally it
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}

