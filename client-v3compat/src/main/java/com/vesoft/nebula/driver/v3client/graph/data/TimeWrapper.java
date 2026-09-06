/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Wrapper for a time value, matching the v3 client.
 *
 * <p>The stored time is normalized to UTC; the local-time helpers apply the
 * {@link #getTimezoneOffset() timezone offset} inherited from {@link BaseDataObject}.
 */
public class TimeWrapper extends BaseDataObject {
    private final LocalTime utcTime;

    public TimeWrapper(LocalTime localTime) {
        this.utcTime = localTime;
    }

    public TimeWrapper(OffsetTime offsetTime) {
        this.utcTime = offsetTime.withOffsetSameInstant(ZoneOffset.UTC).toLocalTime();
    }

    /**
     * @return utc Time hour
     */
    public byte getHour() {
        return (byte) utcTime.getHour();
    }

    /**
     * @return utc Time minute
     */
    public byte getMinute() {
        return (byte) utcTime.getMinute();
    }

    /**
     * @return utc Time second
     */
    public byte getSecond() {
        return (byte) utcTime.getSecond();
    }

    /**
     * @return utc Time microsec
     */
    public int getMicrosec() {
        return utcTime.getNano() / 1000;
    }

    /**
     * @return the local time ({@link LocalTime}) after applying {@link #getTimezoneOffset()}.
     */
    public Object getLocalTime() {
        return toLocalTime(getTimezoneOffset());
    }

    /**
     * @return the time ({@link LocalTime}) with the specified timezone offset.
     */
    public Object getTimeWithTimezoneOffset(int timezoneOffset) {
        return toLocalTime(timezoneOffset);
    }

    /**
     * @return the local time string with the timezone offset applied.
     */
    public String getLocalTimeStr() {
        return format(toLocalTime(getTimezoneOffset()));
    }

    /**
     * @return the utc time string.
     */
    public String getUTCTimeStr() {
        return format(utcTime);
    }

    private LocalTime toLocalTime(int timezoneOffset) {
        if (timezoneOffset == 0) {
            return utcTime;
        }
        return utcTime.atOffset(ZoneOffset.UTC)
                       .withOffsetSameInstant(ZoneOffset.ofTotalSeconds(timezoneOffset))
                       .toLocalTime();
    }

    private String format(LocalTime time) {
        return String.format("%02d:%02d:%02d.%06d",
                             time.getHour(), time.getMinute(), time.getSecond(),
                             time.getNano() / 1000);
    }

    @Override
    public String toString() {
        return String.format("utc time: %s, timezoneOffset: %d", getUTCTimeStr(),
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
        TimeWrapper that = (TimeWrapper) o;
        return utcTime.equals(that.utcTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(utcTime);
    }
}
