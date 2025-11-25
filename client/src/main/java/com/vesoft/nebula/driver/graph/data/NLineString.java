/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import java.util.List;

public class NLineString extends Geography {

    private List<NPoint> points;

    public NLineString(List<NPoint> points) {
        super(GeoShape.GeoShapeLineString);
        this.points = points;
        lineString = this;
    }

    public List<NPoint> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LINESTRING(");
        for (NPoint point : points) {
            sb.append(point.getLng());
            sb.append(" ");
            sb.append(point.getLat());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

}
