/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.database.exception;

import com.vesoft.nebula.database.constant.ErrorEnum;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description LinkConfigException
 * @Date 2020/3/19 - 15:42
 */
public class LinkConfigException extends RuntimeException {

    private int code;

    private String msg;


    public LinkConfigException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public LinkConfigException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public LinkConfigException(ErrorEnum errorEnum) {
        super(errorEnum.getMsg());
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMsg();
    }

    public LinkConfigException(ErrorEnum errorEnum, Throwable cause) {
        super(errorEnum.getMsg(), cause);
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMsg();
    }

}
