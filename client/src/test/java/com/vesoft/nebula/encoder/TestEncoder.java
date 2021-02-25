/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package test.java.com.vesoft.nebula.encoder;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.NullType;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.encoder.MetaCacheImplTest;
import com.vesoft.nebula.encoder.NebulaCodecImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

public class TestEncoder {
    final String expectResult = "090cc001081000200000004000000000000000db0f494069"
        + "57148b0abf05405d0000000c0000004e6562756c61204772617068bb334e5e000000"
        + "00e40702140a1e2d00000000e40702140a1e2d00000000000000000000000000000000"
        + "48656c6c6f20776f726c6421";

    private List<String> getCols() {
        return Arrays.asList("Col01","Col02", "Col03", "Col04", "Col05", "Col06",
            "Col07","Col08", "Col09", "Col10", "Col11", "Col12", "Col13", "Col14");
    }

    private List<Object> getValues() {
        final double e = 2.71828182845904523536028747135266249775724709369995;
        final float pi = (float)3.14159265358979;
        final Value strVal = new Value();
        strVal.setSVal("Hello world!".getBytes());
        final Value fixVal = new Value();
        fixVal.setSVal("Nebula Graph".getBytes());
        final Value timestampVal = new Value();
        timestampVal.setIVal(1582183355);
        final Value intVal = new Value();
        intVal.setIVal(64);
        final Value timeVal = new Value();
        timeVal.setTVal(new Time((byte)10, (byte)30, (byte)45, 0));
        final Value datetimeValue = new Value();
        datetimeValue.setDtVal(new DateTime((short)2020, (byte)2, (byte)20,
            (byte)10, (byte)30, (byte)45, 0));
        final Value dateValue = new Value();
        dateValue.setDVal(new Date((short)2020, (byte)2, (byte)20));
        final Value nullVal = new Value();
        nullVal.setNVal(NullType.__NULL__);
        return Arrays.asList(true, 8, 16, 32, intVal, pi, e, strVal, fixVal,
            timestampVal, dateValue, timeVal, datetimeValue, nullVal);
    }

