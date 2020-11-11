/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.graph.descriptors;

import org.apache.flink.table.descriptors.CatalogDescriptor;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.util.StringUtils;

import java.util.Map;

import static org.apache.flink.graph.descriptors.NebulaCatalogValidator.*;
import static org.apache.flink.util.Preconditions.checkArgument;

public class NebulaCatalogDescriptor extends CatalogDescriptor {
    private final String address;
    private final String username;
    private final String password;

    public NebulaCatalogDescriptor(String address, String username, String password) {
        super(CATALOG_TYPE_VALUE_NEBULA, 1);
        checkArgument(!StringUtils.isNullOrWhitespaceOnly(address));
        // Nebula 1.1.0 allow no username and password
        // checkArgument(!StringUtils.isNullOrWhitespaceOnly(username));
        // checkArgument(!StringUtils.isNullOrWhitespaceOnly(password));

        this.address = address;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Map<String, String> toCatalogProperties() {
        final DescriptorProperties properties = new DescriptorProperties();

        properties.putString(CATALOG_NEBULA_ADDRESS, address);
        properties.putString(CATALOG_NEBULA_USERNAME, username);
        properties.putString(CATALOG_NEBULA_PASSWORD, password);
        return properties.asMap();
    }
}
