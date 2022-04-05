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
    public static CollisionResult detectIntersectPolygons(FlatVector[] verticesA, FlatVector[] verticesB) {

        FlatVector normal = FlatVector.getZero();
        float depth = Float.MAX_VALUE;

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
                return new CollisionResult(false);
            }

            float axisDepth = Math.min(projectionB.max - projectionA.min, projectionA.max - projectionB.min);

            if(axisDepth < depth) {
                depth = axisDepth;
                normal = axis;
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
                return new CollisionResult(false);
            }

            float axisDepth = Math.min(projectionB.max - projectionA.min, projectionA.max - projectionB.min);

            if(axisDepth < depth) {
                depth = axisDepth;
                normal = axis;
            }

        }

        depth /= FlatMath.length(normal);
        normal = FlatMath.normalize(normal);

        // make sure the normal is always pointing from the first one to the second
        FlatVector centerA = findArithmeticMean(verticesA);
        FlatVector centerB = findArithmeticMean(verticesB);

        FlatVector direction = FlatVector.subtract(centerB, centerA);

        if(FlatMath.dot(direction, normal) < 0f) {
            normal.negative();
        }

        return new CollisionResult(true, normal, depth);
    }

    private static FlatVector findArithmeticMean(FlatVector[] vertices) {
        float sumX = 0f;
        float sumY = 0f;

        for (int i = 0; i < vertices.length; i++) {
            FlatVector v = vertices[i];
            sumX += v.getX();
            sumY += v.getY();
        }
        return new FlatVector(sumX / vertices.length, sumY / vertices.length);
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

    public static class CollisionResult {
        public boolean isIntersect;
        public FlatVector normal;
        public float depth;

        public CollisionResult(boolean isIntersect) {
            this.isIntersect = isIntersect;
        }

        public CollisionResult(boolean isIntersect, FlatVector normal, float depth) {
            this.isIntersect = isIntersect;
            this.normal = normal;
            this.depth = depth;
        }

    }

    public static float getIntersectCirclesDepth(FlatVector centerA, float radiusA,
                                           FlatVector centerB, float radiusB) {
        float distance = FlatMath.distance(centerA, centerB);
        float radii = radiusA + radiusB;

        return distance >= radii ? 0f : radii - distance;
    }

    // get the direction of the intersection when the first circle is pushed away
    // the direction points from the first circle to the second
    public static FlatVector getIntersectCirclesNormal(FlatVector centerA, FlatVector centerB) {
        return FlatMath.normalize(FlatVector.subtract(centerB, centerA));
    }
}
