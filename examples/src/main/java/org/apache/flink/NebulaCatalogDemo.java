/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink;

import org.apache.flink.connector.nebula.utils.NebulaCatalogUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.CatalogDatabase;
import org.apache.flink.table.catalog.ObjectPath;
import org.apache.flink.table.catalog.exceptions.DatabaseNotExistException;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;

public class NebulaCatalogDemo {

    private static String catalogName = "testCatalog";
    private static String defaultSpace = "flinkSink";
    private static String username = "root";
    private static String password = "nebula";
    private static String address = "127.0.0.1:45500";
    private String table = "VERTEX.player";
    static StreamTableEnvironment tEnv;
    static Catalog catalog;

    static {
        catalog = NebulaCatalogUtils
                .createNebulaCatalog(catalogName, defaultSpace, address, username, password);
        StreamExecutionEnvironment bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        tEnv = StreamTableEnvironment.create(bsEnv);
        tEnv.registerCatalog(catalogName, catalog);
        tEnv.useCatalog(catalogName);
    }

    public void testListDatabases() {
        String[] spaces = tEnv.listDatabases();
        for (String space : spaces) {
            System.out.println("space: " + space);
        }
    }

    public void testGetDatabase() throws DatabaseNotExistException {
        CatalogDatabase database = catalog.getDatabase(defaultSpace);
        assert defaultSpace.equalsIgnoreCase(database.getComment());
        assert database.getProperties().containsKey("spaceId");
    }

    public void testTableExists() {
        ObjectPath path = new ObjectPath(defaultSpace, table);
        assert catalog.tableExists(path);
    }

    public void testListTables() {
        tEnv.useDatabase(defaultSpace);
        String[] tables = tEnv.listTables();
        assert tables.length == 1;
        assert tables[0].equalsIgnoreCase(table);
    }

    public void testGetTable() throws TableNotExistException {
        CatalogBaseTable table = catalog.getTable(new ObjectPath(defaultSpace, this.table));
        assert table.getComment().equalsIgnoreCase("nebulaTableCatalog");
        assert table.getSchema().getFieldCount() == 2;
    }
}