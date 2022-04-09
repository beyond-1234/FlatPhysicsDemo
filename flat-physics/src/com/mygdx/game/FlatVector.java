package com.mygdx.game;

import java.util.Objects;

// tow dims vector
// use meter as unit
public class FlatVector {
    private float x;
    private float y;
    private static final FlatVector Zero = new FlatVector(0f, 0f);

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

    public FlatVector transform(FlatTransform t) {
        // rotation first, then translate

        // https://matthew-brett.github.io/teaching/rotation_2d.html
        // x2=cosβx1−sinβy1
        // y2=sinβx1+cosβy1
        float x1 = this.x;
        float y1 = this.y;
        this.x = t.getCos() * x1 - t.getSin() * y1;
        this.y = t.getSin() * x1 + t.getCos() * y1;

        this.x = this.x + t.getX();
        this.y = this.y + t.getY();

        return this;
    }

//    public void setFlatVector(float x, float y) {
//        this.x = x;
//        this.y = y;
//    }

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

    public static FlatVector transform(FlatVector v, FlatTransform t) {
        // rotation first, then translate

        // https://matthew-brett.github.io/teaching/rotation_2d.html
        // x2=cosβx1−sinβy1
        // y2=sinβx1+cosβy1
        float rx = t.getCos() * v.x - t.getSin() * v.y;
        float ry = t.getSin() * v.x + t.getCos() * v.y;

        float tx = rx + t.getX();
        float ty = ry + t.getY();

        return new FlatVector(tx, ty);
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
