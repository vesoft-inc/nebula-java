package com.vesoft.nebula.dao;

import com.vesoft.nebula.domain.GraphLabel;
import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.exception.NebulaException;

/**
 * Description  GraphTypeManager is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 18:06
 * @version 1.0.0
 */
public interface GraphTypeManager {
    /**
     * 根据类类型获取顶点类型
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws NebulaException
     */
    public <T> GraphVertexType<T> getGraphVertexType(Class<T> clazz) throws NebulaException;


    /**
     * 根据类类型获取顶点类型
     *
     * @param clazz
     * @return
     * @throws NebulaException
     */
    public <S, T, E> GraphEdgeType<S, T, E> getGraphEdgeType(Class<E> clazz) throws NebulaException;

    /**
     * 根据类对象获取图标签
     *
     * @param clazz
     * @return
     * @throws NebulaException
     */
    public GraphLabel getGraphLabel(Class clazz) throws NebulaException;

}
