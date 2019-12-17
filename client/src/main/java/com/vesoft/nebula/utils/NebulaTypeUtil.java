/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.utils;

import com.vesoft.nebula.SupportedType;

/**
 *
 */
public final class NebulaTypeUtil {
    private static final NebulaTypeUtil instance = new NebulaTypeUtil();

    private NebulaTypeUtil() {

    }

    public static Class supportedTypeToClass(int type) {
        switch (type) {
            case SupportedType.BOOL:
                return Boolean.class;
            case SupportedType.INT:
            case SupportedType.VID:
                return Long.class;
            case SupportedType.FLOAT:
            case SupportedType.DOUBLE:
                return Double.class;
            case SupportedType.STRING:
                return String.class;
            default:
                return Void.class;
        }
    }
}
