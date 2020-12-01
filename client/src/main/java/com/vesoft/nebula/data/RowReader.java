/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vesoft.nebula.ColumnDef;
import com.vesoft.nebula.NebulaCodec;
import com.vesoft.nebula.NebulaCodec.Pair;
import com.vesoft.nebula.Schema;
import com.vesoft.nebula.data.PropertyDef.PropertyType;
import com.vesoft.nebula.utils.NativeUtils;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RowReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(RowReader.class);
    private long schemaVersion;
    private Map<String, Integer> propertyNameIndex = Maps.newHashMap();
    private List<Pair> defs = Lists.newLinkedList();
    private List<PropertyType> types = Lists.newLinkedList();

    static {
        try {
            NativeUtils.loadLibraryFromJar("/libnebula_codec.so", NebulaCodec.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public RowReader(Schema schema) {
        this(schema, 0);
    }

    public RowReader(Schema schema, long schemaVersion) {
        this.schemaVersion = schemaVersion;
        int idx = 0;
        for (ColumnDef columnDef : schema.columns) {
            PropertyType type = PropertyType.getEnum(columnDef.getType().getType());
            String name = columnDef.getName();
            switch (type) {
                case BOOL:
                    defs.add(new Pair(name, Boolean.class.getName()));
                    break;
                case INT:
                case VID:
                    defs.add(new Pair(name, Long.class.getName()));
                    break;
                case FLOAT:
                    defs.add(new Pair(name, Float.class.getName()));
                    break;
                case DOUBLE:
                    defs.add(new Pair(name, Double.class.getName()));
                    break;
                case STRING:
                    defs.add(new Pair(name, byte[].class.getName()));
                    break;
                case TIMESTAMP:
                    defs.add(new Pair(name, Long.class.getName()));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type in schema: " + type);
            }
            types.add(type);
            propertyNameIndex.put(name, idx);
            idx++;
        }
    }

    public Property[] decodeValue(byte[] value) {
        return decodeValue(value, schemaVersion);
    }

    public Property[] decodeValue(byte[] value, long schemaVersion) {
        // decode by jni codec
        List<byte[]> decodedResult = NebulaCodec.decode(value, defs.toArray(new Pair[defs.size()]),
                schemaVersion);

        Property[] properties = new Property[defs.size()];
        try {
            for (int i = 0; i < defs.size(); i++) {
                String field = defs.get(i).getField();
                PropertyType type = types.get(i);
                byte[] data = decodedResult.get(i);
                switch (types.get(i)) {
                    case BOOL:
                        properties[i] = getBoolProperty(field, data);
                        break;
                    case INT:
                    case VID:
                        properties[i] = getIntProperty(field, data);
                        break;
                    case FLOAT:
                        properties[i] = getFloatProperty(field, data);
                        break;
                    case DOUBLE:
                        properties[i] = getDoubleProperty(field, data);
                        break;
                    case STRING:
                        properties[i] = getStringProperty(field, data);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid type in schema: " + type);
                }
            }
        } catch (BufferUnderflowException e) {
            LOGGER.error("Decode value failed: " + e.getMessage());
        }
        return properties;
    }

    public Property[] vertexKey(long vertexId, int tagId) {
        Property[] properties = new Property[2];
        properties[0] = new Property(PropertyType.VERTEX_ID, "_vertexId", vertexId);
        properties[1] = new Property(PropertyType.TAG_ID, "_tagId", tagId);
        return properties;
    }

    public Property[] edgeKey(long srcId, int edgeType, long dstId) {
        Property[] properties = new Property[3];
        properties[0] = new Property(PropertyType.SRC_ID, "_srcId", srcId);
        properties[1] = new Property(PropertyType.EDGE_TYPE, "_edgeType", edgeType);
        properties[2] = new Property(PropertyType.DST_ID, "_dstId", dstId);
        return properties;
    }

    // unused
    public Property[] decodeVertexKey(byte[] key) {
        Property[] properties = new Property[2];
        ByteBuffer buffer = ByteBuffer.wrap(key);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        try {
            buffer.getInt();
            long vertexId = buffer.getLong();
            int tagId = buffer.getInt();
            properties[0] = new Property(PropertyType.VERTEX_ID, "_vertexId", vertexId);
            properties[1] = new Property(PropertyType.TAG_ID, "_tagId", tagId);
            return properties;
        } catch (BufferUnderflowException e) {
            LOGGER.error("Decode key failed: " + e.getMessage());
        }
        return null;
    }

    // unused
    public Property[] decodeEdgeKey(byte[] key) {
        ByteBuffer buffer = ByteBuffer.wrap(key);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        try {
            Property[] properties = {
                new Property(PropertyType.SRC_ID, "_srcId", buffer.getLong()),
                new Property(PropertyType.EDGE_TYPE, "_edgeType", buffer.getInt()),
                new Property(PropertyType.EDGE_RANK, "_rank", buffer.getLong()),
                new Property(PropertyType.DST_ID, "_dstId", buffer.getLong()),
            };
            return properties;
        } catch (BufferUnderflowException e) {
            LOGGER.error("Decode key failed: " + e.getMessage());
        }
        return null;
    }

    public Property getProperty(Row row, String name) {
        if (!propertyNameIndex.containsKey(name)) {
            return null;
        }
        return row.getProperties()[propertyNameIndex.get(name)];
    }

    public Property getProperty(Row row, int index) {
        if (index < 0 || index >= row.getProperties().length) {
            return null;
        }
        return row.getProperties()[index];
    }

    private Property getBoolProperty(String name, byte[] data) {
        boolean value = data[0] != 0x00 ? true : false;
        return new Property(PropertyType.BOOL, name, value);
    }

    private Property getIntProperty(String name, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return new Property(PropertyType.INT, name, buffer.getLong());
    }

    private Property getFloatProperty(String name, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return new Property(PropertyType.FLOAT, name, buffer.getFloat());
    }

    private Property getDoubleProperty(String name, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return new Property(PropertyType.DOUBLE, name, buffer.getDouble());
    }

    private Property getStringProperty(String name, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return new Property(PropertyType.STRING, name, new String(buffer.array()));
    }
}
