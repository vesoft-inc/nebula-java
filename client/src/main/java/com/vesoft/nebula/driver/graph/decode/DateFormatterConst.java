/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import java.time.format.DateTimeFormatter;

public class DateFormatterConst {
    public static DateTimeFormatter zonedDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXXXX");
    public static DateTimeFormatter localDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    public static DateTimeFormatter zonedTimeFormatter     =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSXXXXX");
    public static DateTimeFormatter localTimeFormatter     =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS");

}
