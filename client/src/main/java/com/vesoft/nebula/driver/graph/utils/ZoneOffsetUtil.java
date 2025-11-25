/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.utils;

public class ZoneOffsetUtil {
    static final int SECONDS_PER_MINUTE = 60;
    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    /**
     * build zone offset seconds to String offset
     *
     * @param offsetSeconds offset seconds
     * @return offset string, like +08:00
     */
    public static String buildOffset(int offsetSeconds) {
        if (offsetSeconds == 0) {
            return "Z";
        } else {
            int absOffsetSeconds = Math.abs(offsetSeconds);
            StringBuilder buf = new StringBuilder();
            int absHours = absOffsetSeconds / SECONDS_PER_HOUR;
            int absMinutes = (absOffsetSeconds / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
            buf.append(offsetSeconds < 0 ? "-" : "+")
                    .append(absHours < 10 ? "0" : "").append(absHours)
                    .append(absMinutes < 10 ? ":0" : ":").append(absMinutes);
            int absSeconds = absOffsetSeconds % SECONDS_PER_MINUTE;
            if (absSeconds != 0) {
                buf.append(absSeconds < 10 ? ":0" : ":").append(absSeconds);
            }
            return buf.toString();
        }
    }
}
