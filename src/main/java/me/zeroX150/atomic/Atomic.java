package me.zeroX150.atomic;

import com.woopra.java.sdk.WoopraEvent;
import com.woopra.java.sdk.WoopraTracker;
import com.woopra.java.sdk.WoopraVisitor;
import me.zeroX150.atomic.feature.gui.notifications.NotificationRenderer;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.ConfigManager;
import me.zeroX150.atomic.helper.Rotations;
import me.zeroX150.atomic.helper.Utils;
import me.zeroX150.atomic.helper.font.FontRenderer;
import me.zeroX150.atomic.helper.keybind.KeybindManager;
import me.zeroX150.atomic.mixin.game.IMinecraftClientAccessor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.Session;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Atomic implements ModInitializer {

    public static final String MOD_NAME = "Atomic client";
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Map<UUID, String> capes = new HashMap<>();
    public static FontRenderer fontRenderer;
    public static FontRenderer monoFontRenderer;
    public static Thread FAST_TICKER;
    public static File CONFIG_STORAGE;
    public static WoopraTracker analytics = new WoopraTracker("me.x150.atomic");
    static WoopraVisitor visitor;
    static ExecutorService analyticsRunner = Executors.newFixedThreadPool(2);

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static void sendAnalyticsMessage(WoopraEvent e) {
        e.withTimestamp(System.currentTimeMillis());
        analyticsRunner.execute(() -> analytics.track(visitor, e));
    }

    public static void setSession(Session s) {
        visitor.setProperty("uuid", s.getProfile().getId().toString());
        visitor.setProperty("Name", s.getProfile().getName());
        ((IMinecraftClientAccessor) client).setSession(s);
    }

    @Override
    public void onInitialize() {
        visitor = new WoopraVisitor("uniqueId", client.getSession().getProfile().getName()).withProperty("uuid", client.getSession().getProfile().getId().toString());
        analytics.identify(visitor);
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
