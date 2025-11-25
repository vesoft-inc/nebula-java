/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.utils;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.vesoft.nebula.driver.graph.data.HostAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AddressUtil {

    /**
     * validate the graph addresses
     *
     * @param addresses graph server addresses, multiple addresses are split by comma
     * @return List of HostAddress
     * @throws IllegalArgumentException if address id not split by comma or port is beyond range
     * @throws UnknownHostException     if address host is wrong
     */
    public static List<HostAddress> validateAddress(String addresses) throws UnknownHostException {
        List<HostAddress> newAddrs = new ArrayList<>();
        for (String addr : addresses.split(",")) {
            if (addr == null || addr.isEmpty()) {
                continue;
            }
            String host;
            String portString = null;

            if (addr.startsWith("[")) {
                String[] hostAndPort = getHostAndPortFromBracketedAddr(addr);
                host = hostAndPort[0];
                portString = hostAndPort[1].trim();
            } else {
                int colonPos = addr.indexOf(":");
                if (colonPos >= 0 && addr.indexOf(":", colonPos + 1) == -1) {
                    // exactly 1 colon.
                    host = addr.substring(0, colonPos);
                    portString = addr.substring(colonPos + 1).trim();
                } else if (colonPos == -1) {
                    // no colon
                    throw new IllegalArgumentException("No port:" + addr);
                } else {
                    // many colons
                    throw new IllegalArgumentException(
                        "Possible bracketless IPv6 literal: " + addr);
                }
            }

            int port;
            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unparseable port number: " + addr);
            }

            // get all host name
            InetAddress[] inetAddresses = InetAddress.getAllByName(host);
            for (InetAddress inetAddress : inetAddresses) {
                String ip = inetAddress.getHostAddress();
                if (!(InetAddresses.isInetAddress(ip)
                    || InetAddresses.isUriInetAddress(ip)
                    || InternetDomainName.isValid(ip))
                    || (port <= 0 || port >= 65535)) {
                    throw new IllegalArgumentException(
                        String.format("host %s and port %d is invalid.", ip, port));
                }
            }
            newAddrs.add(new HostAddress(host, port));
        }
        return newAddrs;
    }

    private static String[] getHostAndPortFromBracketedAddr(String hostPortString) {
        int colonIndex        = hostPortString.indexOf(':');
        int closeBracketIndex = hostPortString.lastIndexOf(']');
        if (colonIndex == -1 || closeBracketIndex <= colonIndex) {
            throw new IllegalArgumentException("Invalid bracketed host/port: " + hostPortString);
        }
        String host = hostPortString.substring(1, closeBracketIndex);
        if (closeBracketIndex + 1 == hostPortString.length()) {
            throw new IllegalArgumentException("Invalid port for addr:" + hostPortString);
        } else {
            if (hostPortString.charAt(closeBracketIndex + 1) != ':') {
                throw new IllegalArgumentException(
                    "Only a colon may follow a close bracket:" + hostPortString);
            }
            for (int i = closeBracketIndex + 2; i < hostPortString.length(); ++i) {
                if (!Character.isDigit(hostPortString.charAt(i))) {
                    throw new IllegalArgumentException("Port must be numeric: " + hostPortString);
                }
            }
            return new String[]{host, hostPortString.substring(closeBracketIndex + 2)};
        }
    }
}
