package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.ColorValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import net.minecraft.client.util.math.MatrixStack;

public class ClickGUI extends Module {
    public static SliderValue logoSize;
    public static SliderValue smooth;
    public static SliderValue dragFactor;
    public static BooleanValue enableTails;
    public static SliderValue tailSize;
    public static BooleanValue instant;
    public static BooleanValue doOpenAnimation;

    public static ColorValue cInactive, cActive, cHighlight, cHRet, cHExp, cFont, cTitle;

    public static MultiValue theme;

    public ClickGUI() {
        super("ClickGUI", "Opens the click gui", ModuleType.RENDER);
        logoSize = (SliderValue) this.config.create("Logo size", 0.28, 0, 1, 2).description("The size of the logo at the top (0 to disable)");
        smooth = (SliderValue) this.config.create("Movement smooth", 10, 10, 30, 1).description("The factor to smooth movements of the tabs by");
        dragFactor = (SliderValue) this.config.create("Drag factor", 0.5, 0, 3, 1).description("The factor to rotate the tab by when you move it (0 to disable)");
        enableTails = (BooleanValue) this.config.create("Enable tails", false).description("Whether or not to enable tails");
        tailSize = (SliderValue) this.config.create("Tail size", 30, 10, 200, 0).description("The length of the tails");
        theme = (MultiValue) this.config.create("Theme", "Atomic", "Atomic", "Dark", "Walmart Sigma", "Custom").description("The theme of the clickgui");
        tailSize.showOnlyIf(() -> enableTails.getValue());
        instant = (BooleanValue) this.config.create("Skip animation", false).description("Disables the animation and shows the clickgui instantly");
        doOpenAnimation = (BooleanValue) this.config.create("Open animation", true).description("Disables the little wobble effect when you toggle a category container");

        Themes.Palette p = Themes.Theme.DARK.getPalette();
        cInactive = (ColorValue) this.config.create("Inactive", p.inactive(), false).description("The color for inactive stuff");
        cActive = (ColorValue) this.config.create("Active", p.active(), false).description("The color for active stuff");
        cHighlight = (ColorValue) this.config.create("Indicator", p.l_highlight(), false).description("The color for the active module indicator");
        cHRet = (ColorValue) this.config.create("R. Header", p.h_ret(), false).description("The color for a retracted header");
        cHExp = (ColorValue) this.config.create("E. Header", p.h_exp(), false).description("The color for an expanded header");
        cFont = (ColorValue) this.config.create("Font", p.fontColor(), false).description("The text color");
        cTitle = (ColorValue) this.config.create("Title", p.titleColor(), false).description("The header title background color");
        cInactive.showOnlyIfModeIsSet(theme, "custom");
        cActive.showOnlyIfModeIsSet(theme, "custom");
        cHighlight.showOnlyIfModeIsSet(theme, "custom");
        cHRet.showOnlyIfModeIsSet(theme, "custom");
        cHExp.showOnlyIfModeIsSet(theme, "custom");
        cFont.showOnlyIfModeIsSet(theme, "custom");
        cTitle.showOnlyIfModeIsSet(theme, "custom");

        this.config.get("Keybind").setValue(344);
    }

    @Override
    public void tick() {
        if (!(Atomic.client.currentScreen instanceof me.zeroX150.atomic.feature.gui.clickgui.ClickGUI)) {
            if (me.zeroX150.atomic.feature.gui.clickgui.ClickGUI.INSTANCE == null)
                new me.zeroX150.atomic.feature.gui.clickgui.ClickGUI();
            Atomic.client.setScreen(me.zeroX150.atomic.feature.gui.clickgui.ClickGUI.INSTANCE);
        } else toggle();
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
    public void onHudRender() {

    }
}