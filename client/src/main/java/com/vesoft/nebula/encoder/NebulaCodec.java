/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.List;

public interface NebulaCodec {
    byte[] vertexKey(int vidLen,
                     int partitionId,
                     byte[] vertexId,
                     int tagId);

    byte[] orphanVertexKey(int vidLen,
                          int partitionId,
                          byte[] vertexId);

    byte[] edgeKey(int vidLen,
                   int partitionId,
                   byte[] srcId,
                   int edgeType,
                   long edgeRank,
                   byte[] dstId,
                   byte edgeVerHolder);

    byte[] edgeKeyByDefaultVer(int vidLen,
                               int partitionId,
                               byte[] srcId,
                               int edgeType,
                               long edgeRank,
                               byte[] dstId);

    byte[] encodeTag(TagItem tag,
                     List<String> names,
                     List<Object> values);

    byte[] encodeEdge(EdgeItem edge,
                      List<String> names,
                      List<Object> values);
}
