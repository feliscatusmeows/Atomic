/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui.children;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.ClickGUI;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.helper.Utils;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;

public class Clickable implements ContainerMember {
    final Module parent;
    final double width = 100;
    double hoverAnimation = 0;
    double enabledBarAnimation = 0;

    public Clickable(Module parent) {
        this.parent = parent;
    }

    double getMargin() {
        return ClickGUI.currentActiveTheme.h_margin();
    }

    public void render(double x, double y, MatrixStack stack, double actualX, double actualY, float delta, double parentY) {
        Color fillColor = ClickGUI.currentActiveTheme.inactive();
        Color fontColor = ClickGUI.currentActiveTheme.fontColor();
        if (!ClickGUI.INSTANCE.searchTerm.isEmpty()) {
            boolean isGood = true;
            for (char c : ClickGUI.INSTANCE.searchTerm.toLowerCase().toCharArray()) {
                if (!parent.getName().toLowerCase().contains(c + "")) {
                    isGood = false;
                    break;
                }
            }
            if (!isGood) {
                //fillColor = fillColor.darker().darker();
                fontColor = Renderer.Util.modify(fontColor, -1, -1, -1, 60);
            }
        }
        boolean isHovered = (actualX != -1 && actualY != -1 && isHovered(actualX, actualY, parentY));
        double apg = .03 * (delta + .5);
        if (isHovered) {
            ClickGUI.INSTANCE.renderDescription(parent.getDescription());
            //if (hoverAnimation < 0.3) hoverAnimation = 0.3d;
            hoverAnimation += apg;
        } else hoverAnimation -= apg;
        if (parent.isEnabled()) {
            enabledBarAnimation += 0.03 * (delta + 0.5);
        } else enabledBarAnimation -= 0.03 * (delta + 0.5);
        enabledBarAnimation = MathHelper.clamp(enabledBarAnimation, 0, 1);
        hoverAnimation = MathHelper.clamp(hoverAnimation, 0, 1);
        double barAnimInter = ease1(enabledBarAnimation);
        double hoverAnimationInter = ease2(hoverAnimation);
        double floor = Math.floor(y + (getMargin() + 9));
        DrawableHelper.fill(stack, (int) (x - getMargin()), (int) Math.floor(y - getMargin()), (int) (x + width + getMargin()), (int) floor, fillColor.getRGB());
        DrawableHelper.fill(stack, (int) (x - getMargin()), (int) Math.floor(y - getMargin()), (int) (x - getMargin() + (width + getMargin() * 2) * hoverAnimationInter), (int) floor, ClickGUI.currentActiveTheme.active().getRGB());
        //DrawableHelper.fill(stack, (int) (x - getMargin()), (int) Math.floor(y - getMargin()), (int) (x - getMargin() + 1.5), (int) Math.floor(y - getMargin() + ((getMargin() * 2 + 9) * barAnimInter) * animProgress), ClickGUI.currentActiveTheme.l_highlight().getRGB());
        Renderer.R2D.fill(stack, ClickGUI.currentActiveTheme.l_highlight(), x - getMargin(), y - getMargin(), x - getMargin() + 1.5, y - getMargin() + ((getMargin() * 2 + 9) * barAnimInter));
        if (ClickGUI.currentActiveTheme.centerText())
            Atomic.fontRenderer.drawCenteredString(stack, parent.getName(), (float) (x + (width / 2f)), (float) y, fontColor.getRGB());
        else
            Atomic.fontRenderer.drawString(stack, parent.getName(), (float) (x), (float) y, fontColor.getRGB());
    }

    double ease1(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    double ease2(double x) {
        return 1 - Math.pow(1 - x, 5);
    }

    public void clicked(boolean isLeft) {
        if (isLeft) parent.toggle();
        else if (parent.config.getAll().size() != 0) ClickGUI.INSTANCE.showModuleConfig(parent);
    }

    boolean isHovered(double x, double y, double py) {
        double mx = Utils.Mouse.getMouseX();
        double my = Utils.Mouse.getMouseY();
        if (my < py || my > py + 200) return false;
        return mx < x + width + getMargin() && mx > x - getMargin() && my < y + 9 + getMargin() && my > y - getMargin();
    }
}
