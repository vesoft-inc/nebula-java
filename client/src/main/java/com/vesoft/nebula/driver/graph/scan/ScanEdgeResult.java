/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.scan;

import com.vesoft.nebula.driver.graph.data.Edge;
import com.vesoft.nebula.driver.graph.data.Node;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.data.ValueWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScanEdgeResult extends ScanResult {

    private List<String> propNames = new ArrayList<>();
    private List<String> returnColumns = new ArrayList<>();

    public ScanEdgeResult(List<ResultSet> results, List<String> propNames) {
        super(results);
        this.propNames = propNames;
        returnColumns.add("src");
        returnColumns.add("dst");
        returnColumns.addAll(propNames);
    }

    /**
     * get node table row's column names
     *
     * @return list of row column names
     */
    public List<String> getPropNames() {
        return returnColumns;
    }

    protected void convertResultToRow() {
        if (isEmpty) {
            return;
        }
        if (tableRows.isEmpty()) {
            for (ResultSet resultSet : results) {

                while (resultSet.hasNext()) {
                    ResultSet.Record   record    = resultSet.next();
                    List<ValueWrapper> rowValues = new ArrayList<>();

                    Node         srcNode = record.get("src").asNode();
                    Node dstNode = record.get("dst").asNode();
                    Edge edge    = record.get("edge").asEdge();
                    for (String key : srcNode.getProperties().keySet()) {
                        rowValues.add(srcNode.getProperties().get(key));
                    }
                    for (String key : dstNode.getProperties().keySet()) {
                        rowValues.add(dstNode.getProperties().get(key));
                    }
                    Map<String, ValueWrapper> properties = edge.getProperties();
                    for (String propName : propNames) {
                        rowValues.add(properties.get(propName));
                    }
                    tableRows.add(new TableRow(rowValues));
                }
            }
        }
    }
}
