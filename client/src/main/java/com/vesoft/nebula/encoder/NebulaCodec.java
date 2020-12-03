/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import java.util.List;

public interface NebulaCodec {
    byte[] vertexKey(int vidLen,
                     int partitionId,
                     byte[] vertexId,
                     int tagId,
                     long tagVersion);

    byte[] edgeKey(int vidLen,
                   int partitionId,
                   byte[] srcId,
                   int edgeType,
                   long edgeRank,
                   byte[] dstId,
                   long edgeVersion);

    byte[] encode(String spaceName,
                  String schemaName,
                  List<String> names,
                  List<Object> values);
}
