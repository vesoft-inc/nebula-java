package com.vesoft.nebula.domain.impl;

import com.vesoft.nebula.dao.GraphValueFormatter;
import com.vesoft.nebula.domain.AbstractGraphLabel;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import com.vesoft.nebula.enums.GraphKeyPolicy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

/**
 * Description  GraphVertexType is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 14:08
 * @version 1.0.0
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class GraphVertexType<T> extends AbstractGraphLabel {

    /**
     * 图的类型tag名称
     */
    private String vertexName;

    /**
     * 主键策略
     */
    private GraphKeyPolicy graphKeyPolicy;

    /**
     * 对应的业务实体类
     */
    private Class<T> typeClass;

    private boolean idAsField;

    private GraphValueFormatter idValueFormatter;


    protected GraphVertexType() {
    }

    private GraphVertexType(String vertexName, Class<T> typeClass, GraphKeyPolicy graphKeyPolicy,
                            Map<String, GraphValueFormatter> propertyFormatMap,
                            Map<String, GraphDataTypeEnum> dataTypeMap, List<String> mustFields,
                            Map<String, String> propertyFieldMap, boolean idAsField, GraphValueFormatter idValueFormatter) {
        this.graphKeyPolicy = graphKeyPolicy;
        this.vertexName = vertexName;
        this.typeClass = typeClass;
        this.propertyFormatMap = propertyFormatMap;
        this.mustFields = mustFields;
        this.propertyFieldMap = propertyFieldMap;
        this.dataTypeMap = dataTypeMap;
        this.idAsField = idAsField;
        this.idValueFormatter = idValueFormatter;
    }

    /**
     * 方法内部决定是否需要加工
     *
     * @param vertexKey 真实的id数据
     * @return
     */
    public String getVertexIdKey(String vertexKey) {
        if (idValueFormatter != null) {
            vertexKey = (String) idValueFormatter.format(vertexKey);
        }
        return vertexKey;
    }

    @Override
    public boolean isTag() {
        return true;
    }

    @Override
    public boolean isEdge() {
        return false;
    }

    @Override
    public String getName() {
        return this.getVertexName();
    }
}
