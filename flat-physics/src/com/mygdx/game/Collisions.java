package com.mygdx.game;

public class Collisions {

    public static float getIntersectCirclesDepth(FlatVector centerA, float radiusA,
                                           FlatVector centerB, float radiusB) {
        float distance = FlatMath.distance(centerA, centerB);
        float radii = radiusA + radiusB;

        return distance >= radii ? 0f : radii - distance;
    }

    // get the direction of the intersection when the first circle is pushed away
    // the direction points from the second circle to the first
    public static FlatVector getIntersectCirclesNormal(FlatVector centerA, FlatVector centerB) {
        return FlatMath.normalize(FlatVector.subtract(centerB, centerA));
    }
}
