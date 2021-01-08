/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Time;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class BaseTableRow {
    protected final List<Object> values;
    protected String decodeType = "utf-8";

    public BaseTableRow(List<Object> values) {
        this.values = values;
    }

    public BaseTableRow(List<Object> values, String decodeType) {
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

    public String getString(int i) {
        if (values.get(i) instanceof String) {
            return (String) values.get(i);
        } else {
            throw new ClassCastException("value is not String type");
        }
    }

    public long getLong(int i) {
        if (values.get(i) instanceof Long) {
            return (long) values.get(i);
        } else {
            throw new ClassCastException("value is not long type");
        }
    }

    public boolean getBoolean(int i) {
        if (values.get(i) instanceof Boolean) {
            return (boolean) values.get(i);
        } else {
            throw new ClassCastException("value is not boolean type");
        }
    }

    public double getDouble(int i) {
        if (values.get(i) instanceof Double) {
            return (double) values.get(i);
        } else {
            throw new ClassCastException("value is not boolean type");
        }
    }

    public Date getDate(int i) {
        if (values.get(i) instanceof Date) {
            return (Date) values.get(i);
        } else {
            throw new ClassCastException("value is not nebula Date type");
        }
    }


    public Time getTime(int i) {
        if (values.get(i) instanceof Time) {
            return (Time) values.get(i);
        } else {
            throw new ClassCastException("value is not nebula Time type");
        }
    }

    public DateTime getDateTime(int i) {
        if (values.get(i) instanceof DateTime) {
            return (DateTime) values.get(i);
        } else {
            throw new ClassCastException("value is not nebula DateTime type");
        }
    }

    public List<Object> getValues() {
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
