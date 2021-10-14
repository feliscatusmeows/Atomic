/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.children.Clickable;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.impl.client.ClientConfig;
import me.zeroX150.atomic.helper.Transitions;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClickGUI extends Screen implements FastTickable {
    public static final Identifier LOGO = new Identifier("atomic", "logo.png");
    public static Themes.Palette currentActiveTheme = Themes.Theme.ATOMIC.getPalette();
    public static ClickGUI INSTANCE;
    final List<Draggable> containers = new ArrayList<>();
    public ConfigWidget currentConfig = null;
    public String searchTerm = "";
    long lastRender = System.currentTimeMillis();
    int currentSortMode = 0;
    boolean closed = false;
    double aProg = 1.0;
    String desc = "";
    int clicks = 0;
    int p = 0;

    public ClickGUI() {
        super(Text.of(""));
        double width = Atomic.client.getWindow().getScaledWidth();
        INSTANCE = this;

        for (ModuleType value : ModuleType.values()) {
            if (value == ModuleType.HIDDEN) continue;
            Draggable d = new Draggable(value.getName(), false);
            d.lastRenderX = width;
            d.lastRenderY = 0;
            d.posX = 0;
            d.posY = 0;
            d.expanded = true;
            for (Module module : ModuleRegistry.getModules()) {
                if (module.getModuleType() == value) {
                    Clickable w = new Clickable(module);
                    d.addChild(w);
                }
            }
            containers.add(d);
        }
        sort(true);
    }

    public void onFastTick() {
        double a = me.zeroX150.atomic.feature.module.impl.render.ClickGUI.instant.getValue() ? 1 : 0.02;
        aProg += closed ? a : -a;
        aProg = MathHelper.clamp(aProg, 0, 1);
        for (Draggable container : containers.toArray(new Draggable[0])) {
            container.tick();
        }
        if (currentConfig != null) currentConfig.tick();
        //trackedScroll = Transitions.transition(trackedScroll, actualScroll, me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());

        Themes.Palette cTheme = currentActiveTheme;
        Themes.Palette p = switch (me.zeroX150.atomic.feature.module.impl.render.ClickGUI.theme.getValue().toLowerCase()) {
            case "walmart sigma" -> Themes.Theme.SIGMA.getPalette();
            case "atomic" -> Themes.Theme.ATOMIC.getPalette();
            case "dark" -> Themes.Theme.DARK.getPalette();
            case "custom" -> new Themes.Palette(
                    me.zeroX150.atomic.feature.module.impl.render.ClickGUI.cInactive.getColor(),
                    me.zeroX150.atomic.feature.module.impl.render.ClickGUI.cActive.getColor(),
                    me.zeroX150.atomic.feature.module.impl.render.ClickGUI.cHighlight.getColor(),
                    me.zeroX150.atomic.feature.module.impl.render.ClickGUI.cHRet.getColor(),
                    me.zeroX150.atomic.feature.module.impl.render.ClickGUI.cHExp.getColor(),
                    me.zeroX150.atomic.feature.module.impl.render.ClickGUI.cFont.getColor(),
                    me.zeroX150.atomic.feature.module.impl.render.ClickGUI.cTitle.getColor(),
                    Themes.Theme.ATOMIC.getPalette().h_margin(),
                    Themes.Theme.ATOMIC.getPalette().h_paddingX(),
                    Themes.Theme.ATOMIC.getPalette().centerText(),
                    Themes.Theme.ATOMIC.getPalette().titleHeight()
            ); // oh god
            default -> throw new IllegalStateException("Unexpected value: " + me.zeroX150.atomic.feature.module.impl.render.ClickGUI.theme.getValue().toLowerCase());
        };
        Color newInactive = Transitions.transition(cTheme.inactive(), p.inactive(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        Color newActive = Transitions.transition(cTheme.active(), p.active(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        Color newHiglight = Transitions.transition(cTheme.l_highlight(), p.l_highlight(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        Color newRet = Transitions.transition(cTheme.h_ret(), p.h_ret(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        Color newExp = Transitions.transition(cTheme.h_exp(), p.h_exp(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        Color newFCol = Transitions.transition(cTheme.fontColor(), p.fontColor(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        Color newTCol = Transitions.transition(cTheme.titleColor(), p.titleColor(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        double newMargin = Transitions.transition(cTheme.h_margin(), p.h_margin(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        double newPadding = Transitions.transition(cTheme.h_paddingX(), p.h_paddingX(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        double newDHeight = Transitions.transition(cTheme.titleHeight(), p.titleHeight(), me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        currentActiveTheme = new Themes.Palette(newInactive, newActive, newHiglight, newRet, newExp, newFCol, newTCol, newMargin, newPadding, p.centerText(), newDHeight);

        for (Draggable container : containers) {
            container.onFastTick();
        }
    }

    @Override
    public void tick() {
        p++;
        if (p > 11) {
            p = 0;
            clicks--;
            clicks = MathHelper.clamp(clicks, 0, 3);
        }
        super.tick();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    void sort(boolean overwrite) {
        currentSortMode++;
        currentSortMode %= 2;
        double offsetX = 20;
        double offsetY = me.zeroX150.atomic.feature.module.impl.render.ClickGUI.logoSize.getValue() * 130 + 20;
        for (Draggable container : containers.stream().sorted(Comparator.comparingInt(value -> -value.children.size())).collect(Collectors.toList())) {
            container.posX = offsetX;
            container.posY = offsetY;
            if (overwrite) {
                container.lastRenderX = offsetX;
                container.lastRenderY = offsetY;
            }
            offsetX += 120;
            if (offsetX + 120 > Atomic.client.getWindow().getScaledWidth()) {
                offsetX = 20;
                offsetY += 27;
            }
        }
    }

    @Override
    public void onClose() {
        if (!searchTerm.isEmpty()) {
            searchTerm = "";
            return;
        }
        closed = true;

    }

    @Override
    protected void init() {
        aProg = 1;
        closed = false;
        int off = 21;
        int offY = 70;
        ButtonWidget sort = new ButtonWidget(width - offY, height - off, 20, 20, Text.of("S"), button -> sort(false));
        ButtonWidget expand = new ButtonWidget(width - offY - 21, height - off, 20, 20, Text.of("E"), button -> {
            for (Draggable container : containers) {
                container.expanded = true;
            }
        });
        ButtonWidget retract = new ButtonWidget(width - offY - 42, height - off, 20, 20, Text.of("R"), button -> {
            for (Draggable container : containers) {
                container.expanded = false;
            }
        });
        ButtonWidget opt = new ButtonWidget(width - offY - 42 - 1 - 100, height - off, 100, 20, Text.of("Options"), button -> showModuleConfig(ModuleRegistry.getByClass(ClientConfig.class)));
        ButtonWidget opt1 = new ButtonWidget(width - offY - 42 - 1 - 100 - 1 - 100, height - off, 100, 20, Text.of("ClickGUI options"), button -> showModuleConfig(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.render.ClickGUI.class)));
        addDrawableChild(sort);
        addDrawableChild(expand);
        addDrawableChild(retract);
        addDrawableChild(opt);
        addDrawableChild(opt1);
        super.init();
    }

    public void showModuleConfig(Module m) {
        if (m == null) {
            currentConfig = null;
            return;
        }
        if (currentConfig == null) {
            ConfigWidget currentConfig1 = new ConfigWidget(m);
            currentConfig1.posX = width - 200;
            currentConfig1.posY = height / 1.5d;
            currentConfig1.lastRenderX = width + 6;
            currentConfig1.lastRenderY = -1;
            currentConfig = currentConfig1;
            return;
        }
        double currentLRX = currentConfig.lastRenderX;
        double currentLRY = currentConfig.lastRenderY;
        double currentPX = currentConfig.posX;
        double currentPY = currentConfig.posY;
        ConfigWidget w = new ConfigWidget(m);
        w.lastRenderX = currentLRX;
        w.lastRenderY = currentLRY;
        w.posX = currentPX;
        w.posY = currentPY;
        currentConfig = w;
    }

    public void renderDescription(String desc) {
        this.desc = desc;
    }

    double easeOutBounce(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (aProg == 1 && closed) {
            Atomic.client.setScreen(null);
            return;
        }
        double aProgI = easeOutBounce(aProg);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Matrix4f matrices4 = matrices.peek().getModel();
        int a = (int) Math.floor(Math.abs(1 - aProgI) * 150);
        float offset = (float) ((System.currentTimeMillis() % 3000) / 3000d);
        float hsv2p = 0.25f + offset;
        float hsv3p = 0.5f + offset;
        float hsv4p = 0.75f + offset;
        Color hsv1 = Color.getHSBColor(offset % 1, 0.6f, 0.8f);
        Color hsv2 = Color.getHSBColor(hsv2p % 1, 0.6f, 0.8f);
        Color hsv3 = Color.getHSBColor(hsv3p % 1, 0.6f, 0.8f);
        Color hsv4 = Color.getHSBColor(hsv4p % 1, 0.6f, 0.8f);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrices4, 0, 0, 0).color(hsv1.getRed(), hsv1.getGreen(), hsv1.getBlue(), a).next();
        bufferBuilder.vertex(matrices4, 0, height, 0).color(hsv2.getRed(), hsv2.getGreen(), hsv2.getBlue(), a).next();
        bufferBuilder.vertex(matrices4, width, height, 0).color(hsv3.getRed(), hsv3.getGreen(), hsv3.getBlue(), a).next();
        bufferBuilder.vertex(matrices4, width, 0, 0).color(hsv4.getRed(), hsv4.getGreen(), hsv4.getBlue(), a).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
        float scaleInv = (float) Transitions.easeOutBack(Math.abs(1 - aProg));
        float api = 1 - scaleInv;
        matrices.translate(-api * width, 0, 0);
        //matrices.scale(scaleInv, scaleInv, 1);
        if (System.currentTimeMillis() - lastRender > 1) lastRender = System.currentTimeMillis();
        double logoSize = me.zeroX150.atomic.feature.module.impl.render.ClickGUI.logoSize.getValue();
        if (logoSize != 0) {
            RenderSystem.setShaderTexture(0, ClickGUI.LOGO);
            RenderSystem.enableBlend();
            RenderSystem.blendEquation(32774);
            RenderSystem.blendFunc(770, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1F, 1F);
            drawTexture(matrices, (int) (width / 2 - (504 * logoSize / 2)), 10, 0, 0, 0, (int) (504 * logoSize), (int) (130 * logoSize), (int) (130 * logoSize), (int) (504 * logoSize));
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
        //matrices.translate(0, trackedScroll, 0);
        if (currentConfig != null) currentConfig.render(matrices, mouseX, mouseY, delta);
        //matrices.translate(0, -trackedScroll, 0);
        matrices.push();
        for (Draggable container : containers) {
            matrices.push();
            container.render(matrices, delta, api, 0);
            matrices.pop();
        }
        matrices.pop();
        Atomic.fontRenderer.drawCenteredString(matrices, desc, width / 2f, height - 70, Color.WHITE.getRGB());
        desc = "";
        Atomic.monoFontRenderer.drawCenteredString(matrices, searchTerm.isEmpty() ? "Click any key to search" : ("Search (esc to clear): " + searchTerm), width / 2f, height - 11, 0xFFFFFF);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (aProg != 0) return true;
        boolean flag = false;
        for (Draggable container : Lists.reverse(containers).toArray(new Draggable[0])) {
            if (container.mouseClicked(button == 0, mouseX, mouseY)) {
                // put element all the way to the back if its clicked, creating the
                // overlaying effect each time you move something
                containers.remove(container);
                containers.add(container);
                flag = true;
                break;
            }
        }
        if (!flag && currentConfig != null) {
            currentConfig.mouseClicked(mouseX, mouseY, button);
        }
        if (!flag & button == 1) {
            clicks++;
            if (clicks >= 2) {
                for (Draggable container : containers) {
                    container.expanded = true;
                }

            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Draggable container : containers) {
            container.mouseReleased();
        }
        if (currentConfig != null) currentConfig.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Draggable container : containers) {
            container.mouseMove(deltaX, deltaY);
        }
        if (currentConfig != null) currentConfig.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (currentConfig != null) currentConfig.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean shouldContinue = aProg == 0;
        if (currentConfig != null) if (currentConfig.charTyped(chr, modifiers)) shouldContinue = false;
        if (shouldContinue) {
            if (SharedConstants.isValidChar(chr)) searchTerm += chr;
        }
        return true;
    }

    @Nullable
    @Override
    public Element getFocused() {
        return null;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean shouldContinue = true;
        if (currentConfig != null) if (currentConfig.keyPressed(keyCode, scanCode, modifiers)) shouldContinue = false;
        if (shouldContinue) {
            if (keyCode == 259) {
                searchTerm = "";
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (currentConfig != null) currentConfig.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        List<Draggable> e = new ArrayList<>(containers);
        Collections.reverse(e);
        for (Draggable container : e) {
            if (container.mouseScrolled(mouseX, mouseY, amount)) break;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}