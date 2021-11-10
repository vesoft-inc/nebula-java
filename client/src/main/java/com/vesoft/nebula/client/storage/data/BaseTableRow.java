/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.client.graph.data.DateTimeWrapper;
import com.vesoft.nebula.client.graph.data.DateWrapper;
import com.vesoft.nebula.client.graph.data.GeographyWrapper;
import com.vesoft.nebula.client.graph.data.TimeWrapper;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class BaseTableRow {
    protected final List<ValueWrapper> values;
    protected String decodeType = "utf-8";

    public BaseTableRow(List<ValueWrapper> values) {
        this.values = values;
    }

    public BaseTableRow(List<ValueWrapper> values, String decodeType) {
        this.values = values;
        this.decodeType = decodeType;
    }

    /**
     * number of elements in vertexTableRow
     */
    public int size() {
        return values.size();
    }

    /**
     * check whether the value at position i is null
     */
    public boolean isNullAt(int i) {
        return values.get(i) == null;
    }

    public String getString(int i) throws UnsupportedEncodingException {
        return values.get(i).asString();
    }

    public long getLong(int i) {
        return values.get(i).asLong();
    }

    public boolean getBoolean(int i) {
        return values.get(i).asBoolean();
    }

    public double getDouble(int i) {
        return values.get(i).asDouble();
    }

    public DateWrapper getDate(int i) {
        return values.get(i).asDate();
    }

    public TimeWrapper getTime(int i) {
        return values.get(i).asTime();
    }

    public DateTimeWrapper getDateTime(int i) {
        return values.get(i).asDateTime();
    }

    public GeographyWrapper getGeography(int i) {
        return values.get(i).asGeography();
    }

    public List<ValueWrapper> getValues() {
        return values;
    }


    /**
     * Displays all elements of this vertexTableRow in a string using a separator string.
     */
    public String mkString(String sep) {
        return mkString("", sep, "");
    }

    /**
     * Displays all elements of this vertexTableRow in a string using
     * start, end, and separator strings.
     */
    public String mkString(String start, String sep, String end) {
        int n = size();
        StringBuilder builder = new StringBuilder();
        builder.append(start);
        if (n > 0) {
            builder.append(values.get(0));
            int i = 1;
            while (i < n) {
                builder.append(sep);
                builder.append(values.get(i));
                i++;
            }
        }
        builder.append(end);
        return builder.toString();
    }

}
