/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client;

import com.vesoft.nebula.meta.MetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaClientImpl implements MetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClientImpl.class.getName());

    private MetaService.Client client;

    public MetaClientImpl() {

    }

    @Override
    public void close() throws Exception {
    }
}
