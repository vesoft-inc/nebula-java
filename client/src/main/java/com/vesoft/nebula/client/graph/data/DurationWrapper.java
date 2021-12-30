/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Duration;
import java.util.Objects;

public class DurationWrapper extends BaseDataObject {
    private final Duration duration;

    /**
     * DurationWrapper is a wrapper for the duration type of nebula-graph
     */
    public DurationWrapper(Duration duration) {
        this.duration = duration;
    }

    /**
     * @return utc duration seconds
     */
    public long getSeconds() {
        return duration.seconds;
    }

    /**
     * @retrun utc duration microseconds
     */
    public int getMicroseconds() {
        return duration.microseconds;
    }

    /**
     * @return utc duration months
     */
    public int getMonths() {
        return duration.months;
    }

    /**
     * @return the duration string
     */
    public String getDurationString() {
        return String.format("duration({months:%d, seconds:%d, microseconds:%d})",
                getMonths(), getSeconds(), getMicroseconds());
    }


    @Override
    public String toString() {
        long year = duration.seconds / (60 * 60 * 24);
        long remainSeconds = duration.seconds - (year) * (60 * 60 * 24);
        return String.format("P%dM%dDT%sS",
                duration.months, year, remainSeconds + duration.microseconds / 1000000.0);
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
        return duration.months == that.getMonths()
                && duration.seconds == that.getSeconds()
                && duration.microseconds == that.getMicroseconds();
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration);
    }

}
