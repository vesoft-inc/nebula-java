/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.dao;

import com.vesoft.nebula.domain.GraphLabel;

/**
 * Description  GraphQueryEngine is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 11:09
 * @version 1.0.0
 */
public interface GraphQueryEngine extends GraphEngine {

    /**
     * 是否包含多标签操作
     *
     * @return
     */
    public boolean containsMultiLabel();

    /**
     * 获取查询的图标签
     *
     * @return
     */
    public GraphLabel getGraphLabel();

}
