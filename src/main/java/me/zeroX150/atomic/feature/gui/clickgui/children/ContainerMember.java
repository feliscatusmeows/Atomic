/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui.children;

import net.minecraft.client.util.math.MatrixStack;

public interface ContainerMember {
    void render(double x, double y, MatrixStack stack, double actualX, double actualY, float delta, double parentY);

    void clicked(boolean isLeft);
}
