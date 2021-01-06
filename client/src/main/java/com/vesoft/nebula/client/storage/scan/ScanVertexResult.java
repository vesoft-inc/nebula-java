/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.storage.data.ScanStatus;
import com.vesoft.nebula.client.storage.data.VertexRow;
import com.vesoft.nebula.client.storage.data.VertexTableRow;
import com.vesoft.nebula.client.storage.processor.VertexProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanVertexResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanVertexResult.class);

    private final List<DataSet> dataSets;

    private final ScanStatus scanStatus;

    /**
     * VertexRow for table view
     */
    private List<VertexTableRow> vertexTableRows;

    /**
     * schema for VertexRow's values
     */
    private List<String> propNames;

    /**
     * Vertex for structure view with prop name
     */
    private List<VertexRow> verticeRows;

    private Map<ValueWrapper, VertexRow> vidVertices;

    private String decodeType = "utf-8";

    public ScanVertexResult(List<DataSet> dataSets, ScanStatus status) {
        this.dataSets = dataSets;
        this.scanStatus = status;
    }

    public List<VertexTableRow> getVertexTableRows() {
        if (vertexTableRows == null) {
            constructVertexTableRow();
        }
        return vertexTableRows;
    }

    public List<String> getPropNames() {
        if (propNames == null) {
            constrcutPropNames();
        }
        return propNames;
    }

    /**
     * get vertex with id
     *
     * @param vid Long type
     * @return Vertex
     */
    public VertexRow getVertex(String vid) {
        if (vidVertices == null) {
            constructVertexRow();
        }
        if (vidVertices.isEmpty()) {
            return null;
        }
        return vidVertices.get(vid);
    }

    /**
     * get all vertex
     *
     * @return List
     */
    public List<VertexRow> getVertices() {
        if (verticeRows == null) {
            constructVertexRow();
        }
        return verticeRows;
    }


    public Map<ValueWrapper, VertexRow> getVidVertices() {
        if (vidVertices == null) {
            constructVertexRow();
        }
        return vidVertices;
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

    private void constructVertexRow() {
        if (dataSets.isEmpty()) {
            return;
        }
        synchronized (this) {
            if (vidVertices == null) {
                vidVertices = VertexProcessor.constructVertexRow(dataSets, decodeType);
                verticeRows = new ArrayList<>(vidVertices.values());
            }
        }
    }

    private void constructVertexTableRow() {
        if (dataSets.isEmpty()) {
            return;
        }
        synchronized (this) {
            if (vertexTableRows == null) {
                vertexTableRows = VertexProcessor.constructVertexTableRow(dataSets, decodeType);
            }
        }
    }

    private void constrcutPropNames() {
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


