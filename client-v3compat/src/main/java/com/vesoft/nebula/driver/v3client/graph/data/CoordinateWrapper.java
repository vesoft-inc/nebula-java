/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.graph.data.NPoint;
import java.util.Objects;

/**
 * Wrapper for a geographic coordinate (a point), matching the v3 client.
 */
public class CoordinateWrapper extends BaseDataObject {
    private final NPoint point;

    public CoordinateWrapper(NPoint point) {
        this.point = point;
    }

    public double getX() {
        return point.getLng();
    }

    public double getY() {
        return point.getLat();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CoordinateWrapper that = (CoordinateWrapper) o;
        return this.getX() == that.getX() && this.getY() == that.getY();
    }

    @Override
    public String toString() {
        return "COORDINATE(" + point.getLng() + " " + point.getLat() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
