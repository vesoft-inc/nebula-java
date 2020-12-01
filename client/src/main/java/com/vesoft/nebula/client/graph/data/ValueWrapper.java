/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Time;
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
    }

    private final Value value;
    private final String decodeType = "utf-8";

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
            default:
                throw new IllegalArgumentException("Unknown field id " + value.getSetField());
        }
    }

    public ValueWrapper(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value.getSetField() == 0;
    }

    public boolean isNull() {
        return value.getSetField() == Value.NVAL;
    }

    public boolean isBoolean() {
        return value.getSetField() == Value.BVAL;
    }

    public boolean isLong() {
        return value.getSetField() == Value.IVAL;
    }

    public boolean isDouble() {
        return value.getSetField() == Value.FVAL;
    }

    public boolean isString() {
        return value.getSetField() == Value.SVAL;
    }

    public boolean isList() {
        return value.getSetField() == Value.LVAL;
    }

    public boolean isSet() {
        return value.getSetField() == Value.UVAL;
    }

    public boolean isMap() {
        return value.getSetField() == Value.MVAL;
    }

    public boolean isTime() {
        return value.getSetField() == Value.TVAL;
    }

    public boolean isDate() {
        return value.getSetField() == Value.DVAL;
    }

    public boolean isDateTime() {
        return value.getSetField() == Value.DTVAL;
    }

    public boolean isVertex() {
        return value.getSetField() == Value.VVAL;
    }

    public boolean isEdge() {
        return value.getSetField() == Value.EVAL;
    }

    public boolean isPath() {
        return value.getSetField() == Value.PVAL;
    }

    public NullType asNull() throws InvalidValueException {
        if (value.getSetField() == Value.NVAL) {
            return new NullType((int)value.getFieldValue());
        } else {
            throw new InvalidValueException(
                    "Cannot get field nullType because value's type is " + descType());
        }
    }

    public boolean asBoolean() throws InvalidValueException {
        if (value.getSetField() == Value.BVAL) {
            return (boolean)(value.getFieldValue());
        }
        throw new InvalidValueException(
            "Cannot get field boolean because value's type is " + descType());
    }

    public long asLong() throws InvalidValueException {
        if (value.getSetField() == Value.IVAL) {
            return (long)(value.getFieldValue());
        } else {
            throw new InvalidValueException(
                    "Cannot get field long because value's type is " + descType());
        }
    }

    public String asString() throws InvalidValueException, UnsupportedEncodingException {
        if (value.getSetField() == Value.SVAL) {
            return new String((byte[])value.getFieldValue(), decodeType);
        }
        throw new InvalidValueException(
                "Cannot get field string because value's type is " + descType());
    }

    public double asDouble() throws InvalidValueException {
        if (value.getSetField() == Value.FVAL) {
            return (double)value.getFieldValue();
        }
        throw new InvalidValueException(
                "Cannot get field double because value's type is " + descType());
    }

    public ArrayList<ValueWrapper> asList() throws InvalidValueException {
        if (value.getSetField() != Value.LVAL) {
            throw new InvalidValueException(
                "Cannot get field type `list' because value's type is " + descType());
        }
        ArrayList<ValueWrapper> values = new ArrayList<>();
        for (Value value : (ArrayList<Value>)(value.getFieldValue())) {
            values.add(new ValueWrapper(value));
        }
        return values;
    }

    public HashSet<ValueWrapper> asSet() throws InvalidValueException {
        if (value.getSetField() != Value.UVAL) {
            throw new InvalidValueException(
                "Cannot get field type `set' because value's type is " + descType());
        }
        HashSet<ValueWrapper> values = new HashSet<>();
        for (Value value : ((HashSet<Value>)(value.getFieldValue()))) {
            values.add(new ValueWrapper(value));
        }
        return values;
    }

    public HashMap<String, ValueWrapper> asMap()
        throws InvalidValueException, UnsupportedEncodingException {
        if (value.getSetField() != Value.MVAL) {
            throw new InvalidValueException(
                "Cannot get field type `set' because value's type is " + descType());
        }
        HashMap<String, ValueWrapper> kvs = new HashMap<>();
        Map<Object, Value> inValues = value.getMVal();
        for (Object key : inValues.keySet()) {
            kvs.put((String)key, new ValueWrapper(inValues.get(key)));
        }
        return kvs;
    }

    public Time asTime() throws InvalidValueException {
        if (value.getSetField() == Value.TVAL) {
            return (Time)value.getFieldValue();
        }
        throw new InvalidValueException(
            "Cannot get field time because value's type is " + descType());
    }

    public Date asDate() throws InvalidValueException {
        if (value.getSetField() == Value.DVAL) {
            return (Date)value.getFieldValue();
        }
        throw new InvalidValueException(
            "Cannot get field date because value's type is " + descType());
    }

    public DateTime asDateTime() throws InvalidValueException {
        if (value.getSetField() == Value.DTVAL) {
            return (DateTime) value.getFieldValue();
        }
        throw new InvalidValueException(
            "Cannot get field datetime because value's type is " + descType());
    }

    public Node asNode() throws InvalidValueException, UnsupportedEncodingException  {
        if (value.getSetField() == Value.VVAL) {
            return new Node(value.getVVal());
        }
        throw new InvalidValueException(
                "Cannot get field Node because value's type is " + descType());
    }

    public Relationship asRelationship() {
        if (value.getSetField() == Value.EVAL) {
            return new Relationship(value.getEVal());
        }
        throw new InvalidValueException(
                "Cannot get field Relationship because value's type is " + descType());
    }

    public PathWrapper asPath() throws InvalidValueException, UnsupportedEncodingException {
        if (value.getSetField() == Value.PVAL) {
            return new PathWrapper(value.getPVal());
        }
        throw new InvalidValueException(
                "Cannot get field PathWrapper because value's type is " + descType());
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
}
