/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Time;
import java.util.Objects;

public class TimeWrapper extends BaseDataObject {
    private final Time time;

    /**
     *  TimeWrapper is a wrapper for the time tyoe of nebula-graph
     * @param time the utc time
     */
    public TimeWrapper(Time time) {
        this.time = time;
    }

    /**
     * @return utc Time hour
     */
    public byte getHour() {
        return time.getHour();
    }

    /**
     * @return utc Time minute
     */
    public byte getMinute() {
        return time.getMinute();
    }

    /**
     * @return utc Time second
     */
    public byte getSecond() {
        return time.getSec();
    }

    /**
     * @return utc Time microsec
     */
    public int getMicrosec() {
        return time.getMicrosec();
    }

    /**
     * @return the Time with the timezone
     */
    public Time getLocalTime() {
        return TimeUtil.timeConvertWithTimezone(time, getTimezoneOffset());
    }

    /**
     * @return the Time with the specified timezoneOffset
     */
    public Time getTimeWithTimezoneOffset(int timezoneOffset) {
        return TimeUtil.timeConvertWithTimezone(time, timezoneOffset);
    }

    /**
     * @return the Time String with the timezone
     */
    public String getLocalTimeStr() {
        Time localTime = TimeUtil.timeConvertWithTimezone(time, getTimezoneOffset());
        return String.format("%02d:%02d:%02d.%06d",
            localTime.hour, localTime.minute, localTime.sec, localTime.microsec);
    }

    /**
     * @return the utc Time String
     */
    public String getUTCTimeStr() {
        return String.format("%02d:%02d:%02d.%06d",
                             time.hour, time.minute, time.sec, time.microsec);
    }

    /**
     * @return the utc time string
     */
    @Override
    public String toString() {
        return String.format("utc time: %02d:%02d:%02d.%06d, timezoneOffset: %d",
            time.hour, time.minute, time.sec, time.microsec, getTimezoneOffset());
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
        return time.hour == that.getHour()
            && time.minute == that.getMinute()
            && time.sec == time.getSec()
            && time.microsec == time.getMicrosec();
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }
}
