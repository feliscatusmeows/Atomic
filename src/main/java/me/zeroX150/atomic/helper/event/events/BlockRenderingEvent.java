package me.zeroX150.atomic.helper.event.events;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class BlockRenderingEvent extends RenderEvent {
    final BlockPos bp;
    final BlockState state;

    public BlockRenderingEvent(MatrixStack stack, BlockPos pos, BlockState state) {
        super(stack);
        this.bp = pos;
        this.state = state;
    }

    public BlockPos getPosition() {
        return bp;
    }

    public BlockState getBlockState() {
        return state;
    }
}
