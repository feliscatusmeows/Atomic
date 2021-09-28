package me.zeroX150.atomic.mixin.game;

import com.woopra.java.sdk.WoopraEvent;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.external.AntiReducedDebugInfo;
import me.zeroX150.atomic.feature.module.impl.external.FastUse;
import me.zeroX150.atomic.feature.module.impl.misc.WindowCustomization;
import me.zeroX150.atomic.helper.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private int itemUseCooldown;

    @Inject(method = "stop", at = @At("HEAD"))
    public void onStop(CallbackInfo ci) {
        ConfigManager.saveState();
    }

    @Inject(method = "hasReducedDebugInfo", at = @At("HEAD"), cancellable = true)
    public void overwriteReducedDebugInfo(CallbackInfoReturnable<Boolean> cir) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(AntiReducedDebugInfo.class)).isEnabled())
            cir.setReturnValue(false);
    }

    @Redirect(method = "handleInputEvents", at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I"
    ))
    public int replaceItemUseCooldown(MinecraftClient minecraftClient) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(FastUse.class)).isEnabled()) return 0;
        else return this.itemUseCooldown;
    }

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> cir) {
        String v = Objects.requireNonNull(ModuleRegistry.getByClass(WindowCustomization.class)).title.getValue();
        if (Objects.requireNonNull(ModuleRegistry.getByClass(WindowCustomization.class)).isEnabled() && !v.isEmpty())
            cir.setReturnValue(v);
    }

    @Inject(method = "setScreen", at = @At("HEAD"))
    void bruh(Screen screen, CallbackInfo ci) {
        DateFormat df = new SimpleDateFormat("k:m:s");
        String t = df.format(System.currentTimeMillis());
        if (screen != null && screen.getClass().getPackageName().contains("atomic")) { // we opened a screen, analytics time
            Atomic.sendAnalyticsMessage(new WoopraEvent("screen", new Object[][]{
                    {"openTime", t},
                    {"name", screen.getClass().getName()}
            }));
        }
    }
}
