/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Time;
import java.util.Objects;

public class TimeWrapper {
    private Time time;

    public TimeWrapper(Time time) {
        this.time = time;
    }

    public byte getHour() {
        return time.getHour();
    }

    public byte getMinute() {
        return time.getMinute();
    }

    public byte getSecond() {
        return time.getSec();
    }

    public int getMicrosec() {
        return time.getMicrosec();
    }

    public String getLocalTime() {
        // TODO: Need server support set and get timezone
        return "";
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%06d",
            time.hour, time.minute, time.sec, time.microsec);
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
