package com.mygdx.game;

// flat rigid body class
public class FlatBody {

    public static final int CIRCLE_SHAPE    = 0;
    public static final int BOX_SHAPE       = 1;
    public static final int POLYGON_SHAPE   = 2;

    private FlatVector      position;
    private FlatVector      linearVelocity;
    private float           rotation;
    private float           rotationalVelocity;

    private float           density;
    // unit is kg
    private float           mass;
    private float           restitution;
    private float           area;

    // whether is affected by other rigid body
    private boolean         isStatic;

    private float           radius;
    private float           width;
    private float           height;

    private int             shapeType;

    private FlatBody() {
    }

    private FlatBody(FlatVector position, FlatVector linearVelocity, float rotation, float rotationalVelocity,
                     float density, float mass, float restitution, float area,
                     boolean isStatic, float radius, float width, float height, int shapeType) {
        this();
        this.position = position;
        this.linearVelocity = linearVelocity;
        this.rotation = rotation;
        this.rotationalVelocity = rotationalVelocity;
        this.density = density;
        this.mass = mass;
        this.restitution = restitution;
        this.area = area;
        this.isStatic = isStatic;
        this.radius = radius;
        this.width = width;
        this.height = height;
        this.shapeType = shapeType;
    }

    public FlatBody(FlatVector position, float density, float mass, float restitution, float area,
                    boolean isStatic, int shapeType) {
        this();
        this.position = position;
        this.density = density;
        this.mass = mass;
        this.restitution = restitution;
        this.area = area;
        this.isStatic = isStatic;
        this.shapeType = shapeType;
    }

    public void move(FlatVector amount) {
        this.position.add(amount);
    }

    public void moveTo(FlatVector pos) {
        this.position = pos;
    }

    public static FlatBody createFlatBody(float area, float mass, FlatVector position, float density,
                                          boolean isStatic, float restitution, int shapeType) {
        FlatBody body = null;
        restitution = FlatMath.clamp(restitution, 0f, 1f);

        if(area < FlatWorld.MIN_BODY_SIZE) {
            throw new IllegalArgumentException("area is too small, min area is " + FlatWorld.MIN_BODY_SIZE);
        }
        if(area > FlatWorld.MAX_BODY_SIZE) {
            throw new IllegalArgumentException("area is too large, max area is " + FlatWorld.MAX_BODY_SIZE);
        }
        if(density < FlatWorld.MIN_DENSITY) {
            throw new IllegalArgumentException("density is too small, min density is " + FlatWorld.MIN_DENSITY);
        }
        if(density > FlatWorld.MAX_DENSITY) {
            throw new IllegalArgumentException("density is too large, max density is " + FlatWorld.MAX_DENSITY);
        }

        body = new FlatBody(position, density, mass, restitution, area, isStatic, shapeType);
        return body;
    }

    public static FlatBody createCircleBody(float radius, FlatVector position, float density,
                                            boolean isStatic, float restitution) {
        float area = radius * radius * (float) Math.PI;
        float mass = area * density;
        FlatBody body = createFlatBody(area, mass, position, density, isStatic, restitution, CIRCLE_SHAPE);
        body.radius = radius;
        return body;
    }

    public static FlatBody createBoxBody(float width, float height, FlatVector position, float density,
                                         boolean isStatic, float restitution) {
        float area = width * height;
        float mass = area * density;
        FlatBody body = createFlatBody(area, mass, position, density, isStatic, restitution, BOX_SHAPE);
        body.width = width;
        body.height = height;
        return body;
    }

    public FlatVector getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getDensity() {
        return density;
    }

    public float getArea() {
        return area;
    }

    public float getRadius() {
        return radius;
    }

    public int getShapeType() {
        return shapeType;
    }
}
