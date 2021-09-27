package me.zeroX150.atomic.mixin.game.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.feature.module.impl.external.ClientConfig;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@SuppressWarnings("ConstantConditions")
@Mixin(ClickableWidget.class)
public abstract class AButtonWidgetMixin implements FastTickable {

    final Color unselectedColor = new Color(25, 44, 49, 70);
    final Color disabledColor = new Color(0, 0, 0, 70);
    @Shadow
    public int x;
    @Shadow
    public int y;
    @Shadow
    public boolean active;
    @Shadow
    protected int width;
    @Shadow
    protected int height;
    double animProg = 0;

    @Shadow
    public abstract boolean isHovered();

    @Shadow
    public abstract Text getMessage();

    @Override
    public void onFastTick() {
        double e = 0.03d;
        if (!this.isHovered() || !this.active) e *= -1;
        //else if (animProg < 0.3) animProg = 0.3d;
        animProg += e;
        animProg = MathHelper.clamp(animProg, 0, 1);
    }

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ClientConfig.customButtons.getValue()) return;
        int dxStart, dyStart, dWidth, dHeight;

        if ((((Object) this) instanceof ButtonWidget) || (((Object) this) instanceof CyclingButtonWidget<?>)) {
            dxStart = x;
            dyStart = y;
            dWidth = width;
            dHeight = height;
        } else if (((Object) this) instanceof SliderWidget inst) {
            ISliderWidgetAccessor accessor = (ISliderWidgetAccessor) inst;
            double prog = accessor.getValue();
            dHeight = 20;
            dWidth = 4;
            dxStart = (int) (x + (prog * (width - 4))); // wtf why
            dyStart = y;
        } else return;
        ci.cancel();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        animProg = MathHelper.clamp(animProg, 0, 1);
        //animProg = this.active ? animProg : 0;
        double interpolatedAProg = ease(this.animProg);
        boolean isSlider = ((Object) this) instanceof SliderWidget;
        if (!isSlider) {
            double rw = Renderer.lerp(width, 0, interpolatedAProg) / 2d;
            if (rw != 0)
                Renderer.fill(matrices, new Color(0, 194, 111, 255), x + (width / 2d) - rw, y + height - 1, x + (width / 2d) + rw, y + height);
        } else {
            DrawableHelper.fill(matrices, dxStart, dyStart, dxStart + dWidth, dyStart + dHeight, new Color(0, 194, 111, 255).getRGB());
        }
        DrawableHelper.fill(matrices, x, y, x + width, y + height, this.active ? Renderer.lerp(new Color(38, 83, 92, 100), unselectedColor, interpolatedAProg).getRGB() : disabledColor.getRGB());
        DrawableHelper.drawCenteredText(matrices, Atomic.client.textRenderer, this.getMessage(), this.x + this.width / 2,
                this.y + (this.height - 8) / 2, Color.white.getRGB());
    }

    double ease(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

}
