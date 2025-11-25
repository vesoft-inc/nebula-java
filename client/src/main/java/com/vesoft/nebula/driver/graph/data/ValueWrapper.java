/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_BOOL;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_DATE;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_DECIMAL;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_DURATION;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_EDGE;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_EMBEDDINGVECTOR;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_FLOAT32;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_FLOAT64;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_GEOGRAPHY;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_INT16;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_INT32;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_INT64;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_INT8;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_LIST;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_LOCALDATETIME;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_LOCALTIME;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_NODE;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_PATH;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_RECORD;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_STRING;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_UINT16;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_UINT32;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_UINT64;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_UINT8;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_ZONEDDATETIME;
import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_ZONEDTIME;

import com.vesoft.nebula.driver.graph.decode.ColumnType;
import com.vesoft.nebula.driver.graph.exception.InvalidValueException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class ValueWrapper {

    private final Object     value;
    private final ColumnType type;

    DateTimeFormatter zonedDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXXXX");
    DateTimeFormatter localDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    DateTimeFormatter zonedTimeFormatter     = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSXXXXX");
    DateTimeFormatter localTimeFormatter     = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS");

    public ValueWrapper(Object value, ColumnType type) {
        this.value = value;
        this.type = type;
    }

    public ColumnType getDataType() {
        return type;
    }

    public String getDataTypeString() {
        switch (type) {
            case COLUMN_TYPE_BOOL:
                return "BOOLEAN";
            case COLUMN_TYPE_UINT8:
                return "UINT8";
            case COLUMN_TYPE_INT8:
                return "INT8";
            case COLUMN_TYPE_UINT16:
                return "UINT16";
            case COLUMN_TYPE_INT16:
                return "INT16";
            case COLUMN_TYPE_UINT32:
                return "UINT32";
            case COLUMN_TYPE_INT32:
                return "INT32";
            case COLUMN_TYPE_UINT64:
                return "UINT64";
            case COLUMN_TYPE_INT64:
                return "INT64";
            case COLUMN_TYPE_FLOAT32:
                return "FLOAT";
            case COLUMN_TYPE_FLOAT64:
                return "DOUBLE";
            case COLUMN_TYPE_STRING:
                return "STRING";
            case COLUMN_TYPE_NODE:
                return "NODE";
            case COLUMN_TYPE_EDGE:
                return "EDGE";
            case COLUMN_TYPE_LIST:
                return "LIST";
            case COLUMN_TYPE_EMBEDDINGVECTOR:
                return "VECTOR";
            case COLUMN_TYPE_DURATION:
                return "DURATION";
            case COLUMN_TYPE_LOCALTIME:
                return "LOCAL_TIME";
            case COLUMN_TYPE_LOCALDATETIME:
                return "LOCAL_DATETIME";
            case COLUMN_TYPE_ZONEDTIME:
                return "ZONED_TIME";
            case COLUMN_TYPE_ZONEDDATETIME:
                return "ZONED_DATETIME";
            case COLUMN_TYPE_DATE:
                return "DATE";
            case COLUMN_TYPE_RECORD:
                return "RECORD";
            case COLUMN_TYPE_PATH:
                return "PATH";
            case COLUMN_TYPE_DECIMAL:
                return "DECIMAL";
            case COLUMN_TYPE_ANY:
                return "ANY";
            case COLUMN_TYPE_GEOGRAPHY:
                return "GEOGRAPHY";
            default:
                throw new IllegalArgumentException("Unknown data type: " + type);
        }
    }


    /**
     * get the Object value
     *
     * @return Object
     */
    public Object getValue() {
        return value;
    }

    /**
     * if the value is null
     *
     * @return true if value is null
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * check if the Value is Boolean type
     *
     * @return true if Value's type is COLUMN_TYPE_BOOL
     */
    public boolean isBoolean() {
        return type == COLUMN_TYPE_BOOL;
    }

    /**
     * check if the Value is Long type
     *
     * @return true if Value's type is COLUMN_TYPE_UINT64 or COLUMN_TYPE_INT64
     */
    public boolean isLong() {
        return type == COLUMN_TYPE_UINT64 || type == COLUMN_TYPE_INT64;
    }

    /**
     * check if the Value is Int type
     *
     * @return true if Value's type is COLUMN_TYPE_UINT8 or COLUMN_TYPE_INT8 or COLUMN_TYPE_UINT16
     *     or COLUMN_TYPE_INT16 or COLUMN_TYPE_UINT32 or COLUMN_TYPE_INT32
     */
    public boolean isInt() {
        return type == COLUMN_TYPE_UINT8 || type == COLUMN_TYPE_INT8
            || type == COLUMN_TYPE_UINT16 || type == COLUMN_TYPE_INT16
            || type == COLUMN_TYPE_UINT32 || type == COLUMN_TYPE_INT32;
    }

    /**
     * check if the Value is Float type
     *
     * @return true if Value's type is COLUMN_TYPE_FLOAT32
     */
    public boolean isFloat() {
        return type == COLUMN_TYPE_FLOAT32;
    }


    /**
     * check if the Value is Double type
     *
     * @return true if the type is COLUMN_TYPE_FLOAT64
     */
    public boolean isDouble() {
        return type == COLUMN_TYPE_FLOAT64;
    }

    /**
     * check if the Value is String type
     *
     * @return true if Value's type is COLUMN_TYPE_STRING
     */
    public boolean isString() {
        return type == COLUMN_TYPE_STRING;
    }

    /**
     * check if the Value is List type
     *
     * @return true if Value's type is COLUMN_TYPE_LIST
     */
    public boolean isList() {
        return type == COLUMN_TYPE_LIST;
    }

    /**
     * check if the Value is Vector type
     *
     * @return true if Value's type is COLUMN_TYPE_EMBEDDINGVECTOR
     */
    public boolean isVector() {
        return type == COLUMN_TYPE_EMBEDDINGVECTOR;
    }

    /**
     * check if the Value is Node type
     *
     * @return true if Value's type is COLUMN_TYPE_NODE
     */
    public boolean isNode() {
        return type == COLUMN_TYPE_NODE;
    }

    /**
     * check if the Value is Edge type
     *
     * @return true if Value's type is COLUMN_TYPE_EDGE
     */
    public boolean isEdge() {
        return type == COLUMN_TYPE_EDGE;
    }

    /**
     * check if the Value is Local Time type
     *
     * @return true if Value's type is COLUMN_TYPE_LOCALTIME
     */
    public boolean isLocalTime() {
        return type == COLUMN_TYPE_LOCALTIME;
    }

    /**
     * check if the Value is Zoned Time type
     *
     * @return true if Value's type is COLUMN_TYPE_ZONEDTIME
     */
    public boolean isZonedTime() {
        return type == COLUMN_TYPE_ZONEDTIME;
    }

    /**
     * check if the Value is Local Datetime type
     *
     * @return true if Value's type is COLUMN_TYPE_LOCALDATETIME
     */
    public boolean isLocalDateTime() {
        return type == COLUMN_TYPE_LOCALDATETIME;
    }

    /**
     * check if the Value is Zoned Datetime type
     *
     * @return true if Value's type is COLUMN_TYPE_ZONEDDATETIME
     */
    public boolean isZonedDateTime() {
        return type == COLUMN_TYPE_ZONEDDATETIME;
    }

    /**
     * check if the Value is Date type
     *
     * @return true if Value's type is COLUMN_TYPE_DATE
     */
    public boolean isDate() {
        return type == COLUMN_TYPE_DATE;
    }

    /**
     * check if the Value is Record type
     *
     * @return true if Value's type is COLUMN_TYPE_RECORD
     */
    public boolean isRecord() {
        return type == COLUMN_TYPE_RECORD;
    }

    /**
     * check if the Value is Duration type
     *
     * @return true if Value's type is COLUMN_TYPE_DURATION
     */
    public boolean isDuration() {
        return type == COLUMN_TYPE_DURATION;
    }

    /**
     * check if the Value is Path type
     *
     * @return true if Value's type is COLUMN_TYPE_PATH
     */
    public boolean isPath() {
        return type == COLUMN_TYPE_PATH;
    }

    /**
     * check if the Value is Decimal type
     *
     * @return true if Value's type is COLUMN_TYPE_DECIMAL
     */
    public boolean isDecimal() {
        return type == COLUMN_TYPE_DECIMAL;
    }

    /**
     * check if the Value is Geography type
     *
     * @return true if Value's type is COLUMN_TYPE_GEOGRAPHY
     */
    public boolean isGeography() {
        return type == COLUMN_TYPE_GEOGRAPHY;
    }

    /**
     * Convert the original Value to boolean
     *
     * @return boolean value
     * @throws InvalidValueException if the value type is not bool
     */
    public boolean asBoolean() throws InvalidValueException {
        if (type == COLUMN_TYPE_BOOL) {
            return (java.lang.Boolean) value;
        }
        throw new InvalidValueException(
            "Cannot get field `boolean` because value's type is " + getDataType());
    }


    /**
     * Convert the original int8/uint8/int16/uint16/int32/uint32 Value to int
     *
     * <p>if the value in NebulaGraph is uint32 type and is negative, you can convert it to Long:
     *
     * <p>Integer.toUnsignedLong((int)value)
     *
     * @return int value
     * @throws InvalidValueException if the value type is not int
     */
    public int asInt() throws InvalidValueException {
        if (type == COLUMN_TYPE_INT8 || type == COLUMN_TYPE_UINT8
            || type == COLUMN_TYPE_UINT16 || type == COLUMN_TYPE_INT16
            || type == COLUMN_TYPE_UINT32 || type == COLUMN_TYPE_INT32) {
            return Integer.parseInt(value.toString());
        }
        throw new InvalidValueException(
            "Cannot get field `int` because value's type is " + getDataType());
    }

    /**
     * Convert the original int64/uint64 Value to long
     *
     * <p>if the value in NebulaGraph is uint64 and is negative, you can convert it to BigInteger:
     *
     * <p>if (value >= 0) {
     * return BigInteger.valueOf(value); } else { return BigInteger.valueOf((long)value &
     * Long.MAX_VALUE).setBit(63); }
     *
     * @return long
     * @throws InvalidValueException if the value type is not int64 or uint64
     */
    public long asLong() throws InvalidValueException {
        if (type == COLUMN_TYPE_INT64 || type == COLUMN_TYPE_UINT64) {
            return (long) value;
        }
        throw new InvalidValueException(
            "Cannot get field `long` because value's type is " + getDataType());
    }


    /**
     * Convert the original data type Value to String
     *
     * @return String value
     * @throws InvalidValueException if the value type is not string
     */
    public String asString() throws InvalidValueException {
        if (type == COLUMN_TYPE_STRING) {
            return (java.lang.String) value;
        }
        throw new InvalidValueException(
            "Cannot get field `string` because value's type is " + getDataType());
    }

    /**
     * Convert the original Value to float
     *
     * @return float value
     * @throws InvalidValueException if the value type is not float
     */
    public float asFloat() throws InvalidValueException {
        if (type == COLUMN_TYPE_FLOAT32) {
            return (float) value;
        }
        throw new InvalidValueException(
            "Cannot get field `float` because value's type is " + getDataType());
    }

    /**
     * Convert the original Value to double
     *
     * @return double value
     * @throws InvalidValueException if the value type is not double
     */
    public double asDouble() throws InvalidValueException {
        if (type == COLUMN_TYPE_FLOAT64) {
            return (double) value;
        }
        throw new InvalidValueException(
            "Cannot get field `double` because value's type is " + getDataType());
    }

    /**
     * Convert the original Value to list
     *
     * @return list value
     * @throws InvalidValueException if the value type is not list
     */
    public List<ValueWrapper> asList() throws InvalidValueException {
        if (type == COLUMN_TYPE_LIST) {
            return (List<ValueWrapper>) value;
        }
        throw new InvalidValueException(
            "Cannot get field `list` because value's type is " + getDataType());
    }

    /**
     * Convert the original Value to Node
     *
     * @return Node value
     * @throws InvalidValueException if the value type is not node
     */
    public Node asNode() throws InvalidValueException {
        if (type == COLUMN_TYPE_NODE) {
            return (Node) value;
        }
        throw new InvalidValueException(
            "cannot get field `node` because value's type is " + getDataType());
    }

    /**
     * Convert the original data Value to Edge
     *
     * @return Edge value
     * @throws InvalidValueException if the value type is not edge
     */
    public Edge asEdge() throws InvalidValueException {
        if (type == COLUMN_TYPE_EDGE) {
            return (Edge) value;
        }
        throw new InvalidValueException(
            "cannot get field `edge` because value's type is " + getDataType());
    }

    /**
     * Convert the original data Value to LocalTime
     *
     * @return {@link LocalTime} value
     * @throws InvalidValueException if the value type is not localtime
     */
    public LocalTime asLocalTime() throws InvalidValueException {
        if (type == COLUMN_TYPE_LOCALTIME) {
            return (LocalTime) value;
        }
        throw new InvalidValueException(
            "cannot get field `localtime` because value's type is " + getDataType());
    }


    /**
     * Convert the original data type Value to OffsetTime
     *
     * @return {@link OffsetTime} time with zone information
     * @throws InvalidValueException if the value type is not Zoned time
     */
    public OffsetTime asZonedTime() throws InvalidValueException {
        if (type == COLUMN_TYPE_ZONEDTIME) {
            return (OffsetTime) value;
        }
        throw new InvalidValueException(
            "cannot get field `zonedtime` because value's type is " + getDataType());
    }


    /**
     * Convert the original data type Value to LocalDate
     *
     * @return {@link LocalDate} value
     * @throws InvalidValueException if the value type is not date
     */
    public LocalDate asDate() throws InvalidValueException {
        if (type == COLUMN_TYPE_DATE) {
            return (LocalDate) value;
        }
        throw new InvalidValueException(
            "cannot get field `date` because value's type is " + getDataType());
    }

    /**
     * Convert the original data Value to LocalDateTime
     *
     * @return {@link LocalDateTime}
     * @throws InvalidValueException if the value type is not Local Datetime
     */
    public LocalDateTime asLocalDateTime() throws InvalidValueException {
        if (type == COLUMN_TYPE_LOCALDATETIME) {
            return (LocalDateTime) value;
        }
        throw new InvalidValueException(
            "cannot get field `localdatetime` because value's type is " + getDataType());
    }

    /**
     * Convert the original data Value to ZonedDateTime
     *
     * @return {@link ZonedDateTime}
     * @throws InvalidValueException if the value type is not Zoned Datetime
     */
    public ZonedDateTime asZonedDateTime() throws InvalidValueException {
        if (type == COLUMN_TYPE_ZONEDDATETIME) {
            return (ZonedDateTime) value;
        }
        throw new InvalidValueException(
            "cannot get field `zoneddatetime` because value's type is " + getDataType());
    }

    /**
     * Convert the original data type Value to NDuration
     *
     * @return {@link NDuration}
     * @throws InvalidValueException if the value type is not duration
     */
    public NDuration asDuration() throws InvalidValueException {
        if (type == COLUMN_TYPE_DURATION) {
            return (NDuration) value;
        }
        throw new InvalidValueException(
            "cannot get field `duration` because value's type is " + getDataType());
    }

    /**
     * Convert the original data type Value to NRecord
     *
     * @return {@link NRecord}
     * @throws InvalidValueException if the value type is not Record
     */
    public NRecord asRecord() throws InvalidValueException {
        if (type == COLUMN_TYPE_RECORD) {
            return (NRecord) value;
        }
        throw new InvalidValueException(
            "cannot get field `record` because value's type is " + getDataType());
    }

    /**
     * Convert the original data type Value to Path
     *
     * @return {@link Path}
     * @throws InvalidValueException if the value type is not Path
     */
    public Path asPath() throws InvalidValueException {
        if (type == COLUMN_TYPE_PATH) {
            return (Path) value;
        }
        throw new InvalidValueException(
            "cannot get field `path` because value's type is " + getDataType());
    }

    /**
     * Convert the original data type Value to BigDecimal
     *
     * @return {@link BigDecimal}
     * @throws InvalidValueException if the value type is not decimal
     */
    public BigDecimal asDecimal() throws InvalidValueException {
        if (type == COLUMN_TYPE_DECIMAL) {
            return (BigDecimal) value;
        }
        throw new InvalidValueException(
            "cannot get field `decimal` because value's type is " + getDataType());
    }

    /**
     * Convert the original data type Value to Vector
     *
     * @return {@link Vector}
     * @throws InvalidValueException if the value type is not Vector
     */
    public EmbeddingVector asVector() throws InvalidValueException {
        if (type == COLUMN_TYPE_EMBEDDINGVECTOR) {
            return (EmbeddingVector) value;
        }
        throw new InvalidValueException(
            "cannot get field `vector` because value's type is " + getDataType());
    }

    /**
     * Convert the original data type Value to Vector
     *
     * @return {@link Geography}
     * @throws InvalidValueException if the value type is not vector
     */
    public Geography asGeography() throws InvalidValueException {
        if (type == COLUMN_TYPE_GEOGRAPHY) {
            return (Geography) value;
        }
        throw new InvalidValueException(
            "cannot get field `geography` because value's type is " + getDataType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValueWrapper that = (ValueWrapper) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Convert Value to String format
     *
     * @return String
     */
    @Override
    public String toString() {
        if (isNull()) {
            return null;
        } else if (isBoolean()) {
            return String.valueOf(asBoolean());
        } else if (isInt()) {
            return String.valueOf(asInt());
        } else if (isLong()) {
            return String.valueOf(asLong());
        } else if (isFloat()) {
            return String.valueOf(asFloat());
        } else if (isDouble()) {
            return String.valueOf(asDouble());
        } else if (isString()) {
            return asString();
        } else if (isList()) {
            return asList().toString();
        } else if (isRecord()) {
            return asRecord().toString();
        } else if (isNode()) {
            return asNode().toString();
        } else if (isEdge()) {
            return asEdge().toString();
        } else if (isLocalTime()) {
            return asLocalTime().format(localTimeFormatter);
        } else if (isZonedTime()) {
            return asZonedTime().format(zonedTimeFormatter);
        } else if (isLocalDateTime()) {
            return asLocalDateTime().format(localDateTimeFormatter);
        } else if (isZonedDateTime()) {
            return asZonedDateTime().format(zonedDateTimeFormatter);
        } else if (isDate()) {
            return asDate().toString();
        } else if (isDuration()) {
            return asDuration().toString();
        } else if (isPath()) {
            return asPath().toString();
        } else if (isDecimal()) {
            return asDecimal().toString();
        } else if (isVector()) {
            return asVector().toString();
        } else if (isGeography()) {
            return asGeography().toString();
        }
        return "Unknown type: " + getDataType();
    }
}
