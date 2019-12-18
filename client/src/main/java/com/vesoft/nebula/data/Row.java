/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

public class Row {
    private Property[] defaultProperties;
    private Property[] properties;

    public Row(Property[] defaultProperties, Property[] properties) {
        this.defaultProperties = defaultProperties;
        this.properties = properties;
    }

    public Property[] getProperties() {
        return properties;
    }

    public Property[] getDefaultProperties() {
        return defaultProperties;
    }
}
