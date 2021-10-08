/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.mixin.network;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.misc.InfChatLength;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SPacketMixin {
    @Redirect(method = "<init>(Ljava/lang/String;)V", at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;length()I"
    ))
    public int redirectLength(String s) {
        return Objects.requireNonNull(ModuleRegistry.getByClass(InfChatLength.class)).isEnabled() ? 1 : s.length();
    }
}
