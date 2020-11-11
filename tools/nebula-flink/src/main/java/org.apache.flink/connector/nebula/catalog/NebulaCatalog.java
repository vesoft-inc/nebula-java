/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.catalog;

import com.facebook.thrift.TException;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.ColumnDef;
import com.vesoft.nebula.Schema;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.TagItem;
import org.apache.commons.collections.map.HashedMap;
import org.apache.flink.connector.nebula.utils.DataTypeEnum;
import org.apache.flink.connector.nebula.utils.NebulaConstant;
import org.apache.flink.connector.nebula.utils.NebulaUtils;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.catalog.*;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.table.catalog.exceptions.DatabaseNotExistException;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.types.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.vesoft.nebula.SupportedType.*;
import static org.apache.flink.connector.nebula.table.NebulaDynamicTableFactory.*;
import static org.apache.flink.table.factories.FactoryUtil.CONNECTOR;

public class NebulaCatalog extends AbstractNebulaCatalog {

    private static final Logger LOG = LoggerFactory.getLogger(NebulaCatalog.class);
    private final List<HostAndPort> hostAndPorts;
    private final MetaClientImpl metaClient;

    public NebulaCatalog(String catalogName, String defaultDatabase, String username, String pwd, String address) {
        super(catalogName, defaultDatabase, username, pwd, address);
        this.hostAndPorts = NebulaUtils.getHostAndPorts(address);
        this.metaClient = new MetaClientImpl(hostAndPorts);
    }

    @Override
    public List<String> listDatabases() throws CatalogException {
        Map<String, Integer> spaceMap;
        try {
            metaClient.connect();
            spaceMap = metaClient.getSpaces();
        } catch (TException e) {
            LOG.error(String.format("failed to connect meta service vis %s ", address), e);
            throw new CatalogException("nebula meta service connect failed.", e);
        }
        return new ArrayList<>(spaceMap.keySet());
    }

    @Override
    public CatalogDatabase getDatabase(String databaseName) throws DatabaseNotExistException, CatalogException {
        if (listDatabases().contains(databaseName.trim())) {
            Map<String, String> props = new HashedMap();
            props.put("spaceId", String.valueOf(metaClient.getSpaceIdFromCache(databaseName)));
            return new CatalogDatabaseImpl(props, databaseName);
        } else {
            throw new DatabaseNotExistException(getName(), databaseName);
        }
    }

    /**
     * objectName in tablePath mush start with VERTEX. or EDGE.
     *
     * @param tablePath A graphSpace name and label name.
     * @return
     */
    @Override
    public boolean tableExists(ObjectPath tablePath) throws CatalogException {
        String graphSpace = tablePath.getDatabaseName();
        String table = tablePath.getObjectName();
        try {
            return (listTables(graphSpace).contains(table));
        } catch (DatabaseNotExistException e) {
            throw new CatalogException("failed to call tableExists function, ", e);
        }
    }


    /**
     * show all tags and edges
     *
     * @param graphSpace nebula graph space
     * @return List of Tag and Edge, tag starts with VERTEX. and edge starts with EDGE. .
     */
    @Override
    public List<String> listTables(String graphSpace) throws DatabaseNotExistException, CatalogException {
        if (!databaseExists(graphSpace)) {
            throw new DatabaseNotExistException(getName(), graphSpace);
        }

        try {
            metaClient.connect();
        } catch (TException e) {
            LOG.error(String.format("failed to connect meta service vis %s ", address), e);
            throw new CatalogException("nebula meta service connect failed.", e);
        }
        List<String> tables = new ArrayList<>();
        for (TagItem tag : metaClient.getTags(graphSpace)) {
            tables.add("VERTEX" + NebulaConstant.POINT + tag.tag_name);
        }
        for (EdgeItem edge : metaClient.getEdges(graphSpace)) {
            tables.add("EDGE" + NebulaConstant.POINT + edge.edge_name);
        }
        return tables;
    }

    @Override
    public CatalogBaseTable getTable(ObjectPath tablePath) throws TableNotExistException, CatalogException {
        if (!tableExists(tablePath)) {
            throw new TableNotExistException(getName(), tablePath);
        }

        String graphSpace = tablePath.getDatabaseName();
        String[] typeAndLabel = tablePath.getObjectName().split(NebulaConstant.SPLIT_POINT);
        String type = typeAndLabel[0];
        String label = typeAndLabel[1];
        if (!DataTypeEnum.checkValidDataType(type)) {
            LOG.warn("tablePath does not exist in nebula");
            return null;
        }

        try {
            metaClient.connect();
        } catch (TException e) {
            LOG.error(String.format("failed to connect meta service vis %s ", address), e);
            throw new CatalogException("nebula meta service connect failed.", e);
        }

        Schema schema;
        if (DataTypeEnum.valueOf(type).isVertex()) {
            schema = metaClient.getTag(graphSpace, label);
        } else {
            schema = metaClient.getEdge(graphSpace, label);
        }

        String[] names = new String[schema.columns.size()];
        DataType[] types = new DataType[schema.columns.size()];
        for(int i=0;i<schema.columns.size();i++){
            names[i] = schema.columns.get(i).getName();
            types[i] = fromNebulaType(schema.columns, i);
        }

        TableSchema.Builder tableBuilder = new TableSchema.Builder()
                .fields(names, types);
        TableSchema tableSchema = tableBuilder.build();

        Map<String, String> props = new HashMap<>();
        props.put(CONNECTOR.key(), IDENTIFIER);
        props.put(ADDRESS.key(), address);
        props.put(USERNAME.key(), username);
        props.put(PASSWORD.key(), password);
        props.put(GRAPH_SPACE.key(), tablePath.getDatabaseName());
        props.put(LABEL_NAME.key(), tablePath.getObjectName());
        return new CatalogTableImpl(tableSchema, props, "nebulaTableCatalog");
    }


    /**
     * construct flink datatype from nebula type
     * @see com.vesoft.nebula.SupportedType
     * */
    private DataType fromNebulaType(List<ColumnDef> columns, int colIndex){
        int type = columns.get(colIndex).getType().type;

        switch (type){
            case INT :
            case VID:
                return DataTypes.BIGINT();
            case BOOL:
                return DataTypes.BOOLEAN();
            case FLOAT:
                return DataTypes.FLOAT();
            case DOUBLE:
                return DataTypes.DOUBLE();
            case STRING:
            case PATH:
                return DataTypes.STRING();
            case TIMESTAMP:
                return DataTypes.TIMESTAMP();
            case YEAR:
            case YEARMONTH:
            case DATE:
            case DATETIME:
                return DataTypes.DATE();
            case UNKNOWN:
                return DataTypes.NULL();
            default:
                throw new UnsupportedOperationException(
                        String.format("Doesn't support nebula type '%s' yet", columns.get(colIndex).getType()));
        }
    }
}
