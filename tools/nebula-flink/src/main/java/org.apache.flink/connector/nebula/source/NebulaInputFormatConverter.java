package org.apache.flink.connector.nebula.source;

import com.vesoft.nebula.data.Row;

public interface NebulaInputFormatConverter<T> {

    public T convert(Row record, boolean isVertex);
}
