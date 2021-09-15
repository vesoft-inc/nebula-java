/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.dao;

import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.exception.NebulaException;

/**
 * Description  GraphVertexTypeFactory is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 15:09
 * @version 1.0.0
 */
public interface GraphVertexTypeFactory {

    /**
     * 根据类创建顶点类型
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws NebulaException
     */
    public <T> GraphVertexType<T> buildGraphVertexType(Class<T> clazz) throws NebulaException;

}
