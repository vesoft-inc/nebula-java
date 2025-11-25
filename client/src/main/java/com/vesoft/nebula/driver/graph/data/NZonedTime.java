/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.driver.graph.utils.ZoneOffsetUtil;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class NZonedTime {

    private final int        hour;
    private final int        minute;
    private final int        second;
    private final int        microSec;
    private final ZoneOffset timeZoneOffset;

    public NZonedTime(int hour, int minute, int second, int microSec, ZoneOffset timeZoneOffset) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.microSec = microSec;
        this.timeZoneOffset = timeZoneOffset;
    }

    public NZonedTime(LocalTime localTime, ZoneOffset timeZoneOffset) {
        this.hour = localTime.getHour();
        this.minute = localTime.getMinute();
        this.second = localTime.getSecond();
        this.microSec = localTime.getNano() / 1000;
        this.timeZoneOffset = timeZoneOffset;
    }

    /**
     * @return utc Time hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * @return utc Time minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * @return utc Time second
     */
    public int getSecond() {
        return second;
    }

    /**
     * @return utc Time microsec
     */
    public int getMicrosec() {
        return microSec;
    }

    /**
     * get zone offset in seconds
     *
     * @return offset
     */
    public int getOffset() {
        return timeZoneOffset.getTotalSeconds();
    }


    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%06d%s",
                             hour,
                             minute,
                             second,
                             microSec,
                             ZoneOffsetUtil.buildOffset(getOffset()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NZonedTime that = (NZonedTime) o;
        return hour == that.getHour()
                && minute == that.getMinute()
                && second == that.getSecond()
                && microSec == that.getMicrosec()
                && getOffset() == that.getOffset();
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute, second, microSec, getOffset());
    }
}
