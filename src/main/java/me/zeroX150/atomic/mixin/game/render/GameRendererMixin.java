/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.NoRender;
import me.zeroX150.atomic.feature.module.impl.render.Zoom;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    Module noRender;
    private boolean vb;
    private boolean dis;

    @Inject(
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0),
            method = "renderWorld")
    void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        Renderer.R3D.setLastRenderStack(matrix);
        if (vb) {
            Atomic.client.options.bobView = true;
            vb = false;
        }
        for (Module module : ModuleRegistry.getModules()) {
            if (module.isEnabled()) module.onWorldRender(matrix);
        }
    }

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void hurtCam(MatrixStack matrices, float f, CallbackInfo ci) {
        if (noRender == null) noRender = ModuleRegistry.getByClass(NoRender.class);
        if (Objects.requireNonNull(noRender).isEnabled() && NoRender.hurtAnimation.getValue()) ci.cancel();
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        double zv = Zoom.getZoomValue(cir.getReturnValue());
        cir.setReturnValue(zv);
    }

    // Mixins are broken as shit in this version or something so I have to do it this fucking dumbass way

    @Inject(at = @At("HEAD"), method = "renderWorld")
    private void renderWorldHead(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        dis = true;
    }

    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void fixTracerBobbing(MatrixStack matrices, float f, CallbackInfo ci) {
        if (Atomic.client.options.bobView && dis) {
            vb = true;
            Atomic.client.options.bobView = false;
            dis = false;
            ci.cancel();
        }
    }
}
