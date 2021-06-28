/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Time;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TimeUtil {
    public static DateTime datetimeConvertWithTimezone(DateTime dateTime, int timezoneOffset) {
        LocalDateTime localDateTime = LocalDateTime.of(dateTime.getYear(),
            dateTime.getMonth(),
            dateTime.getDay(),
            dateTime.getHour(),
            dateTime.getMinute(),
            dateTime.getSec(),
            dateTime.getMicrosec() * 1000);
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffset);
        OffsetDateTime utcOffsetDateTime = localDateTime.atOffset(ZoneOffset.UTC);
        OffsetDateTime localOffsetDateTime = utcOffsetDateTime.withOffsetSameInstant(zoneOffset);
        return new DateTime((short) localOffsetDateTime.getYear(),
                            (byte) localOffsetDateTime.getMonth().getValue(),
                            (byte) localOffsetDateTime.getDayOfMonth(),
                            (byte) localOffsetDateTime.getHour(),
                            (byte) localOffsetDateTime.getMinute(),
                            (byte) localOffsetDateTime.getSecond(),
            localOffsetDateTime.getNano() / 1000);
    }

    public static Time timeConvertWithTimezone(Time time, int timezoneOffset) {
        DateTime dateTime = new DateTime(
            (short) 0,(byte)1, (byte)1,
            time.getHour(), time.getMinute(), time.getSec(), time.getMicrosec());
        DateTime localDateTime = datetimeConvertWithTimezone(dateTime, timezoneOffset);
        return new Time(localDateTime.getHour(),
                        localDateTime.getMinute(),
                        localDateTime.getSec(),
                        localDateTime.getMicrosec());
    }
}
