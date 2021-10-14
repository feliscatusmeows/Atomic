/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.ItemExploits;
import me.zeroX150.atomic.feature.gui.widget.ItemExploitConfigRenderer;
import me.zeroX150.atomic.feature.gui.windowed.Window;
import me.zeroX150.atomic.feature.gui.windowed.WindowScreen;
import me.zeroX150.atomic.helper.PrettyPrintTextFormatter;
import me.zeroX150.atomic.helper.RGBColorText;
import me.zeroX150.atomic.helper.Transitions;
import me.zeroX150.atomic.helper.Utils;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemsScreen extends WindowScreen {
    public static ItemsScreen INSTANCE;
    Window config, itemExploits, exploitPreviewRenderer;
    ItemExploits.ItemExploit currentlySelected;

    public ItemsScreen() {
        super("Items");
        INSTANCE = this;
    }

    public static void setSelectedItemExploit(ItemExploits.ItemExploit newS) {
        INSTANCE.currentlySelected = newS;
        if (INSTANCE.config == null) {
            INSTANCE.config = new Window("config", INSTANCE.itemExploits.getWidth(), 0, 150, 200, false);
            INSTANCE.addWindow(INSTANCE.config);
        }
        if (newS.getGenerator().rendersPreview()) {
            if (INSTANCE.exploitPreviewRenderer == null) {
                INSTANCE.exploitPreviewRenderer = new Window("", INSTANCE.itemExploits.getWidth() + INSTANCE.config.getWidth(), 0, 200, 200, false);
                INSTANCE.exploitPreviewRenderer.addChild(new ItemExploitPreviewRenderer());
                INSTANCE.addWindow(INSTANCE.exploitPreviewRenderer);
            }
            INSTANCE.exploitPreviewRenderer.title = newS.getName() + " preview";
        } else if (INSTANCE.exploitPreviewRenderer != null) {
            INSTANCE.exploitPreviewRenderer.discard();
        }
        INSTANCE.config.clearChildren();
        INSTANCE.config.addChild(new ItemExploitConfigRenderer(newS, 0, 0));
        INSTANCE.config.title = newS.getName() + " config";
    }

    @Override protected void init() {
        if (windows.isEmpty()) {
            exploitPreviewRenderer = itemExploits = config = null;
            itemExploits = new Window("Exploits", 0, 0, 150, height - 100, false);
            itemExploits.addChild(new ItemExploitListWidget());
            addWindow(itemExploits);
            NbtEditorWidget nbtEW = new NbtEditorWidget();
            Window nEditor = new Window("NBT Viewer", Atomic.client.getWindow().getScaledWidth(), Atomic.client.getWindow().getScaledHeight(), 300, 200, false) {
                @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
                    boolean v = super.mouseScrolled(mouseX, mouseY, amount);
                    nbtEW.currentScroll = this.trackedScroll;
                    return v;
                }
            };
            nEditor.addChild(nbtEW);
            addWindow(nEditor);
        }
        super.init();
    }
}

class NbtEditorWidget extends ClickableWidget implements FastTickable {
    static NbtCompound overwrite = null;
    ItemStack before = null;
    double currentScroll = 0;
    RGBColorText text = new RGBColorText("");
    double maxInset = 0;
    double xOffset = 0;
    double smoothXOffset = 0;

    public NbtEditorWidget() {
        super(0, 0, 300, 200, Text.of(""));
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(-this.smoothXOffset, 0, 0);
        int yOffset = 0;
        int xOffset = 0;
        for (RGBColorText.RGBEntry s : text.getEntries()) {
            if (s.equals(RGBColorText.NEWLINE)) {
                xOffset = 0;
                yOffset += 9;
            } else {
                String t = s.value();
                while (t.contains("§")) t = t.replace("§", "&");
                Atomic.monoFontRenderer.drawString(matrices, t, xOffset, yOffset, s.color());
                xOffset += Atomic.monoFontRenderer.getStringWidth(t);
            }
        }

        matrices.pop();
    }

    void updateMaxWidth() {
        maxInset = 0;
        int xOffset = 0;
        for (RGBColorText.RGBEntry s : text.getEntries()) {
            if (s.equals(RGBColorText.NEWLINE)) {
                xOffset = 0;
            } else {
                String t = s.value();
                while (t.contains("§")) t = t.replace("§", "&");
                xOffset += Atomic.monoFontRenderer.getStringWidth(t);
                maxInset = Math.max(maxInset, xOffset);
            }
        }
    }

