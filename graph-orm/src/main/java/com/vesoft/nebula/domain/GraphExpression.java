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
