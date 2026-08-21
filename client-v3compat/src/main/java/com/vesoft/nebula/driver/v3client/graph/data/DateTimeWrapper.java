/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Wrapper for a datetime value, matching the v3 client.
 */
public class DateTimeWrapper extends BaseDataObject {
    private final LocalDateTime utcDateTime;

    public DateTimeWrapper(LocalDateTime localDateTime) {
        this.utcDateTime = localDateTime;
    }

    public DateTimeWrapper(ZonedDateTime zonedDateTime) {
        this.utcDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * @return utc datetime year
     */
    public short getYear() {
        return (short) utcDateTime.getYear();
    }

    /**
     * @return utc datetime month
     */
    public byte getMonth() {
        return (byte) utcDateTime.getMonthValue();
    }

    /**
     * @return utc datetime day
     */
    public byte getDay() {
        return (byte) utcDateTime.getDayOfMonth();
    }

    /**
     * @return utc datetime hour
     */
    public byte getHour() {
        return (byte) utcDateTime.getHour();
    }

    /**
     * @return utc datetime minute
     */
    public byte getMinute() {
        return (byte) utcDateTime.getMinute();
    }

    /**
     * @return utc datetime second
     */
    public byte getSecond() {
        return (byte) utcDateTime.getSecond();
    }

    /**
     * @return utc datetime microsec
     */
    public int getMicrosec() {
        return utcDateTime.getNano() / 1000;
    }

    /**
     * @return the local datetime ({@link LocalDateTime}) after applying
     *     {@link #getTimezoneOffset()}.
     */
    public Object getLocalDateTime() {
        return toLocalDateTime(getTimezoneOffset());
    }

    /**
     * @return the datetime ({@link LocalDateTime}) with the specified timezone offset.
     */
    public Object getDateTimeWithTimezoneOffset(int timezoneOffset) {
        return toLocalDateTime(timezoneOffset);
    }

    /**
     * @return the local datetime string with the timezone offset applied.
     */
    public String getLocalDateTimeStr() {
        return format(toLocalDateTime(getTimezoneOffset()));
    }

    /**
     * @return the utc datetime string.
     */
    public String getUTCDateTimeStr() {
        return format(utcDateTime);
    }

    private LocalDateTime toLocalDateTime(int timezoneOffset) {
        if (timezoneOffset == 0) {
            return utcDateTime;
        }
        return utcDateTime.atOffset(ZoneOffset.UTC)
                          .withOffsetSameInstant(ZoneOffset.ofTotalSeconds(timezoneOffset))
                          .toLocalDateTime();
    }

    private String format(LocalDateTime dateTime) {
        return String.format("%d-%02d-%02dT%02d:%02d:%02d.%06d",
                             dateTime.getYear(), dateTime.getMonthValue(),
                             dateTime.getDayOfMonth(), dateTime.getHour(),
                             dateTime.getMinute(), dateTime.getSecond(),
                             dateTime.getNano() / 1000);
    }

    @Override
    public String toString() {
        return String.format("utc datetime: %s, timezoneOffset: %d", getUTCDateTimeStr(),
                             getTimezoneOffset());
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
        return utcDateTime.equals(that.utcDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(utcDateTime);
    }
}
