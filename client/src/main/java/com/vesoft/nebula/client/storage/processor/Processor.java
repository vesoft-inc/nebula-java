/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.processor;

import com.vesoft.nebula.Value;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);

    /**
     * get decoded field
     */
    protected static Object getField(Value value, String decodeType) {
        if (value.getSetField() == Value.NVAL) {
            return null;
        }
        Object obj = value.getFieldValue();
        if (obj.getClass().getTypeName().equals("byte[]")) {
            try {
                return new String((byte[]) obj, decodeType);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("encode error with " + decodeType, e);
                return null;
            }
        }
        return obj;
    }
}
