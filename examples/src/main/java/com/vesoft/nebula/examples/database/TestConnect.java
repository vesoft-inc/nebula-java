package com.vesoft.nebula.examples.database;

import com.vesoft.nebula.database.NebulaDataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import javax.annotation.Resource;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description TestConnect is used for
 * @Date 2020/3/23 - 10:35
 */
public class TestConnect implements ApplicationRunner {

    public static void main(String[] args){

    }

    @Resource
    private NebulaDataSource nebulaDataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("启动中！");
        for (int i = 0; i < 100; i++) {
            new Thread(new NebulaDatabasePoolTestTh(nebulaDataSource,i)).start();
        }
    }
}
