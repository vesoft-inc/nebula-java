/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.meta.ColumnDef;
import com.vesoft.nebula.meta.ColumnTypeDef;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.GeoShape;
import com.vesoft.nebula.meta.Schema;
import com.vesoft.nebula.meta.TagItem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * NebulaCodecImpl is an encoder to generate the given data. If the schema with default value, and
 * the filed without given data, it will throw error. TODO: Support default value
 */
public class NebulaCodecImpl implements NebulaCodec {
    private static final int PARTITION_ID_SIZE = 4;
    private static final int TAG_ID_SIZE = 4;
    private static final int EDGE_TYPE_SIZE = 4;
    private static final int EDGE_RANKING_SIZE = 8;
    private static final int EDGE_VER_PLACE_HOLDER_SIZE = 1;
    private static final int VERTEX_SIZE = PARTITION_ID_SIZE + TAG_ID_SIZE;
    private static final int EDGE_SIZE =
            PARTITION_ID_SIZE + EDGE_TYPE_SIZE + EDGE_RANKING_SIZE + EDGE_VER_PLACE_HOLDER_SIZE;

    private static final int VERTEX_KEY_TYPE = 0x00000001;
    private static final int EDGE_KEY_TYPE = 0x00000002;
    private static final int ORPHAN_VERTEX_KEY_TYPE = 0x00000007;
    private static final int SEEK = 0xc70f6907;
    private final ByteOrder byteOrder;

    public NebulaCodecImpl() {
        this.byteOrder = ByteOrder.nativeOrder();
    }

    /**
     * @param vidLen the vidLen from the space description
     * @param partitionId the partitionId
     * @param vertexId the vertex id
     * @param tagId the tag id
     * @return
     */
    @Override
    public byte[] vertexKey(int vidLen, int partitionId, byte[] vertexId, int tagId) {
        if (vertexId.length > vidLen) {
            throw new RuntimeException(
                    "The length of vid size is out of the range, expected vidLen less then "
                            + vidLen);
        }
        ByteBuffer buffer = ByteBuffer.allocate(VERTEX_SIZE + vidLen);
        buffer.order(this.byteOrder);
        partitionId = (partitionId << 8) | VERTEX_KEY_TYPE;
        buffer.putInt(partitionId).put(vertexId);
        if (vertexId.length < vidLen) {
            ByteBuffer complementVid = ByteBuffer.allocate(vidLen - vertexId.length);
            Arrays.fill(complementVid.array(), (byte) '\0');
            buffer.put(complementVid);
        }
        buffer.putInt(tagId);
        return buffer.array();
    }

    /**
     * @param vidLen the vidLen from the space description
     * @param partitionId the partitionId
     * @param vertexId the vertex id
     * @return
     */
    @Override
    public byte[] orphanVertexKey(int vidLen, int partitionId, byte[] vertexId) {
        if (vertexId.length > vidLen) {
            throw new RuntimeException(
                    "The length of vid size is out of the range, expected vidLen less then "
                            + vidLen);
        }
        ByteBuffer buffer = ByteBuffer.allocate(PARTITION_ID_SIZE + vidLen);
        buffer.order(this.byteOrder);
        partitionId = (partitionId << 8) | ORPHAN_VERTEX_KEY_TYPE;
        buffer.putInt(partitionId).put(vertexId);
        if (vertexId.length < vidLen) {
            ByteBuffer complementVid = ByteBuffer.allocate(vidLen - vertexId.length);
            Arrays.fill(complementVid.array(), (byte) '\0');
            buffer.put(complementVid);
        }
        return buffer.array();
    }

    /**
     * @param vidLen the vidLen from the space description
     * @param partitionId the partitionId
     * @param srcId the src id
     * @param edgeType the edge type
     * @param edgeRank the ranking
     * @param dstId the dstId
     * @return byte[]
     */
    @Override
    public byte[] edgeKeyByDefaultVer(
            int vidLen, int partitionId, byte[] srcId, int edgeType, long edgeRank, byte[] dstId) {
        return edgeKey(vidLen, partitionId, srcId, edgeType, edgeRank, dstId, (byte) 1);
    }

