package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Quaternion;

public class Animations extends Module {
    final BooleanValue renderOther = (BooleanValue) this.config.create("Render everyone", false).description("Renders the item for everyone, not just you");
    final BooleanValue rotate = (BooleanValue) this.config.create("Rotate", false).description("Rotates the item");
    final BooleanValue rotateX = (BooleanValue) this.config.create("Rotate X", true).description("Whether or not to rotate on the X axis");
    final BooleanValue rotateY = (BooleanValue) this.config.create("Rotate Y", false).description("How much to rotate on the Y axis");
    final BooleanValue rotateZ = (BooleanValue) this.config.create("Rotate Z", false).description("How much to rotate on the Z axis");
    final SliderValue speed = (SliderValue) this.config.create("Rotate speed", 1000, 50, 5000, 0).description("How fast to rotate in MS");

    final BooleanValue scale = (BooleanValue) this.config.create("Scale", true).description("Scales the item");
    final SliderValue scaleX = (SliderValue) this.config.create("Scale X", 0.5, 0, 5, 1).description("How much to scale on the X axis");
    final SliderValue scaleY = (SliderValue) this.config.create("Scale Y", 0.5, 0, 1, 1).description("How much to scale on the Y axis");
    final SliderValue scaleZ = (SliderValue) this.config.create("Scale Z", 0.5, 0, 5, 1).description("How much to scale on the Z axis");

    public Animations() {
        super("Animations", "Does a funny when you use an item", ModuleType.RENDER);
        rotateX.showOnlyIf(rotate::getValue);
        rotateY.showOnlyIf(rotate::getValue);
        rotateZ.showOnlyIf(rotate::getValue);
        scaleX.showOnlyIf(scale::getValue);
        scaleY.showOnlyIf(scale::getValue);
        scaleZ.showOnlyIf(scale::getValue);
    }

    @Override
    public void tick() {

    }

    public void applyRotations(LivingEntity le, MatrixStack stack) {
        if (!renderOther.getValue() && !le.equals(Atomic.client.player)) return;
        if (rotate.getValue()) {
            float e = (float) ((System.currentTimeMillis() % ((int) Math.floor(speed.getValue()))) / speed.getValue() * 360f);
            stack.multiply(new Quaternion((rotateX.getValue() ? 1 : 0) * e, (rotateY.getValue() ? 1 : 0) * e, (rotateZ.getValue() ? 1 : 0) * e, true));
        }
        //stack.multiply(new Quaternion(new Vec3f(0,1,0),,true));
        if (scale.getValue()) {
            stack.scale((float) (0 + scaleX.getValue()), (float) (0 + scaleY.getValue()), (float) (0 + scaleZ.getValue()));
            stack.translate(0, (1 - scaleY.getValue()) * .2, 0);
        }
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

