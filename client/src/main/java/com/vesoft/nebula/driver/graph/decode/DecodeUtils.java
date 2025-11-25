/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE;

import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.decode.datatype.VectorType;
import com.vesoft.nebula.proto.graph.NestedVector;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import org.apache.commons.codec.Charsets;

public class DecodeUtils {
    public static final Charset charset = Charsets.UTF_8;

    /**
     * decode binary to byte
     *
     * @param data binary data
     * @return int value
     */
    public static int bytesToInt8(ByteString data) {
        return data.byteAt(0);
    }

    /**
     * decode binary to unsigned byte
     *
     * @param data binary data
     * @return uint value
     */
    public static int bytesToUInt8(ByteString data) {
        return data.byteAt(0) & 0xFF;
    }

    /**
     * decode binary to short
     *
     * @param data binary data
     * @return short value
     */
    public static Short bytesToInt16(ByteString data, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer.wrap(data.toByteArray());
        return buffer.order(order).getShort();
    }

    /**
     * decode binary to unsigned short
     *
     * @param data binary data
     * @return short value
     */
    public static int bytesToUInt16(ByteString data, ByteOrder order) {
        return bytesToInt16(data, order) & 0xFFFF;
    }

    /**
     * decode binary to int
     *
     * @param data binary data
     * @return int value
     */
    public static int bytesToInt32(ByteString data, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer.wrap(data.toByteArray());
        return buffer.order(order).getInt();
    }


    /**
     * decode binary to unsigned int
     *
     * @param data binary data
     * @return int value
     */
    public static long bytesToUInt32(ByteString data, ByteOrder order) {
        return Integer.toUnsignedLong(bytesToInt32(data, order));
    }

    /**
     * decode binary to long
     *
     * @param data binary data
     * @return long value
     */
    public static long bytesToInt64(ByteString data, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer.wrap(data.toByteArray());
        return buffer.order(order).getLong();
    }


    /**
     * decode binary to float
     *
     * @param data binary data
     * @return float value
     */
    public static float bytesToFloat(ByteString data, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer.wrap(data.toByteArray());
        return buffer.order(order).getFloat();
    }

    /**
     * decode binary to double
     *
     * @param data binary data
     * @return double value
     */
    public static double bytesToDouble(ByteString data, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer.wrap(data.toByteArray());
        return buffer.order(order).getDouble();
    }

    /**
     * decode binary to bool
     *
     * @param data binary data
     * @return Boolean value
     */
    public static boolean bytesToBool(ByteString data) {
        return data.byteAt(0) == 0x01;
    }


    /**
     * decode binary to String with specific size. Used for Any vector.
     * In Any vector, the string binary includes: 2 bytes size for the string + string binary
     *
     * @param data     binary data
     * @param startPos start position for the string
     * @return String value
     */
    public static String bytesToSizedString(ByteString data, int startPos, ByteOrder byteOrder) {
        int length = bytesToInt16(
                data.substring(startPos, ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE + startPos),
                byteOrder);
        startPos += ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE;
        ByteString strBytes = data.substring(startPos, startPos + length);
        return strBytes.toString(charset);
    }

    /**
     * get the type of vector
     *
     * @param vector NestedVector
     * @return {@link  VectorType}
     */
    public static VectorType getVectorType(NestedVector vector) {
        int type = vector.getCommonMetaData().getVectorContentType();
        return VectorType.getVectorType(type & 0xFF);
    }


    /**
     * if all vector data is not null.
     *
     * @param vector NestedVector
     * @return true if no data is null.
     */
    public static boolean isNullBitMapAllSet(NestedVector vector) {
        int type = vector.getCommonMetaData().getVectorContentType();
        return (type & 1 << 8) != 0;
    }

}
