package com.mygdx.game;

import com.mygdx.game.callback.CollisionCallback;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;

public class FlatWorld {
    // all values are set based on some real values
    public static final float MIN_BODY_SIZE = 0.01f * 0.01f;
    public static final float MAX_BODY_SIZE = 2000f * 2000f;
    public static final float MIN_DENSITY = 0.5f; // g/cm^3
    public static final float MAX_DENSITY = 21.4f;

    private ArrayList<FlatBody> bodyList;
    private FlatVector gravity;

    public FlatWorld() {
        this.bodyList = new ArrayList<>();
        this.gravity = new FlatVector(0f, -0.98f);
    }

    public FlatWorld(ArrayList<FlatBody> bodyList, FlatVector gravity) {
        this();
        this.bodyList = bodyList;
        this.gravity = gravity;
    }

    public int getBodyCount() {
        if(this.bodyList == null) return 0;
        return this.bodyList.size();
    }

    public boolean addBody(FlatBody body) {
        return this.bodyList.add(body);
    }

    public boolean removeBody(FlatBody body) {
        return this.bodyList.remove(body);
    }

    public FlatBody removeBody(int i) {
        if (i >= 0 && i < this.bodyList.size())
            return this.bodyList.remove(i);
        return null;
    }

    public FlatBody getBody(int i) {
        if (i >= 0 && i < this.bodyList.size())
            return this.bodyList.get(i);
        return null;
    }

    public void step(CollisionCallback callback) {

        // move step
        for (FlatBody body : this.bodyList) {
            body.step(0.5f, this.gravity);
        }

        // collision step
        for (int i = 0; i < this.bodyList.size() - 1; i++) {

            FlatBody bodyA = this.bodyList.get(i);

            for (int j = i + 1; j < this.bodyList.size(); j++) {
                FlatBody bodyB = this.bodyList.get(j);
                Collisions.CollisionResult collisionResult;

                if(bodyA.isStatic() && bodyB.isStatic()) continue;

                collisionResult = getCollisionResult(bodyA, bodyB);

                if (collisionResult.isIntersect) {
                    FlatVector force = FlatMath.multiply(collisionResult.normal, collisionResult.depth);
                    if(bodyA.isStatic())
                        bodyB.move(FlatMath.divide(force, 2f));
                    else if(bodyB.isStatic())
                        bodyA.move(FlatMath.divide(force,  -2f));
                    else {
                        bodyA.move(FlatMath.divide(force, -2f));
                        bodyB.move(FlatMath.divide(force, 2f));
                    }
                    resolveCollision(bodyA, bodyB, collisionResult.normal);

                    callback.collide(i, j);
                }
            }
        }
    }

    /**
     * add impulses to collided objects
     * make them react realistically to each other
     * @param bodyA
     * @param bodyB
     * @param normal
     */
    public void resolveCollision(FlatBody bodyA, FlatBody bodyB, FlatVector normal) {
        FlatVector relativeVelocity = FlatMath.subtract(bodyB.getLinearVelocity(), bodyA.getLinearVelocity());

        // if forces of two objects towards opposite direction, then don't need to calculate collision
        if(FlatMath.dot(relativeVelocity, normal) > 0f) return;

        float e = Math.min(bodyA.getRestitution(), bodyB.getRestitution());
        float j = -(1f + e) * FlatMath.dot(relativeVelocity, normal);
        // normal dot normal is ignored because normal is already normalized and result is 1
        j /= bodyA.getInvMass() + bodyB.getInvMass();

        FlatVector impulse = FlatMath.multiply(normal, j);
        bodyA.setLinearVelocity(FlatMath.subtract(bodyA.getLinearVelocity(), FlatMath.multiply(impulse, bodyA.getInvMass())));
        bodyB.setLinearVelocity(FlatMath.add(bodyB.getLinearVelocity(), FlatMath.multiply(impulse, bodyB.getInvMass())));
    }


    private Collisions.CollisionResult getCollisionResult(FlatBody bodyA, FlatBody bodyB) {
        Collisions.CollisionResult collisionResult;
        if (bodyA.getShapeType() == bodyB.getShapeType()) {
            if (FlatBody.BOX_SHAPE == bodyA.getShapeType()) {
                collisionResult = doPolygonsCollide(bodyA, bodyB);
            } else {
                collisionResult = doCirclesCollide(bodyA, bodyB);
            }
        } else {
            if (FlatBody.BOX_SHAPE == bodyA.getShapeType()) {
                collisionResult = doCirclePolygonCollide(bodyB, bodyA);

                // make sure normal is pointing from the first to the second
                if (collisionResult.isIntersect) {
                    collisionResult.normal = FlatMath.negative(collisionResult.normal);
                }
            } else {
                collisionResult = doCirclePolygonCollide(bodyA, bodyB);
            }
        }
        return collisionResult;
    }


    private Collisions.CollisionResult doCirclePolygonCollide(FlatBody circle, FlatBody polygon) {
        return Collisions.detectIntersectCirclePolygon(circle.getPosition(), circle.getRadius(),
                                                        polygon.getPosition(), polygon.getTransformedVertices());
    }

    private Collisions.CollisionResult doCirclesCollide(FlatBody bodyA, FlatBody bodyB) {
        return Collisions.detectIntersectCircles(
                bodyA.getPosition(), bodyA.getRadius(),
                bodyB.getPosition(), bodyB.getRadius());
    }

    private Collisions.CollisionResult doPolygonsCollide(FlatBody bodyA, FlatBody bodyB) {
        FlatVector[] verticesA = bodyA.getTransformedVertices();
        FlatVector[] verticesB = bodyB.getTransformedVertices();
        return Collisions.detectIntersectPolygons(bodyA.getPosition(), verticesA, bodyB.getPosition(), verticesB);
    }

}
