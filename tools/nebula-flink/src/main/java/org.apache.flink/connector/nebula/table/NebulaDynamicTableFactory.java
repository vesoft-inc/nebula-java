/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.table;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.connector.nebula.utils.NebulaConstant;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;

import java.util.HashSet;
import java.util.Set;


public class NebulaDynamicTableFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {
    public static final String IDENTIFIER = "nebula";


    public static final ConfigOption<String> ADDRESS = ConfigOptions
            .key("address")
            .stringType()
            .noDefaultValue()
            .withDescription("the nebula server address.");

    public static final ConfigOption<String> USERNAME = ConfigOptions
            .key("username")
            .stringType()
            .noDefaultValue()
            .withDescription("the nebula server name.");

    public static final ConfigOption<String> PASSWORD = ConfigOptions
            .key("password")
            .stringType()
            .noDefaultValue()
            .withDescription("the nebula server password.");

    public static final ConfigOption<String> GRAPH_SPACE = ConfigOptions
            .key("graph-space")
            .stringType()
            .noDefaultValue()
            .withDescription("the nebula graph space name.");

    public static final ConfigOption<String> LABEL_NAME = ConfigOptions
            .key("label-name")
            .stringType()
            .noDefaultValue()
            .withDescription("the nebula graph space label name.");

    public static final ConfigOption<Integer> CONNECT_TIMEOUT = ConfigOptions
            .key("connect-timeout")
            .intType()
            .defaultValue(NebulaConstant.DEFAULT_CONNECT_TIMEOUT_MS)
            .withDescription("the nebula connect timeout duration");

    public static final ConfigOption<Integer> CONNECT_RETRY = ConfigOptions
            .key("connect-retry")
            .intType()
            .defaultValue(NebulaConstant.DEFAULT_CONNECT_RETRY)
            .withDescription("the nebula connect retry times");

    public static final ConfigOption<Integer> TIMEOUT = ConfigOptions
            .key("timeout")
            .intType()
            .defaultValue(NebulaConstant.DEFAULT_TIMEOUT_MS)
            .withDescription("the nebula execute timeout duration");

    public static final ConfigOption<Integer> EXECUTE_RETRY = ConfigOptions
            .key("execute-retry")
            .intType()
            .defaultValue(NebulaConstant.DEFAULT_EXECUTION_RETRY)
            .withDescription("the nebula execute retry times");


    @Override
    public DynamicTableSink createDynamicTableSink(Context context){
        return new NebulaDynamicTableSink(ADDRESS.key(), USERNAME.key(), PASSWORD.key());
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        String address = ADDRESS.key();
        String username = USERNAME.key();
        String password = PASSWORD.key();

        return new NebulaDynamicTableSource(address, username, password);
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        Set<ConfigOption<?>> set = new HashSet<>();
        set.add(ADDRESS);
        set.add(USERNAME);
        set.add(PASSWORD);
        return set;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        Set<ConfigOption<?>> set = new HashSet<>();
        set.add(CONNECT_TIMEOUT);
        set.add(CONNECT_RETRY);
        set.add(TIMEOUT);
        set.add(EXECUTE_RETRY);
        return set;
    }
}
