package org.apache.flink.connector.nebula.writer;

import junit.framework.TestCase;
import org.apache.flink.connector.nebula.connection.NebulaClientOptions;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.connection.NebulaGraphConnectionProvider;
import org.apache.flink.connector.nebula.sink.AbstractNebulaOutPutFormat;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.types.Row;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbstractNebulaOutPutFormatTest extends TestCase {
    @Test
    public void testWrite() throws IOException {
        List<String> cols = new ArrayList<>();
        cols.add("name");
        cols.add("age");
        ExecutionOptions executionOptions = new ExecutionOptions.ExecutionOptionBuilder().setDataType("VERTEX")
                .setGraphSpace("flinkSink")
                .setLabel("player")
                .setFields(cols)
                .setIdIndex(0)
                .setBatch(1)
                .builder();

        NebulaClientOptions clientOptions = new NebulaClientOptions
                .NebulaClientOptionsBuilder()
                .setAddress("192.168.8.171:3699")
                .build();
        NebulaConnectionProvider connectionProvider = new NebulaGraphConnectionProvider(clientOptions);

        Row row = new Row(3);
        row.setField(0, 111);
        row.setField(1, "jena");
        row.setField(2, 12);

        AbstractNebulaOutPutFormat outPutFormat = new AbstractNebulaOutPutFormat(connectionProvider)
                .setExecutionOptions(executionOptions);

        outPutFormat.open(1, 2);
        outPutFormat.writeRecord(row);
    }

}