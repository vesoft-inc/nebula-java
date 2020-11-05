package org.apache.flink.connector.nebula.sink;

import org.apache.flink.connector.nebula.utils.PolicyEnum;

import java.io.Serializable;

public interface NebulaOutputFormatConverter<T> extends Serializable {

    String createValue(T record, Boolean isVertex, PolicyEnum policy);
}
