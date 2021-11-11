/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.entity;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.movement.IgnoreWorldBorder;
import me.zeroX150.atomic.feature.module.impl.movement.Squake;
import me.zeroX150.atomic.feature.module.impl.render.ESP;
import me.zeroX150.atomic.feature.module.impl.render.FreeLook;
import me.zeroX150.atomic.helper.squake.QuakeClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.stream.Stream;

@Mixin(Entity.class) public class EntityMixin {

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "NEW", target = "Lnet/minecraft/util/collection/ReusableStream;<init>")) private ReusableStream<VoxelShape> atomic_overwriteWBCollision(Stream<VoxelShape> stream) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(IgnoreWorldBorder.class)).isEnabled()) {
            return new ReusableStream<>(Stream.empty());
        }
        return new ReusableStream<>(stream);
    }

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true) void atomic_overwriteEspStats(CallbackInfoReturnable<Boolean> cir) {
        // this is a whole different layer of cursed
        ESP e = ModuleRegistry.getByClass(ESP.class);
        if (Objects.requireNonNull(e).isEnabled() && e.outlineMode.getValue().equalsIgnoreCase("shader") && e.shouldRenderEntity((Entity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    private void atomic_modifyVelocity(float movementSpeed, Vec3d movementInput, CallbackInfo info) {
        if (!ModuleRegistry.getByClass(Squake.class).isEnabled()) {
            return;
        }

        if (QuakeClientPlayer.updateVelocity((Entity) (Object) this, movementInput, movementSpeed)) {
            info.cancel();
        }
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.getYaw()F")) float atomic_overwriteFreelookYaw(Entity instance) {
        return instance.equals(Atomic.client.player) && ModuleRegistry.getByClass(FreeLook.class).isEnabled() ? ModuleRegistry.getByClass(FreeLook.class).newyaw : instance.getYaw();
    }
}
