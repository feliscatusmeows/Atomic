package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Objects;

public class AutoElytra extends Module {
    final SliderValue fallDist = (SliderValue) this.config.create("Fall distance", 3, 2, 10, 1).description("How far to fall for the elytra to equip");
    ItemStack before = null;

    public AutoElytra() {
        super("AutoElytra", "Automatically equips an elytra from your inventory if you fell long enough", ModuleType.MOVEMENT);
    }

    boolean equippedElytra() {
        return Objects.requireNonNull(Atomic.client.player).getInventory().armor.get(2).getItem() == Items.ELYTRA;
    }

    @Override
    public void tick() {
        if (Objects.requireNonNull(Atomic.client.player).fallDistance > fallDist.getValue()) {
            if (!equippedElytra()) { // do we not have an elytra equipped?
                before = Atomic.client.player.getInventory().getArmorStack(2); // save stack we have as armor rn
                for (int i = 0; i < (9 * 4 + 1); i++) { // gotta equip
                    ItemStack stack = Atomic.client.player.getInventory().getStack(i); // is it an elytra?
                    if (stack.getItem() == Items.ELYTRA) {
                        Utils.Inventory.moveStackToOther(Utils.Inventory.slotIndexToId(i), 6); // equip
                        break; // we found the item, cancel the loop
                    }
                }
            }
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
        float fd = Objects.requireNonNull(Atomic.client.player).fallDistance;
        if (fd > fallDist.getMin()) {
            return Utils.Math.roundToDecimal(fd, 1) + " | " + fallDist.getValue();
        }
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}

