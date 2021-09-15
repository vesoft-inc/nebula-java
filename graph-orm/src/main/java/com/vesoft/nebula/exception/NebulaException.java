/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.exception;

import com.vesoft.nebula.common.ResponseService;

/**
 * Description  NebulaException is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/15 - 15:16
 * @version 1.0.0
 */
public class NebulaException extends RuntimeException {

    private String code;

    public NebulaException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public NebulaException(Throwable throwable) {
        super(throwable);
        this.code = throwable.getMessage();
    }

    public NebulaException(ResponseService responseService) {
        super(responseService.getResponseMessage());
        this.code = responseService.getResponseCode();
    }

}
