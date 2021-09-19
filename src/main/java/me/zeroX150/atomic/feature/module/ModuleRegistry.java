package me.zeroX150.atomic.feature.module;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.impl.combat.*;
import me.zeroX150.atomic.feature.module.impl.exploit.*;
import me.zeroX150.atomic.feature.module.impl.external.*;
import me.zeroX150.atomic.feature.module.impl.fun.BHop;
import me.zeroX150.atomic.feature.module.impl.fun.Deadmau5;
import me.zeroX150.atomic.feature.module.impl.fun.NWordCounter;
import me.zeroX150.atomic.feature.module.impl.misc.*;
import me.zeroX150.atomic.feature.module.impl.movement.*;
import me.zeroX150.atomic.feature.module.impl.render.*;
import me.zeroX150.atomic.feature.module.impl.render.oreSim.OreSim;
import me.zeroX150.atomic.feature.module.impl.testing.TestModule;
import me.zeroX150.atomic.feature.module.impl.world.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleRegistry {
    static List<Module> modules = new ArrayList<>();

    static {
        modules.add(new TestModule());
        modules.add(new ClickGUI());
        modules.add(new AirJump());
        modules.add(new ArrowJuke());
        modules.add(new AutoSneak());
        modules.add(new Blink());
        modules.add(new EntityFly());
        modules.add(new Hud());
        modules.add(new Boost());
        modules.add(new Flight());
        modules.add(new Jesus());
        modules.add(new MoonGravity());
        modules.add(new NoFall());
        modules.add(new NoJumpCooldown());
        modules.add(new ClickFly());
        modules.add(new Speed());
        modules.add(new Sprint());
        modules.add(new Step());
        modules.add(new AutoLog());
        modules.add(new OreSim());
        modules.add(new Nuker());
        modules.add(new Criticals());
        modules.add(new Killaura());
        modules.add(new AntiAntiXray());
        modules.add(new XRAY());
        modules.add(new AntiOffhandCrash());
        modules.add(new BoatPhase());
        modules.add(new SoundLogger());
        modules.add(new PingSpoof());
        modules.add(new AntiPacketKick());
        modules.add(new Fullbright());
        modules.add(new NameTags());
        modules.add(new ClientConfig());
        modules.add(new Tracers());
        modules.add(new ESP());
        modules.add(new Alts());
        modules.add(new HologramAura());
        modules.add(new TexPackSpoof());
        modules.add(new Bunker());
        modules.add(new SlotSpammer());
        modules.add(new VerticalPhase());
        modules.add(new Freecam());
        modules.add(new NoPush());
        modules.add(new BuildLimit());
        modules.add(new WaterClutch());
        modules.add(new Zoom());
        modules.add(new AutoEndermanAngry());
        modules.add(new MidAirPlace());
        modules.add(new Dupe());
        modules.add(new InventoryWalk());
        modules.add(new TargetHud());
        modules.add(new FarmingAura());
        modules.add(new BetterCrosshair());
        modules.add(new NoBreakDelay());
        modules.add(new AutoFish());
        modules.add(new ChestESP());
        modules.add(new InventoryCleaner());
        modules.add(new OffhandCrash());
        modules.add(new BlockSpammer());
        modules.add(new NoRender());
        modules.add(new VanillaSpoof());
        modules.add(new Scaffold());
        modules.add(new AntiVoid());
        modules.add(new Phase());
        modules.add(new NameProtect());
        modules.add(new LeverAura());
        modules.add(new ChatSequence());
        modules.add(new GodBridge());
        modules.add(new AntiReducedDebugInfo());
        modules.add(new FastUse());
        modules.add(new AutoCone());
        modules.add(new CleanGUI());
        modules.add(new Timer());
        modules.add(new FreeLook());
        modules.add(new ClickNuke());
        modules.add(new MassFillNuke());
        modules.add(new AutoTool());
        modules.add(new NWordCounter());
        modules.add(new AutoLogin());
        modules.add(new Boaty());
        modules.add(new NoComCrash());
        modules.add(new UsefulInfoLogger());
        modules.add(new InfChatLength());
        modules.add(new DiscordRPC());
        modules.add(new PortalGUI());
        modules.add(new BHop());
        modules.add(new IgnoreWorldBorder());
        modules.add(new AutoWalk());
        modules.add(new WindowCustomization());
        modules.add(new InstantBreak());
        modules.add(new Velocity());
        modules.add(new AutoRepeater());
        modules.add(new EntitySpawnInfo());
        modules.add(new CommandBlockPreview());
        modules.add(new BlockTagViewer());
        modules.add(new Waypoints());
        modules.add(new AimAssist());
        modules.add(new MCF());
        modules.add(new ProtectFriends());
        modules.add(new Tunnel());
        modules.add(new Animations());
        modules.add(new AutoElytra());
        modules.add(new ArmorSwitch());
        modules.add(new Deadmau5());
        modules.add(new Squake());
        modules.add(new OOBCrash());

        modules = modules.stream().sorted(Comparator.comparingDouble(value -> -Atomic.fontRenderer.getStringWidth(value.getName()))).collect(Collectors.toList());
    }

    public static List<Module> getModules() {
        return modules;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T getByClass(Class<T> clazz) {
        for (Module module : getModules()) {
            if (module.getClass() == clazz) return (T) module;
        }
        throw new IllegalStateException("Unregistered module");
    }

    public static Module getByName(String n) {
        for (Module module : getModules()) {
            if (module.getName().equalsIgnoreCase(n)) return module;
        }
        return null;
    }
}
