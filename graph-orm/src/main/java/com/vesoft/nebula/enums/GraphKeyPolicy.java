package com.vesoft.nebula.enums;

import lombok.Getter;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description CaseStatusEnum is used for
 * @Date 2020/1/14 - 15:06
 */
public enum GraphKeyPolicy {

    /**
     * uuid
     */
    uuid("uuid"),
    /**
     * hash id
     */
    hash("hash"),
    /**
     * 字符串id
     */
    string_key(""),

    ;

    @Getter
    private String keyWrapWord;


    GraphKeyPolicy(String keyWrapWord) {
        this.keyWrapWord = keyWrapWord;
    }

}
