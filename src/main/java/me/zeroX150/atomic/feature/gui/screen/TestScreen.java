package me.zeroX150.atomic.feature.gui.screen;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.ItemExploits;
import me.zeroX150.atomic.helper.util.NodeNbtFormatter;
import me.zeroX150.atomic.helper.util.PrettyPrintTextFormatter;
import me.zeroX150.atomic.helper.util.RGBColorText;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TestScreen extends ImGuiProxyScreen {
    private TestScreen() {
        super();
    }

        private static TestScreen INSTANCE;
    public static TestScreen instance() {
        if (INSTANCE == null) INSTANCE = new TestScreen();
        return INSTANCE;
    }

    void renderItemExploits() {
        ImGui.setNextWindowSizeConstraints(500, 300, 700, 500);

        ImGui.begin("Item exploits");
        for (ItemExploits.ItemExploit value : ItemExploits.ItemExploit.values()) {
            if (ImGui.collapsingHeader(value.getName())) {
                String t = value.getGenerator().getDescription();
                ImGui.text(t);
                for (ItemExploits.Option option : value.getGenerator().getOptions()) {
                    ImGui.inputText(option.getName(), option.getValueProvider().getValue());
                    if (option.getValueProvider().isInvalid()) {
                        ImGui.sameLine();
                        ImGui.textColored(255, 40, 40, 255, "Check input!");
                    }
                }
                ImGui.button("Generate");
                if (ImGui.isItemClicked()) {
                    if (!Atomic.client.interactionManager.hasCreativeInventory()) {
                        Utils.Client.sendMessage("Need creative mode");
                        return;
                    }
                    ItemStack r = value.getGenerator().exploit();
                    if (r != null) {
                        Atomic.client.player.getInventory().addPickBlock(r);
                    }
                }
            }
        }
        ImGui.end();
    }
    DateFormat df = new SimpleDateFormat("H:m:s, d/M/y");
    void showNbt(NbtCompound c) {
        NodeNbtFormatter f = new NodeNbtFormatter();
        root = (NodeNbtFormatter.ObjectNode) f.format(c);
        root.children.forEach((s, node) -> System.out.println(s+": "+node.toString()));
    }
    NodeNbtFormatter.ObjectNode root = null;
    void renderItemNbtTree(int indent, String name, NodeNbtFormatter.Node r) {
        if (r instanceof NodeNbtFormatter.ObjectNode e) {
            if (ImGui.treeNode(name+" {}")) {
                e.children.forEach((s, node) -> renderItemNbtTree(indent+1,s,node));
                ImGui.treePop();
            }
        } else if (r instanceof NodeNbtFormatter.ListNode ln) {
            if (ImGui.treeNode(name+" []")) {
                for (int i = 0; i < ln.children.size(); i++) {
                    renderItemNbtTree(indent + 1, "Item #" + (i + 1), ln.children.get(i));
                }
                ImGui.treePop();
            }
        } else {
            ImGui.text(name+": "+r.parent.toString());
        }
    }
    void renderItemNbt() {
        ImGui.setNextWindowSizeConstraints(600, 400, 800, 800);
        ImGui.begin("NBT Viewer", ImGuiWindowFlags.HorizontalScrollbar);
        if (!Atomic.client.player.getInventory().getMainHandStack().isEmpty() && ImGui.button("View Current Item")) {
            showNbt(Atomic.client.player.getInventory().getMainHandStack().getOrCreateNbt().copy());
            ImGui.dummy(0,10);
        }
        if (root != null) {
            renderItemNbtTree(0, "root", root);
        }
        ImGui.end();
    }

    @Override protected void renderInternal() {
        renderItemExploits();
        renderItemNbt();
    }

    @Override public boolean isPauseScreen() {
        return false;
    }
}
