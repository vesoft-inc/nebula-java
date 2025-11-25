/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.proto.common.Date;
import java.time.LocalDateTime;
import java.util.Objects;

public class NDate {
    private final Date date;

    public NDate(Date date) {
        this.date = date;
    }

    public int getYear() {
        return date.getYear();
    }

    public int getMonth() {
        return date.getMonth();
    }

    public int getDay() {
        return date.getDay();
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02d", date.getYear(), date.getMonth(), date.getDay());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NDate that = (NDate) o;
        return date.getYear() == that.getYear()
                && date.getMonth() == that.getMonth()
                && date.getDay() == that.getDay();
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
