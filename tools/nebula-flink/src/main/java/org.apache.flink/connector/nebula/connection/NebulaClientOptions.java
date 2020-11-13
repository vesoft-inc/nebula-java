/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.connection;

import java.io.Serializable;

public class NebulaClientOptions implements Serializable {

    private static final long serialVersionUID = 5685521189643221375L;

    protected final String address;

    protected final String username;

    protected final String password;


    private NebulaClientOptions(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    /**
     * Builder for {@link NebulaClientOptions}
     */
    public static class NebulaClientOptionsBuilder {
        private String address;
        private String username;
        private String password;

        public NebulaClientOptionsBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public NebulaClientOptionsBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public NebulaClientOptionsBuilder setPassword(String password) {
            this.password = password;
            return this;
        }


        public NebulaClientOptions build() {
            return new NebulaClientOptions(address, username, password);
        }
    }
}
