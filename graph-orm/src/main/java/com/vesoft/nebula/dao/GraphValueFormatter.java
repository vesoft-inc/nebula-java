/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.dao;

/**
 * 业务说明：
 *
 * @author jiangyiwang
 * @date 2021/7/1
 **/
public interface GraphValueFormatter {

    /**
     * 格式化
     *
     * @param oldValue
     * @return
     */
    public Object format(Object oldValue);


    /**
     * nebula属性值反转为javaBean值
     *
     * @param nebulaValue
     * @return
     */
    public default Object reformat(Object nebulaValue) {
        return nebulaValue;
    }

}