    /**
     * @param vidLen the vidLen from the space description
     * @param partitionId the partitionId
     * @param srcId the src id
     * @param edgeType the edge type
     * @param edgeRank the ranking
     * @param dstId the dstId
     * @param edgeVerHolder the edgeVerHolder
     * @return byte[]
     */
    @Override
    public byte[] edgeKey(
            int vidLen,
            int partitionId,
            byte[] srcId,
            int edgeType,
            long edgeRank,
            byte[] dstId,
            byte edgeVerHolder) {
        if (srcId.length > vidLen || dstId.length > vidLen) {
            throw new RuntimeException(
                    "The length of vid size is out of the range, expected vidLen less then "
                            + vidLen);
        }
        ByteBuffer buffer = ByteBuffer.allocate(EDGE_SIZE + (vidLen << 1));
        buffer.order(this.byteOrder);
        partitionId = (partitionId << 8) | EDGE_KEY_TYPE;
        buffer.putInt(partitionId);
        buffer.put(srcId);
        if (srcId.length < vidLen) {
            ByteBuffer complementVid = ByteBuffer.allocate(vidLen - srcId.length);
            Arrays.fill(complementVid.array(), (byte) '\0');
            buffer.put(complementVid);
        }
        buffer.putInt(edgeType);
        buffer.put(encodeRank(edgeRank));
        buffer.put(dstId);
        if (dstId.length < vidLen) {
            ByteBuffer complementVid = ByteBuffer.allocate(vidLen - dstId.length);
            Arrays.fill(complementVid.array(), (byte) '\0');
            buffer.put(complementVid);
        }
        buffer.put(edgeVerHolder);
        return buffer.array();
    }

    /**
     * @param tag the TagItem
     * @param names the property names
     * @param values the property values
     * @return the encode byte[]
     * @throws RuntimeException expection
     */
    @Override
    public byte[] encodeTag(TagItem tag, List<String> names, List<Object> values)
            throws RuntimeException {
        if (tag == null) {
            throw new RuntimeException("TagItem is null");
        }
        Schema schema = tag.getSchema();
        return encode(schema, tag.getVersion(), names, values);
    }

    /**
     * @param edge the EdgeItem
     * @param names the property names
     * @param values the property values
     * @return the encode byte[]
     * @throws RuntimeException expection
     */
    @Override
    public byte[] encodeEdge(EdgeItem edge, List<String> names, List<Object> values)
            throws RuntimeException {
        if (edge == null) {
            throw new RuntimeException("EdgeItem is null");
        }
        Schema schema = edge.getSchema();
        return encode(schema, edge.getVersion(), names, values);
    }

    /**
     * @param schema the schema
     * @param ver the version of tag or edge
     * @param names the property names
     * @param values the property values
     * @return the encode byte[]
     * @throws RuntimeException expection
     */
    private byte[] encode(Schema schema, long ver, List<String> names, List<Object> values)
            throws RuntimeException {
        if (names.size() != values.size()) {
            throw new RuntimeException(
                    String.format(
                            "The names' size no equal with values' size, [%d] != [%d]",
                            names.size(), values.size()));
        }
        RowWriterImpl writer = new RowWriterImpl(genSchemaProvider(ver, schema), this.byteOrder);
        for (int i = 0; i < names.size(); i++) {
            writer.setValue(names.get(i), values.get(i));
        }
        writer.finish();
        return writer.encodeStr();
    }

    private SchemaProviderImpl genSchemaProvider(long ver, Schema schema) {
        SchemaProviderImpl schemaProvider = new SchemaProviderImpl(ver);
        for (ColumnDef col : schema.getColumns()) {
            ColumnTypeDef type = col.getType();
            boolean nullable = col.isSetNullable() && col.isNullable();
            boolean hasDefault = col.isSetDefault_value();
            int len = type.isSetType_length() ? type.getType_length() : 0;
            GeoShape geoShape = type.isSetGeo_shape() ? type.getGeo_shape() : GeoShape.ANY;
            schemaProvider.addField(
                    new String(col.getName()),
                    type.type.getValue(),
                    len,
                    nullable,
                    hasDefault ? col.getDefault_value() : null,
                    geoShape.getValue());
        }
        return schemaProvider;
    }

    private byte[] encodeRank(long rank) {
        long newRank = rank ^ (1L << 63);
        ByteBuffer rankBuf = ByteBuffer.allocate(Long.BYTES);
        rankBuf.order(ByteOrder.BIG_ENDIAN);
        rankBuf.putLong(newRank);
        return rankBuf.array();
    }
}
