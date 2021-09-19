package me.zeroX150.atomic.feature.gui.clickgui.children;

import net.minecraft.client.util.math.MatrixStack;

public interface ContainerMember {
    void render(double x, double y, MatrixStack stack, double animProgress, double actualX, double actualY, float delta, double scroll, double parentY);

    void clicked(boolean isLeft);
}
