/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.common.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Description  CollectionUtils is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/9 - 14:05
 * @version 1.0.0
 */
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    public static int size(Map map) {
        return map == null ? 0 : map.size();
    }

    public static int size(Collection collection) {
        return collection == null ? 0 : collection.size();
    }

    public static <T> int size(T[] array) {
        return array == null ? 0 : array.length;
    }

    public static String[] toStringArray(Collection<String> collection) {
        String[] strings = new String[size(collection)];
        if (isEmpty(collection)) {
            return strings;
        }
        strings = collection.toArray(strings);
        return strings;
    }

}
