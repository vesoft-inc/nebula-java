/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.storage.data.ScanStatus;
import com.vesoft.nebula.client.storage.data.VertexRow;
import com.vesoft.nebula.client.storage.data.VertexTableRow;
import com.vesoft.nebula.client.storage.processor.VertexProcessor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanVertexResult implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanVertexResult.class);
    private static final long serialVersionUID = -2446261806893848521L;

    private final List<DataSet> dataSets;

    private final ScanStatus scanStatus;

    /** VertexRow for table view */
    private List<VertexTableRow> vertexTableRows = new ArrayList<>();

    /** schema for VertexRow's values */
    private List<String> propNames = new ArrayList<>();

    /** Vertex for structure view with prop name */
    private List<VertexRow> verticeRows = new ArrayList<>();

    private Map<ValueWrapper, VertexRow> vidVertices = new HashMap<>();

    private String decodeType = "utf-8";

    private boolean isEmpty;

    public ScanVertexResult(List<DataSet> dataSets, ScanStatus status) {
        this.dataSets = dataSets;
        this.scanStatus = status;
        this.isEmpty = isDatasetEmpty();
    }

    /**
     * get vertex table rows
     *
     * @return list of {@link VertexTableRow}
     */
    public List<VertexTableRow> getVertexTableRows() {
        if (!isEmpty && vertexTableRows.isEmpty()) {
            constructVertexTableRow();
        }
        return vertexTableRows;
    }

    /**
     * get the result's property names
     *
     * @return list of property name
     */
    public List<String> getPropNames() {
        if (!isEmpty && propNames.isEmpty()) {
            constructPropNames();
        }
        return propNames;
    }

    /**
     * get vertex row with id
     *
     * @param vid ValueWrapper, int type or string type vid
     * @return {@link VertexRow}
     */
    public VertexRow getVertex(ValueWrapper vid) {
        if (!isEmpty && vidVertices.isEmpty()) {
            constructVertexRow();
        }
        if (vidVertices.isEmpty()) {
            return null;
        }
        return vidVertices.get(vid);
    }

    /**
     * get all vertex row
     *
     * @return list of {@link VertexRow}
     */
    public List<VertexRow> getVertices() {
        if (!isEmpty && verticeRows.isEmpty()) {
            constructVertexRow();
        }
        return verticeRows;
    }

    /**
     * get the map of vid and vertex row
     *
     * @return map of vid wrapper{@link ValueWrapper} and {@link VertexRow}
     */
    public Map<ValueWrapper, VertexRow> getVidVertices() {
        if (!isEmpty && vidVertices.isEmpty()) {
            constructVertexRow();
        }
        return vidVertices;
    }

    /**
     * get the result status
     *
     * @return true if all parts scan succeed
     */
    public boolean isAllSuccess() {
        return scanStatus == ScanStatus.ALL_SUCCESS;
    }

    /**
     * whether result data is empty
     *
     * @return true if result data is empty
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * check if dataset is empty
     *
     * @return boolean
     */
    private boolean isDatasetEmpty() {
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

    /** construct vertex row from datasets */
    private void constructVertexRow() {
        if (isEmpty) {
            return;
        }
        synchronized (this) {
            if (vidVertices.isEmpty()) {
                vidVertices = VertexProcessor.constructVertexRow(dataSets, decodeType);
                verticeRows = new ArrayList<>(vidVertices.values());
            }
        }
    }

    /** construct vertex table row from datasets */
    private void constructVertexTableRow() {
        if (isEmpty) {
            return;
        }
        synchronized (this) {
            if (vertexTableRows.isEmpty()) {
                vertexTableRows = VertexProcessor.constructVertexTableRow(dataSets, decodeType);
            }
        }
    }

    /** construct property name from dataset */
    private void constructPropNames() {
        if (isEmpty) {
            return;
        }
        synchronized (this) {
            if (propNames.isEmpty()) {
                List<byte[]> colNames = dataSets.get(0).getColumn_names();
                for (byte[] colName : colNames) {
                    String propName = new String(colName);
                    if (!propName.contains(".")) {
                        continue;
                    }
                    propNames.add(propName.split("\\.")[1]);
                }
            }
        }
    }
}
