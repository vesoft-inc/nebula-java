/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import java.io.Serializable;

public abstract class BaseDataObject implements Serializable {
    private String decodeType = "utf-8";
    private int timezoneOffset = 0;

    public String getDecodeType() {
        return decodeType;
    }

    public BaseDataObject setDecodeType(String decodeType) {
        this.decodeType = decodeType;
        return this;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public BaseDataObject setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
        return this;
    }
}
