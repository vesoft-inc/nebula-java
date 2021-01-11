/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.DateTime;
import java.util.Objects;

public class DateTimeWrapper {
    private DateTime dateTime;

    public DateTimeWrapper(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public short getYear() {
        return dateTime.getYear();
    }

    public byte getMonth() {
        return dateTime.getMonth();
    }

    public byte getDay() {
        return dateTime.getDay();
    }

    public byte getHour() {
        return dateTime.getHour();
    }

    public byte getMinute() {
        return dateTime.getMinute();
    }

    public byte getSecond() {
        return dateTime.getSec();
    }

    public int getMicrosec() {
        return dateTime.getMicrosec();
    }

    public String getLocalDateTime() {
        // TODO: Need server support set and get timezone
        return "";
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02dT%02d:%02d:%02d.%06d",
            dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.sec, dateTime.microsec);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DateTimeWrapper that = (DateTimeWrapper) o;
        return dateTime.year == that.getYear()
            && dateTime.month == that.getMonth()
            && dateTime.day == that.getDay()
            && dateTime.hour == that.getHour()
            && dateTime.minute == that.getMinute()
            && dateTime.sec == dateTime.getSec()
            && dateTime.microsec == dateTime.getMicrosec();
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime);
    }
}
