/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.graph.descriptors;

import org.apache.flink.table.descriptors.CatalogDescriptorValidator;
import org.apache.flink.table.descriptors.DescriptorProperties;

/**
 * Validator for {@link org.apache.flink.connector.nebula.catalog.NebulaCatalog}
 */
public class NebulaCatalogValidator extends CatalogDescriptorValidator {
    public static final String CATALOG_TYPE_VALUE_NEBULA = "nebula";
    public static final String CATALOG_NEBULA_ADDRESS = "address";
    public static final String CATALOG_NEBULA_USERNAME = "username";
    public static final String CATALOG_NEBULA_PASSWORD = "password";

    @Override
    public void validate(DescriptorProperties properties) {
        super.validate(properties);

        properties.validateValue(CATALOG_TYPE, CATALOG_TYPE_VALUE_NEBULA, false);
        properties.validateString(CATALOG_NEBULA_ADDRESS, false, 1);
        properties.validateString(CATALOG_NEBULA_USERNAME, true, 1);
        properties.validateString(CATALOG_NEBULA_PASSWORD, true, 1);
    }
}
