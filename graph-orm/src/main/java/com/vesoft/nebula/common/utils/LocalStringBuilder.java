/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 业务说明：为了避免重复创建StringBuilder并扩容带来的内存浪费和性能消耗<br/>
 * 注意<br/>:
 * 尽量不要用在字符串过大的场景
 *
 * @author jiangyiwang
 * @date 2021/4/28
 **/
public class LocalStringBuilder {

    private LocalStringBuilder() {
    }

    private static final ThreadLocal<StringBuilder> localStringBuilder = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    public static String appendList(String split, List<?> args) {
        if (args == null || args.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return appends(split, args.toArray());
    }

    private static String appends(String split, Object... args) {
        if (args == null || args.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder builder = localStringBuilder.get();
        builder.setLength(0);
        builder.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            if (split != null) {
                builder.append(split);
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

}
