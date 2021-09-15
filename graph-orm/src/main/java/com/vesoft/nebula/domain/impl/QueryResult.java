package com.vesoft.nebula.domain.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description QueryResult is used for
 * @Date 2020/3/27 - 10:13
 */
@ToString
public class QueryResult implements Iterable<QueryResult.Row>, Cloneable, Serializable {

    @Getter
    private List<Row> data = Collections.emptyList();

    public QueryResult() {
    }

    public QueryResult(List<Row> data) {
        if (!CollectionUtils.isEmpty(data)) {
            this.data = data;
        }
    }

    @Override
    public QueryResult clone() {
        List<Row> newList = Lists.newArrayListWithExpectedSize(data.size());
        for (Row datum : data) {
            Row clone = datum.clone();
            newList.add(clone);
        }
        return new QueryResult(newList);
    }

    /**
     * 将查询结果合并
     *
     * @param queryResult
     * @return
     */
    public QueryResult mergeQueryResult(QueryResult queryResult) {
        if (queryResult == null || queryResult.isEmpty()) {
            return this;
        }
        if (this.isEmpty()) {
            this.data = queryResult.getData();
        } else {
            this.data.addAll(queryResult.getData());
        }
        return this;
    }

    public <T> List<T> getEntities(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (Row row : this.data) {
            list.add(row.getEntity(clazz));
        }
        return list;
    }

    public int size() {
        return this.data.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public boolean isNotEmpty() {
        return this.size() != 0;
    }

    @Override
    public Iterator<Row> iterator() {
        return this.data.iterator();
    }

    public Stream<Row> stream() {
        Iterable<Row> iterable = this::iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }


    @Data
    public static class Row implements Iterable<Map.Entry<String, Object>>, Cloneable, Serializable {

        private Map<String, Object> detail = Collections.emptyMap();

        public Row() {
        }

        public Row(Map<String, Object> detail) {
            if (MapUtils.isNotEmpty(detail)) {
                this.detail = detail;
            }
        }

        @Override
        public Row clone() {
            Map<String, Object> newMap = Maps.newHashMapWithExpectedSize(detail.size());
            newMap.putAll(detail);
            return new Row(newMap);
        }

        public int size() {
            return this.detail.size();
        }

        public Row setProp(String key, Object value) {
            this.detail.put(key, value);
            return this;
        }

        public Map<String, Object> getRowData() {
            return this.detail;
        }


        public Object get(String key) {
            return MapUtils.isEmpty(this.detail) ? null : this.detail.get(key);
        }


        public Date getDate(String key) {
            Long value = this.getLong(key);
            return new Timestamp(value * 1000);
        }

        public String getString(String columnLabel) {
            return getString(columnLabel, null);
        }

        public String getString(String columnLabel, String defaultValue) {
            return (String) this.detail.getOrDefault(columnLabel, defaultValue);
        }

        public Boolean getBoolean(String columnLabel) {
            return getBoolean(columnLabel, null);
        }

        public Boolean getBoolean(String columnLabel, Boolean defaultValue) {
            return (boolean) this.detail.getOrDefault(columnLabel, defaultValue);
        }

        public Short getShort(String columnLabel) {
            return getShort(columnLabel, null);
        }

        public Short getShort(String columnLabel, Short defaultValue) {
            return (short) this.detail.getOrDefault(columnLabel, defaultValue);
        }

        public Integer getInt(String columnLabel) {
            return getInt(columnLabel, null);
        }

        public Integer getInt(String columnLabel, Integer defaultValue) {
            Object obj = this.detail.get(columnLabel);
            if (obj instanceof Long) {
                long l = (Long) obj;
                if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Bad value for type int: " + l);
                }
                return (int) l;
            } else {
                return (int) this.detail.getOrDefault(columnLabel, defaultValue);
            }
        }

        public Long getLong(String columnLabel) {
            return getLong(columnLabel, null);
        }

        public Long getLong(String columnLabel, Long defaultValue) {
            return (long) this.detail.getOrDefault(columnLabel, defaultValue);
        }

        public Float getFloat(String columnLabel) {
            return getFloat(columnLabel, null);
        }

        public Float getFloat(String columnLabel, Float defaultValue) {
            return (float) this.detail.getOrDefault(columnLabel, defaultValue);
        }

        public Double getDouble(String columnLabel) {
            return getDouble(columnLabel, null);
        }

        public Double getDouble(String columnLabel, Double defaultValue) {
            Object value = this.detail.get(columnLabel);
            if (value instanceof Long) {
                long lValue = (Long) value;
                return (double) lValue;
            } else if (value instanceof Integer) {
                Integer lValue = (Integer) value;
                return (double) lValue;
            } else {
                return (double) this.detail.getOrDefault(columnLabel, 0.0);
            }
        }

        private static <T> T copyMapToBean(Map<String, ?> map, Class<T> clazz) {
            String json = JSONObject.toJSONString(map);
            return JSONObject.parseObject(json, clazz);
        }

        public <T> T getEntity(Class<T> clazz) {
            return copyMapToBean(this.detail, clazz);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Row row = (Row) o;
            return Objects.equals(this.detail, row.detail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.detail);
        }

        @Override
        public Iterator<Map.Entry<String, Object>> iterator() {
            return this.detail.entrySet().iterator();
        }
    }

}
