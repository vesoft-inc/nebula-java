/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.table;

import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.LookupTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.abilities.SupportsProjectionPushDown;

public class NebulaDynamicTableSource implements ScanTableSource, LookupTableSource, SupportsProjectionPushDown {

    private final String address;
    private final String username;
    private final String password;

    public NebulaDynamicTableSource(String address, String username, String password){
        this.address = address;
        this.username = username;
        this.password = password;
    }

    @Override
    public DynamicTableSource copy() {
        return new NebulaDynamicTableSource(address, username, password);
    }

    @Override
    public String asSummaryString() {
        return "Nebula";
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

        return null;
    }

    @Override
    public boolean supportsNestedProjection() {
        return false;
    }

    @Override
    public void applyProjection(int[][] projectedFields) {

    }
}
