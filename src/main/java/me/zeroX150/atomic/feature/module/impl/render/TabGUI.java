/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.Transitions;
import me.zeroX150.atomic.helper.Utils;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.KeyboardEvent;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TabGUI extends Module {
    final double mheight = 12;
    final double x = 5;
    final double y = 5;
    boolean expanded = false;
    int selectedModule = 0;
    double trackedSelectedModule = 0;
    int fixedSelected = 0;
    double aprog = 0;
    double anim = 0;
    double mwidth = 60;
    int selected = 0;
    double trackedSelected = 0;

    public TabGUI() {
        super("Tab GUI", "Renders a small module manager top left", ModuleType.RENDER);
        Events.registerEventHandler(EventType.KEYBOARD, event -> {
            if (!this.isEnabled()) return;
            KeyboardEvent ke = (KeyboardEvent) event;
            if (ke.getType() != 1) return;
            int kc = ke.getKeycode();
            if (!expanded) {
                if (kc == GLFW.GLFW_KEY_DOWN) {
                    selected++;
                } else if (kc == GLFW.GLFW_KEY_UP) {
                    selected--;
                } else if (kc == GLFW.GLFW_KEY_RIGHT && aprog == 0) {
                    fixedSelected = selected;
                    expanded = true;
                }
            } else {
                if (kc == GLFW.GLFW_KEY_DOWN) {
                    selectedModule++;
                } else if (kc == GLFW.GLFW_KEY_UP) {
                    selectedModule--;
                } else if (kc == GLFW.GLFW_KEY_LEFT) {
                    expanded = false;
                } else if (kc == GLFW.GLFW_KEY_RIGHT || kc == GLFW.GLFW_KEY_ENTER) {
                    ModuleType t = getModulesForDisplay()[fixedSelected];
                    List<Module> v = new ArrayList<>();
                    for (Module module : ModuleRegistry.getModules()) {
                        if (module.getModuleType() == t) v.add(module);
                    }
                    v.get(selectedModule).toggle();
                }
            }
            selected = clampRevert(selected, getModulesForDisplay().length);
            if (expanded) {
                int mcCurrentCategory = 0;
                for (Module module : ModuleRegistry.getModules()) {
                    if (module.getModuleType() == getModulesForDisplay()[selected]) mcCurrentCategory++;
                }
                selectedModule = clampRevert(selectedModule, mcCurrentCategory);
            } else selectedModule = 0;
        });
    }

    int clampRevert(int n, int max) {
        if (n < 0) n = max - 1;
        else if (n >= max) n = 0;
        return n;
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onFastTick() {
        trackedSelected = Transitions.transition(trackedSelected, selected, 10, 0.0001);
        trackedSelectedModule = Transitions.transition(trackedSelectedModule, selectedModule, 10, 0.0001);
        aprog = Transitions.transition(aprog, anim, 5, 0.0001);
    }

    @Override
    public void onHudRender() {
        Color bg = new Color(0, 0, 0, 255);
        Color active = Utils.getCurrentRGB();
        Color inactive = active.darker().darker();
        mwidth = 4 + Atomic.fontRenderer.getStringWidth(getModulesForDisplay()[0].getName()); // types sorted, so 0 will be the longest
        MatrixStack stack = Renderer.R3D.getEmptyMatrixStack();
        double yOffset = 0;
        int index = 0;
        for (ModuleType value : getModulesForDisplay()) {
            int c = 0xFFFFFF;
            if (aprog != 0 && index != selected)
                c = Renderer.Util.lerp(new Color(0xAA, 0xAA, 0xAA), Color.WHITE, aprog).getRGB();
            Renderer.R2D.fill(bg, x, y + yOffset, x + mwidth, y + yOffset + mheight);
            Atomic.fontRenderer.drawString(stack, value.getName(), x + 2, y + yOffset + (mheight - 9) / 2f + 0.5f, c);
            yOffset += mheight;
            index++;
        }
        double selectedOffset = mheight * trackedSelected;
        Renderer.R2D.fill(expanded ? inactive : active, x, y + selectedOffset, x + 1, y + selectedOffset + mheight);
        if (expanded) {
            anim = 1;
        } else anim = 0;
        ModuleType t = getModulesForDisplay()[fixedSelected];
        List<Module> a = new ArrayList<>();
        for (Module module : ModuleRegistry.getModules()) {
            if (module.getModuleType() == t) a.add(module);
        }
        double rx = x + mwidth + 3;
        double ry = y + mheight * fixedSelected;
        int yoff = 0;
        double w = Atomic.fontRenderer.getStringWidth(a.get(0).getName()) + 4;
        Renderer.R2D.scissor(rx - 3, ry, w + 3, mheight * a.size());
        rx -= (w + 3) * (1 - aprog);
        for (Module module : a) {
            Renderer.R2D.fill(bg, rx, ry + yoff, rx + w, ry + yoff + mheight);
            if (module.isEnabled()) {
                Renderer.R2D.fill(Color.WHITE, rx + w, ry + yoff, rx + w - 1, ry + yoff + mheight);
            }
            Atomic.fontRenderer.drawString(stack, module.getName(), rx + 2, ry + yoff + (mheight - 9) / 2f + 0.5f, 0xFFFFFF);
            yoff += mheight;
        }
        double selectedOffset1 = mheight * trackedSelectedModule;
        Renderer.R2D.fill(active, rx, ry + selectedOffset1, rx + 1, ry + selectedOffset1 + mheight);
        Renderer.R2D.unscissor();
    }

    ModuleType[] getModulesForDisplay() {
        return Arrays.stream(ModuleType.values()).filter(moduleType -> moduleType != ModuleType.HIDDEN).sorted(Comparator.comparingDouble(value -> -Atomic.fontRenderer.getStringWidth(value.getName()))).toArray(ModuleType[]::new);
    }
}
