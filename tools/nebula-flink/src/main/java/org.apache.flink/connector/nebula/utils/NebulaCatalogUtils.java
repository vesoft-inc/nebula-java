/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.utils;

import org.apache.flink.connector.nebula.catalog.NebulaCatalog;

/**
 * util for {@link NebulaCatalog}
 */
public class NebulaCatalogUtils {

    /**
     * Create catalog instance from given information
     */
    public static NebulaCatalog createNebulaCatalog(String catalogName, String defaultSpace, String address, String username, String password) {
        return new NebulaCatalog(catalogName, defaultSpace, username, password, address);
    }
}
