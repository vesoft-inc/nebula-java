/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Coordinate;
import com.vesoft.nebula.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PolygonWrapper extends BaseDataObject {
    private final Polygon polygon;

    public PolygonWrapper(Polygon polygon) {
        this.polygon = polygon;
    }

    public List<List<CoordinateWrapper>> getCoordListList() {
        List<List<CoordinateWrapper>> coordListList = new ArrayList<>();
        for (List<Coordinate> cl: polygon.getCoordListList()) {
            List<CoordinateWrapper> coordList = new ArrayList<>();
            for (Coordinate coordinate : cl) {
                coordList.add(new CoordinateWrapper(coordinate));
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
        List<List<CoordinateWrapper>> thisListList = getCoordListList();
        List<List<CoordinateWrapper>> thatListList = that.getCoordListList();
        if (thisListList.size() != thatListList.size()) {
            return false;
        }
        for (int i = 0; i < thisListList.size(); i++) {
            List<CoordinateWrapper> thisList = thisListList.get(i);
            List<CoordinateWrapper> thatList = thatListList.get(i);
            if (thisList.size() != thatList.size()) {
                return false;
            }
            for (int j = 0; j < thisList.size(); j++) {
                if (!thisList.get(j).equals(thatList.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("POLYGON");
        sb.append('(');
        if (polygon.getCoordListList() != null) {
            for (List<Coordinate> cl : polygon.getCoordListList()) {
                sb.append('(');
                for (Coordinate coordinate : cl) {
                    sb.append(coordinate.getX());
                    sb.append(' ');
                    sb.append(coordinate.getY());
                    sb.append(',');
                }
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append(')');
                sb.append(',');
            }
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        sb.append(')');

        return sb.toString();
    }
}
