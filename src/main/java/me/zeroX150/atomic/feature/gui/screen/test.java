package me.zeroX150.atomic.feature.gui.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.widget.HoverableExtenderWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class test extends Screen {
    public test() {
        super(Text.of(""));
    }

    @Override
    protected void init() {
        HoverableExtenderWidget e = new HoverableExtenderWidget(width, height - 70, 100, 50, 5);
        ButtonWidget b = new ButtonWidget(0, 0, 80, 20, Text.of("no way"), button -> Atomic.client.setScreen(null));
        e.addChild(b);
        addDrawableChild(e);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
