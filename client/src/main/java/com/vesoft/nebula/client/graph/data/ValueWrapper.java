/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.exception.InvalidValueException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class ValueWrapper {
    public static class NullType {
        public static final int __NULL__ = 0;
        public static final int NaN = 1;
        public static final int BAD_DATA = 2;
        public static final int BAD_TYPE = 3;
        public static final int ERR_OVERFLOW = 4;
        public static final int UNKNOWN_PROP = 5;
        public static final int DIV_BY_ZERO = 6;
        public static final int OUT_OF_RANGE = 7;
        int nullType;

        public NullType(int nullType) {
            this.nullType = nullType;
        }

        public int getNullType() {
            return nullType;
        }

        @Override
        public String toString() {
            switch (nullType) {
                case __NULL__: return "NULL";
                case NaN: return "NaN";
                case BAD_DATA: return "BAD_DATA";
                case BAD_TYPE: return "BAD_TYPE";
                case ERR_OVERFLOW: return "ERR_OVERFLOW";
                case UNKNOWN_PROP: return "UNKNOWN_PROP";
                case DIV_BY_ZERO: return "DIV_BY_ZERO";
                case OUT_OF_RANGE: return  "OUT_OF_RANGE";
                default: return "Unknown type: " + nullType;
            }
        }
    }

    private final Value value;
    private String decodeType = "utf-8";
    private int timezoneOffset = 0;

    private String descType() {
        switch (value.getSetField()) {
            case Value.NVAL:
                return "NULL";
            case Value.BVAL:
                return "BOOLEAN";
            case Value.IVAL:
                return "INT";
            case Value.FVAL:
                return "FLOAT";
            case Value.SVAL:
                return "STRING";
            case Value.DVAL:
                return "DATE";
            case Value.TVAL:
                return "TIME";
            case Value.DTVAL:
                return "DATETIME";
            case Value.VVAL:
                return "VERTEX";
            case Value.EVAL:
                return "EDGE";
            case Value.PVAL:
                return "PATH";
            case Value.LVAL:
                return "LIST";
            case Value.MVAL:
                return "MAP";
            case Value.UVAL:
                return "SET";
            case Value.GVAL:
                return "DATASET";
            case Value.GGVAL:
                return "GEOGRAPHY";
            default:
                throw new IllegalArgumentException("Unknown field id " + value.getSetField());
        }
    }

    /**
     * @param value the Value get from service
     * @param decodeType the decodeType get from the service to decode the byte array,
     *                   but now the service no return the decodeType, so use the utf-8
     */
    public ValueWrapper(Value value, String decodeType) {
        this.value = value;
        this.decodeType = decodeType;
        this.timezoneOffset = 0;
    }

    /**
     * @param value the Value get from service
     * @param decodeType the decodeType get from the service to decode the byte array,
     *                   but now the service no return the decodeType, so use the utf-8
     * @param timezoneOffset the timezone offset get from the service to calculate local time
     */
    public ValueWrapper(Value value, String decodeType, int timezoneOffset) {
        this.value = value;
        this.decodeType = decodeType;
        this.timezoneOffset = timezoneOffset;
    }

    /**
     * get the original data structure, the Value is the return from nebula-graph
     * @return Value
     */
    public Value getValue() {
        return value;
    }

    /**
     * judge the Value is Empty type, the Empty type is the nebula's type
     * @return boolean
     */
    public boolean isEmpty() {
        return value.getSetField() == 0;
    }

    /**
     * judge the Value is Null type,the Null type is the nebula's type
     * @return boolean
     */
    public boolean isNull() {
        return value.getSetField() == Value.NVAL;
    }

    /**
     * judge the Value is Boolean type
     * @return boolean
     */
    public boolean isBoolean() {
        return value.getSetField() == Value.BVAL;
    }

    /**
     * judge the Value is Long type
     * @return boolean
     */
    public boolean isLong() {
        return value.getSetField() == Value.IVAL;
    }

    /**
     * judge the Value is Double type
     * @return boolean
     */
    public boolean isDouble() {
        return value.getSetField() == Value.FVAL;
    }

    /**
     * judge the Value is String type
     * @return boolean
     */
    public boolean isString() {
        return value.getSetField() == Value.SVAL;
    }

    /**
     * judge the Value is List type, the List type is the nebula's type
     * @return boolean
     */
    public boolean isList() {
        return value.getSetField() == Value.LVAL;
    }

    /**
     * judge the Value is Set type, the Set type is the nebula's type
     * @return boolean
     */
    public boolean isSet() {
        return value.getSetField() == Value.UVAL;
    }

    /**
     * judge the Value is Map type, the Map type is the nebula's type
     * @return boolean
     */
    public boolean isMap() {
        return value.getSetField() == Value.MVAL;
    }

    /**
     * judge the Value is Time type, the Time type is the nebula's type
     * @return boolean
     */
    public boolean isTime() {
        return value.getSetField() == Value.TVAL;
    }

    /**
     * judge the Value is Date type, the Date type is the nebula's type
     * @return boolean
     */
    public boolean isDate() {
        return value.getSetField() == Value.DVAL;
    }

    /**
     * judge the Value is DateTime type, the DateTime type is the nebula's type
     * @return boolean
     */
    public boolean isDateTime() {
        return value.getSetField() == Value.DTVAL;
    }

    /**
     * judge the Value is Vertex type, the Vertex type is the nebula's type
     * @return boolean
     */
    public boolean isVertex() {
        return value.getSetField() == Value.VVAL;
    }

    /**
     * judge the Value is Edge type, the Edge type is the nebula's type
     * @return boolean
     */
    public boolean isEdge() {
        return value.getSetField() == Value.EVAL;
    }

    /**
     * judge the Value is Path type, the Path type is the nebula's type
     * @return boolean
     */
    public boolean isPath() {
        return value.getSetField() == Value.PVAL;
    }

    /**
     * judge the Value is Geography type, the Geography type is the nebula's type
     * @return boolean
     */
    public boolean isGeography() {
        return value.getSetField() == Value.GGVAL;
    }

    /**
     * Convert the original data type Value to NullType
     * @return NullType
     * @throws InvalidValueException if the value type is not null
     */
    public NullType asNull() throws InvalidValueException {
        if (value.getSetField() == Value.NVAL) {
            return new NullType(((com.vesoft.nebula.NullType)value.getFieldValue()).getValue());
        } else {
            throw new InvalidValueException(
                    "Cannot get field nullType because value's type is " + descType());
        }
    }

    /**
     * Convert the original data type Value to boolean
     * @return boolean
     * @throws InvalidValueException if the value type is not boolean
     */
    public boolean asBoolean() throws InvalidValueException {
        if (value.getSetField() == Value.BVAL) {
            return (boolean)(value.getFieldValue());
        }
        throw new InvalidValueException(
            "Cannot get field boolean because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to long
     * @return long
     * @throws InvalidValueException if the value type is not long
     */
    public long asLong() throws InvalidValueException {
        if (value.getSetField() == Value.IVAL) {
            return (long)(value.getFieldValue());
        } else {
            throw new InvalidValueException(
                    "Cannot get field long because value's type is " + descType());
        }
    }

    /**
     * Convert the original data type Value to String
     * @return String
     * @throws InvalidValueException if the value type is not string
     * @throws UnsupportedEncodingException if decode bianry failed
     */
    public String asString() throws InvalidValueException, UnsupportedEncodingException {
        if (value.getSetField() == Value.SVAL) {
            return new String((byte[])value.getFieldValue(), decodeType);
        }
        throw new InvalidValueException(
                "Cannot get field string because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to double
     * @return double
     * @throws InvalidValueException if the value type is not double
     */
    public double asDouble() throws InvalidValueException {
        if (value.getSetField() == Value.FVAL) {
            return (double)value.getFieldValue();
        }
        throw new InvalidValueException(
                "Cannot get field double because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to ArrayList
     * @return ArrayList of ValueWrapper
     * @throws InvalidValueException if the value type is not list
     */
    public ArrayList<ValueWrapper> asList() throws InvalidValueException {
        if (value.getSetField() != Value.LVAL) {
            throw new InvalidValueException(
                "Cannot get field type `list' because value's type is " + descType());
        }
        ArrayList<ValueWrapper> values = new ArrayList<>();
        for (Value value : value.getLVal().getValues()) {
            values.add(new ValueWrapper(value, decodeType, timezoneOffset));
        }
        return values;
    }

    /**
     * Convert the original data type Value to HashSet
     * @return HashSet of ValueWrapper
     * @throws InvalidValueException if the value type is not set
     */
    public HashSet<ValueWrapper> asSet() throws InvalidValueException {
        if (value.getSetField() != Value.UVAL) {
            throw new InvalidValueException(
                "Cannot get field type `set' because value's type is " + descType());
        }
        HashSet<ValueWrapper> values = new HashSet<>();
        for (Value value : value.getUVal().getValues()) {
            values.add(new ValueWrapper(value, decodeType, timezoneOffset));
        }
        return values;
    }

    /**
     * Convert the original data type Value to HashMap
     * @return HashMap, the key is String, value is ValueWrapper
     * @throws InvalidValueException if the value type is not map
     */
    public HashMap<String, ValueWrapper> asMap()
        throws InvalidValueException, UnsupportedEncodingException {
        if (value.getSetField() != Value.MVAL) {
            throw new InvalidValueException(
                "Cannot get field type `set' because value's type is " + descType());
        }
        HashMap<String, ValueWrapper> kvs = new HashMap<>();
        Map<byte[], Value> inValues = value.getMVal().getKvs();
        for (byte[] key : inValues.keySet()) {
            kvs.put(new String(key, decodeType),
                    new ValueWrapper(inValues.get(key), decodeType, timezoneOffset));
        }
        return kvs;
    }

    /**
     * Convert the original data type Value to TimeWrapper
     * @return TimeWrapper
     * @throws InvalidValueException if the value type is not time
     */
    public TimeWrapper asTime() throws InvalidValueException {
        if (value.getSetField() == Value.TVAL) {
            return (TimeWrapper) new TimeWrapper(value.getTVal())
                .setDecodeType(decodeType)
                .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field time because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to DateWrapper
     * @return DateWrapper
     * @throws InvalidValueException if the value type is not date
     */
    public DateWrapper asDate() throws InvalidValueException {
        if (value.getSetField() == Value.DVAL) {
            return new DateWrapper(value.getDVal());
        }
        throw new InvalidValueException(
            "Cannot get field date because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to DateTimeWrapper
     * @return DateTimeWrapper
     * @throws InvalidValueException if the value type is not datetime
     */
    public DateTimeWrapper asDateTime() throws InvalidValueException {
        if (value.getSetField() == Value.DTVAL) {
            return (DateTimeWrapper) new DateTimeWrapper(value.getDtVal())
                .setDecodeType(decodeType)
                .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field datetime because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to Node
     * @return Node
     * @throws InvalidValueException if the value type is not vertex
     * @throws UnsupportedEncodingException if decode binary failed
     */
    public Node asNode() throws InvalidValueException, UnsupportedEncodingException  {
        if (value.getSetField() == Value.VVAL) {
            return (Node) new Node(value.getVVal())
                .setDecodeType(decodeType)
                .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
                "Cannot get field Node because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to Relationship
     *
     * @return Relationship
     * @throws InvalidValueException if the value type is not edge
     */
    public Relationship asRelationship() throws InvalidValueException {
        if (value.getSetField() == Value.EVAL) {
            return (Relationship) new Relationship(value.getEVal())
                .setDecodeType(decodeType)
                .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
                "Cannot get field Relationship because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to Path
     * @return PathWrapper
     * @throws InvalidValueException if the value type is not path
     * @throws UnsupportedEncodingException if decode bianry failed
     */
    public PathWrapper asPath() throws InvalidValueException, UnsupportedEncodingException {
        if (value.getSetField() == Value.PVAL) {
            return (PathWrapper) new PathWrapper(value.getPVal())
                .setDecodeType(decodeType)
                .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
                "Cannot get field PathWrapper because value's type is " + descType());
    }

    /**
     * Convert the original data type Value to geography
     * @return GeographyWrapper
     * @throws InvalidValueException if the value type is not geography
     */
    public GeographyWrapper asGeography() throws InvalidValueException {
        if (value.getSetField() == Value.GGVAL) {
            return (GeographyWrapper) new GeographyWrapper(value.getGgVal())
                    .setDecodeType(decodeType)
                    .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
                "Cannot get field GeographyWrapper because value's type is " + descType());
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
        return Objects.equals(value, that.value)
            && Objects.equals(decodeType, that.decodeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, decodeType);
    }

    /**
     * Convert Value to String format
     * @return String
     */
    @Override
    public String toString() {
        try {
            if (isEmpty()) {
                return "__EMPTY__";
            } else if (isNull()) {
                return asNull().toString();
            } else if (isBoolean()) {
                return String.valueOf(asBoolean());
            } else if (isLong()) {
                return String.valueOf(asLong());
            } else if (isDouble()) {
                return String.valueOf(asDouble());
            } else if (isString()) {
                return "\"" + asString() + "\"";
            } else if (isList()) {
                return asList().toString();
            } else if (isSet()) {
                return asSet().toString();
            } else if (isMap()) {
                return asMap().toString();
            } else if (isTime()) {
                return asTime().toString();
            } else if (isDate()) {
                return asDate().toString();
            } else if (isDateTime()) {
                return asDateTime().toString();
            } else if (isVertex()) {
                return asNode().toString();
            } else if (isEdge()) {
                return asRelationship().toString();
            } else if (isPath()) {
                return asPath().toString();
            } else if (isGeography()) {
                return asGeography().toString();
            }
            return "Unknown type: " + descType();
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}
