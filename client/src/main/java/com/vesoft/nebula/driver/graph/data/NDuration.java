/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import java.util.Objects;

public class NDuration {
    private final boolean isMonthBased;
    private final int     year;
    private final int     month;
    private final int     day;
    private final int     hour;
    private final int     minute;
    private final int     second;
    private final int     microSec;

    public NDuration(boolean isMonthBased,
                     int year,
                     int month,
                     int day,
                     int hour,
                     int minute,
                     int second,
                     int microSec) {
        this.isMonthBased = isMonthBased;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.microSec = microSec;
    }


    /**
     * @return whether the duration is month-based duration type
     */
    public boolean isMonthBased() {
        return isMonthBased;
    }

    /**
     * @return duration year
     */
    public int getYear() {
        return year;
    }

    /**
     * @return duration month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @return duration year
     */
    public int getDay() {
        return day;
    }

    /**
     * @return duration hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * @return duration minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * @return duration seconds
     */
    public int getSecond() {
        return second;
    }

    /**
     * @return duration microseconds
     */
    public int getMicrosecond() {
        return microSec;
    }

    @Override
    public String toString() {
        StringBuilder durationStr = new StringBuilder();
        durationStr.append("P");
        if (isMonthBased()) {
            if (getYear() != 0) {
                durationStr.append(getYear()).append("Y");
            }
            if (getMonth() != 0 || getYear() == 0) {
                durationStr.append(getMonth()).append("M");
            }
        } else {
            if (getDay() != 0) {
                durationStr.append(getDay()).append("D");
            }
            if (getDay() == 0 || getHour() != 0 || getMinute() != 0
                    || getSecond() != 0 || getMicrosecond() != 0) {
                durationStr.append("T");
            }
            if (getHour() != 0) {
                durationStr.append(getHour()).append("H");
            }
            if (getMinute() != 0) {
                durationStr.append(getMinute()).append("M");
            }
            if (getSecond() != 0
                    || getMicrosecond() != 0
                    || (getDay() == 0 && getHour() == 0 && getMinute() == 0 && getSecond() == 0)) {
                if (getMicrosecond() == 0) {
                    durationStr.append(getSecond()).append("S");
                } else {
                    int     totalMicroseconds = getSecond() * 1000000 + getMicrosecond();
                    boolean isMinus           = totalMicroseconds < 0;
                    if (isMinus) {
                        totalMicroseconds = -totalMicroseconds;
                    }
                    int seconds  = totalMicroseconds / 1000000;
                    int microSec = totalMicroseconds % 1000000;
                    if (isMinus) {
                        durationStr
                                .append(String.format("-%d.%06d", seconds, microSec));
                    } else {
                        durationStr
                                .append(String.format("%d.%06d", seconds, microSec));
                    }
                    while (durationStr.charAt(durationStr.length() - 1) == '0') {
                        durationStr.setLength(durationStr.length() - 1);
                    }
                    durationStr.append("S");
                }
            }
        }
        return durationStr.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NDuration that = (NDuration) o;
        return isMonthBased == that.isMonthBased()
                && year == that.getYear()
                && month == that.getMonth()
                && day == that.getDay()
                && hour == that.getHour()
                && minute == that.getMinute()
                && second == that.getSecond()
                && microSec == that.getMicrosecond();
    }

    @Override
    public int hashCode() {
        return Objects.hash(isMonthBased, year, month, day, hour, minute, second, microSec);
    }
}
