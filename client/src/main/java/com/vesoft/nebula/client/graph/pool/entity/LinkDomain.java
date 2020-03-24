/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.pool.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description LinkDomain is used for store the information of the link
 * @Date 2020/3/19 - 10:19
 */
@Data
public class LinkDomain implements Serializable {
    /**
     * nebula host
     */
    private String host;
    /**
     * nebula port
     */
    private int port;

    private String userName;

    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkDomain that = (LinkDomain) o;
        return port == that.port
                && Objects.equals(host, that.host)
                && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, userName);
    }

    @Override
    public String toString() {
        return "LinkDomain{"
                + "host='" + host + '\''
                + ", port=" + port
                + ", userName='" + userName + '\''
                + '}';
    }
}
