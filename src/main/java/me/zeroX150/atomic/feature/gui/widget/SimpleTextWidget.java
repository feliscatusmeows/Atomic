/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.feature.gui.widget;

import me.zeroX150.atomic.Atomic;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public class SimpleTextWidget implements Element, Drawable {
    double x, y;
    int color;
    String text;
    boolean center = false;

    public SimpleTextWidget(double x, double y, String text, int color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (center) Atomic.fontRenderer.drawCenteredString(matrices, text, x, y, color);
        else Atomic.fontRenderer.drawString(matrices, text, x, y, color);
    }
}
