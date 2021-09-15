/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.enums;

import lombok.Getter;

/**
 * Description  EdgeDirectionEnum is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 14:38
 * @version 1.0.0
 */
public enum EdgeDirectionEnum {

    /**
     * 出边，默认值
     */
    OUT_GOING(""),
    /**
     * 反向，逆向查询
     */
    REVERSELY("REVERSELY"),
    /**
     * 双向，双向查询
     */
    BIDIRECT("BIDIRECT"),
    ;

    @Getter
    private String word;

    EdgeDirectionEnum(String word) {
        this.word = word;
    }

}
