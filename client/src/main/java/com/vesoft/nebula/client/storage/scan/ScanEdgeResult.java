/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.client.storage.data.EdgeRow;
import com.vesoft.nebula.client.storage.data.EdgeTableRow;
import com.vesoft.nebula.client.storage.data.ScanStatus;
import com.vesoft.nebula.client.storage.processor.EdgeProcessor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeResult.class);

    private final List<DataSet> dataSets;

    /**
     * scan result
     */
    private final ScanStatus scanStatus;
    /**
     * VertexTableRow for table view
     */
    private List<EdgeTableRow> edgeTableRows;

    /**
     * schema for VertexRow's values
     */
    private List<String> propNames;

    /**
     * Vertex for structure view with prop name
     */
    private List<EdgeRow> edgeRows;

    // todo set decodeType
    private String decodeType = "utf-8";


    public ScanEdgeResult(List<DataSet> dataSets, ScanStatus status) {
        this.dataSets = dataSets;
        this.scanStatus = status;
    }

    public List<EdgeTableRow> getEdgeTableRows() {
        if (edgeTableRows == null) {
            constructEdgeTableRow();
        }
        return edgeTableRows;
    }

    public List<String> getPropNames() {
        if (propNames == null) {
            constructPropNames();
        }
        return propNames;
    }

    public List<EdgeRow> getEdges() {
        if (edgeRows == null) {
            constructEdgeRow();
        }
        return edgeRows;
    }

    /**
     * get the result status
     *
     * @return boolean
     */
    public boolean isAllSuccess() {
        return scanStatus == ScanStatus.ALL_SUCCESS;
    }

    /**
     * whether result data is empty
     *
     * @return boolean
     */
    public boolean isEmpty() {
        if (dataSets == null || dataSets.isEmpty()) {
            return true;
        }
        for (DataSet dataSet : dataSets) {
            if (dataSet.getRows().size() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * convert dataSets to edgeTableRows.
     */
    private void constructEdgeTableRow() {
        if (dataSets.isEmpty()) {
            return;
        }
        synchronized (this) {
            if (edgeTableRows == null) {
                edgeTableRows = EdgeProcessor.constructEdgeTableRow(dataSets, decodeType);
            }
        }
    }

    /**
     * convert datasets to edgeRows.
     */
    private void constructEdgeRow() {
        if (dataSets.isEmpty()) {
            return;
        }
        synchronized (this) {
            if (edgeRows == null) {
                edgeRows = EdgeProcessor.constructEdgeRow(dataSets, decodeType);
            }
        }
    }

    /**
     * extract result's property names
     */
    private void constructPropNames() {
        if (dataSets.isEmpty()) {
            return;
        }
        synchronized (this) {
            if (propNames == null) {
                propNames = new ArrayList<>();
                List<byte[]> colNames = dataSets.get(0).getColumn_names();
                for (byte[] colName : colNames) {
                    String name = new String(colName).split("\\.")[1];
                    propNames.add(name);
                }
            }
        }
    }
}
