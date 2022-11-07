/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Coordinate;
import java.util.Arrays;

public class CoordinateWrapper extends BaseDataObject {
    private final Coordinate coordinate;

    public CoordinateWrapper(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public double getX() {
        return coordinate.getX();
    }

    public double getY() {
        return coordinate.getY();
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
        StringBuilder sb = new StringBuilder();
        sb.append("COORDINATE");
        sb.append('(');
        sb.append(coordinate.getX());
        sb.append(' ');
        sb.append(coordinate.getY());
        sb.append(')');

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] {this.getX(), this.getY()});
    }
}
