/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.catalog;

import org.apache.flink.connector.nebula.utils.NebulaCatalogUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.CatalogDatabase;
import org.apache.flink.table.catalog.ObjectPath;
import org.apache.flink.table.catalog.exceptions.DatabaseNotExistException;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.junit.Test;

public class NebulaCatalogTest {

    private final static String CATALOG_NAME = "testCatalog";
    private final static String DEFAULT_SPACE = "flinkSink";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "nebula";
    private final static String ADDRESS = "127.0.0.1:45500";
    private final static String TABLE = "VERTEX.player";
    static StreamTableEnvironment tEnv;
    static Catalog catalog;

    static{
        catalog = NebulaCatalogUtils.createNebulaCatalog(CATALOG_NAME, DEFAULT_SPACE, ADDRESS, USERNAME, PASSWORD);
        StreamExecutionEnvironment bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        tEnv = StreamTableEnvironment.create(bsEnv);
        tEnv.registerCatalog(CATALOG_NAME, catalog);
        tEnv.useCatalog(CATALOG_NAME);
    }

    @Test
    public void testListDatabases() {
        String[] spaces = tEnv.listDatabases();
        for (String space : spaces) {
            System.out.println("space: " + space);
        }
    }

    @Test
    public void testGetDatabase() throws DatabaseNotExistException {
        CatalogDatabase database = catalog.getDatabase(DEFAULT_SPACE);
        assert DEFAULT_SPACE.equalsIgnoreCase(database.getComment());
        assert database.getProperties().containsKey("spaceId");
    }

    @Test
    public void testTableExists() {
        ObjectPath path = new ObjectPath(DEFAULT_SPACE, TABLE);
        assert catalog.tableExists(path);
    }

    @Test
    public void testListTables() {
        tEnv.useDatabase(DEFAULT_SPACE);
        String[] tables = tEnv.listTables();
        assert tables.length == 1;
        assert tables[0].equalsIgnoreCase(TABLE);
    }

    @Test
    public void testGetTable() throws TableNotExistException {
        CatalogBaseTable table = catalog.getTable(new ObjectPath(DEFAULT_SPACE, TABLE));
        assert table.getComment().equalsIgnoreCase(DEFAULT_SPACE);
        assert table.getSchema().getFieldCount() == 2;
    }
}