/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public final class AddressUtil {

    /**
     * Transform Int to IP String
     *
     * @param ip The encoded ip address
     * @return
     */
    public static String intToIPv4(int ip) {
        StringBuilder sb = new StringBuilder();
        int num = 0;
        boolean needPoint = false;
        for (int i = 0; i < 4; i++) {
            if (needPoint) {
                sb.append('.');
            }
            needPoint = true;
            int offset = 8 * (3 - i);
            num = (ip >> offset) & 0xff;
            sb.append(num);
        }
        return sb.toString();
    }

    /**
     * Transform IP String to int
     *
     * @param address The IP address.
     * @return
     */
    public static int ip2Integer(String address) {
        if (!isIPv4Address(address)) {
            throw new RuntimeException("Invalid ip address");
        }

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(address);
        int result = 0;
        int counter = 0;
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group());
            result = (value << 8 * (3 - counter++)) | result;
        }
        return result;
    }

    /**
     * Check is the IP String valid or not
     *
     * @param address The IP address.
     * @return
     */
    private static boolean isIPv4Address(String address) {
        String lower = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";
        String regex = lower + "(\\." + lower + "){3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }
}