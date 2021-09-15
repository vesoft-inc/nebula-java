/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.domain;

/**
 * Description  GraphExpression is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/19 - 10:57
 * @version 1.0.0
 */
public interface GraphExpression {

    /**
     * 构建sql
     *
     * @return
     */
    public String buildSql();

}
