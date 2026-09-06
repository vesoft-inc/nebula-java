/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.graph.data.NLineString;
import com.vesoft.nebula.driver.graph.data.NPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper for a geographic line string, matching the v3 client.
 */
public class LineStringWrapper extends BaseDataObject {
    private final NLineString lineString;

    public LineStringWrapper(NLineString lineString) {
        this.lineString = lineString;
    }

    public List<CoordinateWrapper> getCoordinateList() {
        List<CoordinateWrapper> coordList = new ArrayList<>();
        for (NPoint point : lineString.getPoints()) {
            coordList.add(new CoordinateWrapper(point));
        }
        return coordList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStringWrapper that = (LineStringWrapper) o;
        return this.getCoordinateList().equals(that.getCoordinateList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LINESTRING(");
        List<NPoint> points = lineString.getPoints();
        for (int i = 0; i < points.size(); i++) {
            NPoint point = points.get(i);
            sb.append(point.getLng()).append(' ').append(point.getLat());
            if (i < points.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
