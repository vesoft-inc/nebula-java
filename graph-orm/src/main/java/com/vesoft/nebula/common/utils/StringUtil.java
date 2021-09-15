/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.common.utils;

import com.google.common.collect.Lists;
import com.vesoft.nebula.exception.CheckThrower;

import java.util.List;

public final class StringUtil {
    private StringUtil() {
    }

    /**
     * 分组聚合
     * eg:<br/>
     * aggregate([1,2,3,4,5],2,':') == [1:2,3:4,5]
     *
     * @param list
     * @param partSize  分组大小，每个分组组成一个字符串
     * @param separator 连接符，分组中的元素间使用此字符串连接
     * @return
     */
    public static List<String> aggregate(List<String> list, int partSize, String separator) {
        if (CollectionUtils.isEmpty(list) || partSize == 1) {
            return list;
        }
        CheckThrower.ifFalseThrow(partSize >= 1, "PartSize must greater than one");
        if (list.size() <= partSize) {
            return Lists.newArrayList(LocalStringBuilder.appendList(separator, list));
        }
        List<List<String>> partition = Lists.partition(list, partSize);
        List<String> result = Lists.newArrayListWithExpectedSize(partition.size());
        for (List<String> onePart : partition) {
            result.add(LocalStringBuilder.appendList(separator, onePart));
        }
        return result;
    }

}
