/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.children.ContainerMember;
import me.zeroX150.atomic.helper.Transitions;
import me.zeroX150.atomic.helper.font.FontRenderer;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Draggable {
    public final String title;
    final List<PositionD> recordedPositions = new ArrayList<>();
    final double width = 100;
    final List<ContainerMember> children = new ArrayList<>();
    double openAnimation = 0;
    double posX;
    double posY;
    double lastRenderX = -1;
    double lastRenderY = -1;
    double trackedLastRenderX = -1;
    boolean expanded;
    boolean dragged = false;
    long lastRender = System.currentTimeMillis();
    double scroll = -1;
    double trackedScroll = -1;
    double maxScroll = 0;

    public Draggable(String title, boolean isExpanded) {
        this.title = title;
        this.expanded = isExpanded;
    }

    double getMargin() {
        return ClickGUI.currentActiveTheme.h_margin();
    }

    double getPaddingX() {
        return ClickGUI.currentActiveTheme.h_paddingX();
    }

    public void addChild(ContainerMember child) {
        this.children.add(child);
    }

    public void onFastTick() {
        double h = (getMargin() * 2 + 9) * children.size();
        trackedScroll = Transitions.transition(trackedScroll, scroll, 7);
        maxScroll = h - Math.min(199, h);
        if (scroll == -1) {
            scroll = trackedScroll = maxScroll;
        }
    }

    public void tick() {
        float xDiff = (float) (lastRenderX - posX);
        float yDiff = (float) (lastRenderY - posY);
        double nxDiff = (xDiff / (me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue()));
        double nyDiff = (yDiff / (me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue()));
        if (Math.abs(nxDiff) < 0.02) nxDiff = xDiff;
        if (Math.abs(nyDiff) < 0.02) nyDiff = yDiff;
        lastRenderX -= nxDiff;
        lastRenderY -= nyDiff;
        trackedLastRenderX = lastRenderX;
    }

    double easeOutBounce(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

    public void mouseReleased() {
        this.dragged = false;
    }

    public boolean mouseClicked(boolean isLeft, double x, double y) {
        double actualScroll = maxScroll - trackedScroll;
        if (lastRenderX + width + getMargin() > x && lastRenderX - getMargin() < x && lastRenderY + ClickGUI.currentActiveTheme.titleHeight() + getMargin() > y && lastRenderY - getMargin() < y) {
            if (isLeft) {
                this.dragged = true;
            } else this.expanded = !this.expanded;
            return true;
        } else if (this.expanded) {
            y += actualScroll;
            double yOffset = ClickGUI.currentActiveTheme.titleHeight() + getMargin() * 2;
            for (ContainerMember child : children) {
                double childPosY = lastRenderY + yOffset;
                double childPosX = lastRenderX;
                if (y < (lastRenderY + ClickGUI.currentActiveTheme.titleHeight() + getMargin() - 1 + actualScroll) + 200 && childPosX + width + getMargin() > x && childPosX - getMargin() < x && childPosY + 9 + getMargin() > y && childPosY - getMargin() < y) {
                    child.clicked(isLeft);
                    break;
                }
                yOffset += 9 + getMargin() * 2;
            }
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (openAnimation != 1) return false;
        double h = (getMargin() * 2 + 9) * children.size();
        boolean isMouseOverHere = lastRenderX + width + getMargin() > mouseX && lastRenderX - getMargin() < mouseX && lastRenderY + ClickGUI.currentActiveTheme.titleHeight() + getMargin() + Math.min(200, h) > mouseY && lastRenderY + ClickGUI.currentActiveTheme.titleHeight() + getMargin() < mouseY;
        if (isMouseOverHere) scroll += amount * 10;
        //scroll = scroll > 0 ? 0 : scroll;
        scroll = MathHelper.clamp(scroll, 0, maxScroll);

        //scroll = Math.max(scroll, h);
        return isMouseOverHere;
    }

    public void render(MatrixStack stack, float delta, double aProgI, double yOff) {
        if (this.expanded) openAnimation += (System.currentTimeMillis() - lastRender) / 500d;
        else openAnimation -= (System.currentTimeMillis() - lastRender) / 500d;
        if (System.currentTimeMillis() - lastRender > 1) lastRender = System.currentTimeMillis();
        openAnimation = MathHelper.clamp(openAnimation, 0, 1);
        double openAnimationInter = easeOutBounce(openAnimation);
        if (lastRenderX == -1) lastRenderX = posX;
        if (lastRenderY == -1) lastRenderY = posY;
        stack.translate(lastRenderX - getMargin() - getPaddingX(), lastRenderY - getMargin(), 0);
        PositionD v = new PositionD(lastRenderX - getMargin() - getPaddingX(), lastRenderY - getMargin(), 0);
        if (!recordedPositions.contains(v)) recordedPositions.add(v);
        else recordedPositions.add(null);
        while (recordedPositions.size() > me.zeroX150.atomic.feature.module.impl.render.ClickGUI.tailSize.getValue())
            recordedPositions.remove(0);
        double val = 0;
        double incr = 1d / recordedPositions.stream().filter(Objects::nonNull).count();
        if (me.zeroX150.atomic.feature.module.impl.render.ClickGUI.enableTails.getValue())
            for (PositionD recordedPosition : recordedPositions) {
                if (recordedPosition == null) continue;
                MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
                ms.push();
                ms.translate(recordedPosition.x(), recordedPosition.y(), -100);
                ms.translate(aProgI * Atomic.client.getWindow().getScaledWidth(), 0, 0);
                ms.multiply(new Quaternion(new Vec3f(0, 0, 1), (float) (recordedPosition.rot()), true));
                Color c = Renderer.Util.modify(new Color(Color.HSBtoRGB((float) val, 0.6f, 0.6f)), -1, -1, -1, 30);
                Renderer.R2D.fill(ms, c, -getPaddingX() * 2, 0, width + getMargin() + getPaddingX() * 2 + 4, ClickGUI.currentActiveTheme.titleHeight() + getMargin() * 2);
                val += incr;
                ms.pop();
            }
        if (this.openAnimation != 0) {
            double p = -aProgI * Atomic.client.getWindow().getScaledWidth();
            Renderer.R2D.scissor(p + (lastRenderX - getMargin() - getPaddingX() * 3) - 1, (lastRenderY + ClickGUI.currentActiveTheme.titleHeight() + getMargin() - 1), (width + getMargin() + 4 + getPaddingX() * 2) + 2, (Math.min(200, (getMargin() * 2 + 9) * children.size()) * openAnimationInter));
            double yOffset = ClickGUI.currentActiveTheme.titleHeight() + getMargin() * 2;
            stack.push();
            //stack.scale(1, (float) openAnimationInter, 1);
            double actualScroll = maxScroll - trackedScroll;
            stack.translate(0, -actualScroll, 0);
            for (ContainerMember child : children) {
                double px = this.dragged || openAnimationInter != 1 ? -1 : lastRenderX;
                double py = this.dragged || openAnimationInter != 1 ? -1 : lastRenderY + (yOffset * openAnimationInter);
                child.render(getMargin(), getMargin() + yOffset, stack, px, py + yOff - actualScroll, delta, lastRenderY + ClickGUI.currentActiveTheme.titleHeight() + getMargin() - 1);
                yOffset += 9 + getMargin() * 2;
            }
            stack.pop();
            Renderer.R2D.unscissor();
        }
        Renderer.R2D.fill(stack, Renderer.Util.lerp(ClickGUI.currentActiveTheme.h_exp(), ClickGUI.currentActiveTheme.h_ret(), openAnimationInter), -(getPaddingX() * 2), 0, width + getMargin() + 4 + getPaddingX() * 2, ClickGUI.currentActiveTheme.titleHeight() + getMargin() * 2);
        //DrawableHelper.fill(stack, (int) -(getPaddingX() * 2), 0, (int) (width + getMargin() + 4 + getPaddingX() * 2), (int) (ClickGUI.currentActiveTheme.titleHeight() + getMargin() * 2), Renderer.lerp(ClickGUI.currentActiveTheme.h_exp(), ClickGUI.currentActiveTheme.h_ret(), openAnimationInter).getRGB());
        FontRenderer.FontType ft = FontRenderer.FontType.SHADOW_THIN;
        if (me.zeroX150.atomic.feature.module.impl.render.ClickGUI.theme.getValue().equalsIgnoreCase("walmart sigma"))
            ft = FontRenderer.FontType.NORMAL;
        if (!ClickGUI.currentActiveTheme.centerText())
            Atomic.fontRenderer.drawString(stack, title, getMargin(), getMargin() + (ClickGUI.currentActiveTheme.titleHeight() / 2 - 9 / 2d), ft, ClickGUI.currentActiveTheme.titleColor().getRGB());
        else
            Atomic.fontRenderer.drawCenteredString(stack, title, (float) (getMargin() + width / 2f), (float) getMargin(), ft, ClickGUI.currentActiveTheme.titleColor().getRGB());
        //DrawableHelper.drawCenteredText(stack, Atomic.client.textRenderer, title, (int) (getMargin() + (width / 2)), (int) margin, 0xFFFFFF);
        stack.translate(-(lastRenderX - getMargin() - getPaddingX()), -(lastRenderY - getMargin()), 0);
    }

    public void mouseMove(double deltaX, double deltaY) {
        if (this.dragged) {
            this.posX += deltaX;
            this.posY += deltaY;
        }
    }

    record PositionD(double x, double y, double rot) {
    }
}
