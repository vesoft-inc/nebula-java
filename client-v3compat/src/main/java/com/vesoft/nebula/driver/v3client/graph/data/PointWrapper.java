/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.graph.data.NPoint;
import java.util.Objects;

/**
 * Wrapper for a geographic point, matching the v3 client.
 */
public class PointWrapper extends BaseDataObject {
    private final NPoint point;

    public PointWrapper(NPoint point) {
        this.point = point;
    }

    public CoordinateWrapper getCoordinate() {
        return new CoordinateWrapper(point);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointWrapper that = (PointWrapper) o;
        return this.getCoordinate().equals(that.getCoordinate());
    }

    @Override
    public String toString() {
        return "POINT(" + point.getLng() + " " + point.getLat() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(point.getLng(), point.getLat());
    }
}