    @Override public void onFastTick() {
        if (overwrite != null) {
            this.maxInset = 0;
            this.xOffset = 0;

            RGBColorText t = new PrettyPrintTextFormatter().apply(overwrite);
            if (t.getEntries().stream().map(RGBColorText.RGBEntry::value).count() > 5000) {
                text = new RGBColorText("Text too big, use full screen nbt editor", 0xFFAAAA);
            } else {
                text = t;
            }
            height = 9;
            for (RGBColorText.RGBEntry entry : t.getEntries()) {
                if (entry.equals(RGBColorText.NEWLINE)) height += 9;
            }
            updateMaxWidth();
            overwrite = null;
        }
        if (before != Atomic.client.player.getMainHandStack()) {
            this.maxInset = 0;
            this.xOffset = 0;

            before = Atomic.client.player.getMainHandStack();
            RGBColorText t = new PrettyPrintTextFormatter().apply(before.getOrCreateNbt());
            if (t.getEntries().stream().map(RGBColorText.RGBEntry::value).count() > 5000) {
                text = new RGBColorText("Text too big, use full screen nbt editor", 0xFFAAAA);
            } else {
                text = t;
            }
            height = 9;
            for (RGBColorText.RGBEntry entry : t.getEntries()) {
                if (entry.equals(RGBColorText.NEWLINE)) height += 9;
            }
            updateMaxWidth();
        }
        smoothXOffset = Transitions.transition(smoothXOffset, xOffset, 7, 0.001);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            xOffset += 5;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            xOffset -= 5;
        }
        double maxLength = maxInset - width;
        //System.out.println(maxLength);
        maxLength = Math.max(0, maxLength);
        xOffset = MathHelper.clamp(xOffset, 0, maxLength);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }
}

class ItemExploitDisplayWidget extends ClickableWidget implements FastTickable {
    ButtonWidget generate;
    List<String> description = new ArrayList<>();
    ItemExploits.ItemExploit src;

    public ItemExploitDisplayWidget(ItemExploits.ItemExploit exploit, int x, int y) {
        super(x, y, 150, 0, Text.of(""));
        src = exploit;
        String desc = exploit.getGenerator().getDescription();
        String[] t = Utils.splitLinesToWidth(desc, 150, Atomic.monoFontRenderer);
        description.addAll(Arrays.asList(t));
        generate = new ButtonWidget(x + 5,
                y
                        + 2 // padding
                        + 8 // title height
                        + 2 // padding
                        + description.size() * 8 // description height
                        + 5, // padding
                140,
                20,
                Text.of("Generate"),
                button -> ItemsScreen.setSelectedItemExploit(exploit));
        height = 2 + 8 + 2 + description.size() * 8 + 2 + 20 + 2;
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Renderer.R2D.fill(matrices, new Color(10, 10, 10, 50), x, y, x + width, y + height);
        Atomic.fontRenderer.drawCenteredString(matrices, src.getName(), 150 / 2f, y + 2, 0xFFFFFF);
        int yOff = y + 2 + 8 + 2;
        for (String s : description) {
            Atomic.monoFontRenderer.drawCenteredString(matrices, s, 150 / 2f, yOff, 0xFFFFFF);
            yOff += 8;
        }
        generate.render(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return generate.mouseClicked(mouseX, mouseY, button);
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override public void onFastTick() {
        if (generate instanceof FastTickable e) {
            e.onFastTick();
        }
    }
}

class ItemExploitPreviewRenderer extends ClickableWidget {
    public ItemExploitPreviewRenderer() {
        super(0, 0, 200, 200, Text.of(""));
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ItemsScreen.INSTANCE.currentlySelected.getGenerator().renderPreview(matrices, 200, 200);
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }
}

class ItemExploitListWidget extends ClickableWidget {
    List<ItemExploitDisplayWidget> widgets = new ArrayList<>();

    public ItemExploitListWidget() {
        super(0, 0, 150, Atomic.client.getWindow().getHeight() - 100, Text.of(""));
        int yoff = 0;
        for (ItemExploits.ItemExploit value : ItemExploits.ItemExploit.values()) {
            ItemExploitDisplayWidget w = new ItemExploitDisplayWidget(value, 0, yoff);
            yoff += w.getHeight() + 5;
            widgets.add(w);
        }
        height = yoff - 5;
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (ItemExploitDisplayWidget widget : widgets) {
            widget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ItemExploitDisplayWidget widget : widgets) {
            widget.mouseClicked(mouseX, mouseY, button);
        }

        return false;
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }
}