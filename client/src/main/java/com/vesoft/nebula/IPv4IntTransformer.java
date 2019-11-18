package com.vesoft.nebula;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPv4IntTransformer {

    /**
     * Transform Int to IP String
     *
     * @param ip
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
     * @param ipv4Addr
     * @return
     */
    public static int ip2Integer(String ipv4Addr) {
        if (!isIPv4Address(ipv4Addr))
            throw new RuntimeException("Invalid ip address");

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(ipv4Addr);
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
     * @param ipv4Addr
     * @return
     */
    private static boolean isIPv4Address(String ipv4Addr) {
        String lower = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";
        String regex = lower + "(\\." + lower + "){3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipv4Addr);
        return matcher.matches();
    }
}