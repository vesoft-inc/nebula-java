package com.vesoft.nebula.domain.impl;

import com.vesoft.nebula.domain.CommonResultSet;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author zhoupeng
 * @date 2019/2/14
 */
public abstract class GraphPropertyEntity implements CommonResultSet, Serializable, Cloneable {
    private final Map<String, Object> props;

    public boolean containsKey(String key) {
        return props.containsKey(key);
    }

    public GraphPropertyEntity(Map<String, Object> props) {
        this.props = props;
    }

    /**
     * 设置属性值
     *
     * @param propertyName
     * @param propertyValue
     */
    public void setProperty(String propertyName, Object propertyValue) {
        props.put(propertyName, propertyValue);
    }

    public Map<String, Object> getProps() {
        return this.props;
    }

    @Override
    public String getString(String columnLabel) {
        return (String) props.getOrDefault(columnLabel, "");
    }

    @Override
    public boolean getBoolean(String columnLabel) {
        return (boolean) props.getOrDefault(columnLabel, false);
    }

    @Override
    public short getShort(String columnLabel) {
        return (short) props.getOrDefault(columnLabel, 0);
    }

    @Override
    public int getInt(String columnLabel) {
        Object obj = props.get(columnLabel);
        if (obj instanceof Long) {
            long l = (Long) obj;
            if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Bad value for type int: " + l);
            }

            return (int) l;
        } else {
            return (int) props.getOrDefault(columnLabel, 0);
        }
    }

    @Override
    public long getLong(String columnLabel) {
        return (long) props.getOrDefault(columnLabel, 0L);
    }

    @Override
    public float getFloat(String columnLabel) {
        return (float) props.getOrDefault(columnLabel, 0.0);
    }

    @Override
    public double getDouble(String columnLabel) {
        return (double) props.getOrDefault(columnLabel, 0.0);
    }

    @Override
    public Date getDate(String columnLabel) {
        return (Date) props.get(columnLabel);
    }

    @Override
    public Time getTime(String columnLabel) {
        return (Time) props.get(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) {
        return (Timestamp) props.get(columnLabel);
    }

    @Override
    public Object getObject(String columnLabel) {
        return props.get(columnLabel);
    }
}
