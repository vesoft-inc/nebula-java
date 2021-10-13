package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Coordinate;
import com.vesoft.nebula.Point;
import java.util.Objects;

public class PointWrapper extends BaseDataObject {
    private final Point point;

    public PointWrapper(Point point) {
        this.point = point;
    }

    public CoordinateWrapper getCoordinate() {
        return new CoordinateWrapper(point.getCoord());
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
        StringBuilder sb = new StringBuilder();
        sb.append("POINT");
        sb.append('(');
        Coordinate coordinate = point.getCoord();
        if (coordinate != null) {
            sb.append(coordinate.getX());
            sb.append(' ');
            sb.append(coordinate.getY());
        }
        sb.append(')');

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(point);
    }

}
