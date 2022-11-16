/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Date;
import java.util.Objects;

public class DateWrapper extends BaseDataObject {
    private Date date;

    public DateWrapper(Date date) {
        this.date = date;
    }

    public short getYear() {
        return date.getYear();
    }

    public byte getMonth() {
        return date.getMonth();
    }

    public byte getDay() {
        return date.getDay();
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02d", date.year, date.month, date.day);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DateWrapper that = (DateWrapper) o;
        return date.year == that.getYear()
            && date.month == that.getMonth()
            && date.day == that.getDay();
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
