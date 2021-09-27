package me.zeroX150.atomic.feature.gui.widget;

import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.helper.Transitions;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HoverableExtenderWidget implements Drawable, Element, FastTickable, Selectable {
    List<Drawable> renders = new ArrayList<>();
    List<Element> children = new ArrayList<>();
    double sourceX, sourceY, width, height, extend, x;
    double renderX;
    boolean isHovered = false;

    public HoverableExtenderWidget(double x, double y, double width, double height, double extend) {
        sourceX = renderX = this.x = x - extend;
        sourceY = y;
        this.width = width;
        this.height = height;
        this.extend = extend;
    }

    public <T extends Element & Drawable> void addChild(T drawableElement) {
        renders.add(drawableElement);
        children.add(drawableElement);
    }

    @Override
    public void onFastTick() {
        renderX = Transitions.transition(renderX, this.x, 7);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        isHovered = renderX < mouseX && renderX + width > mouseX && sourceY < mouseY && sourceY + height > mouseY;
        if (isHovered) {
            this.x = sourceX - width + extend;
        } else this.x = sourceX;
        Renderer.fill(matrices, new Color(17, 17, 17, 200), renderX, sourceY, renderX + width, sourceY + height);
        matrices.translate(renderX, sourceY, 0);
        int mx = (int) (mouseX - renderX);
        int my = (int) (mouseY - sourceY);
        //Atomic.monoFontRenderer.drawString(new MatrixStack(),mx+" "+my, 1, 1, 0xFFFFFF);
        for (Drawable child : renders) {
            child.render(matrices, mx, my, delta);
        }
        matrices.pop();
    }

    @Override
    public SelectionType getType() {
        return isHovered ? SelectionType.HOVERED : SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element child : children) {
            if (child.mouseClicked(mouseX - renderX, mouseY - sourceY, button)) return true;
        }
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Element child : children) {
            if (child.mouseReleased(mouseX - renderX, mouseY - sourceY, button)) return true;
        }
        return Element.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (Element child : children) {
            child.mouseMoved(mouseX - renderX, mouseY - sourceY);
        }
        Element.super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child : children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return Element.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Element child : children) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) return true;
        }
        return Element.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Element child : children) {
            if (child.charTyped(chr, modifiers)) return true;
        }
        return Element.super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Element child : children) {
            if (child.mouseScrolled(mouseX - renderX, mouseY - sourceY, amount)) return true;
        }
        return Element.super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Element child : children) {
            if (child.mouseDragged(mouseX - renderX, mouseY - sourceY, button, deltaX, deltaY)) return true;
        }
        return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
