package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import net.minecraft.client.util.math.MatrixStack;

public class Squake extends Module {
    public BooleanValue uncappedBunnyhop = (BooleanValue) config.create("Uncapped B-Hop", true).description("vroom");

    public SliderValue groundAccelerate = (SliderValue) config.create("Ground Accel", 10.0D, 1.0D, 20.0D, 2).description("very cool");
    public SliderValue airAccelerate = (SliderValue) config.create("Air Accel", 14.0D, 1.0D, 25.0D, 2).description("sv_airaccel");
    public SliderValue airAccelPerTick = config.create("Max Air Accel", 0.045D, 0.01D, 0.1D, 2);

    public BooleanValue trimpingEnabled = (BooleanValue) config.create("Trimping Enabled", true).description("crouch while zoomin to go brrr");
    public SliderValue trimpMultiplier = config.create("Trimp Multiplier", 1.4D, 1.0D, 3.0D, 2);

    public BooleanValue sharkingEnabled = (BooleanValue) config.create("Sharking Enabled", false).description("funy water glide (BROKEN)");
    public SliderValue sharkingSurfaceTension = config.create("Surface Tension", 0.2D, 0.1D, 0.5D, 2);
    public SliderValue sharkingWaterFriction = config.create("Water Friction", 0.1D, 0.1D, 0.5D, 2);

    public SliderValue hardCap = config.create("Hard Cap", 2.0D, 1.0D, 10.0D, 2);
    public SliderValue softCap = config.create("Soft Cap", 1.4D, 1.0D, 10.0D, 2);

    public Squake() {
        super("Squake", "pasted straight from squeek502", ModuleType.MOVEMENT);
        trimpMultiplier.showOnlyIf(() -> trimpingEnabled.getValue());
        sharkingSurfaceTension.showOnlyIf(() -> sharkingEnabled.getValue());
        sharkingWaterFriction.showOnlyIf(() -> sharkingEnabled.getValue());
    }

    @Override
    public void tick() {

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