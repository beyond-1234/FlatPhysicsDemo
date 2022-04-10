package com.mygdx.game;

public class FlatTransform {

    private float x;
    private float y;
    private float sin;
    private float cos;

    private static final FlatTransform Zero = new FlatTransform(0f, 0f, 0f);

    public FlatTransform() {
    }

    public FlatTransform(float x, float y, float sin, float cos) {
        this.x = x;
        this.y = y;
        this.sin = sin;
        this.cos = cos;
    }


    public FlatTransform(FlatVector v, float angle) {
        this.x = v.getX();
        this.y = v.getY();
        this.sin = (float) Math.sin(angle);
        this.cos = (float) Math.cos(angle);
        System.out.println(angle);
        System.out.println(this.cos);
    }


    public FlatTransform(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.sin = (float) Math.sin(angle);
        this.cos = (float) Math.cos(angle);
    }

    public static FlatTransform getZero() {
        return Zero;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSin() {
        return sin;
    }

    public float getCos() {
        return cos;
    }
}
