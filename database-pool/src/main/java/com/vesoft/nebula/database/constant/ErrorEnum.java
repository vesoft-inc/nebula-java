/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.database.constant;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description ErrorEnum
 * @Date 2020/3/19 - 10:34
 */
public enum ErrorEnum {
    /**
     * lack connection
     */
    CONNECT_LACK(10001,"lack connection"),

    LINK_ERROR(10002,"nebula address configuration error，format：host1::port1@@user1::password1;;host2::port2@@user2::password2")
    ;

    ErrorEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    /**
     * status code
     */
    private int code;
    /**
     * description
     */
    private String msg;

    public int getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

}
