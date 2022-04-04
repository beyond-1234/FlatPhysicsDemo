package com.mygdx.game;

import java.util.Objects;

// tow dims vector
// use meter as unit
public class FlatVector {
    private float x;
    private float y;
    private static FlatVector Zero = new FlatVector(0f, 0f);

    public FlatVector() {
        this.x = 0f;
        this.y = 0f;
    }

    public FlatVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static FlatVector getZero() {
        return Zero;
    }

    public FlatVector add(FlatVector b) {
        this.x += b.x;
        this.y += b.y;
        return this;
    }

    public FlatVector subtract(FlatVector b) {
        this.x -= b.x;
        this.y -= b.y;
        return this;
    }

    public FlatVector negative() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public FlatVector multiply(float scale) {
        this.x *= scale;
        this.y *= scale;
        return this;
    }

    public FlatVector divide(float scale) {
        this.x /= scale;
        this.y /= scale;
        return this;
    }

    public void setFlatVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static FlatVector add(FlatVector a, FlatVector b) {
        return new FlatVector(a.x + b.x, a.y + b.y);
    }

    public static FlatVector subtract(FlatVector a, FlatVector b) {
        return new FlatVector(a.x - b.x, a.y - b.y);
    }

    public static FlatVector negative(FlatVector a) {
        return new FlatVector(-a.x, -a.y);
    }

    public static FlatVector multiply(FlatVector a, float scale) {
        return new FlatVector(a.x * scale, a.y * scale);
    }

    public static FlatVector divide(FlatVector a, float scale) {
        return new FlatVector(a.x / scale, a.y / scale);
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    private boolean equals(FlatVector o) {
        return Float.compare(o.x, x) == 0 && Float.compare(o.y, y) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlatVector that = (FlatVector) o;
        return this.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "FlatVector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    protected FlatVector clone() {
        return new FlatVector(this.x, this.y);
    }
}
