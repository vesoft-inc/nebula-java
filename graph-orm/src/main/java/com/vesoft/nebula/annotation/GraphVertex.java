/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.annotation;


import com.vesoft.nebula.enums.GraphKeyPolicy;
import java.lang.annotation.*;

/**
 * 业务说明：标注顶点
 *
 * @author j-huangzhaolai-jk
 * @date 2021/4/28
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GraphVertex {

    /**
     * 顶点名称
     *
     * @return
     */
    String value();

    /**
     * 主键生成方法
     *
     * @return
     */
    GraphKeyPolicy keyPolicy();

    /**
     * 顶点id是否作为属性
     *
     * @return
     */
    boolean idAsField() default true;

}
