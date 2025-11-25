/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToBool;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToDouble;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToFloat;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt16;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt32;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt64;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt8;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToUInt16;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToUInt32;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToUInt8;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.getVectorType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.decode.datatype.VectorType;
import com.vesoft.nebula.proto.graph.NestedVector;
import com.vesoft.nebula.proto.graph.VectorCommonMetaData;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import org.junit.Test;

public class DecodeUtilsTest {

    @Test
    public void testBytesToInt8() {
        //  test positive and boundary Value
        ByteString data   = ByteString.copyFrom(new byte[]{0x7F});  // 127 in decimal
        int        result = bytesToInt8(data);
        assertEquals(127, result);

        // test negative and boundary Value
        data = ByteString.copyFrom(new byte[]{(byte) 0x80});  // -128 in decimal
        result = bytesToInt8(data);
        assertEquals(-128, result);

        // test zero Value
        data = ByteString.copyFrom(new byte[]{0x00});
        result = bytesToInt8(data);
        assertEquals(0, result);

        // test empty data
        ByteString emptyData = ByteString.copyFrom(new byte[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> {
            bytesToInt8(emptyData);
        });
    }


    @Test
    public void testBytesToUInt8() {
        // test positive Value
        ByteString data   = ByteString.copyFrom(new byte[]{0x7F});  // 127 in decimal
        int        result = bytesToUInt8(data);
        assertEquals(127, result);

        // test negative and boundary Value
        data = ByteString.copyFrom(new byte[]{(byte) 0xFF});  // -1 in signed, 255 in unsigned
        result = bytesToUInt8(data);
        assertEquals(255, result);

        // test zero Value
        data = ByteString.copyFrom(new byte[]{0x00});
        result = bytesToUInt8(data);
        assertEquals(0, result);

        // test empty data
        ByteString emptyData = ByteString.copyFrom(new byte[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> {
            bytesToUInt8(emptyData);
        });
    }


    @Test
    public void testBytesToInt16() {
        // test positive value
        ByteString data   = ByteString.copyFrom(new byte[]{0x01, 0x7F}); //383 in big-endian
        short      result = bytesToInt16(data, ByteOrder.BIG_ENDIAN);
        assertEquals(383, result);

        data = ByteString.copyFrom(new byte[]{0x7F, 0x01});  // 383 in little-endian
        result = bytesToInt16(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(383, result);

        // test negative value
        data = ByteString.copyFrom(new byte[]{(byte) 0xFF, (byte) 0x80}); // -128 in big-endian
        result = bytesToInt16(data, ByteOrder.BIG_ENDIAN);
        assertEquals(-128, result);

        data = ByteString.copyFrom(new byte[]{(byte) 0x80, (byte) 0xFF});  // -128 in little-endian
        result = bytesToInt16(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(-128, result);

        // test zero value
        data = ByteString.copyFrom(new byte[]{0x00, 0x00});
        result = bytesToInt16(data, ByteOrder.BIG_ENDIAN);
        assertEquals(0, result);
        result = bytesToInt16(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(0, result);

        // test boundary Values BigEndian
        // 32767 big-endian
        ByteString maxPositive = ByteString.copyFrom(new byte[]{0x7F, (byte) 0xFF});
        // -32768 big-endian
        ByteString maxNegative = ByteString.copyFrom(new byte[]{(byte) 0x80, 0x00});
        result = bytesToInt16(maxPositive, ByteOrder.BIG_ENDIAN);
        assertEquals((short) 32767, result);
        result = bytesToInt16(maxNegative, ByteOrder.BIG_ENDIAN);
        assertEquals((short) -32768, result);

        // test boundary Values LittleEndian
        // 32767 in decimal (little-endian)
        maxPositive = ByteString.copyFrom(new byte[]{(byte) 0xFF, 0x7F});
        // -32768 in decimal (little-endian)
        maxNegative = ByteString.copyFrom(new byte[]{0x00, (byte) 0x80});
        result = bytesToInt16(maxPositive, ByteOrder.LITTLE_ENDIAN);
        assertEquals(32767, result);
        result = bytesToInt16(maxNegative, ByteOrder.LITTLE_ENDIAN);
        assertEquals(-32768, result);
    }


    @Test
    public void testBytesToUInt16() {
        // test positive Value BigEndian
        ByteString data   = ByteString.copyFrom(new byte[]{0x01, 0x7F});  // 383 in big-endian
        int        result = bytesToUInt16(data, ByteOrder.BIG_ENDIAN);
        assertEquals(383, result);

        // test positive Value LittleEndian
        data = ByteString.copyFrom(new byte[]{0x7F, 0x01});  // 383 in little-endian
        result = bytesToUInt16(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(383, result);

        // test negative Value BigEndian
        // -128 in signed, 65408 in unsigned (big-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0xFF, (byte) 0x80});
        result = bytesToUInt16(data, ByteOrder.BIG_ENDIAN);
        assertEquals(65408, result);

        // test negative Value LittleEndian
        // -128 in signed, 65408 in unsigned (little-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0x80, (byte) 0xFF});
        result = bytesToUInt16(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(65408, result);

        // test zero value
        data = ByteString.copyFrom(new byte[]{0x00, 0x00});
        result = bytesToUInt16(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(0, result);
    }

    @Test
    public void testBytesToInt32() {
        // test positive Value BigEndian
        // 383 in decimal (big-endian)
        ByteString data   = ByteString.copyFrom(new byte[]{0x00, 0x00, 0x01, 0x7F});
        int        result = bytesToInt32(data, ByteOrder.BIG_ENDIAN);
        assertEquals(383, result);

        // test positive Value LittleEndian
        // 383 in decimal (little-endian)
        data = ByteString.copyFrom(new byte[]{0x7F, 0x01, 0x00, 0x00});
        result = bytesToInt32(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(383, result);

        // test negative Value BigEndian
        // -128 in decimal (big-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x80});
        result = bytesToInt32(data, ByteOrder.BIG_ENDIAN);
        assertEquals(-128, result);

        // test negative Value LittleEndian
        // -128 in decimal (little-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        result = bytesToInt32(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(-128, result);

        // test zero value
        data = ByteString.copyFrom(new byte[]{0x00, 0x00, 0x00, 0x00});
        result = bytesToInt32(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(0, result);

        // test boundary Values BigEndian
        // 2147483647 in decimal (big-endian)
        ByteString maxPositive = ByteString.copyFrom(
                new byte[]{0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        // -2147483648 in decimal (big-endian)
        ByteString maxNegative = ByteString.copyFrom(new byte[]{(byte) 0x80, 0x00, 0x00, 0x00});
        assertEquals(2147483647, bytesToInt32(maxPositive, ByteOrder.BIG_ENDIAN));
        assertEquals(-2147483648, bytesToInt32(maxNegative, ByteOrder.BIG_ENDIAN));

        // test boundary Values LittleEndian
        // 2147483647 in decimal (little-endian)
        maxPositive = ByteString.copyFrom(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F});
        // -2147483648 in decimal (little-endian)
        maxNegative = ByteString.copyFrom(new byte[]{0x00, 0x00, 0x00, (byte) 0x80});
        assertEquals(2147483647, bytesToInt32(maxPositive, ByteOrder.LITTLE_ENDIAN));
        assertEquals(-2147483648, bytesToInt32(maxNegative, ByteOrder.LITTLE_ENDIAN));

        // test empty Data
        ByteString emptyData = ByteString.copyFrom(new byte[]{});
        assertThrows(BufferUnderflowException.class, () -> {
            bytesToInt32(emptyData, ByteOrder.BIG_ENDIAN);
        });
    }


    @Test
    public void testBytesToUInt32() {
        // test positive Value BigEndian
        // 383 in decimal (big-endian)
        ByteString data   = ByteString.copyFrom(new byte[]{0x00, 0x00, 0x01, 0x7F});
        long       result = bytesToUInt32(data, ByteOrder.BIG_ENDIAN);
        assertEquals(383L, result);

        // test positive Value LittleEndian
        // 383 in decimal (little-endian)
        data = ByteString.copyFrom(new byte[]{0x7F, 0x01, 0x00, 0x00});
        result = bytesToUInt32(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(383L, result);

        // test negative Value BigEndian
        // -128 in signed, 4294967168 in unsigned (big-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x80});
        result = bytesToUInt32(data, ByteOrder.BIG_ENDIAN);
        assertEquals(4294967168L, result);

        // test negative Value LittleEndian
        // -128 in signed, 4294967168 in unsigned (little-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        result = bytesToUInt32(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(4294967168L, result);

        // test zero Value
        data = ByteString.copyFrom(new byte[]{0x00, 0x00, 0x00, 0x00});
        result = bytesToUInt32(data, ByteOrder.BIG_ENDIAN);
        assertEquals(0L, result);

        // test boundary Value LittleEndian
        // 2147483647 in decimal (little-endian)
        ByteString maxPositive = ByteString.copyFrom(
                new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F});
        // -2147483648 in signed, 2147483648 in unsigned (little-endian)
        ByteString maxNegative = ByteString.copyFrom(new byte[]{0x00, 0x00, 0x00, (byte) 0x80});
        assertEquals(2147483647L, bytesToUInt32(maxPositive, ByteOrder.LITTLE_ENDIAN));
        assertEquals(2147483648L, bytesToUInt32(maxNegative, ByteOrder.LITTLE_ENDIAN));

    }

    @Test
    public void testBytesToInt64() {
        // test positive Value BigEndian
        // 383 in decimal (big-endian)
        ByteString data = ByteString.copyFrom(
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x7F});
        long result = bytesToInt64(data, ByteOrder.BIG_ENDIAN);
        assertEquals(383L, result);

        // test positive Value LittleEndian
        // 383 in decimal (little-endian)
        data = ByteString.copyFrom(new byte[]{0x7F, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        result = bytesToInt64(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(383L, result);

        // test negative Value BigEndian
        // -128 in decimal (big-endian)
        data = ByteString.copyFrom(
                new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                           (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x80});
        result = bytesToInt64(data, ByteOrder.BIG_ENDIAN);
        assertEquals(-128L, result);

        // test negative Value LittleEndian
        // -128 in decimal (little-endian)
        data = ByteString.copyFrom(
                new byte[]{(byte) 0x80, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                           (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        result = bytesToInt64(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(-128L, result);

        // test zero value
        data = ByteString.copyFrom(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        result = bytesToInt64(data, ByteOrder.BIG_ENDIAN);
        assertEquals(0L, result);

        // test boundary Values LittleEndian
        // 9223372036854775807 in decimal (little-endian)
        ByteString maxPositive = ByteString.copyFrom(
                new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                           (byte) 0xFF, (byte) 0xFF, 0x7F});
        // -9223372036854775808 in decimal (little-endian)
        ByteString maxNegative = ByteString.copyFrom(
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80});
        assertEquals(9223372036854775807L,
                     bytesToInt64(maxPositive, ByteOrder.LITTLE_ENDIAN));
        assertEquals(-9223372036854775808L,
                     bytesToInt64(maxNegative, ByteOrder.LITTLE_ENDIAN));

        // test insufficient Data
        ByteString insufficientData = ByteString.copyFrom(
                new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07});
        assertThrows(BufferUnderflowException.class, () -> {
            bytesToInt64(insufficientData, ByteOrder.BIG_ENDIAN);
        });
    }


    @Test
    public void testBytesToFloat() {
        // test positive Value BigEndian
        // 100.0 in decimal (big-endian)
        ByteString data   = ByteString.copyFrom(new byte[]{0x42, (byte) 0xC8, 0x00, 0x00});
        float      result = bytesToFloat(data, ByteOrder.BIG_ENDIAN);
        assertEquals(100.0f, result, 0.001);

        // test positive Value LittleEndian
        // 100.0 in decimal (little-endian)
        data = ByteString.copyFrom(new byte[]{0x00, 0x00, (byte) 0xC8, 0x42});
        result = bytesToFloat(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(100.0f, result, 0.001);

        // test negative Value BigEndian
        // -100.0 in decimal (big-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0xC2, (byte) 0xC8, 0x00, 0x00});
        result = bytesToFloat(data, ByteOrder.BIG_ENDIAN);
        assertEquals(-100.0f, result, 0.001);

        // test negative Value LittleEndian
        // -100.0 in decimal (little-endian)
        data = ByteString.copyFrom(new byte[]{0x00, 0x00, (byte) 0xC8, (byte) 0xC2});
        result = bytesToFloat(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(-100.0f, result, 0.001);

        // test nan Value
        // NaN in decimal (big-endian)
        data = ByteString.copyFrom(new byte[]{0x7F, (byte) 0xC0, 0x00, 0x00});
        result = bytesToFloat(data, ByteOrder.BIG_ENDIAN);
        assertTrue(Float.isNaN(result));

        // test positive infinity
        // -Infinity in decimal (big-endian)
        data = ByteString.copyFrom(new byte[]{(byte) 0xFF, (byte) 0x80, 0x00, 0x00});
        result = bytesToFloat(data, ByteOrder.BIG_ENDIAN);
        assertEquals(Float.NEGATIVE_INFINITY, result, 0.001);

        // test insufficient Data
        ByteString insufficientData = ByteString.copyFrom(new byte[]{0x01, 0x02, 0x03});
        assertThrows(BufferUnderflowException.class, () -> {
            bytesToFloat(insufficientData, ByteOrder.BIG_ENDIAN);
        });
    }


    @Test
    public void testBytesToDouble() {
        // test positive Value BigEndian
        // 100.2 in decimal (big-endian)
        ByteString data = ByteString.copyFrom(
                new byte[]{0x40, 0x59, 0x0C, (byte) 0xCC, (byte) 0xCC,
                           (byte) 0xCC, (byte) 0xCC, (byte) 0xCD});
        double result = bytesToDouble(data, ByteOrder.BIG_ENDIAN);
        assertEquals(100.2, result, 0.0000001);

        // test positive Value LittleEndian
        // 100.2 in decimal (little-endian)
        data = ByteString.copyFrom(
                new byte[]{(byte) 0xCD, (byte) 0xCC, (byte) 0xCC,
                           (byte) 0xCC, (byte) 0xCC, 0x0C, 0x59, 0x40});
        result = bytesToDouble(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(100.2, result, 0.0000001);

        // test negative Value LittleEndian
        // -100.2 in decimal (little-endian)
        data = ByteString.copyFrom(
                new byte[]{(byte) 0xCD, (byte) 0xCC, (byte) 0xCC, (byte) 0xCC,
                           (byte) 0xCC, 0x0C, 0x59, (byte) 0xC0});
        result = bytesToDouble(data, ByteOrder.LITTLE_ENDIAN);
        assertEquals(-100.2, result, 0.0000001);

        // test insufficient Data
        ByteString insufficientData = ByteString.copyFrom(new byte[]{0x01, 0x02, 0x03});
        assertThrows(BufferUnderflowException.class, () -> {
            bytesToDouble(insufficientData, ByteOrder.BIG_ENDIAN);
        });
    }

    @Test
    public void testBytesToBool() {
        // true
        ByteString data   = ByteString.copyFrom(new byte[]{0x01});
        boolean    result = bytesToBool(data);
        assertTrue(result);

        // false
        data = ByteString.copyFrom(new byte[]{0x00});
        result = bytesToBool(data);
        assertFalse(result);

        // non-zero, should be false based on the method definition
        data = ByteString.copyFrom(new byte[]{0x02});
        result = bytesToBool(data);
        assertFalse(result);

        // test empty binary
        ByteString emptyData = ByteString.copyFrom(new byte[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> {
            bytesToBool(emptyData);
        });
    }

    @Test
    public void testBytesToSizedString() {
        // test LittleEndian
        byte[]     data1      = {0x05, 0x00, 'H', 'e', 'l', 'l', 'o'};
        ByteString byteString = ByteString.copyFrom(data1);
        int        startPos   = 0;
        ByteOrder  byteOrder  = ByteOrder.LITTLE_ENDIAN;
        String     result     = DecodeUtils.bytesToSizedString(byteString, startPos, byteOrder);
        assertEquals("Hello", result);

        // test BigEndian
        byte[] data2 = {0x00, 0x05, 'H', 'e', 'l', 'l', 'o'};
        byteString = ByteString.copyFrom(data2);
        byteOrder = ByteOrder.BIG_ENDIAN;
        result = DecodeUtils.bytesToSizedString(byteString, startPos, byteOrder);
        assertEquals("Hello", result);

        // test empty
        byte[] emptyData = {0x00, 0x00};
        byteOrder = ByteOrder.LITTLE_ENDIAN;
        byteString = ByteString.copyFrom(emptyData);
        result = DecodeUtils.bytesToSizedString(byteString, startPos, byteOrder);
        assertEquals("", result);

        // test with start pos
        byte[] dataWithPos = {0x00, 0x00, 0x05, 0x00, 'H', 'e', 'l', 'l', 'o'};
        byteString = ByteString.copyFrom(dataWithPos);
        startPos = 2;
        byteOrder = ByteOrder.LITTLE_ENDIAN;
        result = DecodeUtils.bytesToSizedString(byteString, startPos, byteOrder);
        assertEquals("Hello", result);
    }

    @Test
    public void testGetVectorType() {
        // test flat vector
        NestedVector vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(258) // 0x102
                                           .build())
                .build();
        assertEquals(VectorType.FLAT_VECTOR, getVectorType(vector));
        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(2)
                                           .build())
                .build();
        assertEquals(VectorType.FLAT_VECTOR, getVectorType(vector));
        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(514) // 0x202
                                           .build())
                .build();
        assertEquals(VectorType.FLAT_VECTOR, getVectorType(vector));

        // test const vector
        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(257) // 0x101
                                           .build())
                .build();
        assertEquals(VectorType.CONST_VECTOR, getVectorType(vector));
        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(1)
                                           .build())
                .build();
        assertEquals(VectorType.CONST_VECTOR, getVectorType(vector));
        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(513) // 0x201
                                           .build())
                .build();
        assertEquals(VectorType.CONST_VECTOR, getVectorType(vector));

        // test invalid vector
        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(0)
                                           .build())
                .build();
        assertEquals(VectorType.INVALID_VECTOR, getVectorType(vector));

        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(256) // 0x100
                                           .build())
                .build();
        assertEquals(VectorType.INVALID_VECTOR, getVectorType(vector));

        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(512) // 0x200
                                           .build())
                .build();
        assertEquals(VectorType.INVALID_VECTOR, getVectorType(vector));
    }

    @Test
    public void testIsNullAllSet() {
        NestedVector vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(0x00000100)
                                           .build())
                .build();
        assertTrue(DecodeUtils.isNullBitMapAllSet(vector));

        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(0x00000000)
                                           .build())
                .build();
        assertFalse(DecodeUtils.isNullBitMapAllSet(vector));

        vector = NestedVector
                .newBuilder()
                .setCommonMetaData(VectorCommonMetaData
                                           .newBuilder()
                                           .setVectorContentType(0x00000200)
                                           .build())
                .build();
        assertFalse(DecodeUtils.isNullBitMapAllSet(vector));
    }
}
