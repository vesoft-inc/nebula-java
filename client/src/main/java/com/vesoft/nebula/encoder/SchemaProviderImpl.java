/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.meta.PropertyType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaProviderImpl implements SchemaProvider {
    private final long ver;
    private final List<Field> fields = new ArrayList<>();
    private final Map<String, Integer> fieldNameIndex = new HashMap<>();
    private int numNullableFields = 0;

    static class SchemaField implements Field {
        private final String name;
        private final int type;
        private final boolean nullable;
        private final boolean hasDefault;
        private final byte[] defaultValue;
        private final int size;
        private final int offset;
        private final int nullFlagPos;

        public SchemaField(String name,
                           int type,
                           boolean nullable,
                           boolean hasDefault,
                           byte[] defaultValue,
                           int size,
                           int offset,
                           int nullFlagPos) {
            this.name = name;
            this.type = type;
            this.nullable = nullable;
            this.hasDefault = hasDefault;
            this.defaultValue = defaultValue;
            this.size = size;
            this.offset = offset;
            this.nullFlagPos = nullFlagPos;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int type() {
            return type;
        }

        @Override
        public boolean nullable() {
            return nullable;
        }

        @Override
        public boolean hasDefault() {
            return hasDefault;
        }

        @Override
        public byte[] defaultValue() {
            return defaultValue;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int offset() {
            return offset;
        }

        @Override
        public int nullFlagPos() {
            return nullFlagPos;
        }
    }

    public SchemaProviderImpl(long ver) {
        this.ver = ver;
    }

    @Override
    public long getVersion() {
        return ver;
    }

    @Override
    public int getNumFields() {
        return fields.size();
    }

    @Override
    public int getNumNullableFields() {
        return numNullableFields;
    }

    @Override
    public int size() {
        if (fields.size() > 0) {
            Field lastField = fields.get(fields.size() - 1);
            return lastField.offset() + lastField.size();
        }
        return 0;
    }

    @Override
    public int getFieldIndex(String name) {
        // Not found
        return fieldNameIndex.getOrDefault(name, -1);
    }

    @Override
    public String getFiledName(int index) {
        if (index < 0 || index >= fields.size()) {
            throw new RuntimeException(
                "Index[" + index + "] is out of range[0-" + fields.size() + "]");
        }
        return fields.get(index).name();
    }

    @Override
    public int getFiledType(int index) {
        if (index < 0 || index >= fields.size()) {
            throw new RuntimeException(
                "Index[" + index + "] is out of range[0-" + fields.size() + "]");
        }
        return fields.get(index).type();
    }

    @Override
    public int getFiledType(String name) {
        if (!fieldNameIndex.containsKey(name)) {
            // Not found
            return -1;
        } else {
            return fields.get(fieldNameIndex.get(name)).type();
        }
    }

    @Override
    public Field field(int index) {
        if (index < 0 || index >= fields.size()) {
            throw new RuntimeException("Invalid index " + index);
        }
        return fields.get(index);
    }

    @Override
    public Field field(String name) {
        if (!fieldNameIndex.containsKey(name)) {
            throw new RuntimeException("Unknown field \"" + name + "\"");
        }

        return fields.get(fieldNameIndex.get(name));
    }

    public void addField(String name,
                         int type,
                         int fixedStrLen,
                         boolean nullable,
                         byte[] defaultValue) {
        int size = fieldSize(type, fixedStrLen);

        int offset = 0;
        if (fields.size() > 0) {
            Field lastField = fields.get(fields.size() - 1);
            offset = lastField.offset() + lastField.size();
        }

        int nullFlagPos = 0;
        if (nullable) {
            nullFlagPos = numNullableFields++;
        }

        fields.add(new SchemaField(name,
            type,
            nullable,
            defaultValue != null,
            defaultValue,
            size,
            offset,
            nullFlagPos));
        fieldNameIndex.put(name, fields.size() - 1);
    }

    @Override
    public int fieldSize(int type, int fixedStrLimit) {
        switch (type) {
            case PropertyType.BOOL:
                return 1;
            case PropertyType.VID:
            case PropertyType.INT64:
            case PropertyType.TIMESTAMP:
                return Long.BYTES;
            case PropertyType.INT32:
                return Integer.BYTES;
            case PropertyType.INT16:
                return Short.BYTES;
            case PropertyType.INT8:
                return Byte.BYTES;
            case PropertyType.FLOAT:
                return Float.BYTES;
            case PropertyType.DOUBLE:
                return Double.BYTES;
            case PropertyType.STRING:
                return 8;  // string offset + string length
            case PropertyType.FIXED_STRING:
                if (fixedStrLimit < 0)  {
                    throw new RuntimeException("Fixed string length must be greater than zero");
                }
                return fixedStrLimit;
            case PropertyType.DATE:
                return Short.BYTES        // year
                      + Byte.BYTES        // month
                      + Byte.BYTES;       // day
            case PropertyType.TIME:
                return Byte.BYTES         // hour
                      + Byte.BYTES        // minute
                      + Byte.BYTES        // sec
                      + Integer.BYTES;    // microsec
            case PropertyType.DATETIME:
                return Short.BYTES          // year
                      + Byte.BYTES          // month
                      + Byte.BYTES          // day
                      + Byte.BYTES          // hour
                      + Byte.BYTES          // minute
                      + Byte.BYTES          // sec
                      + Integer.BYTES;      // microsec
            default:
                throw new RuntimeException(
                    "Incorrect field type " + PropertyType.VALUES_TO_NAMES.get(type));
        }
    }
}
