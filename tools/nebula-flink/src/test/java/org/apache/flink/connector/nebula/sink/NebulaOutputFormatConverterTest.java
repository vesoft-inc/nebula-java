/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.connector.nebula.statement.VertexExecutionOptions;
import org.apache.flink.connector.nebula.utils.PolicyEnum;
import org.apache.flink.types.Row;
import org.junit.Test;

public class NebulaOutputFormatConverterTest {
    @Test
    public void testCreateValue(){
        ExecutionOptions rowInfoConfig = new VertexExecutionOptions.ExecutionOptionBuilder().setIdIndex(0).builder();
        NebulaRowVertexOutputFormatConverter helper = new NebulaRowVertexOutputFormatConverter((VertexExecutionOptions) rowInfoConfig);

        Row row = new Row(3);
        row.setField(0, 111);
        row.setField(1, "jena");
        row.setField(2, 12);
        String value = helper.createValue(row, PolicyEnum.HASH);
        assert "HASH(\"111\"): (jena,12)".equals(value);
    }
}
