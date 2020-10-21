/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception{
        BufferedWriter out = new BufferedWriter( new OutputStreamWriter(
                new FileOutputStream("/Users/nicole/Documents/tmp/vertex.json", true)));

        String template = "{\"vertexId\":%d,\"name\":\"%s\",\"age\":%d}";
        Random random = new Random();
        for(int i = 0; i< 10000000; i++){
            String value = String.format(template, random.nextInt(100000000), "Tom" + random.nextInt(10000000), random.nextInt(100));
            out.write(value + "\n");
        }
        out.close();

    }
}
