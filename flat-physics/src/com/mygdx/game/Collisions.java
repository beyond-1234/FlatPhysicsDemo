package com.mygdx.game;

public class Collisions {


    /**
     * Use SAT method to detect polygons intersection
     *
     * SAT: find normal direction of one edge, then project all vertices onto the axis pointing normal direction
     * we do this operation for all edges,
     * if the projections of polygons overlap, we move on to the next edge
     * once we find that on one axis the projections of polygons don't overlap, means they don't intersect
     * if polygons overlap on every axis, then means they intersect
     * @param verticesA transformed vertices of first polygon
     * @param verticesB transformed vertices of second polygon
     * @return is intersected
     */
    public static boolean isIntersectPolygons(FlatVector[] verticesA, FlatVector[] verticesB) {

        for (int i = 0; i < verticesA.length; i++) {
            FlatVector va = verticesA[i];
            FlatVector vb = verticesA[(i + 1) % verticesA.length];

            FlatVector edge = FlatVector.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            FlatVector axis = new FlatVector(-edge.getY(), edge.getX());

            PolygonProjection projectionA = projectPolygon(verticesA, axis);
            PolygonProjection projectionB = projectPolygon(verticesB, axis);

            // detect overlap
            if(projectionA.min >= projectionB.max || projectionB.min >= projectionA.max) {
                return false;
            }
        }

        for (int i = 0; i < verticesB.length; i++) {
            FlatVector va = verticesB[i];
            FlatVector vb = verticesB[(i + 1) % verticesB.length];

            FlatVector edge = FlatVector.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            FlatVector axis = new FlatVector(-edge.getY(), edge.getX());

            PolygonProjection projectionA = projectPolygon(verticesA, axis);
            PolygonProjection projectionB = projectPolygon(verticesB, axis);

            // detect overlap
            if(projectionA.min >= projectionB.max || projectionB.min >= projectionA.max) {
                return false;
            }
        }
        return true;
    }

    private static PolygonProjection projectPolygon(FlatVector[] vertices, FlatVector axis) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        for (FlatVector v :
                vertices) {
            float projection = FlatMath.dot(v, axis);

            if(projection < min) min = projection;
            if(projection > max) max = projection;
        }
        return new PolygonProjection(min ,max);
    }

    private static class PolygonProjection {
        float min;
        float max;

        public PolygonProjection(float min, float max) {
            this.min = min;
            this.max = max;
        }
    }

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
