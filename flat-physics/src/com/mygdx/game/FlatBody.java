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

    private FlatVector      force;

    private float           density;
    // unit is kg
    private float           mass;
    // 1 / mass
    private float           invMass;
    private float           restitution;
    private float           area;

    // whether is affected by other rigid body
    private boolean         isStatic;

    private float           radius;
    private float           width;
    private float           height;

    // rect and polygon need vertex info to detect collision
    private FlatVector[]    vertices;
    private FlatVector[]    transformedVertices;
    private boolean         doesVerticesRequireUpdate;
    // rect need to be divided int triangles inorder to work properly
    // here stores the order of triangles by storing the vertex in order grouped by three vertices
    private short[]         triangles;

    private int             shapeType;

    private FlatBody() {
        this.linearVelocity = new FlatVector(0f, 0f);
        this.rotation = 0f;
        this.rotationalVelocity = 0f;
        this.force = FlatVector.getZero();
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
        this.invMass = isStatic ? 0f : 1f / mass;
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
        this.invMass = isStatic ? 0f : 1f / mass;
        this.restitution = restitution;
        this.area = area;
        this.isStatic = isStatic;
        this.shapeType = shapeType;
    }


//    public FlatBody(float radius, FlatVector position,  float density,
//                    float mass, float restitution, float area,
//                    boolean isStatic) {
//        this(position, density, mass, restitution, area, isStatic, CIRCLE_SHAPE);
//
//        this.radius = radius;
//    }
//
//    public FlatBody(float width, float height, FlatVector position,
//                    float density, float mass, float restitution, float area,
//                    boolean isStatic) {
//        this(position, density, mass, restitution, area, isStatic, BOX_SHAPE);
//
//        this.width = width;
//        this.height = height;
//        this.vertices = createBoxVertices(width, height);
//        this.transformedVertices = new FlatVector[vertices.length];
//        this.triangles = createTrisVerticesOrder();
//    }

    // create an array of vertices for a box centered by origin
    private static FlatVector[] createBoxVerticesAtOrigin(float width, float height) {
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

    private static short[] createTrisVerticesOrder() {
        short[] tris = new short[6];
        tris[0] = 0;
        tris[1] = 1;
        tris[2] = 2;
        tris[3] = 0;
        tris[4] = 2;
        tris[5] = 3;
        return tris;
    }

    // calculate and cache actual vertices with position and rotation
    // when we actually need to use these vertices
    public FlatVector[] getTransformedVertices() {
        if(this.doesVerticesRequireUpdate) {
            FlatTransform transform = new FlatTransform(this.position, this.rotation);

            for (int i = 0; i < this.vertices.length; i++) {
                FlatVector v = this.vertices[i];
                this.transformedVertices[i] = FlatVector.transform(v, transform);
            }
        }

        this.doesVerticesRequireUpdate = false;

        return this.transformedVertices;
    }

    public void step(float time, FlatVector gravity) {

//        FlatVector acceleration = FlatMath.divide(this.force, this.mass);
//        this.linearVelocity = FlatMath.add(this.linearVelocity, FlatMath.multiply(acceleration, time));

        this.position = FlatMath.add(this.position, FlatMath.multiply(this.linearVelocity, time));
        this.rotation += (this.rotationalVelocity * time);

        this.force = FlatVector.getZero();

        this.doesVerticesRequireUpdate = true;

        if(this.isStatic) return;

        this.linearVelocity = FlatMath.add(this.linearVelocity, FlatMath.multiply(gravity, time));
    }

    public void move(FlatVector amount) {
        this.position = FlatMath.add(this.position, amount);
        this.doesVerticesRequireUpdate = true;
    }

    public void moveTo(FlatVector pos) {
        this.position = pos;
        this.doesVerticesRequireUpdate = true;
    }

    /**
     * rotate by angle
     * @param amount the amount of angle in degree to rotate by.
     */
    public void rotate(float amount) {
        this.rotation -= amount;
        this.rotation %= 360f;
        this.doesVerticesRequireUpdate = true;
    }

    /**
     * rotate to angle
     * @param rotation the amount of angle in degree to rotate to.
     */
    public void rotateTo(float rotation) {
        this.rotation = -rotation;
        this.doesVerticesRequireUpdate = true;
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
        body.vertices = createBoxVerticesAtOrigin(width, height);
        body.transformedVertices = new FlatVector[body.vertices.length];
        body.triangles = createTrisVerticesOrder();
        body.doesVerticesRequireUpdate = true;

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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public FlatVector[] getVertices() {
        return vertices;
    }

    public short[] getTriangles() {
        return triangles;
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setForce(FlatVector force) {
        this.force = force;
    }

    public FlatVector getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(FlatVector linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getMass() {
        return mass;
    }

    public float getInvMass() {
        return invMass;
    }

    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public String toString() {
        return "FlatBody{" +
                "position=" + position +
                ", rotation=" + rotation +
                ", shapeType=" + shapeType +
                '}';
    }
}
