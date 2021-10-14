/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.entity;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.movement.IgnoreWorldBorder;
import me.zeroX150.atomic.feature.module.impl.movement.Squake;
import me.zeroX150.atomic.feature.module.impl.render.ESP;
import me.zeroX150.atomic.helper.squake.QuakeClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.stream.Stream;

@Mixin(Entity.class)
public class EntityMixin {
    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;concat(Ljava/util/stream/Stream;Ljava/util/stream/Stream;)Ljava/util/stream/Stream;"
    )) <T> Stream<T> disableWBCollision(Stream<? extends T> a, Stream<? extends T> b) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(IgnoreWorldBorder.class)).isEnabled())
            return Stream.empty();
        else return Stream.concat(a, b);
    }

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    void setEspGlow(CallbackInfoReturnable<Boolean> cir) {
        // this is a whole different layer of cursed
        ESP e = ModuleRegistry.getByClass(ESP.class);
        if (Objects.requireNonNull(e).isEnabled() && e.outlineMode.getValue().equalsIgnoreCase("shader") && e.shouldRenderEntity((Entity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    private void updateVelocity(float movementSpeed, Vec3d movementInput, CallbackInfo info) {
        if (!ModuleRegistry.getByClass(Squake.class).isEnabled())
            return;

        if (QuakeClientPlayer.updateVelocity((Entity) (Object) this, movementInput, movementSpeed))
            info.cancel();
    }
}
