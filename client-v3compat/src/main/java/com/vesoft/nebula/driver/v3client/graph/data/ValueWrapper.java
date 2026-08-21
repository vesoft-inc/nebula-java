/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.graph.decode.ColumnType;
import com.vesoft.nebula.driver.v3client.graph.exception.InvalidValueException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Wrapper around a NebulaGraph value, matching the v3 client's {@code ValueWrapper} API.
 *
 * <p>Internally it wraps a v5 driver {@link com.vesoft.nebula.driver.graph.data.ValueWrapper}.
 */
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
                case __NULL__:
                    return "NULL";
                case NaN:
                    return "NaN";
                case BAD_DATA:
                    return "BAD_DATA";
                case BAD_TYPE:
                    return "BAD_TYPE";
                case ERR_OVERFLOW:
                    return "ERR_OVERFLOW";
                case UNKNOWN_PROP:
                    return "UNKNOWN_PROP";
                case DIV_BY_ZERO:
                    return "DIV_BY_ZERO";
                case OUT_OF_RANGE:
                    return "OUT_OF_RANGE";
                default:
                    return "Unknown type: " + nullType;
            }
        }
    }

    private final com.vesoft.nebula.driver.graph.data.ValueWrapper value;
    private final int timezoneOffset;

    public ValueWrapper(com.vesoft.nebula.driver.graph.data.ValueWrapper value) {
        this(value, 0);
    }

    public ValueWrapper(com.vesoft.nebula.driver.graph.data.ValueWrapper value,
                        int timezoneOffset) {
        this.value = value;
        this.timezoneOffset = timezoneOffset;
    }

    /**
     * Build a compatibility value wrapping a {@code long} (used for node/edge ids).
     */
    public static ValueWrapper ofLong(long value) {
        return new ValueWrapper(new com.vesoft.nebula.driver.graph.data.ValueWrapper(
            value, ColumnType.COLUMN_TYPE_INT64));
    }

    /**
     * @return the underlying v5 value wrapper.
     */
    public Object getValue() {
        return value;
    }

    /**
     * The v5 driver has no distinct "empty" type; always returns {@code false}.
     */
    public boolean isEmpty() {
        return false;
    }

    public boolean isNull() {
        return value.isNull();
    }

    public boolean isBoolean() {
        return value.isBoolean();
    }

    public boolean isLong() {
        return value.isLong() || value.isInt();
    }

    public boolean isDouble() {
        return value.isDouble() || value.isFloat() || value.isDecimal();
    }

    public boolean isString() {
        return value.isString();
    }

    public boolean isList() {
        return value.isList();
    }

    public boolean isSet() {
        return value.isSet();
    }

    public boolean isMap() {
        return value.isMap();
    }

    public boolean isTime() {
        return value.isLocalTime() || value.isZonedTime();
    }

    public boolean isDate() {
        return value.isDate();
    }

    public boolean isDateTime() {
        return value.isLocalDateTime() || value.isZonedDateTime();
    }

    public boolean isVertex() {
        return value.isNode();
    }

    public boolean isEdge() {
        return value.isEdge();
    }

    public boolean isPath() {
        return value.isPath();
    }

    public boolean isGeography() {
        return value.isGeography();
    }

    public boolean isDuration() {
        return value.isDuration();
    }

    public NullType asNull() throws InvalidValueException {
        if (value.isNull()) {
            return new NullType(NullType.__NULL__);
        }
        throw new InvalidValueException(
            "Cannot get field nullType because value's type is " + value.getDataTypeString());
    }

    public boolean asBoolean() throws InvalidValueException {
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        throw new InvalidValueException(
            "Cannot get field boolean because value's type is " + value.getDataTypeString());
    }

    public long asLong() throws InvalidValueException {
        if (value.isLong()) {
            return value.asLong();
        }
        if (value.isInt()) {
            return value.asInt();
        }
        throw new InvalidValueException(
            "Cannot get field long because value's type is " + value.getDataTypeString());
    }

    public String asString() throws InvalidValueException, UnsupportedEncodingException {
        if (value.isString()) {
            return value.asString();
        }
        throw new InvalidValueException(
            "Cannot get field string because value's type is " + value.getDataTypeString());
    }

    public double asDouble() throws InvalidValueException {
        if (value.isDouble()) {
            return value.asDouble();
        }
        if (value.isFloat()) {
            return value.asFloat();
        }
        if (value.isDecimal()) {
            return value.asDecimal().doubleValue();
        }
        throw new InvalidValueException(
            "Cannot get field double because value's type is " + value.getDataTypeString());
    }

    public ArrayList<ValueWrapper> asList() throws InvalidValueException {
        if (value.isList()) {
            ArrayList<ValueWrapper> values = new ArrayList<>();
            for (com.vesoft.nebula.driver.graph.data.ValueWrapper element : value.asList()) {
                values.add(new ValueWrapper(element, timezoneOffset));
            }
            return values;
        }
        throw new InvalidValueException(
            "Cannot get field `list' because value's type is " + value.getDataTypeString());
    }

    public HashSet<ValueWrapper> asSet() throws InvalidValueException {
        if (value.isSet()) {
            HashSet<ValueWrapper> values = new HashSet<>();
            Set<?> set = value.asSet();
            for (Object element : set) {
                values.add(new ValueWrapper(
                    (com.vesoft.nebula.driver.graph.data.ValueWrapper) element, timezoneOffset));
            }
            return values;
        }
        throw new InvalidValueException(
            "Cannot get field `set' because value's type is " + value.getDataTypeString());
    }

    public HashMap<String, ValueWrapper> asMap()
        throws InvalidValueException, UnsupportedEncodingException {
        if (value.isMap()) {
            HashMap<String, ValueWrapper> kvs = new HashMap<>();
            Map<?, ?> map = value.asMap();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                com.vesoft.nebula.driver.graph.data.ValueWrapper key =
                    (com.vesoft.nebula.driver.graph.data.ValueWrapper) entry.getKey();
                com.vesoft.nebula.driver.graph.data.ValueWrapper val =
                    (com.vesoft.nebula.driver.graph.data.ValueWrapper) entry.getValue();
                kvs.put(key.toString(), new ValueWrapper(val, timezoneOffset));
            }
            return kvs;
        }
        throw new InvalidValueException(
            "Cannot get field `map' because value's type is " + value.getDataTypeString());
    }

    public TimeWrapper asTime() throws InvalidValueException {
        TimeWrapper wrapper;
        if (value.isLocalTime()) {
            wrapper = new TimeWrapper(value.asLocalTime());
        } else if (value.isZonedTime()) {
            wrapper = new TimeWrapper(value.asZonedTime());
        } else {
            throw new InvalidValueException(
                "Cannot get field time because value's type is " + value.getDataTypeString());
        }
        return (TimeWrapper) wrapper.setTimezoneOffset(timezoneOffset);
    }

    public DateWrapper asDate() throws InvalidValueException {
        if (value.isDate()) {
            return (DateWrapper) new DateWrapper(value.asDate()).setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field date because value's type is " + value.getDataTypeString());
    }

    public DateTimeWrapper asDateTime() throws InvalidValueException {
        DateTimeWrapper wrapper;
        if (value.isLocalDateTime()) {
            wrapper = new DateTimeWrapper(value.asLocalDateTime());
        } else if (value.isZonedDateTime()) {
            wrapper = new DateTimeWrapper(value.asZonedDateTime());
        } else {
            throw new InvalidValueException(
                "Cannot get field datetime because value's type is " + value.getDataTypeString());
        }
        return (DateTimeWrapper) wrapper.setTimezoneOffset(timezoneOffset);
    }

    public Node asNode() throws InvalidValueException, UnsupportedEncodingException {
        if (value.isNode()) {
            return (Node) new Node(value.asNode()).setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field Node because value's type is " + value.getDataTypeString());
    }

    public Relationship asRelationship() throws InvalidValueException {
        if (value.isEdge()) {
            return (Relationship) new Relationship(value.asEdge()).setTimezoneOffset(
                timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field Relationship because value's type is " + value.getDataTypeString());
    }

    public PathWrapper asPath() throws InvalidValueException, UnsupportedEncodingException {
        if (value.isPath()) {
            return new PathWrapper(value.asPath(), timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field PathWrapper because value's type is " + value.getDataTypeString());
    }

    public GeographyWrapper asGeography() throws InvalidValueException {
        if (value.isGeography()) {
            return (GeographyWrapper) new GeographyWrapper(value.asGeography())
                .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field GeographyWrapper because value's type is " + value.getDataTypeString());
    }

    public DurationWrapper asDuration() throws InvalidValueException {
        if (value.isDuration()) {
            return (DurationWrapper) new DurationWrapper(value.asDuration())
                .setTimezoneOffset(timezoneOffset);
        }
        throw new InvalidValueException(
            "Cannot get field DurationWrapper because value's type is " + value.getDataTypeString());
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

    @Override
    public String toString() {
        try {
            if (isNull()) {
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
            } else if (isDuration()) {
                return asDuration().toString();
            }
            return "Unknown type: " + value.getDataTypeString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
