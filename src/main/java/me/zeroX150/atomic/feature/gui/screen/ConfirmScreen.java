package me.zeroX150.atomic.feature.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.Transitions;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConfirmScreen extends Screen implements FastTickable {
    final BooleanConsumer callback;
    final Screen parent;
    final String title;
    final String desc;
    double animProg = 0;
    boolean closed = false;

    public ConfirmScreen(String title, String description, Screen parent, BooleanConsumer callback) {
        super(Text.of(""));
        this.callback = callback;
        this.parent = parent;
        this.title = title;
        this.desc = description;
    }

    @Override
    public void onClose() {
        closed = true;
    }

    @Override
    public void onFastTick() {
        double a = 0.02;
        if (closed) a *= -1;
        animProg += a;
        animProg = MathHelper.clamp(animProg, 0, 1);
    }

    double ease(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (closed && animProg == 0) {
            Atomic.client.setScreen(parent);
            return;
        }
        double animProg1 = ease(this.animProg);
        double animProg = Transitions.easeOutBack(this.animProg);
        parent.render(matrices, 0, 0, delta);
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(0, 0, 0, (int) ((animProg1) * 100)).getRGB());

        matrices.translate((1 - animProg) * getW(), (1 - animProg) * getH(), 0);
        matrices.scale((float) animProg, (float) animProg, 0);
        //matrices.translate(-animProg*this.width,0,0);
        Atomic.fontRenderer.drawCenteredString(matrices, title, getW(), 20, 0xFFFFFF);
        int yOff = 0;
        String e = "";
        List<String> parts = new ArrayList<>();
        for (char c : desc.toCharArray()) {
            e += c;
            if (Atomic.monoFontRenderer.getStringWidth(e) > width - 50) {
                parts.add(e);
                e = "";
            }
        }
        parts.add(e);
        for (String s : String.join("\n", parts).split("\n")) {
            Atomic.monoFontRenderer.drawCenteredString(matrices, s, getW(), 40 + yOff, 0xFFFFFF);
            yOff += 10;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    int getW() {
        return (int) (width / 2d);
    }

    int getH() {
        return (int) (height / 2d);
    }

    @Override
    protected void init() {
        ButtonWidget yes = new ButtonWidget(getW() - 155, getH() - 10, 150, 20, Text.of("Yes"), button -> {
            callback.accept(true);
            onClose();
        });
        ButtonWidget no = new ButtonWidget(getW() + 5, getH() - 10, 150, 20, Text.of("No"), button -> {
            callback.accept(false);
            onClose();
        });
        addDrawableChild(yes);
        addDrawableChild(no);
        closed = false;
        animProg = 0;
    }
}
