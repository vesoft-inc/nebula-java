/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.meta.PropertyType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RowWriterImpl implements RowWriter {
    private final SchemaProviderImpl schema;
    private final int headerLen;
    private int numNullBytes = 0;
    private boolean outOfSpaceStr = false;
    private long approxStrLen = 0;
    private ByteBuffer buf;
    private final List<Boolean> isSet;
    private final List<byte[]> strList = new ArrayList<>();
    private ByteOrder byteOrder;
    static int[] andBits = {0x7F, 0xBF, 0xDF, 0xEF, 0xF7, 0xFB, 0xFD, 0xFE};
    static int[] orBits = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};
    static int[] nullBits = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

    public RowWriterImpl(SchemaProviderImpl schema, ByteOrder byteOrder) throws RuntimeException {
        this.byteOrder = byteOrder;
        if (schema == null) {
            throw new RuntimeException("Null schema object");
        }
        this.schema = schema;
        byte header;
        long ver = schema.getVersion();
        int schemaVerSize = 0;
        if (ver > 0) {
            if (ver <= 0x00FF) {
                header = 0x09;  // 0x08 | 0x01, one byte for the schema version
                headerLen = 2;
                schemaVerSize = 1;
            } else if (ver < 0x00FFFF) {
                header = 0x0A;  // 0x08 | 0x02, two bytes for the schema version
                headerLen = 3;
                schemaVerSize = 2;
            } else if (ver < 0x00FFFFFF) {
                header = 0x0B;  // 0x08 | 0x03, three bytes for the schema version
                headerLen = 4;
                schemaVerSize = 3;
            } else if (ver < 0x00FFFFFFFF) {
                header = 0x0C;  // 0x08 | 0x04, four bytes for the schema version
                headerLen = 5;
                schemaVerSize = 4;
            } else if (ver < 0x00FFFFFFFFFFL) {
                header = 0x0D;  // 0x08 | 0x05, five bytes for the schema version
                headerLen = 6;
                schemaVerSize = 5;
            } else if (ver < 0x00FFFFFFFFFFFFL) {
                header = 0x0E;  // 0x08 | 0x06, six bytes for the schema version
                headerLen = 7;
                schemaVerSize = 6;
            } else if (ver < 0x00FFFFFFFFFFFFFFL) {
                header = 0x0F;  // 0x08 | 0x07, severn bytes for the schema version
                headerLen = 8;
                schemaVerSize = 7;
            } else {
                throw new RuntimeException("Schema version too big");
            }
        } else {
            header = 0x08;
            headerLen = 1;
        }

        // Null flags
        int numNullables = schema.getNumNullableFields();
        if (numNullables > 0) {
            numNullBytes = ((numNullables - 1) >> 3) + 1;
        }
        buf = ByteBuffer.allocate(headerLen + numNullBytes + schema.size() + Long.BYTES);
        buf.order(this.byteOrder);
        buf.put(header);
        if (ver > 0) {
            ByteBuffer longBuf = ByteBuffer.allocate(8);
            longBuf.order(this.byteOrder);
            longBuf.putLong(ver);
            buf.put(longBuf.array(), 0, schemaVerSize);
        }
        // Reserve the space for the data, including the Null bits
        // All variant length string will be appended to the end\
        // buf.position(headerLen + numNullBytes + schema.size());
        isSet = new ArrayList<>(Collections.nCopies(schema.getNumFields(), false));
    }

    public SchemaProvider schema() {
        return schema;
    }

    @Override
    public void write(int index, boolean v) throws RuntimeException {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case BOOL:
            case INT8: {
                if (v) {
                    buf.put(offset, (byte)0x01);
                } else  {
                    buf.put(offset, (byte)0);
                }
                break;
            }
            case INT16: {
                if (v) {
                    buf.put(offset, (byte)0x01);
                } else  {
                    buf.put(offset, (byte)0);
                }
                buf.put(offset + 1, (byte) 0);
                break;
            }
            case INT32: {
                if (v) {
                    buf.put(offset, (byte)0x01);
                } else  {
                    buf.put(offset, (byte)0);
                }
                buf.put(offset + 1, (byte) 0)
                     .put(offset + 2, (byte) 0)
                     .put(offset + 3, (byte) 0);
                break;
            }
            case TIMESTAMP:
            case INT64: {
                if (v) {
                    buf.put(offset, (byte)0x01);
                } else  {
                    buf.put(offset, (byte)0);
                }
                buf.put(offset + 1, (byte) 0)
                    .put(offset + 2, (byte) 0)
                    .put(offset + 3, (byte) 0)
                    .put(offset + 4, (byte) 0)
                    .put(offset + 5, (byte) 0)
                    .put(offset + 6, (byte) 0)
                    .put(offset + 7, (byte) 0);
                break;
            }
            default:
                throw new RuntimeException("Write boolean with unsupported type: " + field.type());
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, float v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case INT8: {
                if (v > 127 || v < 0x80) {
                    throw new RuntimeException(
                      "Value: " + v + " is out of range, the type is byte");
                }
                buf.put(offset, (byte)v);
                break;
            }
            case INT16: {
                if (v > 0x7FFF || v < 0x8000) {
                    throw new RuntimeException(
                      "Value: " + v + " is out of range, the type is short");
                }
                buf.putShort(offset, (short) v);
                break;
            }
            case INT32: {
                if (v > 0x7FFFFFFF || v < 0x80000000) {
                    throw new RuntimeException(
                      "Value: " + v + " is out of range, the type is int");
                }
                buf.putInt(offset, (int) v);
                break;
            }
            case TIMESTAMP:
            case INT64: {
                if (v > 0x7FFFFFFFFFFFFFFFL || v < 0x8000000000000000L) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is long");
                }
                buf.putLong(offset, (long) v);
                break;
            }
            case FLOAT: {
                buf.putFloat(offset, v);
                break;
            }
            case DOUBLE: {
                buf.putDouble(offset, v);
                break;
            }
            default:
                throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, double v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case INT8: {
                if (v > 127 || v < 0x80) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is byte");
                }
                buf.put(offset, (byte)v);
                break;
            }
            case INT16: {
                if (v > 0x7FFF || v < 0x8000) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is short");
                }
                buf.putShort(offset, (short) v);
                break;
            }
            case INT32: {
                if (v > 0x7FFFFFFF || v < 0x80000000) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is int");
                }
                buf.putInt(offset, (int) v);
                break;
            }
            case TIMESTAMP:
            case INT64: {
                if (v > 0x7FFFFFFFFFFFFFFFL || v < 0x8000000000000000L) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is long");
                }
                buf.putLong(offset, (long) v);
                break;
            }
            case FLOAT: {
                buf.putFloat(offset, (float)v);
                break;
            }
            case DOUBLE: {
                buf.putDouble(offset, v);
                break;
            }
            default:
                throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, byte v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case BOOL: {
                buf.put(offset, v == 0 ? (byte)0x00 : (byte)0x01);
                break;
            }
            case INT8: {
                buf.put(offset, v);
                break;
            }
            case INT16: {
                buf.putShort(offset, v);
                break;
            }
            case INT32: {
                buf.putInt(offset, v);
                break;
            }
            case TIMESTAMP:
            case INT64: {
                buf.putLong(offset, v);
                break;
            }
            case FLOAT: {
                buf.putFloat(offset, v);
                break;
            }
            case DOUBLE: {
                buf.putDouble(offset, v);
                break;
            }
            default:
                throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, short v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case BOOL: {
                buf.put(offset, v == 0 ? (byte)0x00 : (byte)0x01);
                break;
            }
            case INT8: {
                if (v > 0x7F || v < -0x7F) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is byte");
                }
                buf.put(offset, (byte)v);
                break;
            }
            case INT16: {
                buf.putShort(offset, v);
                break;
            }
            case INT32: {
                buf.putInt(offset, v);
                break;
            }
            case TIMESTAMP:
            case INT64: {
                buf.putLong(offset, v);
                break;
            }
            case FLOAT: {
                buf.putFloat(offset, v);
                break;
            }
            case DOUBLE: {
                buf.putDouble(offset, v);
                break;
            }
            default:
                throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, int v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case BOOL: {
                buf.put(offset, v == 0 ? (byte)0x00 : (byte)0x01);
                break;
            }
            case INT8: {
                if (v > 0x7F || v < -0x7F) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is byte");
                }
                buf.put(offset, (byte)v);
                break;
            }
            case INT16: {
                if (v > 0x7FFF || v < -0x7FFF) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is short");
                }
                buf.putShort(offset, (short) v);
                break;
            }
            case INT32: {
                buf.putInt(offset, v);
                break;
            }
            case TIMESTAMP:
            case INT64: {
                buf.putLong(offset, v);
                break;
            }
            case FLOAT: {
                buf.putFloat(offset, v);
                break;
            }
            case DOUBLE: {
                buf.putDouble(offset, v);
                break;
            }
            default:
                throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }

        isSet.set(index, true);
    }

    @Override
    public void write(int index, long v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case BOOL: {
                buf.put(offset, v == 0 ? (byte)0x00 : (byte)0x01);
                break;
            }
            case INT8: {
                if (v > 0x7F || v < -0x7F) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is byte");
                }
                buf.put(offset, (byte)v);
                break;
            }
            case INT16: {
                if (v > 0x7FFF || v < -0x7FFF) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is short");
                }
                buf.putShort(offset, (short) v);
                break;
            }
            case INT32: {
                if (v > 0x7FFFFFFF || v < -0x7FFFFFFF) {
                    throw new RuntimeException("Value: " + v + " is out of range, the type is int");
                }
                buf.putInt(offset, (int)v);
                break;
            }
            case INT64: {
                buf.putLong(offset, v);
                break;
            }
            case TIMESTAMP: {
                if (v < 0 || v >  Long.MAX_VALUE / 1000000000) {
                    throw new RuntimeException(
                        "Value: " + v + " is out of range, the type is timestamp");
                }
                buf.putLong(offset, v);
                break;
            }
            case FLOAT: {
                buf.putFloat(offset, v);
                break;
            }
            case DOUBLE: {
                buf.putDouble(offset, v);
                break;
            }
            default:
                throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, byte[] v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case STRING: {
                strList.add(v);
                outOfSpaceStr = true;
                approxStrLen += v.length;
                break;
            }
            case FIXED_STRING: {
                // In-place string. If the pass-in string is longer than the pre-defined
                // fixed length, the string will be truncated to the fixed length
                int len = Math.min(v.length, field.size());
                for (int i = 0; i < len; i++) {
                    buf.put(offset + i, v[i]);
                }
                if (len < field.size()) {
                    byte[] fixStr = new byte[field.size() - len];
                    for (byte b : fixStr) {
                        buf.put(offset + len, b);
                    }
                }
                break;
            }
            default:
                throw new RuntimeException("Value: " + new String(v) + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, Time v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        if (typeEnum == PropertyType.TIME) {
            buf.put(offset, v.hour)
                 .put(offset + Byte.BYTES, v.minute)
                 .put(offset  + 2 * Byte.BYTES, v.sec)
                 .putInt(offset + 3 * Byte.BYTES, v.microsec);
        } else {
            throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, Date v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case DATE:
                buf.putShort(offset, v.year)
                    .put(offset + Short.BYTES, v.month)
                    .put(offset + Short.BYTES + Byte.BYTES, v.day);
                break;
            case DATETIME:
                buf.putShort(offset, v.year)
                    .put(offset + Short.BYTES, v.month)
                    .put(offset + Short.BYTES + Byte.BYTES, v.day)
                    .put(offset + Short.BYTES + 2 * Byte.BYTES, (byte) 0)
                    .put(offset + Short.BYTES + 3 * Byte.BYTES, (byte) 0)
                    .put(offset + Short.BYTES + 4 * Byte.BYTES, (byte) 0)
                    .putInt(offset + Short.BYTES + 5 * Byte.BYTES, 0);
                break;
            default:
                throw new RuntimeException("Value: " + v + "'s type is unexpected");
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public void write(int index, DateTime v) {
        SchemaProvider.Field field = schema.field(index);
        PropertyType typeEnum = PropertyType.findByValue(field.type());
        if (typeEnum == null) {
            throw new RuntimeException("Incorrect field type " + field.type());
        }
        int offset = headerLen + numNullBytes + field.offset();
        switch (typeEnum) {
            case DATE:
                buf.putShort(offset, v.year)
                    .put(offset + Short.BYTES, v.month)
                    .put(offset + Short.BYTES + Byte.BYTES, v.day);
                break;
            case DATETIME:
                buf.putShort(offset, v.year)
                    .put(offset + Short.BYTES, v.month)
                    .put(offset + Short.BYTES + Byte.BYTES, v.day)
                    .put(offset + Short.BYTES + 2 * Byte.BYTES, v.hour)
                    .put(offset + Short.BYTES + 3 * Byte.BYTES, v.minute)
                    .put(offset + Short.BYTES + 4 * Byte.BYTES, v.sec)
                    .putInt(offset + Short.BYTES + 5 * Byte.BYTES, v.microsec);
                break;
            default:
                throw new RuntimeException();
        }
        if (field.nullable()) {
            clearNullBit(field.nullFlagPos());
        }
        isSet.set(index, true);
    }

    @Override
    public byte[] encodeStr() {
        return buf.array();
    }

    public void setValue(String name, Object value) {
        int index = schema.getFieldIndex(name);
        if (index < 0) {
            throw new RuntimeException("Prop name `" + name + "' is not exist");
        }
        if (value == null) {
            throw new RuntimeException("Prop value `" + name + "' is null object");
        }
        setValue(index, value);
    }

    public void setValue(int index, Object value) {
        if (value instanceof Value) {
            setValue(index, (Value) value);
        } else if (value instanceof Boolean) {
            write(index, (boolean) value);
        } else if (value instanceof Byte) {
            write(index, (byte) value);
        } else if (value instanceof Short) {
            write(index, (short) value);
        } else if (value instanceof Integer) {
            write(index, (int)value);
        } else if (value instanceof Long) {
            write(index, (long)value);
        } else if (value instanceof String) {
            write(index, ((String) value).getBytes());
        } else if (value instanceof Float) {
            write(index, (float)value);
        } else if (value instanceof Double) {
            write(index, (double)value);
        } else if (value instanceof Time) {
            write(index, (Time)value);
        } else if (value instanceof Date) {
            write(index, (Date)value);
        } else if (value instanceof DateTime) {
            write(index, (DateTime)value);
        } else {
            throw new RuntimeException("Unsupported value object `" + value.getClass() + "\"");
        }
    }

    public void setValue(String name, Value value) {
        int index = schema.getFieldIndex(name);
        if (index < 0) {
            throw new RuntimeException("Prop name `" + name + "' is not exist");
        }
        if (value == null) {
            throw new RuntimeException("Prop value `" + name + "' is null object");
        }
        setValue(index, value);
    }

    public void setValue(int index, Value value) {
        switch (value.getSetField()) {
            case Value.NVAL:
                setNull(index);
                break;
            case Value.BVAL:
                write(index, value.isBVal());
                break;
            case Value.IVAL:
                write(index, value.getIVal());
                break;
            case Value.FVAL:
                write(index, value.getFVal());
                break;
            case Value.SVAL:
                write(index, value.getSVal());
                break;
            case Value.TVAL:
                write(index, value.getTVal());
                break;
            case Value.DVAL:
                write(index, value.getDVal());
                break;
            case Value.DTVAL:
                write(index, value.getDtVal());
                break;
            default:
                throw new RuntimeException(
                  "Unknown value: " + value.getFieldValue().getClass()
                    + "from index `" + index + "\"");
        }
    }

    public void setNull(String name) {
        int index = schema.getFieldIndex(name);
        setNull(index);
    }

    public void setNull(int index) {
        if (index < 0 || index >= schema.getNumFields()) {
            throw new RuntimeException("Unknown filed from index `" + index + "\"");
        }

        // Make sure the field is nullable
        SchemaProvider.Field field = schema.field(index);
        if (!field.nullable()) {
            throw new RuntimeException("Index `" + index + "\" is not nullable");
        }

        setNullBit(field.nullFlagPos());
        isSet.set(index, true);
    }

    private void setNullBit(int pos) {
        int offset = headerLen + (pos >> 3);
        buf.put(offset,
            (byte)((int)buf.get(offset) | orBits[(int)(pos & 0x0000000000000007L)]));
    }

    private boolean checkNullBit(int pos)  {
        int offset = headerLen + (pos >> 3);
        int flag = buf.get(offset) & nullBits[(int)(pos & 0x0000000000000007L)];
        return flag != 0;
    }

    private void clearNullBit(int pos) {
        int offset = headerLen + (pos >> 3);
        buf.put(offset, (byte)(buf.get(offset) & andBits[(int) (pos & 0x00000007L)]));
    }

    public void checkUnsetFields() {
        for (int i = 0; i < schema.getNumFields(); i++) {
            if (!isSet.get(i)) {
                SchemaProvider.Field field = schema.field(i);
                if (!field.nullable() && !field.hasDefault()) {
                    // The field neither can be NULL, nor has a default value
                    throw new RuntimeException("Filed: " + field.name() + " unset");
                }

                if (field.hasDefault()) {
                    throw new RuntimeException("Unsupported default value yet");
                    // TODO: Implement default from expr
                    // byte[] defValEcpr = field.defaultValue();
                    // switch (defVal.getSetField()) {
                    //     case Value.NVAL:
                    //         setNullBit(field.nullFlagPos());
                    //         break;
                    //     case Value.BVAL:
                    //         write(i, defVal.isBVal());
                    //         break;
                    //     case Value.IVAL:
                    //         write(i, defVal.getIVal());
                    //         break;
                    //     case Value.FVAL:
                    //         write(i, defVal.getFVal());
                    //         break;
                    //     case Value.SVAL:
                    //         write(i, defVal.getSVal());
                    //         break;
                    //     case Value.TVAL:
                    //         write(i, defVal.getTVal());
                    //         break;
                    //     case Value.DVAL:
                    //         write(i, defVal.getDVal());
                    //         break;
                    //     case Value.DTVAL:
                    //         write(i, defVal.getDtVal());
                    //         break;
                    //     default:
                    //         throw new RuntimeException("Unsupported default value type");
                    // }
                } else {
                    // Set NULL
                    setNullBit(field.nullFlagPos());
                }
            }
        }
    }

    public ByteBuffer processOutOfSpace() {
        ByteBuffer temp;
        temp = ByteBuffer.allocate((int) (headerLen
            + numNullBytes + schema.size() + approxStrLen + Long.BYTES));
        temp.order(this.byteOrder);

        // Reserve enough space to avoid memory re-allocation
        // Copy the data except the strings
        temp = temp.put(buf.array(), 0, buf.array().length - Long.BYTES);

        int strOffset = buf.array().length - Long.BYTES;

        // Now let's process all strings
        int strNum = 0;
        for (int i = 0;  i < schema.getNumFields(); i++) {
            SchemaProvider.Field field = schema.field(i);
            PropertyType typeEnum = PropertyType.findByValue(field.type());
            if (typeEnum == null) {
                throw new RuntimeException("Incorrect field type " + field.type());
            }
            if (typeEnum != PropertyType.STRING) {
                continue;
            }
            int offset = headerLen + numNullBytes + field.offset();
            if (field.nullable() && checkNullBit(field.nullFlagPos())) {
                // Null string
                // Set the new offset and length
                temp.putInt(offset, 0);
                temp.putInt(offset + Integer.BYTES, 0);
                continue;
            } else {
                // Out of space string
                if (strNum >= strList.size()) {
                    throw new RuntimeException("Wrong strNum: " + strNum);
                }
                temp.put(strList.get(strNum));

                // Set the new offset and length
                temp.putInt(offset, strOffset);
                int len = strList.get(strNum).length;
                temp.putInt(offset + Integer.BYTES, len);
                strOffset += len;
            }
            strNum++;
        }
        return temp;
    }

    public void finish() {
        // First to check whether all fields are set. If not, to check whether
        // it can be NULL or there is a default value for the field
        checkUnsetFields();

        // Next to process out-of-space strings
        if (outOfSpaceStr) {
            buf = processOutOfSpace();
        }
        // Save the timestamp to the tail of buf
        buf.putLong(buf.array().length - Long.BYTES, getTimestamp());
    }

    private long getTimestamp() {
        long curTime = System.currentTimeMillis() * 1000;
        long nanoTime = System.nanoTime();
        return curTime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
    }
}
