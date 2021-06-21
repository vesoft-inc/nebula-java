/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.DateTime;
import java.util.Objects;

public class DateTimeWrapper extends BaseDataObject {
    private final DateTime dateTime;

    public DateTimeWrapper(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return utc datetime year
     */
    public short getYear() {
        return dateTime.getYear();
    }

    /**
     * @return utc datetime month
     */
    public byte getMonth() {
        return dateTime.getMonth();
    }

    /**
     * @return utc datetime day
     */
    public byte getDay() {
        return dateTime.getDay();
    }

    /**
     * @return utc datetime hour
     */
    public byte getHour() {
        return dateTime.getHour();
    }

    /**
     * @return utc datetime minute
     */
    public byte getMinute() {
        return dateTime.getMinute();
    }

    /**
     * @return utc datetime second
     */
    public byte getSecond() {
        return dateTime.getSec();
    }

    /**
     * @return utc datetime microsec
     */
    public int getMicrosec() {
        return dateTime.getMicrosec();
    }

    /**
     * @return the DataTime with the timezone
     */
    public DateTime getLocalDateTime() {
        return TimeUtil.datetimeConvertWithTimezone(dateTime, getTimezoneOffset());
    }

    /**
     * @return the DateTime with the specified timezoneOffset
     */
    public DateTime getDateTimeWithTimezoneOffset(int timezoneOffset) {
        return TimeUtil.datetimeConvertWithTimezone(dateTime, timezoneOffset);
    }

    /**
     * @return the DataTime String with the timezone
     */
    public String getLocalDateTimeStr() {
        DateTime localDateTime = TimeUtil.datetimeConvertWithTimezone(dateTime,
                                                                      getTimezoneOffset());
        return String.format("%d-%02d-%02dT%02d:%02d:%02d.%06d",
            localDateTime.year, localDateTime.month, localDateTime.day,
            localDateTime.hour, localDateTime.minute, localDateTime.sec,
            localDateTime.microsec);
    }

    /**
     * @return the utc DateTime String
     */
    public String getUTCDateTimeStr() {
        return String.format("%d-%02d-%02dT%02d:%02d:%02d.%06d",
            dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.sec, dateTime.microsec);
    }

    /**
     * @return the utc datetime string
     */
    @Override
    public String toString() {
        return String.format("utc datetime: %d-%02d-%02dT%02d:%02d:%02d.%06d, timezoneOffset: %d",
            dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.sec, dateTime.microsec, getTimezoneOffset());
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
