/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;

/**
 * the class maintains some additional information for execution result.
 */
public class ExtraInfo {

    // cursor for scan procedure
    private String cursor;
    // the number of affected nodes
    private long   affectedNodes;
    // the number of affected forward edges
    private long   affectedEdges;
    // the number of affected reverse edges

    private long totalServerTimeUs;

    private long parseTimeUs;

    private long buildTimeUs;

    private long optimizeTimeUs;

    private long serializeTimeUs;

    private long numWarnings;

    private long numExportedRecords;

    private List<String> exportPaths = new ArrayList<>();


    public ExtraInfo() {
        this.cursor = null;
        affectedNodes = 0;
        affectedEdges = 0;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public void setAffectedNodes(long affectedNodes) {
        this.affectedNodes = affectedNodes;
    }

    public void setAffectedEdges(long affectedEdges) {
        this.affectedEdges = affectedEdges;
    }

    public void setTotalServerTimeUs(long totalServerTimeUs) {
        this.totalServerTimeUs = totalServerTimeUs;
    }

    public void setParseTimeUs(long parseTimeUs) {
        this.parseTimeUs = parseTimeUs;
    }

    public void setBuildTimeUs(long buildTimeUs) {
        this.buildTimeUs = buildTimeUs;
    }

    public void setOptimizeTimeUs(long optimizeTimeUs) {
        this.optimizeTimeUs = optimizeTimeUs;
    }

    public void setSerializeTimeUs(long serializeTimeUs) {
        this.serializeTimeUs = serializeTimeUs;
    }

    public String getCursor() {
        return this.cursor;
    }

    public long getAffectedNodes() {
        return this.affectedNodes;
    }

    public long getAffectedEdges() {
        return this.affectedEdges;
    }

    public long getTotalServerTimeUs() {
        return totalServerTimeUs;
    }

    public long getParseTimeUs() {
        return parseTimeUs;
    }


    public long getBuildTimeUs() {
        return buildTimeUs;
    }

    public long getOptimizeTimeUs() {
        return optimizeTimeUs;
    }

    public long getSerializeTimeUs() {
        return serializeTimeUs;
    }

    public long getNumWarnings() {
        return numWarnings;
    }

    public void setNumWarnings(long numWarnings) {
        this.numWarnings = numWarnings;
    }

    public long getNumExportedRecords() {
        return numExportedRecords;
    }

    public void setNumExportedRecords(long numExportedRecords) {
        this.numExportedRecords = numExportedRecords;
    }

    public List<String> getExportPaths() {
        return exportPaths;
    }

    public void setExportPaths(List<ByteString> exportPaths) {
        for (ByteString path : exportPaths) {
            this.exportPaths.add(path.toString(Charsets.UTF_8));
        }
    }

    @Override
    public String toString() {
        return "ExtraInfo{"
            + "cursor='" + cursor + '\''
            + ", affectedNodes=" + affectedNodes
            + ", affectedEdges=" + affectedEdges
            + ", totalServerTimeUs=" + totalServerTimeUs
            + ", parseTimeUs=" + parseTimeUs
            + ", buildTimeUs=" + buildTimeUs
            + ", optimizeTimeUs=" + optimizeTimeUs
            + ", serializeTimeUs=" + serializeTimeUs
            + ", numWarnings=" + numWarnings
            + ", numExportedRecords=" + numExportedRecords
            + ", exportedPaths=" + exportPaths
            + '}';
    }
}
