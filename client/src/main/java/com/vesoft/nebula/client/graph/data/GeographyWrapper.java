/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Geography;
import java.util.Objects;

public class GeographyWrapper extends BaseDataObject {
    private final Geography geography;

    public GeographyWrapper(Geography geography) {
        this.geography = geography;
    }

    public PolygonWrapper getPolygonWrapper() {
        return new PolygonWrapper(geography.getPgVal());
    }

    public LineStringWrapper getLineStringWrapper() {
        return new LineStringWrapper(geography.getLsVal());
    }

    public PointWrapper getPointWrapper() {
        return new PointWrapper(geography.getPtVal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeographyWrapper that = (GeographyWrapper)o;
        switch (geography.getSetField()) {
            case Geography.PTVAL:
                return getPointWrapper().equals(that.getPointWrapper());
            case Geography.LSVAL:
                return getLineStringWrapper().equals(that.getLineStringWrapper());
            case Geography.PGVAL:
                return getPolygonWrapper().equals(that.getPolygonWrapper());
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        switch (geography.getSetField()) {
            case Geography.PTVAL:
                return getPointWrapper().toString();
            case Geography.LSVAL:
                return getLineStringWrapper().toString();
            case Geography.PGVAL:
                return getPolygonWrapper().toString();
            default:
                return "";
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(geography);
    }
}
