/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.driver.graph.exception.InvalidValueException;

public class Geography {

    private GeoShape shape;

    protected NPoint      point;
    protected NLineString lineString;
    protected NPolygon    polygon;


    public Geography(GeoShape shape) {
        this.shape = shape;
    }

    public GeoShape getShape() {
        return shape;
    }

    public NPoint asPoint() {
        if (shape != GeoShape.GeoShapePoint) {
            throw new RuntimeException("geo shape is " + shape.name());
        }
        return point;
    }

    public NLineString asLineString() {
        if (shape != GeoShape.GeoShapeLineString) {
            throw new RuntimeException("geo shape is " + shape.name());
        }
        return lineString;
    }

    public NPolygon asPolygon() {
        if (shape != GeoShape.GeoShapePolygon) {
            throw new RuntimeException("geo shape is " + shape.name());
        }
        return polygon;
    }

    public enum GeoShape {
        GeoShapePoint(1),
        GeoShapeLineString(5),
        GeoShapePolygon(9);
        private final int shape;

        GeoShape(int shape) {
            this.shape = shape;
        }

        public static GeoShape getGeoShape(int shape) {
            for (GeoShape geoShape : values()) {
                if (geoShape.shape == shape) {
                    return geoShape;
                }
            }
            throw new RuntimeException("does not define the GeoShape type:" + shape);
        }
    }

    public String toString() {
        switch (shape) {
            case GeoShapePoint:
                return point.toString();
            case GeoShapeLineString:
                return lineString.toString();
            case GeoShapePolygon:
                return polygon.toString();
            default:
                throw new RuntimeException("do not support geo shape:" + shape);
        }
    }
}
