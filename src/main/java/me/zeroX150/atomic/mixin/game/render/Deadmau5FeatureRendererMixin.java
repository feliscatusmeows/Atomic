package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.fun.Deadmau5;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(Deadmau5FeatureRenderer.class)
public class Deadmau5FeatureRendererMixin {
    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"
    ))
    boolean bruh(String s, Object anObject) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(Deadmau5.class)).isEnabled()) return true;
        return s.equals(anObject);
    }

    @Redirect(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;hasSkinTexture()Z"
    ))
    boolean bruh1(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(Deadmau5.class)).isEnabled()) return true;
        return abstractClientPlayerEntity.hasSkinTexture();
    }
}
