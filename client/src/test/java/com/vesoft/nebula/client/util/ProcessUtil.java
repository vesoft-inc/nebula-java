/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessUtil {
    public static void printProcessStatus(String cmd, Process p) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            System.out.print(cmd + " output: ");
            while ((line = reader.readLine()) != null) {
                System.out.print(line);
            }
            System.out.print("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
