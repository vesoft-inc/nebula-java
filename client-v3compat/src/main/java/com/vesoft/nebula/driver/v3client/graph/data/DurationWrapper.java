/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.graph.data.NDuration;
import java.util.Objects;

/**
 * Wrapper for a duration value, matching the v3 client.
 *
 * <p>The v5 duration carries a richer field set (years/months/days/hours/minutes/seconds/
 * microseconds); it is folded into the v3 three-field model ({@code months}/{@code seconds}/
 * {@code microseconds}) on a best-effort basis.
 */
public class DurationWrapper extends BaseDataObject {
    private final NDuration duration;

    public DurationWrapper(NDuration duration) {
        this.duration = duration;
    }

    /**
     * @return the seconds part of the duration.
     */
    public long getSeconds() {
        if (duration.isMonthBased()) {
            return 0;
        }
        return duration.getDay() * 86400L + duration.getHour() * 3600L
               + duration.getMinute() * 60L + duration.getSecond();
    }

    /**
     * @return the microseconds part of the duration.
     */
    public int getMicroseconds() {
        return duration.getMicrosecond();
    }

    /**
     * @return the months part of the duration.
     */
    public int getMonths() {
        if (!duration.isMonthBased()) {
            return 0;
        }
        return duration.getYear() * 12 + duration.getMonth();
    }

    /**
     * @return the duration string.
     */
    public String getDurationString() {
        return String.format("duration({months:%d, seconds:%d, microseconds:%d})",
                             getMonths(), getSeconds(), getMicroseconds());
    }

    @Override
    public String toString() {
        return duration.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DurationWrapper that = (DurationWrapper) o;
        return duration.equals(that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration);
    }
}
