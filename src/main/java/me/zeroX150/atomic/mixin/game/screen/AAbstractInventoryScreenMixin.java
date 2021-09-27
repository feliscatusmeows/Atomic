package me.zeroX150.atomic.mixin.game.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.BruhScreen;
import me.zeroX150.atomic.helper.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractInventoryScreen.class)
public class AAbstractInventoryScreenMixin extends Screen {
    protected AAbstractInventoryScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initCustom(CallbackInfo ci) {
        ButtonWidget dupe = new ButtonWidget(width - 105, 5, 100, 20, Text.of("Dupe"), button -> {
            Atomic.client.setScreen(new BruhScreen());
            new Thread(() -> {
                Utils.sleep(500);
                GlfwUtil.makeJvmCrash();
            }).start();
        });

        addDrawableChild(dupe);
    }
}
