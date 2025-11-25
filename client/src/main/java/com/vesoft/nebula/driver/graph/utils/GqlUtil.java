/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.utils;

public class GqlUtil {
    public static String escape(String value) {
        StringBuilder builder = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\"':
                    builder.append("\\\"");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\'':
                    builder.append("\\'");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }
}
