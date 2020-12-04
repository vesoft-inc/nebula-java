/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.utils;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.meta.PropertyType;

public class NebulaTypeUtil {

    public static Class supportedTypeToClass(int type) {
        switch (type) {
            case PropertyType.BOOL:
                return Boolean.class;
            case PropertyType.INT8:
            case PropertyType.INT16:
            case PropertyType.INT32:
            case PropertyType.INT64:
            case PropertyType.VID:
            case PropertyType.TIMESTAMP:
                return Long.class;
            case PropertyType.FLOAT:
            case PropertyType.DOUBLE:
                return Double.class;
            case PropertyType.STRING:
            case PropertyType.FIXED_STRING:
                return String.class;
            case PropertyType.DATE:
                return Date.class;
            case PropertyType.TIME:
                return Time.class;
            case PropertyType.DATETIME:
                return DateTime.class;
            default:
                return Void.class;
        }
    }
}
