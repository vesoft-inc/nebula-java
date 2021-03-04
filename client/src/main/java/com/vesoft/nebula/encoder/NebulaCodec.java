/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import java.util.List;

public interface NebulaCodec {
    byte[] vertexKey(String spaceName,
                     String vertexId,
                     String tagName);

    byte[] edgeKey(String spaceName,
                   String srcId,
                   String edgeName,
                   long edgeRank,
                   String dstId);

    byte[] inboundEdgeKey(String spaceName,
                   String srcId,
                   String edgeName,
                   long edgeRank,
                   String dstId);

    byte[] encodeTag(String spaceName,
                     String tagName,
                     List<String> names,
                     List<Object> values);

    byte[] encodeEdge(String spaceName,
                      String edgeName,
                      List<String> names,
                      List<Object> values);
}
