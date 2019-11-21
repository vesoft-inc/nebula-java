/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import java.util.List;

/**
 *
 */
public class Cluster {

    private enum Type {
        META,
        STORAGE,
        GRAPH
    }

    public class Builder {
        private final List<HostAndPort> addresses;
        private String user;
        private String password;
        private long connectionTimeout;
        private int connectionRetry;
        private int executionRetry;

        private Type type;

        public Builder() {
            addresses = Lists.newLinkedList();
        }

        /**
         * Add node address.
         *
         * @param host The host of address.
         * @param port The port of address.
         * @return
         */
        public Cluster.Builder addNode(String host, int port) {
            addresses.forEach(address -> {
                if (address.getHost().equals(host) && address.getPort() == port) {
                    throw new IllegalArgumentException("Address have duplicate");
                }
            });

            addresses.add(HostAndPort.fromParts(host, port));
            return this;
        }

        /**
         * Supplement user name.
         *
         * @param user Nebula user name.
         * @return
         */
        public Cluster.Builder withUser(String user) {
            checkArgument(!Strings.isNullOrEmpty(user));
            this.user = user;
            return this;
        }

        /**
         * Supplement user password.
         *
         * @param password Nebula password.
         * @return
         */
        public Cluster.Builder withPassword(String password) {
            checkArgument(!Strings.isNullOrEmpty(password));
            this.password = password;
            return this;
        }

        /**
         * Supplement connection timeout.
         *
         * @param timeout the connection timeout.
         * @return
         */
        public Cluster.Builder withConnectionTimeout(long timeout) {
            checkArgument(timeout > 0);
            this.connectionTimeout = timeout;
            return this;
        }

        /**
         * Supplement connection retry.
         *
         * @param retry the connection retry.
         * @return
         */
        public Cluster.Builder withConnectionRetry(int retry) {
            checkArgument(retry > 0);
            this.connectionRetry = retry;
            return this;
        }

        /**
         * Supplement execution retry.
         *
         * @param retry the execution retry.
         * @return
         */
        public Cluster.Builder withExecutionRetry(int retry) {
            checkArgument(retry > 0);
            this.executionRetry = retry;
            return this;
        }

        /**
         * @return
         */
        public Cluster.Builder withMeta() {
            type = Type.META;
            return this;
        }

        /**
         * @return
         */
        public Cluster.Builder withStorage() {
            type = Type.STORAGE;
            return this;
        }

        /**
         * @return
         */
        public Cluster.Builder withGraph() {
            type = Type.GRAPH;
            return this;
        }

        /**
         * @return
         */
        public Cluster build() {
            Cluster cluster = new Cluster();
            return cluster;
        }
    }


    /**
     *
     */
    public Session build() {
        return null;
    }
}
