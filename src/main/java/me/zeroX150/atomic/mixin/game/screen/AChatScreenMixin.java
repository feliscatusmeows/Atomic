package me.zeroX150.atomic.mixin.game.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.external.ClientConfig;
import me.zeroX150.atomic.feature.module.impl.external.InfChatLength;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.mixin.game.render.ITextFieldAccessor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Objects;

@Mixin(ChatScreen.class)
public abstract class AChatScreenMixin extends Screen {
    @Shadow
    protected TextFieldWidget chatField;

    protected AChatScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void onChatFieldUpdate(@SuppressWarnings("SameParameterValue") String chatText);

    @Inject(method = "render", at = @At("RETURN"))
    public void renderPost(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int maxLength = ((ITextFieldAccessor) chatField).getMaxLength();
        int cLength = chatField.getText().length();
        boolean showExtra = maxLength != Integer.MAX_VALUE;
        double perUsed = showExtra ? ((double) cLength / maxLength) : 0;
        String v = cLength + (showExtra ? (" / " + maxLength + " " + ((int) Math.round(perUsed * 100)) + "%") : "");
        float w = Atomic.monoFontRenderer.getStringWidth(v);
        Atomic.monoFontRenderer.drawString(matrices, v, this.width - 2 - w, this.height - 25, Renderer.lerp(new Color(255, 50, 50), new Color(50, 255, 50), perUsed).getRGB());
    }

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    public void chatFieldUpdatePre(String chatText, CallbackInfo ci) {
        chatField.setMaxLength((Objects.requireNonNull(ModuleRegistry.getByClass(InfChatLength.class)).isEnabled() || chatText.startsWith(ClientConfig.chatPrefix.getValue())) ? Integer.MAX_VALUE : 256);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initPost(CallbackInfo ci) {
        this.onChatFieldUpdate("");
    }
}
