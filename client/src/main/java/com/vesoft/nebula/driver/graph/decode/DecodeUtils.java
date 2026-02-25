/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.CHUNK_INDEX_START_POSITION_IN_STRING_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.CHUNK_OFFSET_START_POSITION_IN_STRING_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.STRING_MAX_VALUE_LENGTH_IN_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.STRING_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.STRING_VALUE_LENGTH_SIZE;

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
     * decode binary to int directly from ByteString without creating sub-byteString
     *
     * @param data     binary data
     * @param offset   offset in the data
     * @param order    byte order
     * @return int value
     */
    public static int bytesToInt32AtOffset(ByteString data, int offset, ByteOrder order) {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (data.byteAt(offset) & 0xFF)
                    | ((data.byteAt(offset + 1) & 0xFF) << 8)
                    | ((data.byteAt(offset + 2) & 0xFF) << 16)
                    | ((data.byteAt(offset + 3) & 0xFF) << 24);
        } else {
            return (data.byteAt(offset) << 24)
                    | ((data.byteAt(offset + 1) & 0xFF) << 16)
                    | ((data.byteAt(offset + 2) & 0xFF) << 8)
                    | (data.byteAt(offset + 3) & 0xFF);
        }
    }

    /**
     * decode binary to long directly from ByteString without creating sub-byteString
     *
     * @param data     binary data
     * @param offset   offset in the data
     * @param order    byte order
     * @return long value
     */
    public static long bytesToInt64AtOffset(ByteString data, int offset, ByteOrder order) {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (long) (data.byteAt(offset) & 0xFF)
                    | ((long) (data.byteAt(offset + 1) & 0xFF) << 8)
                    | ((long) (data.byteAt(offset + 2) & 0xFF) << 16)
                    | ((long) (data.byteAt(offset + 3) & 0xFF) << 24)
                    | ((long) (data.byteAt(offset + 4) & 0xFF) << 32)
                    | ((long) (data.byteAt(offset + 5) & 0xFF) << 40)
                    | ((long) (data.byteAt(offset + 6) & 0xFF) << 48)
                    | ((long) (data.byteAt(offset + 7) & 0xFF) << 56);
        } else {
            return ((long) data.byteAt(offset) << 56)
                    | ((long) (data.byteAt(offset + 1) & 0xFF) << 48)
                    | ((long) (data.byteAt(offset + 2) & 0xFF) << 40)
                    | ((long) (data.byteAt(offset + 3) & 0xFF) << 32)
                    | ((long) (data.byteAt(offset + 4) & 0xFF) << 24)
                    | ((long) (data.byteAt(offset + 5) & 0xFF) << 16)
                    | ((long) (data.byteAt(offset + 6) & 0xFF) << 8)
                    | (data.byteAt(offset + 7) & 0xFF);
        }
    }

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
     * decode binary to byte at offset
     *
     * @param data   binary data
     * @param offset offset in the data
     * @return int value
     */
    public static int bytesToInt8AtOffset(ByteString data, int offset) {
        return data.byteAt(offset);
    }

    /**
     * decode binary to unsigned byte at offset
     *
     * @param data   binary data
     * @param offset offset in the data
     * @return uint value
     */
    public static int bytesToUInt8AtOffset(ByteString data, int offset) {
        return data.byteAt(offset) & 0xFF;
    }

    /**
     * decode binary to short
     *
     * @param data binary data
     * @return short value
     */
    public static Short bytesToInt16(ByteString data, ByteOrder order) {
        if (data.size() < 2) {
            throw new java.nio.BufferUnderflowException();
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((data.byteAt(0) & 0xFF) | ((data.byteAt(1) & 0xFF) << 8));
        } else {
            return (short) ((data.byteAt(0) << 8) | (data.byteAt(1) & 0xFF));
        }
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
     * decode binary to unsigned short at offset
     *
     * @param data   binary data
     * @param offset offset in the data
     * @param order  byte order
     * @return short value
     */
    public static int bytesToUInt16AtOffset(ByteString data, int offset, ByteOrder order) {
        return bytesToInt16AtOffset(data, offset, order) & 0xFFFF;
    }

    /**
     * decode binary to short at offset
     *
     * @param data   binary data
     * @param offset offset in the data
     * @param order  byte order
     * @return short value
     */
    public static short bytesToInt16AtOffset(ByteString data, int offset, ByteOrder order) {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((data.byteAt(offset) & 0xFF) | ((data.byteAt(offset + 1) & 0xFF) << 8));
        } else {
            return (short) ((data.byteAt(offset) << 8) | (data.byteAt(offset + 1) & 0xFF));
        }
    }

    /**
     * decode binary to int
     *
     * @param data binary data
     * @return int value
     */
    public static int bytesToInt32(ByteString data, ByteOrder order) {
        if (data.size() < 4) {
            throw new java.nio.BufferUnderflowException();
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (data.byteAt(0) & 0xFF)
                    | ((data.byteAt(1) & 0xFF) << 8)
                    | ((data.byteAt(2) & 0xFF) << 16)
                    | ((data.byteAt(3) & 0xFF) << 24);
        } else {
            return (data.byteAt(0) << 24)
                    | ((data.byteAt(1) & 0xFF) << 16)
                    | ((data.byteAt(2) & 0xFF) << 8)
                    | (data.byteAt(3) & 0xFF);
        }
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
     * decode binary to float at offset
     *
     * @param data   binary data
     * @param offset offset in the data
     * @param order  byte order
     * @return float value
     */
    public static float bytesToFloatAtOffset(ByteString data, int offset, ByteOrder order) {
        int bits = bytesToInt32AtOffset(data, offset, order);
        return Float.intBitsToFloat(bits);
    }

    /**
     * decode binary to double at offset
     *
     * @param data   binary data
     * @param offset offset in the data
     * @param order  byte order
     * @return double value
     */
    public static double bytesToDoubleAtOffset(ByteString data, int offset, ByteOrder order) {
        long bits = bytesToInt64AtOffset(data, offset, order);
        return Double.longBitsToDouble(bits);
    }

    /**
     * decode binary to bool at offset
     *
     * @param data   binary data
     * @param offset offset in the data
     * @return Boolean value
     */
    public static boolean bytesToBoolAtOffset(ByteString data, int offset) {
        return data.byteAt(offset) == 0x01;
    }

    /**
     * decode flat string directly from vector data at specified row
     *
     * @param vectorData vector data
     * @param rowIndex   row index
     * @param order      byte order
     * @return String value
     */
    public static String decodeFlatString(ByteString vectorData, int rowIndex, ByteOrder order) {
        int offset = rowIndex * STRING_SIZE;
        
        // Read string length
        int stringValueLength = bytesToInt32AtOffset(vectorData, offset, order);
        
        if (stringValueLength <= STRING_MAX_VALUE_LENGTH_IN_HEADER) {
            // Short string: data is in the header
            int dataOffset = offset + STRING_VALUE_LENGTH_SIZE;
            return vectorData.substring(dataOffset, dataOffset + stringValueLength)
                    .toString(charset);
        }
        
        // Long string: read chunk index and offset
        int chunkIndex = bytesToInt32AtOffset(
                vectorData,
                offset + CHUNK_INDEX_START_POSITION_IN_STRING_HEADER,
                order);
        int chunkOffset = bytesToInt32AtOffset(
                vectorData,
                offset + CHUNK_OFFSET_START_POSITION_IN_STRING_HEADER,
                order);
        
        // Note: For flat string, we can't access nested vectors without passing them
        // This method is optimized for the common case where the string is short
        // For long strings, fallback to the original bytesToString method
        return null;
    }

    /**
     * decode binary to long
     *
     * @param data binary data
     * @return long value
     */
    public static long bytesToInt64(ByteString data, ByteOrder order) {
        if (data.size() < 8) {
            throw new java.nio.BufferUnderflowException();
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (long) (data.byteAt(0) & 0xFF)
                    | ((long) (data.byteAt(1) & 0xFF) << 8)
                    | ((long) (data.byteAt(2) & 0xFF) << 16)
                    | ((long) (data.byteAt(3) & 0xFF) << 24)
                    | ((long) (data.byteAt(4) & 0xFF) << 32)
                    | ((long) (data.byteAt(5) & 0xFF) << 40)
                    | ((long) (data.byteAt(6) & 0xFF) << 48)
                    | ((long) (data.byteAt(7) & 0xFF) << 56);
        } else {
            return ((long) data.byteAt(0) << 56)
                    | ((long) (data.byteAt(1) & 0xFF) << 48)
                    | ((long) (data.byteAt(2) & 0xFF) << 40)
                    | ((long) (data.byteAt(3) & 0xFF) << 32)
                    | ((long) (data.byteAt(4) & 0xFF) << 24)
                    | ((long) (data.byteAt(5) & 0xFF) << 16)
                    | ((long) (data.byteAt(6) & 0xFF) << 8)
                    | (data.byteAt(7) & 0xFF);
        }
    }


    /**
     * decode binary to float
     *
     * @param data binary data
     * @return float value
     */
    public static float bytesToFloat(ByteString data, ByteOrder order) {
        int bits = bytesToInt32(data, order);
        return Float.intBitsToFloat(bits);
    }

    /**
     * decode binary to double
     *
     * @param data binary data
     * @return double value
     */
    public static double bytesToDouble(ByteString data, ByteOrder order) {
        long bits = bytesToInt64(data, order);
        return Double.longBitsToDouble(bits);
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
