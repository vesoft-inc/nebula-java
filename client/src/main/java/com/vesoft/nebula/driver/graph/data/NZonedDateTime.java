/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.driver.graph.utils.ZoneOffsetUtil;
import com.vesoft.nebula.proto.common.ZonedDatetime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class NZonedDateTime {
    private final LocalDateTime localDateTime;
    private final ZoneOffset    zoneOffset;

    public NZonedDateTime(LocalDateTime localDateTime, ZoneOffset zoneOffset) {
        this.localDateTime = localDateTime;
        this.zoneOffset = zoneOffset;
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
        return localDateTime.getMonth().getValue();
    }

    /**
     * @return datetime day
     */
    public int getDay() {
        return localDateTime.getDayOfMonth();
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
        return localDateTime.getSecond();
    }

    /**
     * @return utc datetime microsec
     */
    public int getMicrosec() {
        return localDateTime.getNano() / 1000;
    }

    /**
     * @return zoned offset in seconds
     */
    public int getOffset() {
        return zoneOffset.getTotalSeconds();
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02dT%02d:%02d:%02d.%06d%s",
                             getYear(), getMonth(), getDay(),
                             getHour(), getMinute(), getSecond(),
                             getMicrosec(), ZoneOffsetUtil.buildOffset(getOffset()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NZonedDateTime that = (NZonedDateTime) o;
        return getYear() == that.getYear()
                && getMonth() == that.getMonth()
                && getDay() == that.getDay()
                && getHour() == that.getHour()
                && getMinute() == that.getMinute()
                && getSecond() == that.getSecond()
                && getMicrosec() == that.getMicrosec()
                && getOffset() == that.getOffset();
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDateTime, getOffset());
    }
}
