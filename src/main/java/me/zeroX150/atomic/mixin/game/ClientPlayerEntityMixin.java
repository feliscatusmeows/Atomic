package me.zeroX150.atomic.mixin.game;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.external.PortalGUI;
import me.zeroX150.atomic.helper.ConfigManager;
import me.zeroX150.atomic.helper.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        Utils.TickManager.tick();
        if (!ConfigManager.enabled) ConfigManager.enableModules();
        for (Module module : ModuleRegistry.getModules()) {
            if (module.isEnabled()) module.tick();
        }
    }

    @Redirect(method = "updateNausea", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;isPauseScreen()Z"
    ))
    public boolean overwritePauseScreenGet(Screen screen) {
        // tell minecraft that the current screen is a pause screen, if the module is enabled
        // (a pause screen doesnt trigger the "clear screen" when in a portal)
        // else, tell it the truth
        return Objects.requireNonNull(ModuleRegistry.getByClass(PortalGUI.class)).isEnabled() || screen.isPauseScreen();
    }
}
