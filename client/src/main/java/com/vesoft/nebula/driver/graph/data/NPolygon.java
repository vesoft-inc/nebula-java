/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import java.util.List;

public class NPolygon extends Geography {

    List<List<NPoint>> loops;

    public NPolygon(List<List<NPoint>> loops) {
        super(GeoShape.GeoShapePolygon);
        this.loops = loops;
        polygon = this;
    }

    /**
     * get the loops of the polygon
     *
     * @return List of points.
     */
    public List<List<NPoint>> getLoops() {
        return loops;
    }

    /**
     * get the number of loops of the polygon
     *
     * @return number of loops
     */
    public int getLoopNum() {
        return loops.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("POLYGON(");
        for (List<NPoint> loop : loops) {
            sb.append("(");
            for (NPoint point : loop) {
                sb.append(point.getLng());
                sb.append(" ");
                sb.append(point.getLat());
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("),");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');
        return sb.toString();
    }

}