    @Test()
    public void testVertexKey() {
        MetaCacheImplTest cacheImplTest = new MetaCacheImplTest();
        NebulaCodecImpl codec = new NebulaCodecImpl(cacheImplTest);
        // test vertexKey
        byte[] vertexKey = codec.vertexKey("test_space","Tom", "person");
        String hexStr = Hex.encodeHexString(vertexKey);
        String expectResult = "01d80100546f6d00000000000000000000000000"
            + "0000000002000000";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // less than vidLen
        String vid = "0123456789";
        vertexKey = codec.genVertexKey(10,123, vid.getBytes(), 2020);
        hexStr = Hex.encodeHexString(vertexKey);
        expectResult = "017b000030313233343536373839e4070000";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // large than vidLen
        vertexKey = codec.genVertexKey(100,123, vid.getBytes(), 2020);
        hexStr = Hex.encodeHexString(vertexKey);
        expectResult = "017b00003031323334353637383900000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000000000000e407"
            + "0000";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());
    }

    @Test()
    public void testEdgeKey() {
        MetaCacheImplTest cacheImplTest = new MetaCacheImplTest();
        NebulaCodecImpl codec = new NebulaCodecImpl(cacheImplTest);

        // test edgeKey
        byte[] edgeKey = codec.edgeKey("test_space","Kate", "friend", 0, "Lily");
        String hexStr = Hex.encodeHexString(edgeKey);
        String expectResult = "02ef00004b617465000000000000000000000000000000000300000080000"
            + "000000000004c696c790000000000000000000000000000000001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        String srcVid = "0123456789";
        String dstVid = "9876543210";

        // less than vidLen
        edgeKey = codec.genEdgeKeyByDefaultVer(
            10,123, srcVid.getBytes(), 1010, 10L, dstVid.getBytes());
        hexStr = Hex.encodeHexString(edgeKey);
        expectResult = "027b000030313233343536373839f2030000800000000000000a3938373635343332313001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // large than vidLen
        edgeKey = codec.genEdgeKeyByDefaultVer(
            100,123, srcVid.getBytes(), 1010, 10L, dstVid.getBytes());
        hexStr = Hex.encodeHexString(edgeKey);
        expectResult = "027b0000303132333435363738390000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000f20300008000000000"
            + "00000a3938373635343332313000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // less than vidLen with negative edgeType
        edgeKey = codec.genEdgeKeyByDefaultVer(
            10,123, srcVid.getBytes(), -1010, 10L, dstVid.getBytes());
        hexStr = Hex.encodeHexString(edgeKey);
        expectResult = "027b0000303132333435363738390efcffff800000000000000a39"
            + "38373635343332313001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // large than vidLen with negative edgeType
        edgeKey = codec.genEdgeKeyByDefaultVer(
            100,123, srcVid.getBytes(), -1010, 10L, dstVid.getBytes());
        hexStr = Hex.encodeHexString(edgeKey);
        expectResult = "027b0000303132333435363738390000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000efcffff800000000000000a393"
            + "8373635343332313000000000000000000000000000000000000000000000000000000000000000000"
            + "0000000000000000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());
    }

    @Test()
    public void testEncodeVertexValue() {
        MetaCacheImplTest cacheImplTest = new MetaCacheImplTest();
        NebulaCodecImpl codec = new NebulaCodecImpl(cacheImplTest);
        // encode failed, loss filed value
        List<String> colNames = Arrays.asList("Col01", "Col02", "Col03", "Col04", "Col05");
        List<Object> colVals = Arrays.asList(true, 8, 16, 32, (long)100);
        try {
            codec.encodeTag("test","tag_no_default", colNames, colVals);
            Assert.fail();
        } catch (Exception exception) {
            assert (true);
        }

        // encode all type succeeded
        try {
            byte[] encodeStr = codec.encodeTag("test", "tag_no_default", getCols(), getValues());
            String hexStr = Hex.encodeHexString(encodeStr);
            // write into file to use storage test to decode
            // File file = new File("encode_java.txt");
            // FileOutputStream fileOutputStream = new FileOutputStream(file);
            // fileOutputStream.write(encodeStr);
            Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }

        // encode empty string succeeded
        try {
            byte[] encodeStr = codec.encodeTag("test", "tag_with_empty_string",
                Collections.singletonList("Col01"), Collections.singletonList(""));
            String hexStr = Hex.encodeHexString(encodeStr);
            String expectResult = "080900000000000000";
            Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }

        // test with default
        try {
            codec.encodeTag("test",
                "tag_with_default",
                Arrays.asList("Col01", "Col02"),
                Arrays.asList(true, 8));
        } catch (RuntimeException e) {
            Assert.assertArrayEquals(e.getMessage().getBytes(),
                "Unsupported default value yet".getBytes());
            assert (true);
        }
    }

    @Test()
    public void testEncodeEdge() {
        MetaCacheImplTest cacheImplTest = new MetaCacheImplTest();
        NebulaCodecImpl codec = new NebulaCodecImpl(cacheImplTest);
        // encode failed, loss filed value
        List<String> colNames = Arrays.asList("Col01", "Col02", "Col03", "Col04", "Col05");
        List<Object> colVals = Arrays.asList(true, 8, 16, 32, (long)100);
        try {
            codec.encodeEdge("test", "edge_no_default", colNames, colVals);
            Assert.fail();
        } catch (Exception exception) {
            assert (true);
        }

        // encode succeeded
        try {
            byte[] encodeStr = codec.encodeEdge("test", "edge_no_default", getCols(), getValues());
            String hexStr = Hex.encodeHexString(encodeStr);
            Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }
        // encode empty string succeeded
        try {
            byte[] encodeStr = codec.encodeEdge("test", "edge_with_empty_string",
                Collections.singletonList("Col01"), Collections.singletonList(""));
            String hexStr = Hex.encodeHexString(encodeStr);
            String expectResult = "080900000000000000";
            Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }
    }

    @Test()
    public void testEncodeWithNotExistedSchema() {
        MetaCacheImplTest cacheImplTest = new MetaCacheImplTest();
        NebulaCodecImpl codec = new NebulaCodecImpl(cacheImplTest);
        List<String> colNames = Arrays.asList("Col01", "Col02", "Col03", "Col04", "Col05");
        List<Object> colVals = Arrays.asList(true, 8, 16, 32, (long) 100);
        try {
            codec.encodeTag("test", "not_existed", colNames, colVals);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertArrayEquals(e.getMessage().getBytes(),
                "Tag: not_existed does not exist.".getBytes());
            assert (true);
        }

        try {
            codec.encodeEdge("test", "not_existed", colNames, colVals);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertArrayEquals(e.getMessage().getBytes(),
                "Edge: not_existed does not exist.".getBytes());
            assert (true);
        }
    }
}
