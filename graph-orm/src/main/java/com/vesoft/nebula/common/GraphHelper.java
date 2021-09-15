package com.vesoft.nebula.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vesoft.nebula.annotation.GraphProperty;
import com.vesoft.nebula.dao.GraphValueFormatter;
import com.vesoft.nebula.domain.GraphLabel;
import com.vesoft.nebula.domain.GraphLabelBuilder;
import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.enums.ErrorEnum;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import com.vesoft.nebula.enums.GraphKeyPolicy;
import com.vesoft.nebula.enums.GraphPropertyTypeEnum;
import com.vesoft.nebula.exception.NebulaException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author zhoupeng
 * @date 2020/4/16
 */
@Log4j2
public class GraphHelper {
    private static String ENDPOINT_TEMPLATE = "%s(\"%s\")";
    private static String STRING_ID_TEMPLATE = "%s \"%s\" ";
    public static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[\n\t\"\'()<>/\\\\]");


    /**
     * nebula 中的时间戳是精确到秒的
     *
     * @return
     */
    public static long getNebulaCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }


    /**
     * 将 localDate 转换为 nebula time
     *
     * @param date
     * @return
     */
    public static long changeToNebulaTime(LocalDate date) {
        Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
        return timestamp.getTime() / 1000;
    }

    private static String generateKeyPolicy(GraphKeyPolicy graphKeyPolicy, String vertexIdKey) {
        if (graphKeyPolicy.equals(GraphKeyPolicy.string_key)) {
            return String.format(STRING_ID_TEMPLATE, graphKeyPolicy.getKeyWrapWord(), vertexIdKey);
        } else {
            return String.format(ENDPOINT_TEMPLATE, graphKeyPolicy.getKeyWrapWord(), vertexIdKey);
        }
    }

    public static String getQueryId(GraphVertexType vertexTag, String vertexKey) {
        String vertexIdKey = vertexTag.getVertexIdKey(vertexKey);
        GraphKeyPolicy graphKeyPolicy = vertexTag.getGraphKeyPolicy();
        return generateKeyPolicy(graphKeyPolicy, vertexIdKey);
    }

    public static String getQuerySrcId(GraphEdgeType edgeType, String vertexKey) {
        String vertexIdKey = edgeType.getSrcIdKey(vertexKey);
        GraphKeyPolicy graphKeyPolicy = edgeType.getSrcVertexType().getGraphKeyPolicy();
        return generateKeyPolicy(graphKeyPolicy, vertexIdKey);
    }

    public static String getQueryDstId(GraphEdgeType edgeType, String vertexKey) {
        String vertexIdKey = edgeType.getDstIdKey(vertexKey);
        GraphKeyPolicy graphKeyPolicy = edgeType.getDstVertexType().getGraphKeyPolicy();
        return generateKeyPolicy(graphKeyPolicy, vertexIdKey);
    }

    /**
     * 顶点列表id
     *
     * @param vertexTag
     * @param vertexKeyList
     * @return
     */
    public static String getQueryId(GraphVertexType vertexTag, Collection<String> vertexKeyList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isEmpty(vertexKeyList)) {
            return "";
        }

        for (String vertexId : vertexKeyList) {
            stringBuilder.append(getQueryId(vertexTag, vertexId)).append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    /**
     * 去掉特殊字符
     *
     * @param str
     * @return
     */
    public static String removeSpecialChar(String str) {
        return SPECIAL_CHAR_PATTERN.matcher(str).replaceAll("");
    }


    private static void collectGraphField(GraphLabelBuilder graphLabelBuilder, Field declaredField, List<String> mustProps,
                                          Map<String, String> propertyFieldMap, Map<String, GraphValueFormatter> propertyFormatMap,
                                          Map<String, GraphDataTypeEnum> dataTypeMap, boolean srcIdAsField, boolean dstIdAsField) {
        declaredField.setAccessible(true);
        GraphProperty graphProperty = declaredField.getAnnotation(GraphProperty.class);
        if (graphProperty == null) {
            return;
        }
        String value = graphProperty.value();
        dataTypeMap.put(value, graphProperty.dataType());
        Class<? extends GraphValueFormatter> formatter = graphProperty.formatter();
        GraphPropertyTypeEnum graphPropertyTypeEnum = graphProperty.propertyTypeEnum();
        switch (graphPropertyTypeEnum) {
            case GRAPH_VERTEX_ID:
                if (srcIdAsField && dstIdAsField) {
                    propertyFieldMap.put(declaredField.getName(), value);
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        graphLabelBuilder.dstIdValueFormatter(formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            case GRAPH_EDGE_SRC_ID:
                if (srcIdAsField) {
                    propertyFieldMap.put(declaredField.getName(), value);
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        graphLabelBuilder.srcIdValueFormatter(formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            case GRAPH_EDGE_DST_ID:
                if (dstIdAsField) {
                    propertyFieldMap.put(declaredField.getName(), value);
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        graphLabelBuilder.dstIdValueFormatter(formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            case ORDINARY_PROPERTY:
                propertyFieldMap.put(declaredField.getName(), value);
                if (graphProperty.required()) {
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        propertyFormatMap.put(value, formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            default:
                break;
        }

    }

    public static void collectGraphProperties(GraphLabelBuilder graphLabelBuilder, Class clazz,
                                              boolean srcIdAsField, boolean dstIdAsField) throws NebulaException {
        Field[] declaredFields = clazz.getDeclaredFields();
        int size = declaredFields.length;
        List<String> mustProps = Lists.newArrayListWithExpectedSize(size);
        //所有属性（包括必要属性）
        Map<String, String> propertyFieldMap = Maps.newHashMapWithExpectedSize(size);
        //字段类型
        Map<String, GraphDataTypeEnum> dataTypeMap = Maps.newHashMapWithExpectedSize(size);
        //字段转换工厂
        Map<String, GraphValueFormatter> propertyFormatMap = Maps.newHashMapWithExpectedSize(size);
        for (Field declaredField : declaredFields) {
            collectGraphField(graphLabelBuilder, declaredField, mustProps, propertyFieldMap, propertyFormatMap,
                    dataTypeMap, srcIdAsField, dstIdAsField);
        }
        graphLabelBuilder.labelClass(clazz);
        graphLabelBuilder.dataTypeMap(dataTypeMap);
        graphLabelBuilder.mustProps(mustProps);
        graphLabelBuilder.propertyFieldMap(propertyFieldMap);
        graphLabelBuilder.propertyFormatMap(propertyFormatMap);
    }

    public static Object formatFieldValue(Field declaredField, GraphProperty graphProperty, Object input, GraphLabel graphLabel) {
        Object value = null;
        try {
            value = declaredField.get(input);
        } catch (IllegalAccessException e) {
            log.warn("获取值异常{}", input, e);
        }
        return graphLabel.formatValue(graphProperty.value(), value);
    }

}
