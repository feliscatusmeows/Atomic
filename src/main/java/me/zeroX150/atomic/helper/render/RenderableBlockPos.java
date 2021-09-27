package me.zeroX150.atomic.helper.render;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RenderableBlockPos extends RenderablePosition {
    public RenderableBlockPos(Color color, BlockPos position) {
        super(color, new Vec3d(position.getX() + .5, position.getY() + .5, position.getZ() + .5));
    }
}
