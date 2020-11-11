/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import junit.framework.TestCase;
import org.apache.flink.connector.nebula.connection.NebulaClientOptions;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.connection.NebulaGraphConnectionProvider;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.connector.nebula.statement.VertexExecutionOptions;
import org.apache.flink.types.Row;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AbstractNebulaOutPutFormatTest extends TestCase {
    @Test
    public void testWrite() throws IOException {
        List<String> cols = Arrays.asList("name", "age");
        ExecutionOptions executionOptions = new VertexExecutionOptions.ExecutionOptionBuilder()
                .setGraphSpace("flinkSink")
                .setTag("player")
                .setFields(cols)
                .setIdIndex(0)
                .setBatch(1)
                .builder();

        NebulaClientOptions clientOptions = new NebulaClientOptions
                .NebulaClientOptionsBuilder()
                .setAddress("127.0.0.1:3699")
                .build();
        NebulaConnectionProvider connectionProvider = new NebulaGraphConnectionProvider(clientOptions);

        Row row = new Row(3);
        row.setField(0, 111);
        row.setField(1, "jena");
        row.setField(2, 12);

        AbstractNebulaOutPutFormat outPutFormat = new NebulaBatchOutputFormat(connectionProvider)
                .setExecutionOptions(executionOptions);

        outPutFormat.open(1, 2);
        outPutFormat.writeRecord(row);
    }

}