/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.entity;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.exploit.Phase;
import me.zeroX150.atomic.feature.module.impl.movement.NoPush;
import me.zeroX150.atomic.feature.module.impl.render.Freecam;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void dontPushOutOfBlocks(double x, double z, CallbackInfo ci) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(Freecam.class)).isEnabled() || Objects.requireNonNull(ModuleRegistry.getByClass(NoPush.class)).isEnabled() || Objects.requireNonNull(ModuleRegistry.getByClass(Phase.class)).isEnabled())
            ci.cancel();
    }
}
