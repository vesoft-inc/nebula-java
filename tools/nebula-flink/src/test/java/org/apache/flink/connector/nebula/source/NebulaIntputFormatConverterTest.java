/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.source;

import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.PropertyDef;
import com.vesoft.nebula.data.Row;
import org.junit.Test;

public class NebulaIntputFormatConverterTest {
    @Test
    public void convert(){
        NebulaRowVertexInputFormatConverter converter = new NebulaRowVertexInputFormatConverter();

        Property[] defaultProps = new Property[1];
        defaultProps[0] = new Property(PropertyDef.PropertyType.VID, "id", 1L);
        Property[] props = new Property[2];
        props[0] = new Property(PropertyDef.PropertyType.STRING, "name", "nicole");
        props[1] = new Property(PropertyDef.PropertyType.INT, "age", 22L);
        Row row = new Row(defaultProps, props);
        org.apache.flink.types.Row flinkRow = converter.convert(row);
        assert (flinkRow.getArity() == 3);
        assert (flinkRow.getField(0) == defaultProps[0]);
    }
}
