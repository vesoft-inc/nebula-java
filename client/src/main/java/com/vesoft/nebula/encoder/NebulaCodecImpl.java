/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.meta.MetaCache;
import com.vesoft.nebula.meta.ColumnDef;
import com.vesoft.nebula.meta.ColumnTypeDef;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.PropertyType;
import com.vesoft.nebula.meta.Schema;
import com.vesoft.nebula.meta.SpaceItem;
import com.vesoft.nebula.meta.TagItem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.MurmurHash2;

/**
 *  NebulaCodecImpl is an encoder to generate the given data.
 *  If the schema with default value, and the filed without given data, it will throw error.
 *  TODO: Support default value
 */
public class NebulaCodecImpl implements NebulaCodec {
    private static final int PARTITION_ID_SIZE = 4;
    private static final int TAG_ID_SIZE = 4;
    private static final int TAG_VERSION_SIZE = 8;
    private static final int EDGE_TYPE_SIZE = 4;
    private static final int EDGE_RANKING_SIZE = 8;
    private static final int EDGE_VERSION_SIZE = 8;
    private static final int VERTEX_SIZE = PARTITION_ID_SIZE + TAG_ID_SIZE + TAG_VERSION_SIZE;
    private static final int EDGE_SIZE = PARTITION_ID_SIZE + EDGE_TYPE_SIZE
        + EDGE_RANKING_SIZE + EDGE_VERSION_SIZE;

    private static final int DATA_KEY_TYPE = 0x00000001;
    private static final int TAG_MASK      = 0xBFFFFFFF;
    private static final int EDGE_MASK     = 0x40000000;
    private static final int SEEK          = 0xc70f6907;
    private final MetaCache metaCache;
    private final ByteOrder byteOrder;

    public NebulaCodecImpl(MetaCache metaCache) {
        this.byteOrder = ByteOrder.nativeOrder();
        this.metaCache = metaCache;
    }

    private int getSpaceVidLen(String spaceName) throws RuntimeException {
        SpaceItem spaceItem = metaCache.getSpace(spaceName);
        if (spaceItem == null) {
            throw new RuntimeException("SpaceName: " + spaceName + "is not existed");
        }
        if (spaceItem.properties.vid_type.type != PropertyType.FIXED_STRING) {
            throw new RuntimeException("Only supported fixed string vid type.");
        }
        return spaceItem.properties.vid_type.type_length;
    }

    private int getPartSize(String spaceName) throws RuntimeException {
        Map<Integer, List<HostAddr>> partsAlloc = metaCache.getPartsAlloc(spaceName);
        if (partsAlloc == null) {
            throw new RuntimeException("SpaceName: " + spaceName + " is not existed");
        }
        return partsAlloc.size();
    }

    @Override
    public byte[] vertexKey(String spaceName, String vertexId, String tagName)
        throws RuntimeException {
        int vidLen = getSpaceVidLen(spaceName);
        long hash = MurmurHash2.hash64(vertexId.getBytes(), vertexId.length(), SEEK);
        int partitionId = (int) (hash % getPartSize(spaceName) + 1);
        TagItem tagItem = metaCache.getTag(spaceName, tagName);
        return genVertexKey(vidLen, partitionId,
            vertexId.getBytes(), tagItem.tag_id, tagItem.version);
    }

    /**
     * @param spaceName the space name
     * @param srcId the src id
     * @param edgeName the edge name
     * @param edgeRank the ranking
     * @param dstId the dst id
     * @return
     */
    @Override
    public byte[] edgeKey(String spaceName,
                          String srcId,
                          String edgeName,
                          long edgeRank,
                          String dstId)
        throws RuntimeException {
        int vidLen = getSpaceVidLen(spaceName);
        long hash = MurmurHash2.hash64(srcId.getBytes(), srcId.length(), SEEK);
        int partitionId = (int) (hash % getPartSize(spaceName) + 1);
        EdgeItem edgeItem = metaCache.getEdge(spaceName, edgeName);
        return genEdgeKey(vidLen,partitionId, srcId.getBytes(),
            edgeItem.edge_type, edgeRank, dstId.getBytes(), edgeItem.version);
    }

