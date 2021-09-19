package me.zeroX150.atomic.helper.render;

import net.minecraft.util.math.Vec3d;

public class RenderablePosition {
    protected final Vec3d pos;
    protected final Vec3d dimensions;
    protected Color color;

    public RenderablePosition(Color color, Vec3d position, Vec3d dimensions) {
        this.color = color;
        this.pos = position;
        this.dimensions = dimensions;
    }

    public RenderablePosition(Color color, Vec3d position) {
        this(color, position, new Vec3d(1, 1, 1));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vec3d getDimensions() {
        return dimensions;
    }

    public Vec3d getPos() {
        return pos;
    }
}

