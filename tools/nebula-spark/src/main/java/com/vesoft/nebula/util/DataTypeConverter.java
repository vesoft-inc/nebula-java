package com.vesoft.nebula.util;

import org.apache.spark.sql.types.DataType;

import static org.apache.spark.sql.types.DataTypes.*;

public class DataTypeConverter {
    public static DataType convertDataType(Class clazz) {
        if (Boolean.class.equals(clazz)) {
            return BooleanType;
        } else if (Long.class.equals(clazz)) {
            return LongType;
        } else if (Double.class.equals(clazz)) {
            return DoubleType;
        }
        return StringType;
    }
}