    /**
     * @param vidLen the vidLen from the space description
     * @param partitionId the partitionId
     * @param vertexId the vertex id
     * @param tagId the tag id
     * @param tagVersion the version
     * @return
     */
    public byte[] genVertexKey(int vidLen,
                               int partitionId,
                               byte[] vertexId,
                               int tagId,
                               long tagVersion) {
        if (vertexId.length > vidLen) {
            throw new RuntimeException(
                "The length of vid size is out of the range, expected vidLen less then " + vidLen);
        }
        ByteBuffer buffer = ByteBuffer.allocate(VERTEX_SIZE + vidLen);
        buffer.order(this.byteOrder);
        partitionId = (partitionId << 8) | DATA_KEY_TYPE;
        tagId &= TAG_MASK;
        buffer.putInt(partitionId)
            .put(vertexId);
        if (vertexId.length < vidLen) {
            ByteBuffer complementVid = ByteBuffer.allocate(vidLen - vertexId.length);
            Arrays.fill(complementVid.array(), (byte) '\0');
            buffer.put(complementVid);
        }
        buffer.putInt(tagId);
        buffer.putLong(tagVersion);
        return buffer.array();
    }

    /**
     * @param vidLen the vidLen from the space description
     * @param partitionId the partitionId
     * @param srcId the src id
     * @param edgeType the edge type
     * @param edgeRank the ranking
     * @param dstId the dstId
     * @param edgeVersion the edgeVersion
     * @return
     */
    public byte[] genEdgeKey(int vidLen,
                             int partitionId,
                             byte[] srcId,
                             int edgeType,
                             long edgeRank,
                             byte[] dstId,
                             long edgeVersion) {
        if (srcId.length > vidLen || dstId.length > vidLen) {
            throw new RuntimeException(
                "The length of vid size is out of the range, expected vidLen less then " + vidLen);
        }
        ByteBuffer buffer = ByteBuffer.allocate(EDGE_SIZE + (vidLen << 1));
        buffer.order(this.byteOrder);
        partitionId = (partitionId << 8) | DATA_KEY_TYPE;
        buffer.putInt(partitionId);
        buffer.put(srcId);
        if (srcId.length < vidLen) {
            ByteBuffer complementVid = ByteBuffer.allocate(vidLen - srcId.length);
            Arrays.fill(complementVid.array(), (byte) '\0');
            buffer.put(complementVid);
        }
        edgeType |= EDGE_MASK;
        buffer.putInt(edgeType);
        buffer.putLong(edgeRank);
        buffer.put(dstId);
        if (dstId.length < vidLen) {
            ByteBuffer complementVid = ByteBuffer.allocate(vidLen - dstId.length);
            Arrays.fill(complementVid.array(), (byte) '\0');
            buffer.put(complementVid);
        }
        buffer.putLong(edgeVersion);
        return buffer.array();
    }

    public SchemaProviderImpl genSchemaProvider(long ver, Schema schema) {
        SchemaProviderImpl schemaProvider = new SchemaProviderImpl(ver);
        for (ColumnDef col : schema.getColumns()) {
            ColumnTypeDef type = col.getType();
            boolean nullable = col.isSetNullable();
            boolean hasDefault = col.isSetDefault_value();
            int len = type.isSetType_length() ? type.getType_length() : 0;
            schemaProvider.addField(new String(col.getName()),
                                    type.type,
                                    len,
                                    nullable,
                                    hasDefault ? null : null);
        }
        return schemaProvider;
    }

    /**
     * @param spaceName the space name
     * @param schemaName the tag name or edge name
     * @param names the property names
     * @param values the property values
     * @return the encode byte[]
     * @throws RuntimeException expection
     */
    @Override
    public byte[] encode(String spaceName,
                         String schemaName,
                         List<String> names,
                         List<Object> values)
        throws RuntimeException {
        if (names == null || values == null || names.size() != values.size()) {
            throw new RuntimeException("NebulaCodeImpl input wrong value");
        }
        Schema schema;
        long ver;
        try {
            TagItem tag = metaCache.getTag(spaceName, schemaName);
            schema = tag.getSchema();
            ver = tag.getVersion();
        } catch (IllegalArgumentException e) {
            EdgeItem edge;
            try {
                edge = metaCache.getEdge(spaceName, schemaName);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(String.format(
                        "schemaName %s does not exist in space %s.", schemaName, spaceName));
            }
            schema = edge.getSchema();
            ver = edge.getVersion();
        }

        RowWriterImpl writer = new RowWriterImpl(genSchemaProvider(ver, schema), this.byteOrder);
        for (int i = 0; i < names.size(); i++) {
            writer.setValue(names.get(i), values.get(i));
        }
        writer.finish();
        return writer.encodeStr();
    }
}
