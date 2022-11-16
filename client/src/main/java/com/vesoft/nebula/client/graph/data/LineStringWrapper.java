/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Coordinate;
import com.vesoft.nebula.LineString;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineStringWrapper extends  BaseDataObject {
    private final LineString lineString;

    public LineStringWrapper(LineString lineString) {
        this.lineString = lineString;
    }

    public List<CoordinateWrapper> getCoordinateList() {
        List<CoordinateWrapper> coordList = new ArrayList<>();
        for (Coordinate coord : lineString.getCoordList()) {
            coordList.add(new CoordinateWrapper(coord));
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
        List<CoordinateWrapper> thisList = getCoordinateList();
        List<CoordinateWrapper> thatList = that.getCoordinateList();
        if (thisList.size() != thatList.size()) {
            return false;
        }
        for (int i = 0; i < thisList.size(); i++) {
            if (!thisList.get(i).equals(thatList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LINESTRING");
        sb.append('(');
        if (lineString.getCoordList() != null) {
            for (Coordinate coordinate : lineString.getCoordList()) {
                sb.append(coordinate.getX());
                sb.append(' ');
                sb.append(coordinate.getY());
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
