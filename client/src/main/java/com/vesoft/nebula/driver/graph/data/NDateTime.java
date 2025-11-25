/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;


import com.vesoft.nebula.proto.common.LocalDatetime;
import java.util.Objects;

public class NDateTime {
    private final LocalDatetime localDateTime;

    public NDateTime(LocalDatetime localDateTime) {
        this.localDateTime = localDateTime;
    }

    /**
     * @return utc datetime year
     */
    public int getYear() {
        return localDateTime.getYear();
    }

    /**
     * @return utc datetime month
     */
    public int getMonth() {
        return localDateTime.getMonth();
    }

    /**
     * @return datetime day
     */
    public int getDay() {
        return localDateTime.getDay();
    }

    /**
     * @return datetime hour
     */
    public int getHour() {
        return localDateTime.getHour();
    }

    /**
     * @return datetime minute
     */
    public int getMinute() {
        return localDateTime.getMinute();
    }

    /**
     * @return utc datetime second
     */
    public int getSecond() {
        return localDateTime.getSec();
    }

    /**
     * @return utc datetime microsec
     */
    public int getMicrosec() {
        return localDateTime.getMicrosec();
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02dT%02d:%02d:%02d.%06d",
                localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDay(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSec(),
                localDateTime.getMicrosec());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NDateTime that = (NDateTime) o;
        return localDateTime.getYear() == that.getYear()
                && localDateTime.getMonth() == that.getMonth()
                && localDateTime.getDay() == that.getDay()
                && localDateTime.getHour() == that.getHour()
                && localDateTime.getMinute() == that.getMinute()
                && localDateTime.getSec() == that.getSecond()
                && localDateTime.getMicrosec() == that.getMicrosec();
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDateTime);
    }
}
