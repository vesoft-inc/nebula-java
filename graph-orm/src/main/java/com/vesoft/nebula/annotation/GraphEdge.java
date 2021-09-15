package com.vesoft.nebula.annotation;

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
public @interface GraphEdge {

    /**
     * 边名称
     *
     * @return
     */
    String value();

    /**
     * 边起点类
     *
     * @return
     */
    Class srcVertex();

    /**
     * 边终点类
     *
     * @return
     */
    Class dstVertex();

    /**
     * 起点id是否作为字段
     *
     * @return
     */
    boolean srcIdAsField() default true;

    /**
     * 末尾id是否作为字段
     *
     * @return
     */
    boolean dstIdAsField() default true;

}
