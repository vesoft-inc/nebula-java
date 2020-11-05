package org.apache.flink;

import org.apache.flink.connector.nebula.sink.NebulaRowOutputFormatConverter;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.connector.nebula.utils.PolicyEnum;
import org.apache.flink.types.Row;
import org.junit.Test;

public class NebulaReadWriteHelperTest {
    @Test
    public void testCreateValue(){
        ExecutionOptions rowInfoConfig = new ExecutionOptions.ExecutionOptionBuilder().setIdIndex(0).builder();
        NebulaRowOutputFormatConverter helper = new NebulaRowOutputFormatConverter(rowInfoConfig);

        Row row = new Row(3);
        row.setField(0, 111);
        row.setField(1, "jena");
        row.setField(2, 12);
        String value = helper.createValue(row, true, PolicyEnum.HASH);
        System.out.println(value);
    }
}
