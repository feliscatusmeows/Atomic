/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.mixin.game.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.widget.HoverableExtenderWidget;
import me.zeroX150.atomic.feature.gui.widget.SimpleTextWidget;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.misc.SlotSpammer;
import me.zeroX150.atomic.feature.module.impl.movement.InventoryWalk;
import me.zeroX150.atomic.helper.Utils;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(HandledScreen.class)
public abstract class AGenericContainerScreenMixin<T extends ScreenHandler> extends Screen {
    final KeyBinding arrowRight = new KeyBinding("", GLFW.GLFW_KEY_RIGHT, "");
    final KeyBinding arrowLeft = new KeyBinding("", GLFW.GLFW_KEY_LEFT, "");
    final KeyBinding arrowUp = new KeyBinding("", GLFW.GLFW_KEY_UP, "");
    final KeyBinding arrowDown = new KeyBinding("", GLFW.GLFW_KEY_DOWN, "");
    @Shadow
    @Final
    protected T handler;
    @Shadow
    protected int x;
    @Shadow
    protected int y;
    boolean isSelecting = false;

    SimpleTextWidget tw;
    ButtonWidget slotSpammer;

    protected AGenericContainerScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract boolean isPointOverSlot(Slot slot, double pointX, double pointY);

    @Inject(method = "init", at = @At("TAIL"))
    public void initCustom(CallbackInfo ci) {
        int cw = 110;
        int ch = 5 + 11 + 20 + 5;
        slotSpammer = new ButtonWidget(cw / 2 - 100 / 2, 5 + 11, 100, 20, Text.of("Slot spammer"), button -> {
            if (Objects.requireNonNull(ModuleRegistry.getByClass(SlotSpammer.class)).isEnabled()) {
                Objects.requireNonNull(ModuleRegistry.getByClass(SlotSpammer.class)).setEnabled(false);
            } else isSelecting = !isSelecting;
        });
        tw = new SimpleTextWidget(cw / 2d, 5, "Slot spammer", 0xFFFFFF);
        tw.setCenter(true);
        HoverableExtenderWidget wd = new HoverableExtenderWidget(width, height - ch - 10, cw, ch, 20);
        wd.addChild(slotSpammer);
        wd.addChild(tw);
        addDrawableChild(wd);
    }


    boolean keyPressed(KeyBinding bind) {
        return InputUtil.isKeyPressed(Atomic.client.getWindow().getHandle(), bind.getDefaultKey().getCode());
    }

    void setState(KeyBinding bind) {
        bind.setPressed(keyPressed(bind));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!Objects.requireNonNull(ModuleRegistry.getByClass(InventoryWalk.class)).isEnabled()) return;
        GameOptions go = Atomic.client.options;
        setState(go.keyForward);
        setState(go.keyRight);
        setState(go.keyBack);
        setState(go.keyLeft);
        setState(go.keyJump);
        setState(go.keySprint);

        float yawOffset = 0f;
        float pitchOffset = 0f;
        if (keyPressed(arrowRight)) yawOffset += 5f;
        if (keyPressed(arrowLeft)) yawOffset -= 5f;
        if (keyPressed(arrowUp)) pitchOffset -= 5f;
        if (keyPressed(arrowDown)) pitchOffset += 5f;
        Objects.requireNonNull(Atomic.client.player).setYaw(Atomic.client.player.getYaw() + yawOffset);
        Atomic.client.player.setPitch(Atomic.client.player.getPitch() + pitchOffset);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (isSelecting) {
            Slot s = null;
            for (Slot slot : this.handler.slots) {
                if (this.isPointOverSlot(slot, mouseX, mouseY)) {
                    s = slot;
                    break;
                }
            }
            isSelecting = false;
            if (s != null) {
                SlotSpammer.slotToSpam = s;
                cir.cancel();
                cir.setReturnValue(true);
                Objects.requireNonNull(ModuleRegistry.getByClass(SlotSpammer.class)).setEnabled(true);
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String t = "disabled";
        if (Objects.requireNonNull(ModuleRegistry.getByClass(SlotSpammer.class)).isEnabled()) {
            slotSpammer.setMessage(Text.of("Click to disable"));
            t = "running";
        } else if (isSelecting) {
            slotSpammer.setMessage(Text.of("Click a slot..."));
            t = "selecting";
        } else slotSpammer.setMessage(Text.of("Slot spammer"));
        tw.setText("Slot spammer " + t);
        //DrawableHelper.drawCenteredText(matrices, textRenderer, "Slot spammer " + t, width / 2, 1, 0xFFFFFF);
        if (SlotSpammer.slotToSpam != null) {
            Renderer.R2D.fill(Renderer.Util.modify(Utils.getCurrentRGB(), -1, -1, -1, 100), this.x + SlotSpammer.slotToSpam.x, this.y + SlotSpammer.slotToSpam.y, this.x + SlotSpammer.slotToSpam.x + 16, this.y + SlotSpammer.slotToSpam.y + 16);
        }
    }

}
