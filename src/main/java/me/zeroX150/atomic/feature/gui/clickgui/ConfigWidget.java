/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.widget.SimpleCustomTextFieldWidget;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.ColorValue;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.Transitions;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigWidget {
    public final Module parent;
    final double margin = 4;
    final double width = 200;
    final Map<DynamicValue<?>, List<ClickableWidget>> children = new LinkedHashMap<>();
    double posX;
    double posY;
    double lastRenderX;
    double lastRenderY;

    public ConfigWidget(Module p) {
        this.posX = 1;
        this.posY = 1;
        this.lastRenderX = 1;
        this.lastRenderY = 1;
        this.parent = p;
        int yOffset = (int) Math.ceil(9 + (margin * 2));
        List<DynamicValue<?>> v = parent.config.getAll();
        if (parent.config.organizeClickGUIList) {
            v.sort(Comparator.comparingDouble(value -> value.getKey().equalsIgnoreCase("keybind") ? -2 : (value.getKey().equalsIgnoreCase("toasts") ? -1 : Atomic.fontRenderer.getStringWidth(value.getKey())))); // sort by name
            v.sort(Comparator.comparingInt(value -> value.getKey().equalsIgnoreCase("keybind") ? -2 : (value.getKey().equalsIgnoreCase("toasts") ? -1 : Math.abs(value.getClass().getName().hashCode())))); // then by type
            v.sort(Comparator.comparingInt(value -> value.selectors.size())); // and lastly by how many selectors we have
        }

        for (DynamicValue<?> dynamicValue : v) {
            List<ClickableWidget> cw = new ArrayList<>();
            if (dynamicValue.getKey().equalsIgnoreCase("Keybind")) {
                KeyListenerBtn t = new KeyListenerBtn(1, yOffset, 100, parent) {
                    @Override
                    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (isHovered()) {
                            ClickGUI.INSTANCE.renderDescription(dynamicValue.getDescription());
                        }
                        super.render(matrices, mouseX, mouseY, delta);
                    }
                };
                cw.add(t);
            } else if (dynamicValue instanceof BooleanValue) {
                Toggleable t = new Toggleable(1, yOffset, 100, (BooleanValue) dynamicValue) {
                    @Override
                    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (isHovered()) {
                            ClickGUI.INSTANCE.renderDescription(dynamicValue.getDescription());
                        }
                        super.render(matrices, mouseX, mouseY, delta);
                    }
                };
                cw.add(t);
            } else if (dynamicValue instanceof SliderValue) {
                Slider t = new Slider(1, yOffset, 99, (SliderValue) dynamicValue) {
                    @Override
                    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (isHovered()) {
                            ClickGUI.INSTANCE.renderDescription(dynamicValue.getDescription());
                        }
                        super.render(matrices, mouseX, mouseY, delta);
                    }
                };
                cw.add(t);
            } else if (dynamicValue instanceof MultiValue mval) {
                ButtonMultiSelectable t = new ButtonMultiSelectable(1, yOffset, 100, mval) {
                    @Override
                    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (isHovered()) {
                            ClickGUI.INSTANCE.renderDescription(dynamicValue.getDescription());
                        }
                        super.render(matrices, mouseX, mouseY, delta);
                    }
                };
                cw.add(t);
            } else if (dynamicValue instanceof ColorValue orig) {
                ColorConfig conf = new ColorConfig(1, yOffset, 100, orig) {
                    @Override
                    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (isHovered()) {
                            ClickGUI.INSTANCE.renderDescription(orig.getDescription());
                        }
                        super.render(matrices, mouseX, mouseY, delta);
                    }
                };
                cw.add(conf);

            } else {
                SimpleCustomTextFieldWidget w = new SimpleCustomTextFieldWidget(1, yOffset, 100, 12, Text.of(dynamicValue.getKey())) {
                    @Override
                    public void event_onTextChange() {
                        dynamicValue.setValue(this.getText());
                    }

                    @Override
                    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                        if (isHovered()) {
                            ClickGUI.INSTANCE.renderDescription(dynamicValue.getDescription());
                        }
                        super.render(matrices, mouseX, mouseY, delta);
                    }
                };
                w.setText(dynamicValue.getValue().toString());
                cw.add(w);
            }
            children.put(dynamicValue, cw);
            yOffset += 12;
        }
    }

    public void tick() {
        lastRenderX = Transitions.transition(lastRenderX, posX, me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        if (lastRenderY != -1)
            lastRenderY = Transitions.transition(lastRenderY, posY, me.zeroX150.atomic.feature.module.impl.render.ClickGUI.smooth.getValue());
        if (lastRenderX > Atomic.client.getWindow().getScaledWidth() + 11) ClickGUI.INSTANCE.showModuleConfig(null);
    }

    public void render(MatrixStack ms, int mx, int my, float delta) {
        Renderer.R2D.fill(ms, ClickGUI.currentActiveTheme.h_exp(), lastRenderX - 10, lastRenderY - margin, lastRenderX + width + 10, lastRenderY + 9 + margin);
        int maxOffset = (int) Math.ceil(9 + (margin * 2));
        for (DynamicValue<?> dynamicValue : children.keySet()) {
            if (!dynamicValue.shouldShow()) continue;
            List<ClickableWidget> w = children.get(dynamicValue);
            maxOffset += 12 * w.size();
        }
        this.posY = Atomic.client.getWindow().getScaledHeight() - maxOffset - 30;
        if (this.lastRenderY == -1) this.lastRenderY = posY;
        Renderer.R2D.fill(ms, new Color(238, 37, 37, 255), lastRenderX - 9, lastRenderY - margin + 1, lastRenderX - 9 + 3, lastRenderY + 9 + margin - 1);
        Renderer.R2D.fill(ms, ClickGUI.currentActiveTheme.h_ret(), lastRenderX - 10, lastRenderY + 9 + margin, lastRenderX + width + 10, lastRenderY + maxOffset + 3);
        Atomic.fontRenderer.drawCenteredString(ms, parent.getName() + " config", lastRenderX + (width / 2f), lastRenderY, ClickGUI.currentActiveTheme.fontColor().getRGB());
        int yOffset = (int) Math.ceil(9 + (margin * 2)) - 1;
        List<DynamicValue<?>> dvL = new ArrayList<>(children.keySet());
        for (DynamicValue<?> child1 : dvL) {
            if (!child1.shouldShow()) continue;
            List<ClickableWidget> children = this.children.get(child1);
            int c = child1.isInvalid() ? 0xFF3C3C : ClickGUI.currentActiveTheme.fontColor().getRGB();
            Atomic.fontRenderer.drawCenteredString(ms, child1.getKey(), lastRenderX + (width / 4f), lastRenderY + yOffset + 2, c);
            for (ClickableWidget child : children) {
                child.x = (int) (lastRenderX + width - child.getWidth() - 3);
                child.y = (int) lastRenderY + yOffset + 1;
                child.render(ms, mx, my, delta);
                yOffset += 12;
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (lastRenderX - 9 + 3 > mouseX && lastRenderX - 9 < mouseX && lastRenderY + 9 + margin - 1 > mouseY && lastRenderY - margin + 1 < mouseY) {
            posX = Atomic.client.getWindow().getScaledWidth() + 20;
            return;
        }
        for (List<ClickableWidget> children : this.children.values()) {
            for (ClickableWidget child : children) {
                child.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseReleased(double mx, double my, int b) {
        for (List<ClickableWidget> children : this.children.values()) {
            for (ClickableWidget child : children) {
                child.mouseReleased(mx, my, b);
            }
        }
    }

    public boolean charTyped(char c, int mod) {
        boolean v = false;
        for (List<ClickableWidget> children : this.children.values()) {
            for (ClickableWidget child : children) {
                if (child instanceof KeyListenerBtn b && (System.currentTimeMillis() - b.stoppedScanning) < 50)
                    v = true;
                if (child instanceof SimpleCustomTextFieldWidget) {
                    if (child.charTyped(c, mod)) v = true;
                }
            }
        }
        return v;
    }

    public void mouseMoved(double x, double y) {
        for (List<ClickableWidget> children : this.children.values()) {
            for (ClickableWidget child : children) {
                child.mouseMoved(x, y);
            }
        }
    }

    public boolean keyPressed(int kc, int sc, int m) {
        boolean v = false;
        for (List<ClickableWidget> children : this.children.values()) {
            for (ClickableWidget child : children) {
                if (child instanceof SimpleCustomTextFieldWidget || child instanceof KeyListenerBtn)
                    if (child.keyPressed(kc, sc, m)) v = true;
            }
        }
        return v;
    }

    public void keyReleased(int kc, int sc, int m) {
        for (List<ClickableWidget> children : this.children.values()) {
            for (ClickableWidget child : children) {
                if (child instanceof SimpleCustomTextFieldWidget) child.keyReleased(kc, sc, m);
            }
        }
    }

    public void mouseDragged(double mx, double my, int button, double dx, double dy) {
        for (List<ClickableWidget> children : this.children.values()) {
            for (ClickableWidget child : children) {
                child.mouseDragged(mx, my, button, dx, dy);
            }
        }
    }
}
