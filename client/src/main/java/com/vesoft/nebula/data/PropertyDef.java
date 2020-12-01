/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

public class PropertyDef {
    public enum PropertyType {
        // only basic types are supported for now
        UNKNOWN(0),

        // all types used in thrift interface
        BOOL(1),
        INT(2),             // all int/long are encode as varint in nebula storage
        VID(3),
        FLOAT(4),
        DOUBLE(5),
        STRING(6),
        TIMESTAMP(21),

        // some default property in vertex/edge key
        VERTEX_ID(-1),
        TAG_ID(-2),
        SRC_ID(-3),
        EDGE_TYPE(-4),
        EDGE_RANK(-5),
        DST_ID(-6);

        private int value;

        PropertyType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PropertyType getEnum(int value) {
            for (PropertyType type : PropertyType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return PropertyType.UNKNOWN;
        }
    }

    private PropertyType type;
    private String name;

    public PropertyDef(PropertyType type, String name) {
        this.type = type;
        this.name = name;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
