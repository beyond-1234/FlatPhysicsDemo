package com.mygdx.game;

public class Collisions {

    /**
     * detect circle and polygon with sat method
     * <p>
     * use direction from circle center to every point of polygon as axis
     * first we want to see if exists any overlap in all axis, if found once not overlapped then return false
     * then find the closest point to circle center, use that point to calculate normal and depth
     *
     * @param circleCenter
     * @param vertices
     * @return
     */
    public static CollisionResult detectIntersectCirclePolygon(
            FlatVector circleCenter, float radius, FlatVector[] vertices) {

        FlatVector normal = FlatVector.getZero();
        float depth = Float.MAX_VALUE;
        FlatVector axis = null;
        // find overlap in projections
        for (int i = 0; i < vertices.length; i++) {
            FlatVector va = vertices[i];
            FlatVector vb = vertices[(i + 1) % vertices.length];

            FlatVector edge = FlatMath.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            axis = new FlatVector(-edge.getY(), edge.getX());
            axis = FlatMath.normalize(axis);

            PolygonProjection projectionP = projectPolygon(vertices, axis);
            PolygonProjection projectionC = projectCircle(circleCenter, radius, axis);

            // detect overlap, if not return false
            if (projectionP.min >= projectionC.max || projectionC.min >= projectionP.max) {
                return new CollisionResult(false);
            }

            float axisDepth = Math.min(projectionC.max - projectionP.min, projectionP.max - projectionC.min);

            if(axisDepth < depth) {
                depth = axisDepth;
                normal = axis;
            }
        }

        // calculate normal and depth
        int closestPointIndex = Collisions.findClosestPointOnPolygon(circleCenter, vertices);
        if(closestPointIndex == -1) return new CollisionResult(false);
        FlatVector cp = vertices[closestPointIndex];

//        axis = FlatMath.subtract(cp, circleCenter);
//        axis = FlatMath.normalize(axis);

        PolygonProjection projectionP = projectPolygon(vertices, axis);
        PolygonProjection projectionC = projectCircle(circleCenter, radius, axis);

        // detect overlap, if not return false
        if (projectionP.min >= projectionC.max || projectionC.min >= projectionP.max) {
            return new CollisionResult(false);
        }

        float axisDepth = Math.min(projectionC.max - projectionP.min, projectionP.max - projectionC.min);

        if (axisDepth < depth) {
            depth = axisDepth;
            normal = axis;
        }

        depth /= FlatMath.length(normal);
        normal = FlatMath.normalize(normal);

        // make sure the normal is always pointing from the first one to the second
        FlatVector polygonCenter = findArithmeticMean(vertices);
        FlatVector direction = FlatMath.subtract(polygonCenter, circleCenter);
        if (FlatMath.dot(direction, normal) < 0f) {
            normal = FlatMath.negative(normal);
        }

        return new CollisionResult(true, normal, depth);
    }

    public static CollisionResult detectIntersectCirclePolygon(
            FlatVector circleCenter, float radius, FlatVector polygonCenter, FlatVector[] vertices) {

        FlatVector normal = FlatVector.getZero();
        float depth = Float.MAX_VALUE;
        FlatVector axis = null;
        // find overlap in projections
        for (int i = 0; i < vertices.length; i++) {
            FlatVector va = vertices[i];
            FlatVector vb = vertices[(i + 1) % vertices.length];

            FlatVector edge = FlatMath.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            axis = new FlatVector(-edge.getY(), edge.getX());
            axis = FlatMath.normalize(axis);

            PolygonProjection projectionP = projectPolygon(vertices, axis);
            PolygonProjection projectionC = projectCircle(circleCenter, radius, axis);

            // detect overlap, if not return false
            if (projectionP.min >= projectionC.max || projectionC.min >= projectionP.max) {
                return new CollisionResult(false);
            }

            float axisDepth = Math.min(projectionC.max - projectionP.min, projectionP.max - projectionC.min);

            if(axisDepth < depth) {
                depth = axisDepth;
                normal = axis;
            }
        }

        // calculate normal and depth
        int closestPointIndex = Collisions.findClosestPointOnPolygon(circleCenter, vertices);
        if(closestPointIndex == -1) return new CollisionResult(false);
        FlatVector cp = vertices[closestPointIndex];

//        axis = FlatMath.subtract(cp, circleCenter);
//        axis = FlatMath.normalize(axis);

        PolygonProjection projectionP = projectPolygon(vertices, axis);
        PolygonProjection projectionC = projectCircle(circleCenter, radius, axis);

        // detect overlap, if not return false
        if (projectionP.min >= projectionC.max || projectionC.min >= projectionP.max) {
            return new CollisionResult(false);
        }

        float axisDepth = Math.min(projectionC.max - projectionP.min, projectionP.max - projectionC.min);

        if (axisDepth < depth) {
            depth = axisDepth;
            normal = axis;
        }

        depth /= FlatMath.length(normal);
        normal = FlatMath.normalize(normal);

        FlatVector direction = FlatMath.subtract(polygonCenter, circleCenter);
        if (FlatMath.dot(direction, normal) < 0f) {
            normal = FlatMath.negative(normal);
        }

        return new CollisionResult(true, normal, depth);
    }


