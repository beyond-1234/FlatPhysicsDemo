package com.mygdx.game;

/**
 * Axis-aligned Bounding Box
 */
public class FlatAABB {

    private FlatVector min;
    private FlatVector max;

    public FlatAABB() {
    }

    public FlatAABB(FlatVector min, FlatVector max) {
        this();
        this.min = min;
        this.max = max;
    }

    public FlatAABB(float minX, float minY, float maxX, float maxY) {
        this();
        this.min = new FlatVector(minX, minY);
        this.max = new FlatVector(maxX, maxY);
    }

    public FlatVector getMin() {
        return min;
    }

    public FlatVector getMax() {
        return max;
    }
}
