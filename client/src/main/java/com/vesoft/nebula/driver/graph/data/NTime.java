/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.proto.common.LocalTime;
import java.util.Objects;

public class NTime {
    private final LocalTime localTime;

    public NTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    /**
     * @return utc Time hour
     */
    public int getHour() {
        return localTime.getHour();
    }

    /**
     * @return utc Time minute
     */
    public int getMinute() {
        return localTime.getMinute();
    }

    /**
     * @return utc Time second
     */
    public int getSecond() {
        return localTime.getSec();
    }

    /**
     * @return utc Time microsec
     */
    public int getMicrosec() {
        return localTime.getMicrosec();
    }


    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%06d",
                localTime.getHour(), localTime.getMinute(),
                localTime.getSec(), localTime.getMicrosec());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NTime that = (NTime) o;
        return localTime.getHour() == that.getHour()
                && localTime.getHour() == that.getMinute()
                && localTime.getSec() == that.getSecond()
                && localTime.getMicrosec() == that.getMicrosec();
    }

    @Override
    public int hashCode() {
        return Objects.hash(localTime);
    }
}
