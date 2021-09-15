package com.vesoft.nebula.annotation;

import com.vesoft.nebula.dao.GraphValueFormatter;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import com.vesoft.nebula.enums.GraphPropertyTypeEnum;
import java.lang.annotation.*;

/**
 * 业务说明：图的属性
 *
 * @author j-huangzhaolai-jk
 * @date 2021/4/28
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GraphProperty {

    /**
     * 属性名称
     *
     * @return
     */
    String value();

    /**
     * 数据类型
     *
     * @return
     */
    GraphDataTypeEnum dataType() default GraphDataTypeEnum.STRING;

    /**
     * 是否必需
     *
     * @return
     */
    boolean required() default false;

    /**
     * 属性类型
     *
     * @return
     */
    GraphPropertyTypeEnum propertyTypeEnum() default GraphPropertyTypeEnum.ORDINARY_PROPERTY;

    /**
     * 属性格式化
     *
     * @return
     */
    Class<? extends GraphValueFormatter> formatter() default GraphValueFormatter.class;

}
