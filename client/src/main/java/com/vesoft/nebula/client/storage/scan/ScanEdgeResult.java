/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.client.storage.data.EdgeRow;
import com.vesoft.nebula.client.storage.data.EdgeTableRow;
import com.vesoft.nebula.client.storage.data.ScanStatus;
import com.vesoft.nebula.client.storage.processor.EdgeProcessor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeResult implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeResult.class);
    private static final long serialVersionUID = 519190254786197550L;

    private final List<DataSet> dataSets;

    /**
     * scan result
     */
    private final ScanStatus scanStatus;
    /**
     * VertexTableRow for table view
     */
    private List<EdgeTableRow> edgeTableRows = new ArrayList<>();

    /**
     * schema for VertexRow's values
     */
    private List<String> propNames = new ArrayList<>();

    /**
     * Vertex for structure view with prop name
     */
    private List<EdgeRow> edgeRows = new ArrayList<>();

    // todo set decodeType
    private String decodeType = "utf-8";

    private boolean isEmpty;


    public ScanEdgeResult(List<DataSet> dataSets, ScanStatus status) {
        this.dataSets = dataSets;
        this.scanStatus = status;
        this.isEmpty = isDatasetEmpty();
    }

    /**
     * get edge table rows
     *
     * @return list of {@link EdgeTableRow}
     */
    public List<EdgeTableRow> getEdgeTableRows() {
        if (!isEmpty && edgeTableRows.isEmpty()) {
            constructEdgeTableRow();
        }
        return edgeTableRows;
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
     * get edge rows
     *
     * @return list of {@link EdgeRow}
     */
    public List<EdgeRow> getEdges() {
        if (!isEmpty && edgeRows.isEmpty()) {
            constructEdgeRow();
        }
        return edgeRows;
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

    /**
     * convert dataSets to edgeTableRows.
     */
    private void constructEdgeTableRow() {
        if (isEmpty) {
            return;
        }
        synchronized (this) {
            if (edgeTableRows.isEmpty()) {
                edgeTableRows = EdgeProcessor.constructEdgeTableRow(dataSets, decodeType);
            }
        }
    }

    /**
     * convert datasets to edgeRows.
     */
    private void constructEdgeRow() {
        if (isEmpty) {
            return;
        }
        synchronized (this) {
            if (edgeRows.isEmpty()) {
                edgeRows = EdgeProcessor.constructEdgeRow(dataSets, decodeType);
            }
        }
    }

    /**
     * extract result's property names
     */
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
