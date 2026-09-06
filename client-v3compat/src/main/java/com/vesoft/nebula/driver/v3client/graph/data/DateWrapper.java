/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Wrapper for a date value, matching the v3 client.
 */
public class DateWrapper extends BaseDataObject {
    private final LocalDate date;

    public DateWrapper(LocalDate date) {
        this.date = date;
    }

    public short getYear() {
        return (short) date.getYear();
    }

    public byte getMonth() {
        return (byte) date.getMonthValue();
    }

    public byte getDay() {
        return (byte) date.getDayOfMonth();
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02d", date.getYear(), date.getMonthValue(),
                             date.getDayOfMonth());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DateWrapper that = (DateWrapper) o;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
