/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.gui.screen.ClickGUIScreen;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.ColorValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import net.minecraft.client.util.math.MatrixStack;

public class ClickGUI extends Module {
    public static BooleanValue instant;

    public static ColorValue cInactive, cActive, cHighlight, cHRet, cHExp, cFont, cTitle;

    public static MultiValue theme;

    public ClickGUI() {
        super("Click GUI", "Opens the click gui", ModuleType.RENDER);
        theme = (MultiValue) this.config.create("Theme", "Atomic", "Atomic", "Dark", "Walmart Sigma", "Custom").description("The theme of the clickgui");
        instant = (BooleanValue) this.config.create("Skip animation", false).description("Disables the animation and shows the clickgui instantly");

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

        this.config.createPropGroup("Theme config", cInactive, cActive, cHighlight, cHRet, cHExp, cFont, cTitle);

        this.config.get("Keybind").setValue(344);
    }

    @Override
    public void tick() {
        if (!(Atomic.client.currentScreen instanceof ClickGUIScreen)) {
            Atomic.client.setScreen(ClickGUIScreen.getInstance());
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
