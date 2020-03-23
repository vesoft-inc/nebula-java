/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.database;

import com.vesoft.nebula.database.NebulaDataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import javax.annotation.Resource;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description TestConnect
 * @Date 2020/3/23 - 10:35
 */
public class TestConnect implements ApplicationRunner {

    public static void main(String[] args){

    }

    @Resource
    private NebulaDataSource nebulaDataSource;

    /**
     * to run ExampleApplication than auto test this method
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("start run！");
        for (int i = 0; i < 100; i++) {
            new Thread(new NebulaDatabasePoolTestTh(nebulaDataSource,i)).start();
        }
    }
}
