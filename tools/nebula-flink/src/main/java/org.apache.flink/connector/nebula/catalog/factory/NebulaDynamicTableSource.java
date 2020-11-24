/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.catalog.factory;

import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.table.NebulaRowDataInputFormat;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.InputFormatProvider;
import org.apache.flink.table.connector.source.LookupTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.abilities.SupportsProjectionPushDown;
import org.apache.flink.table.data.RowData;

public class NebulaDynamicTableSource  implements ScanTableSource, LookupTableSource, SupportsProjectionPushDown {

    private final NebulaConnectionProvider connectionProvider;
    private final ExecutionOptions executionOptions;

    public NebulaDynamicTableSource(NebulaConnectionProvider connectionProvider, ExecutionOptions executionOptions){
        this.connectionProvider = connectionProvider;
        this.executionOptions = executionOptions;
    }


    @Override
    public LookupRuntimeProvider getLookupRuntimeProvider(LookupContext context) {
        return null;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        return ChangelogMode.insertOnly();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {
        return InputFormatProvider.of(getInputFormat(connectionProvider));
    }

    @Override
    public DynamicTableSource copy() {
        return new NebulaDynamicTableSource(connectionProvider, executionOptions);
    }

    @Override
    public String asSummaryString() {
        return "Nebula";
    }

    @Override
    public boolean supportsNestedProjection() {
        return false;
    }

    @Override
    public void applyProjection(int[][] projectedFields) {

    }

    private InputFormat<RowData, ?> getInputFormat(NebulaConnectionProvider connectionProvider){
        return new NebulaRowDataInputFormat();
    }
}
