/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.common.collect.Lists;
import com.vesoft.nebula.ColumnDef;
import com.vesoft.nebula.NebulaCodec;
import com.vesoft.nebula.Schema;
import com.vesoft.nebula.SupportedType;
import com.vesoft.nebula.ValueType;
import com.vesoft.nebula.utils.NativeUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaCodecTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NebulaCodecTest.class);

    static {
        try {
            NativeUtils.loadLibraryFromJar("/libnebula_codec.so", NebulaCodec.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void codecTest() throws IOException {
        Object[] values = {
            false,
            -1,
            123454321L,
            3.14F,
            0.618,
            "Hello".getBytes()
        };
        byte[] result = NebulaCodec.encode(values);

        NebulaCodec.Pair[] pairs = new NebulaCodec.Pair[]{
            new NebulaCodec.Pair("b_field", Boolean.class.getName()),
            new NebulaCodec.Pair("i_field", Integer.class.getName()),
            new NebulaCodec.Pair("l_field", Long.class.getName()),
            new NebulaCodec.Pair("f_field", Float.class.getName()),
            new NebulaCodec.Pair("d_field", Double.class.getName()),
            new NebulaCodec.Pair("s_field", byte[].class.getName())
        };

        List<byte[]> decodedResult = NebulaCodec.decode(result, pairs, 0);

        assertEquals(1, decodedResult.get(0).length);
        assertEquals(4, decodedResult.get(1).length);
        assertEquals(8, decodedResult.get(2).length);
        assertEquals(4, decodedResult.get(3).length);
        assertEquals(8, decodedResult.get(4).length);
        assertEquals(5, decodedResult.get(5).length);

        byte byteValue = decodedResult.get(0)[0];
        boolean boolValue = (byteValue == 0x00) ? false : true;
        assertFalse(boolValue);

        ByteBuffer integerByteBuffer = ByteBuffer.wrap(decodedResult.get(1));
        integerByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer intBuffer = integerByteBuffer.asIntBuffer();
        assertEquals(-1, intBuffer.get());

        ByteBuffer longByteBuffer = ByteBuffer.wrap(decodedResult.get(2));
        longByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        LongBuffer longbuffer = longByteBuffer.asLongBuffer();
        assertEquals(123454321L, longbuffer.get());

        ByteBuffer floatByteBuffer = ByteBuffer.wrap(decodedResult.get(3));
        floatByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer floatBuffer = floatByteBuffer.asFloatBuffer();
        assertEquals("Float Value ", 3.14F, floatBuffer.get(), 0.0001);

        ByteBuffer doubleByteBuffer = ByteBuffer.wrap(decodedResult.get(4));
        doubleByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        DoubleBuffer doubleBuffer = doubleByteBuffer.asDoubleBuffer();
        assertEquals("Double Value ", 0.618, doubleBuffer.get(), 0.0001);

        ByteBuffer stringByteBuffer = ByteBuffer.wrap(decodedResult.get(5));
        assertEquals("Hello", new String(stringByteBuffer.array()));
    }

    @Test
    public void readerTest() {
        Object[] values = {
            false,
            -1,
            0x112233445566L,
            3.14F,
            0.618,
            "Hello".getBytes()
        };
        final byte[] encoded = NebulaCodec.encode(values);

        // In decode, all Integer and Long need to pass as SupportedType.INT,
        // and all integral types are decoded as Long
        List<ColumnDef> columns = Lists.newArrayList();
        columns.add(new ColumnDef("b_field", new ValueType(SupportedType.BOOL)));
        columns.add(new ColumnDef("i_field", new ValueType(SupportedType.INT)));
        columns.add(new ColumnDef("l_field", new ValueType(SupportedType.INT)));
        columns.add(new ColumnDef("f_field", new ValueType(SupportedType.FLOAT)));
        columns.add(new ColumnDef("d_field", new ValueType(SupportedType.DOUBLE)));
        columns.add(new ColumnDef("s_field", new ValueType(SupportedType.STRING)));
        Schema schema = new Schema();
        schema.setColumns(columns);

        RowReader reader = new RowReader(schema);
        Property[] properties = reader.decodeValue(encoded, 0);
        assertEquals(6, properties.length);

        assertEquals(false, properties[0].getValueAsBool());
        assertEquals(-1, properties[1].getValueAsLong());
        assertEquals(0x112233445566L, properties[2].getValueAsLong());
        assertEquals(3.14F, properties[3].getValueAsFloat(), 0.00001);
        assertEquals(0.618, properties[4].getValueAsDouble(), 0.00001);
        assertEquals("Hello", properties[5].getValueAsString());

        assertEquals(false, properties[0].getValue());
        assertEquals(-1L, properties[1].getValue());
        assertEquals(0x112233445566L, properties[2].getValue());
        assertEquals(3.14F, (Float) properties[3].getValue(), 0.00001);
        assertEquals(0.618, (Double) properties[4].getValue(), 0.00001);
        assertEquals("Hello", properties[5].getValue());
    }
}

