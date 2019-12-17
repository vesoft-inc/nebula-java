/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import com.vesoft.nebula.data.PropertyDef.PropertyType;

public class Property {
    private PropertyDef def;
    private Object value;

    public Property(PropertyDef def, Object value) {
        this.def = def;
        this.value = value;
    }

    public Property(PropertyType type, String name, Object value) {
        this(new PropertyDef(type, name), value);
    }

    public PropertyType getPropertyType() {
        return def.getType();
    }

    public String getName() {
        return def.getName();
    }

    public Object getValue() {
        switch (def.getType()) {
            case BOOL:
                return getValueAsBool();
            case INT:
            case VID:
            case VERTEX_ID:
            case SRC_ID:
            case DST_ID:
            case EDGE_RANK:
                return getValueAsLong();
            case TAG_ID:
            case EDGE_TYPE:
                return getValueAsInt();
            case FLOAT:
                return getValueAsFloat();
            case DOUBLE:
                return getValueAsDouble();
            case STRING:
                return getValueAsString();
            default:
                return null;
        }
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean getValueAsBool() {
        return (Boolean) value;
    }

    public int getValueAsInt() {
        return (Integer) value;
    }

    public long getValueAsLong() {
        return (Long) value;
    }

    public float getValueAsFloat() {
        return (Float) value;
    }

    public double getValueAsDouble() {
        return (Double) value;
    }

    public String getValueAsString() {
        return (String) value;
    }
}
