package com.mygdx.game;

public class FlatMath {

    public static float clamp(float value, float min, float max) {
        if(min == max)  return min;
        if(min > max)   throw new IllegalArgumentException("min is greater then max");
        if(value < min) return min;
        if(value > max) return max;

        return value;
    }

    // distance from origin to point
    public static float length(FlatVector v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
    }

    //distance between points
    public static float distance(FlatVector a, FlatVector b) {
        float deltaX = a.getX() - b.getX();
        float deltaY = a.getY() - b.getY();
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public static FlatVector normalize(FlatVector v) {
        float len = length(v);
        return new FlatVector(v.getX() / len, v.getY() / len);
    }

    public static float dot(FlatVector a, FlatVector b) {
        // https://www.mathsisfun.com/algebra/vectors-dot-product.html
        // a · b = ax × bx + ay × by
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    public static float cross(FlatVector a, FlatVector b) {
        // https://www.mathsisfun.com/algebra/vectors-cross-product.html
        // in two dimensions, z axis is 0, so cx and cy are 0
        // cx = aybz − azby
        // cy = azbx − axbz
        // cz = axby − aybx
        return a.getX() * b.getY() - a.getY() * b.getX();
    }
}
