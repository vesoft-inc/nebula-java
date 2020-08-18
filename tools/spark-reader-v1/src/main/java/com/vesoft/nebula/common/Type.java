/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.common;

public enum Type {

    VERTEX("VERTEX"),EDGE("EDGE");

    private String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


}
