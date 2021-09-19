package me.zeroX150.atomic;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
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
    public static Thread FAST_TICKER;
    public static File CONFIG_STORAGE;

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
        FAST_TICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickGuiSystem();
                // this is literally just onFastTick but without the world check
                tickModulesNWC(); // order is important
                if (Atomic.client.player == null || Atomic.client.world == null) continue;
                tickModules();
                Rotations.update();
                KeybindManager.update();
            }
        }, "100_tps_ticker");
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