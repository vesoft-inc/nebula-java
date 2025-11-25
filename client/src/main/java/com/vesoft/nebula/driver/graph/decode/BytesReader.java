/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToUInt16;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.charset;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE;

import com.google.protobuf.ByteString;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BytesReader {
    private ByteString data;
    private int        index;

    public BytesReader(ByteString data) {
        this.data = data;
        this.index = 0;
    }

    public ByteString read(int len) {
        ByteString byteString = data.substring(index, index + len);
        index += len;
        return byteString;
    }

    public String readSizedString(ByteOrder byteOrder) {
        int length     = bytesToUInt16(read(ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE), byteOrder);
        int startIndex = index;
        index += length;
        ByteString strBytes = data.substring(startIndex, startIndex + length);
        return strBytes.toString(charset);
    }
}
