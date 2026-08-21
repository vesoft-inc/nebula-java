/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.graph.data.NPoint;
import com.vesoft.nebula.driver.graph.data.NPolygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper for a geographic polygon, matching the v3 client.
 */
public class PolygonWrapper extends BaseDataObject {
    private final NPolygon polygon;

    public PolygonWrapper(NPolygon polygon) {
        this.polygon = polygon;
    }

    public List<List<CoordinateWrapper>> getCoordListList() {
        List<List<CoordinateWrapper>> coordListList = new ArrayList<>();
        for (List<NPoint> loop : polygon.getLoops()) {
            List<CoordinateWrapper> coordList = new ArrayList<>();
            for (NPoint point : loop) {
                coordList.add(new CoordinateWrapper(point));
            }
            coordListList.add(coordList);
        }
        return coordListList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(polygon);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PolygonWrapper that = (PolygonWrapper) o;
        return this.getCoordListList().equals(that.getCoordListList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("POLYGON(");
        List<List<NPoint>> loops = polygon.getLoops();
        for (int i = 0; i < loops.size(); i++) {
            sb.append('(');
            List<NPoint> loop = loops.get(i);
            for (int j = 0; j < loop.size(); j++) {
                NPoint point = loop.get(j);
                sb.append(point.getLng()).append(' ').append(point.getLat());
                if (j < loop.size() - 1) {
                    sb.append(',');
                }
            }
            sb.append(')');
            if (i < loops.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
