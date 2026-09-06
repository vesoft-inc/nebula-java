/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

/**
 * Timezone conversion helpers used by the compatibility wrappers.
 *
 * <p>The v3 client operated on Thrift time structs; the compatibility layer operates directly on
 * {@code java.time} types, so these helpers have matching names but java.time signatures.
 */
public class TimeUtil {

    /**
     * @param utcDateTime the utc datetime
     * @param timezoneOffset the timezone offset, unit is seconds
     * @return the datetime shifted to the timezone offset
     */
    public static LocalDateTime datetimeConvertWithTimezone(LocalDateTime utcDateTime,
                                                            int timezoneOffset) {
        if (timezoneOffset == 0) {
            return utcDateTime;
        }
        return utcDateTime.atOffset(ZoneOffset.UTC)
                          .withOffsetSameInstant(ZoneOffset.ofTotalSeconds(timezoneOffset))
                          .toLocalDateTime();
    }

    /**
     * @param utcTime the utc time
     * @param timezoneOffset the timezone offset, unit is seconds
     * @return the time shifted to the timezone offset
     */
    public static LocalTime timeConvertWithTimezone(LocalTime utcTime, int timezoneOffset) {
        if (timezoneOffset == 0) {
            return utcTime;
        }
        return utcTime.atOffset(ZoneOffset.UTC)
                      .withOffsetSameInstant(ZoneOffset.ofTotalSeconds(timezoneOffset))
                      .toLocalTime();
    }
}
