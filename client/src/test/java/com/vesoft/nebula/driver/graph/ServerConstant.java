/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph;

public class ServerConstant {

    public static String host       = "192.168.8.6";
    public static int    port       = 3820;
    public static int    sslPort    = 4820;
    public static String address    = host + ":" + port;
    public static String sslAddress = host + ":" + sslPort;

    public static String user   = "root";
    public static String passwd = "NebulaGraph01";

    public static String addresses = "127.0.0.1:3820,127.0.0.1:3821,127.0.0.1:3822";

}
