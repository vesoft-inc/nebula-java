/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.engine;

import com.vesoft.nebula.dao.GraphUpdateVertexEngineFactory;
import com.vesoft.nebula.dao.VertexUpdateEngine;
import com.vesoft.nebula.domain.impl.GraphVertexEntity;
import com.vesoft.nebula.exception.NebulaException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * Description  NebulaUpdateVertexEngineFactory is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/19 - 10:52
 * @version 1.0.0
 */
@Slf4j
public class NebulaUpdateVertexEngineFactory implements GraphUpdateVertexEngineFactory {

    @Override
    public <T> VertexUpdateEngine build(List<GraphVertexEntity<T>> graphVertexEntities) throws NebulaException {
        return new NebulaBatchVertexUpdate<>(graphVertexEntities);
    }

}
