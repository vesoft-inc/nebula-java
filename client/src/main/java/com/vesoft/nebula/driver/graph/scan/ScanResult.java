/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.scan;

import com.vesoft.nebula.driver.graph.data.ResultSet;
import java.util.ArrayList;
import java.util.List;


public abstract class ScanResult {

    protected final List<ResultSet> results;
    protected List<TableRow>        tableRows = new ArrayList<>();
    protected boolean isEmpty;

    public ScanResult(List<ResultSet> results) {
        this.results = results;
        isEmpty = isResultsEmpty();
    }

    /**
     * check if scan node result is empty
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return isEmpty;
    }


    /**
     * get node table rows
     *
     * @return list of {@link TableRow}
     */
    public List<TableRow> getTableRows() {
        if (!isEmpty && tableRows.isEmpty()) {
            convertResultToRow();
        }
        return tableRows;
    }

    /**
     * convert the record of results to row
     */
    protected abstract void convertResultToRow();

    /**
     * if the scanNodeResult is empty
     *
     * @return boolean
     */
    private boolean isResultsEmpty() {
        for (ResultSet resultSet : results) {
            if (!resultSet.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
