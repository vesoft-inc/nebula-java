/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.AbstractClient;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.meta.IdName;
import java.util.List;
import java.util.Map;

public abstract class MetaClient extends AbstractClient {

    public MetaClient(List<HostAndPort> addresses, int timeout,
                      int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public MetaClient(List<HostAndPort> addresses) {
        super(addresses);
    }

    public MetaClient(String host, int port) {
        super(host, port);
    }

    public abstract List<IdName> listSpaces();

    public abstract int getSpaceIDFromCache(String name);

    public abstract List<HostAddr> getPart(String spaceName, int partID);

    public abstract Map<String, Map<Integer, List<HostAddr>>> getParts();

    public abstract Integer getTagId(String spaceName, String tagName);

    public abstract Integer getEdgeType(String spaceName, String edgeName);

    /**
     * Get tag schema with specified version.
     *
     * @param spaceName the space's name
     * @param tagName   the tag's name
     * @param version   the tag's version
     * @return
     */
    public abstract Map<String, Class> getTagSchema(String spaceName,
                                                    String tagName,
                                                    long version);

    /**
     * Get edge schema with specified version.
     *
     * @param spaceName the space's name
     * @param edgeName  the edge's name
     * @param version   the edge's version
     * @return
     */
    public abstract Map<String, Class> getEdgeSchema(String spaceName,
                                                     String edgeName,
                                                     long version);

}

