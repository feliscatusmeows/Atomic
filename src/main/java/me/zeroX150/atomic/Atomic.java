/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021. 0x150 and contributors
 */

package me.zeroX150.atomic;

import me.zeroX150.atomic.feature.command.impl.ItemStorage;
import me.zeroX150.atomic.feature.gui.notifications.NotificationRenderer;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.ConfigManager;
import me.zeroX150.atomic.helper.Rotations;
import me.zeroX150.atomic.helper.Utils;
import me.zeroX150.atomic.helper.font.FontRenderer;
import me.zeroX150.atomic.helper.keybind.KeybindManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Atomic implements ModInitializer {

    public static final String MOD_NAME = "Atomic client";
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Map<UUID, String> capes = new HashMap<>();
    public static FontRenderer fontRenderer;
    public static FontRenderer monoFontRenderer;
    public static Thread MODULE_FTTICKER;
    public static Thread FAST_TICKER;
    public static File CONFIG_STORAGE;
    public static long lastScreenChange = System.currentTimeMillis();


    public static ItemGroup ITEMS = FabricItemGroupBuilder.create(new Identifier("atomic", "saveditems")).icon(() -> new ItemStack(Items.COMMAND_BLOCK)).appendItems(itemStacks -> {
        //itemStacks.clear();
        for (ItemStorage.ItemEntry item : ItemStorage.items) {
            ItemStack s = new ItemStack(item.type());
            s.setNbt(item.tag());
            itemStacks.add(s);
        }
    }).build();

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        CONFIG_STORAGE = new File(Atomic.client.runDirectory + "/atomicConfigs");
        fontRenderer = new FontRenderer(Atomic.class.getClassLoader().getResourceAsStream("Font.ttf"));
        monoFontRenderer = new FontRenderer(Atomic.class.getClassLoader().getResourceAsStream("Mono.ttf"));
        KeybindManager.init();
        ConfigManager.loadState();
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::saveState));
        MODULE_FTTICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickModulesNWC(); // always ticks even when we're not in a world
                if (Atomic.client.player == null || Atomic.client.world == null) continue;
                tickModules(); // only ticks when we're in a world
            }
        }, "100_tps_ticker:modules");
        FAST_TICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickGuiSystem(); // ticks gui elements
                if (Atomic.client.player == null || Atomic.client.world == null) continue;
                Rotations.update(); // updates rotations, again only if we are in a world
            }
        }, "100_tps_ticker:gui");
        MODULE_FTTICKER.start();
        FAST_TICKER.start();
    }

    void tickModulesNWC() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) module.onFastTick_NWC();
            } catch (Exception ignored) {
            }
        }
    }

    void tickModules() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) module.onFastTick();
            } catch (Exception ignored) {
            }
        }
    }

    void tickGuiSystem() {
        NotificationRenderer.onFastTick();
        try {
            if (client.currentScreen != null) {
                if (client.currentScreen instanceof FastTickable tickable) {
                    tickable.onFastTick();
                }
                for (Element child : new ArrayList<>(client.currentScreen.children())) { // wow i hate this
                    if (child instanceof FastTickable t) t.onFastTick();
                }
            }
        } catch (Exception ignored) {

        }
    }
}