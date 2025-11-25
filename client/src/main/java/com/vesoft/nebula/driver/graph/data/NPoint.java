/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

public class NPoint extends Geography {

    private double lng;
    private double lat;

    public NPoint(double x, double y) {
        super(GeoShape.GeoShapePoint);
        this.lng = x;
        this.lat = y;
        point = this;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("POINT(")
            .append(lng)
            .append(" ")
            .append(lat)
            .append(")");
        return sb.toString();
    }
}
