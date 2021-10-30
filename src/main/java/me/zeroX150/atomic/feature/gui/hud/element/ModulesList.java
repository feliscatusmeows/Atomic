package me.zeroX150.atomic.feature.gui.hud.element;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.Color;
import java.util.Comparator;

public class ModulesList extends HudElement {
    Alignment used = Alignment.RIGHT;

    public ModulesList() {
        super("Modules", Atomic.client.getWindow().getWidth(), 0, 100, 400);
    }

    @Override public void renderIntern(MatrixStack stack) {
        if (this.posX + this.width > Atomic.client.getWindow().getScaledWidth() - 5)
            used = Alignment.RIGHT; // if we're between width-5 - width
        else if (this.posX < 5) used = Alignment.LEFT; // if we're between 0 - 5
        else used = Alignment.CENTER;
        int moduleOffset = 0;
        float rgbIncrementer = 0.03f;
        float currentRgbSeed = (System.currentTimeMillis() % 4500) / 4500f;
        // jesus fuck
        Module[] v = ModuleRegistry.getModules().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparingDouble(value -> FontRenderers.normal.getStringWidth(value.getName() + (value.getContext() != null ? " " + value.getContext() : "")))) // i mean it works?
                .toArray(Module[]::new);
        ArrayUtils.reverse(v);
        float maxWidth = 0;
        for (Module module : v) {
            currentRgbSeed %= 1f;
            int r = Color.HSBtoRGB(currentRgbSeed, 0.7f, 1f);
            currentRgbSeed += rgbIncrementer;
            String w = module.getName() + (module.getContext() == null ? "" : " " + module.getContext());
            float totalWidth = FontRenderers.normal.getStringWidth(w);
            maxWidth = Math.max(maxWidth, totalWidth);
            Color c = new Color(r);
            Color inv = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
            switch (used) {
                case RIGHT -> {
                    FontRenderers.normal.drawString(stack, module.getName(), width - 4 - totalWidth, moduleOffset + .5, r);
                    Renderer.R2D.fill(stack, c, width - 2, moduleOffset, width, moduleOffset + FontRenderers.normal.getFontHeight() + 1);
                    if (module.getContext() != null)
                        FontRenderers.normal.drawString(stack, module.getContext(), width - 4 - totalWidth + FontRenderers.normal.getStringWidth(module.getName() + " "), moduleOffset + .5, inv.getRGB());
                }
                case LEFT -> {
                    FontRenderers.normal.drawString(stack, module.getName(), 4, moduleOffset + .5, r);
                    Renderer.R2D.fill(stack, c, 0, moduleOffset, 2, moduleOffset + FontRenderers.normal.getFontHeight() + 1);
                    if (module.getContext() != null)
                        FontRenderers.normal.drawString(stack, module.getContext(), 4 + FontRenderers.normal.getStringWidth(module.getName() + " "), moduleOffset + .5, inv.getRGB());
                }
                case CENTER -> {
                    FontRenderers.normal.drawString(stack, module.getName(), width / 2f - totalWidth / 2f, moduleOffset + .5, r);
                    // Atomic.fontRenderer.drawString(stack, module.getContext(), width-4-totalWidth+Atomic.fontRenderer.getStringWidth(module.getName() + " "), moduleOffset + .5, inv.getRGB());
                    if (module.getContext() != null)
                        FontRenderers.normal.drawString(stack, module.getContext(), width / 2f - totalWidth / 2f + FontRenderers.normal.getStringWidth(module.getName() + " "), moduleOffset + .5, inv.getRGB());

                }
            }
            moduleOffset += FontRenderers.normal.getFontHeight() + 1;
        }
        this.height = moduleOffset;
        maxWidth += 4;
        if (maxWidth != this.width) {
            double delta = maxWidth - this.width;
            this.posX -= delta;
            this.width = maxWidth;
        }
    }

    enum Alignment {
        RIGHT, CENTER, LEFT
    }
}
