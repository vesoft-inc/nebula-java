/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import org.apache.flink.connector.nebula.utils.PolicyEnum;

import java.io.Serializable;

public interface NebulaOutputFormatConverter<T> extends Serializable {

    String createValue(T record, Boolean isVertex, PolicyEnum policy);
}
