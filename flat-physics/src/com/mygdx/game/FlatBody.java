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

    // rect and polygon need vertex info to detect collision
    private FlatVector[]    vertices;
    // rect need to be divided int triangles inorder to work properly
    // here stores the order of triangles by storing the vertex in order grouped by three vertices
    private int[]    triangles;

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


    public FlatBody(float radius, FlatVector position,  float density,
                    float mass, float restitution, float area,
                    boolean isStatic) {
        this(position, density, mass, restitution, area, isStatic, CIRCLE_SHAPE);

        this.radius = radius;
    }

    public FlatBody(float width, float height, FlatVector position,
                    float density, float mass, float restitution, float area,
                    boolean isStatic) {
        this(position, density, mass, restitution, area, isStatic, BOX_SHAPE);

        this.width = width;
        this.height = height;
        this.vertices = createBoxVertices(width, height);
        this.triangles = createTrisVerticesOrder();
    }

    // create an array of vertices for a box centered by origin
    private static FlatVector[] createBoxVertices(float width, float height) {
        float left   = -width  / 2f;
        float right  = left    + width;
        float bottom = -height / 2f;
        float top    = bottom  + height;

        FlatVector[] vertices = new FlatVector[4];
        vertices[0] = new FlatVector(left, top);
        vertices[1] = new FlatVector(right, top);
        vertices[2] = new FlatVector(right, bottom);
        vertices[3] = new FlatVector(left, bottom);

        return vertices;
    }

    private int[] createTrisVerticesOrder() {
        int[] tris = new int[6];
        tris[0] = 0;
        tris[1] = 1;
        tris[2] = 2;
        tris[3] = 0;
        tris[4] = 2;
        tris[5] = 3;
        return tris;
    }

    public void move(FlatVector amount) {
        this.position.add(amount);
    }

    public void moveTo(FlatVector pos) {
        this.position = pos;
    }

    public void rotate(float amount) {
        this.rotation += amount;
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
