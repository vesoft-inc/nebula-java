/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import java.io.Serializable;

public abstract class BaseDataObject implements Serializable {
    private String decodeType = "utf-8";

    public String getDecodeType() {
        return decodeType;
    }

    public BaseDataObject setDecodeType(String decodeType) {
        this.decodeType = decodeType;
        return this;
    }

}
