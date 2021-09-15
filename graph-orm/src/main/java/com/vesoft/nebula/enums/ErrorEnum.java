package com.vesoft.nebula.enums;

import com.vesoft.nebula.common.ResponseService;

/**
 * @author zhoupeng
 * @date 2020/1/14
 */
public enum ErrorEnum implements ResponseService {
    /**
     * 系统繁忙
     */
    QUERY_NEBULA_EROR("001", "查询nebula异常"),
    UPDATE_NEBULA_EROR("002", "更新nebula异常"),

    PARAMETER_NOT_NULL("003", "请求参数为空异常！"),

    SESSION_LACK("004", "缺乏session注入"),

    GRAPH_EDGE_LACK("005", "缺乏边注解"),

    NOT_SUPPORT_VERTEX_TAG("006", "不支持的顶点类型"),


    UPDATE_FIELD_DATA_NOT_EMPTY("007", "插入数据内容不能为空"),

    HACKER_QUERY_SQL_NOT_EMPTY("008", "HackerQuery 查询语句不能为空"),

    QUERY_VERTEX_TAG_NOT_UNIFY("010", "查询顶点tag不唯一"),

    FIELD_FORMAT_NO_CONSTRUCTOR("011", "属性格式化器没有无参构造方法"),

    NOT_SUPPORT_EDGE_TAG("012", "不支持的边类型"),

    NOT_SUPPORT_QUERY_DIRECTION_TYPE("013", "不支持的查询方向标识"),

    VERTEX_PROP_VALUE_SIZE_NOT_MATCH("014", "顶点属性名和值个数不匹配"),

    INVALID_ID("015", "定点id非法或缺失"),

    INVALID_VERTEX_TAG("016", "非法的顶点类型或者顶点类型为空"),

    SYSTEM_ERROR("999", "系统繁忙"),

    ;

    private String code;
    private String desc;

    ErrorEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getResponseCode() {
        return this.code;
    }

    @Override
    public String getResponseMessage() {
        return this.desc;
    }
}
