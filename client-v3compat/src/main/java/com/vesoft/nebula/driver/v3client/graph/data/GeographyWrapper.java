/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.graph.data.Geography;
import com.vesoft.nebula.driver.graph.data.Geography.GeoShape;
import java.util.Objects;

/**
 * Wrapper for a geography value, matching the v3 client.
 */
public class GeographyWrapper extends BaseDataObject {
    private final Geography geography;

    public GeographyWrapper(Geography geography) {
        this.geography = geography;
    }

    public PolygonWrapper getPolygonWrapper() {
        return new PolygonWrapper(geography.asPolygon());
    }

    public LineStringWrapper getLineStringWrapper() {
        return new LineStringWrapper(geography.asLineString());
    }

    public PointWrapper getPointWrapper() {
        return new PointWrapper(geography.asPoint());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeographyWrapper that = (GeographyWrapper) o;
        return geography.toString().equals(that.geography.toString());
    }

    @Override
    public String toString() {
        GeoShape shape = geography.getShape();
        switch (shape) {
            case GeoShapePoint:
                return getPointWrapper().toString();
            case GeoShapeLineString:
                return getLineStringWrapper().toString();
            case GeoShapePolygon:
                return getPolygonWrapper().toString();
            default:
                return "";
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(geography);
    }
}
