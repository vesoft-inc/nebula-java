package org.apache.flink.connector.nebula.source;

import com.vesoft.nebula.data.Property;
import org.apache.flink.types.Row;

public class NebulaRowInputFormatConverter implements NebulaInputFormatConverter<Row> {
    @Override
    public Row convert(com.vesoft.nebula.data.Row row, boolean isVertex) {
        Property[] properties = row.getProperties();

        Row record;
        if(isVertex){
            record = new Row(properties.length + 1);
            record.setField(0, row.getDefaultProperties()[0]);
            for (int pos = 0; pos < row.getProperties().length; pos++) {
                record.setField(pos+1, properties[0]);
            }
        }else{
            record = new Row(properties.length + 2);
            record.setField(0, row.getDefaultProperties()[0]);
            record.setField(1, row.getDefaultProperties()[1]);
            for(int pos=0;pos<row.getProperties().length; pos++){
                record.setField(pos+2, properties[pos]);
            }
        }
        return record;
    }
}
