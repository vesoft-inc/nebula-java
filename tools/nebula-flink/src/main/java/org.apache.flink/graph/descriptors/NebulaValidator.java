/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.graph.descriptors;

import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.ConnectorDescriptorValidator;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.table.utils.TableSchemaUtils;
import org.apache.flink.util.Preconditions;

import java.util.Optional;

import static org.apache.flink.table.descriptors.Schema.SCHEMA;

public class NebulaValidator extends ConnectorDescriptorValidator {
    public static final String CONNECTOR_TYPE_VALUE_NEBULA = "nebula";

    public static final String CONNECTOR_ADDRESS = "connector.address";
    public static final String CONNECTOR_SPACE = "connector.space";
    public static final String CONNECTOR_LABEL = "connector.label";
    public static final String CONNECTOR_USERNAME = "connector.username";
    public static final String CONNECTOR_PASSWORD = "connector.password";

    public static final String CONNECTOR_READ_QUERY = "connector.read.query";
    public static final String CONNECTOR_READ_PARTITION_COLUMN = "connector.read.partition.column";
    public static final String CONNECTOR_READ_PARTITION_LOWER_BOUND = "connector.read.partition.lower-bound";
    public static final String CONNECTOR_READ_PARTITION_UPPER_BOUND = "connector.read.partition.upper-bound";
    public static final String CONNECTOR_READ_PARTITION_NUM = "connector.read.partition.num";
    public static final String CONNECTOR_READ_FETCH_SIZE = "connector.read.fetch-size";

    public static final String CONNECTOR_LOOKUP_CACHE_MAX_ROWS = "connector.lookup.cache.max-rows";
    public static final String CONNECTOR_LOOKUP_CACHE_TTL = "connector.lookup.cache.ttl";
    public static final String CONNECTOR_LOOKUP_MAX_RETRIES = "connector.lookup.max-retries";

    public static final String CONNECTOR_WRITE_FLUSH_MAX_ROWS = "connector.write.flush.max-rows";
    public static final String CONNECTOR_WRITE_FLUSH_INTERVAL = "connector.write.flush.interval";
    public static final String CONNECTOR_WRITE_MAX_RETRIES = "connector.write.max-retries";

    @Override
    public void validate(DescriptorProperties properties) {
        super.validate(properties);
        validateCommonProperties(properties);
        validateReadProperties(properties);
        validateLookupProperties(properties);
        validateSinkProperties(properties);
    }

    private void validateCommonProperties(DescriptorProperties properties) {
        properties.validateString(CONNECTOR_ADDRESS, false, 1);
        properties.validateString(CONNECTOR_SPACE, false, 1);
        properties.validateString(CONNECTOR_LABEL, true);
        properties.validateString(CONNECTOR_USERNAME, true);
        properties.validateString(CONNECTOR_PASSWORD, true);

        final String address = properties.getString(CONNECTOR_ADDRESS);
        TableSchema schema = TableSchemaUtils.getPhysicalSchema(properties.getTableSchema(SCHEMA));

        Optional<String> password = properties.getOptionalString(CONNECTOR_PASSWORD);
        if (password.isPresent()) {
            Preconditions.checkArgument(
                    properties.getOptionalString(CONNECTOR_USERNAME).isPresent(),
                    "Nebula username must be provided when nebula password is provided");
        }
    }

    private void validateReadProperties(DescriptorProperties properties) {
        properties.validateString(CONNECTOR_READ_QUERY, true);
        properties.validateString(CONNECTOR_READ_PARTITION_COLUMN, true);
        properties.validateLong(CONNECTOR_READ_PARTITION_LOWER_BOUND, true);
        properties.validateLong(CONNECTOR_READ_PARTITION_UPPER_BOUND, true);
        properties.validateInt(CONNECTOR_READ_PARTITION_NUM, true);
        properties.validateInt(CONNECTOR_READ_FETCH_SIZE, true);

        Optional<Long> lowerBound = properties.getOptionalLong(CONNECTOR_READ_PARTITION_LOWER_BOUND);
        Optional<Long> upperBound = properties.getOptionalLong(CONNECTOR_READ_PARTITION_UPPER_BOUND);
        if (lowerBound.isPresent() && upperBound.isPresent()) {
            Preconditions.checkArgument(lowerBound.get() <= upperBound.get(),
                    CONNECTOR_READ_PARTITION_LOWER_BOUND + " must not be larger than " + CONNECTOR_READ_PARTITION_UPPER_BOUND);
        }

        checkAllOrNone(properties, new String[]{
                CONNECTOR_READ_PARTITION_COLUMN,
                CONNECTOR_READ_PARTITION_LOWER_BOUND,
                CONNECTOR_READ_PARTITION_UPPER_BOUND,
                CONNECTOR_READ_PARTITION_NUM
        });
    }

    private void validateLookupProperties(DescriptorProperties properties) {
        properties.validateLong(CONNECTOR_LOOKUP_CACHE_MAX_ROWS, true);
        properties.validateDuration(CONNECTOR_LOOKUP_CACHE_TTL, true, 1);
        properties.validateInt(CONNECTOR_LOOKUP_MAX_RETRIES, true);

        checkAllOrNone(properties, new String[]{
                CONNECTOR_LOOKUP_CACHE_MAX_ROWS,
                CONNECTOR_LOOKUP_CACHE_TTL
        });
    }

    private void validateSinkProperties(DescriptorProperties properties) {
        properties.validateInt(CONNECTOR_WRITE_FLUSH_MAX_ROWS, true);
        properties.validateDuration(CONNECTOR_WRITE_FLUSH_INTERVAL, true, 1);
        properties.validateInt(CONNECTOR_WRITE_MAX_RETRIES, true);
    }

    private void checkAllOrNone(DescriptorProperties properties, String[] propertyNames) {
        int presentCount = 0;
        for (String name : propertyNames) {
            if (properties.getOptionalString(name).isPresent()) {
                presentCount++;
            }
        }
        Preconditions.checkArgument(presentCount == 0 || presentCount == propertyNames.length,
                "Either all or none of the following properties should be provided:\n" + String.join("\n", propertyNames));
    }

}
