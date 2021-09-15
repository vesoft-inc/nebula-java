/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.common;

/**
 * Description  ResponseService is used for
 * 返回接口，主要用于枚举与异常的转换
 *
 * @author huangzhaolai-jk
 * Date  2021/7/15 - 15:14
 * @version 1.0.0
 */
public interface ResponseService {
    /**
     * 获取返回码
     *
     * @return
     */
    public String getResponseCode();

    /**
     * 获取返回信息
     *
     * @return
     */
    public String getResponseMessage();
}
