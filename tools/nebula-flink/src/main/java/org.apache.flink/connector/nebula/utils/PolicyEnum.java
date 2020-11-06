/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.utils;

public enum PolicyEnum {
    /** HASH policy */
    HASH("HASH"),

    /** UUID policy */
    UUID("UUID");

    private String type;

    PolicyEnum(String type) {
        this.type = type;
    }

    public String policy(){
        return type;
    }
}