    private static int findClosestPointOnPolygon(FlatVector targetPoint, FlatVector[] vertices) {
        int index = -1;
        float minDistance = Float.MAX_VALUE;

        for (int i = 0; i < vertices.length; i++) {
            FlatVector v = vertices[i];
            float distance = FlatMath.distance(v, targetPoint);

            if (distance < minDistance) {
                minDistance = distance;
                index = i;
            }
        }

        return index;
    }


    /**
     * Use SAT method to detect polygons intersection
     * <p>
     * SAT: find normal direction of one edge, then project all vertices onto the axis pointing normal direction
     * we do this operation for all edges,
     * if the projections of polygons overlap, we move on to the next edge
     * once we find that on one axis the projections of polygons don't overlap, means they don't intersect
     * if polygons overlap on every axis, then means they intersect
     *
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

            FlatVector edge = FlatMath.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            FlatVector axis = new FlatVector(-edge.getY(), edge.getX());
            axis = FlatMath.normalize(axis);

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

            FlatVector edge = FlatMath.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            FlatVector axis = new FlatVector(-edge.getY(), edge.getX());
            axis = FlatMath.normalize(axis);

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

//        depth /= FlatMath.length(normal);
//        normal = FlatMath.normalize(normal);

        // make sure the normal is always pointing from the first one to the second
        FlatVector centerA = findArithmeticMean(verticesA);
        FlatVector centerB = findArithmeticMean(verticesB);

        FlatVector direction = FlatMath.subtract(centerB, centerA);

        if(FlatMath.dot(direction, normal) < 0f) {
            normal = FlatMath.negative(normal);
        }

        return new CollisionResult(true, normal, depth);
    }

    public static CollisionResult detectIntersectPolygons(FlatVector centerA, FlatVector[] verticesA, FlatVector centerB, FlatVector[] verticesB) {

        FlatVector normal = FlatVector.getZero();
        float depth = Float.MAX_VALUE;

        for (int i = 0; i < verticesA.length; i++) {
            FlatVector va = verticesA[i];
            FlatVector vb = verticesA[(i + 1) % verticesA.length];

            FlatVector edge = FlatMath.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            FlatVector axis = new FlatVector(-edge.getY(), edge.getX());
            axis = FlatMath.normalize(axis);

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

            FlatVector edge = FlatMath.subtract(vb, va);
            // -y means normal direction is pointing outside of polygon
            FlatVector axis = new FlatVector(-edge.getY(), edge.getX());
            axis = FlatMath.normalize(axis);

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

//        depth /= FlatMath.length(normal);
//        normal = FlatMath.normalize(normal);

        FlatVector direction = FlatMath.subtract(centerB, centerA);

        if(FlatMath.dot(direction, normal) < 0f) {
            normal = FlatMath.negative(normal);
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

    /**
     * get min projection of circle onto axis
     * get max projection of circle onto axis
     *
     * @param center
     * @param radius
     * @param axis
     */
    private static PolygonProjection projectCircle(FlatVector center, float radius, FlatVector axis) {
        FlatVector direction = FlatMath.normalize(axis);
        FlatVector directionAndRadius = FlatMath.multiply(direction, radius);

        float min = FlatMath.dot(FlatMath.add(center, directionAndRadius), axis);
        float max = FlatMath.dot(FlatMath.subtract(center, directionAndRadius), axis);

        if (min > max) {
            float t = min;
            min = max;
            max = t;
        }

        return new PolygonProjection(min, max);
    }

    /**
     * the min projection of all points onto the axis
     * the max projection of all points onto the axis
     *
     * @param vertices
     * @param axis
     * @return
     */
    private static PolygonProjection projectPolygon(FlatVector[] vertices, FlatVector axis) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        for (FlatVector v : vertices) {
            float projection = FlatMath.dot(v, axis);

            if (projection < min) min = projection;
            if (projection > max) max = projection;
        }
        return new PolygonProjection(min, max);
    }

    public static CollisionResult detectIntersectCircles(FlatVector centerA, float radiusA,
                                                         FlatVector centerB, float radiusB) {
        float distance = FlatMath.distance(centerA, centerB);
        float radii = radiusA + radiusB;

        if (distance >= radii)
            return new CollisionResult(false);

        return new CollisionResult(true,
                FlatMath.normalize(FlatMath.subtract(centerB, centerA)),
                radii - distance);
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
//    public static float getIntersectCirclesDepth(FlatVector centerA, float radiusA,
//                                           FlatVector centerB, float radiusB) {
//        float distance = FlatMath.distance(centerA, centerB);
//        float radii = radiusA + radiusB;
//
//        return distance >= radii ? 0f : radii - distance;
//    }
//
//    // get the direction of the intersection when the first circle is pushed away
//    // the direction points from the first circle to the second
//    public static FlatVector getIntersectCirclesNormal(FlatVector centerA, FlatVector centerB) {
//        return FlatMath.normalize(FlatVector.subtract(centerB, centerA));
//    }
}
