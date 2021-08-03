/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package test.java.com.vesoft.nebula.encoder;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.NullType;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.encoder.MetaCacheImplTest;
import com.vesoft.nebula.encoder.NebulaCodecImpl;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.PropertyType;
import com.vesoft.nebula.meta.SpaceItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MurmurHash2;
import org.junit.Assert;
import org.junit.Test;

public class TestEncoder {
    private final MetaCacheImplTest cacheImplTest = new MetaCacheImplTest();
    private final NebulaCodecImpl codec = new NebulaCodecImpl();

    final String allTypeValueExpectResult = "090cc001081000200000004000000000000000db0f494069"
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

    public int getSpaceVidLen(String spaceName) throws RuntimeException {
        SpaceItem spaceItem = cacheImplTest.getSpace(spaceName);
        if (spaceItem == null) {
            throw new RuntimeException("SpaceName: " + spaceName + "is not existed");
        }
        if (spaceItem.properties.vid_type.type != PropertyType.FIXED_STRING) {
            throw new RuntimeException("Only supported fixed string vid type.");
        }
        return spaceItem.properties.vid_type.type_length;
    }

    public int getPartSize(String spaceName) throws RuntimeException {
        Map<Integer, List<HostAddr>> partsAlloc = cacheImplTest.getPartsAlloc(spaceName);
        if (partsAlloc == null) {
            throw new RuntimeException("SpaceName: " + spaceName + " is not existed");
        }
        return partsAlloc.size();
    }

    public int getPartId(int partNum, String vertexId) {
        long hash = MurmurHash2.hash64(vertexId.getBytes(), vertexId.length(), 0xc70f6907);
        long hashValue = Long.parseUnsignedLong(Long.toUnsignedString(hash));
        return (int) (Math.floorMod(hashValue, partNum) + 1);
    }

    private byte[] getVertexKey(String spaceName, String vid, String tagName) {
        int tagId = cacheImplTest.getTag(spaceName, tagName).tag_id;
        return codec.vertexKey(getSpaceVidLen(spaceName),
            getPartId(getPartSize(spaceName), vid), vid.getBytes(), tagId);
    }

    private byte[] getInBoundEdgeKey(String spaceName,
                                     String edgeName,
                                     String srcId,
                                     String dstId,
                                     Long ranking) {
        int edgeType = cacheImplTest.getEdge(spaceName, edgeName).edge_type;
        return codec.edgeKeyByDefaultVer(getSpaceVidLen(spaceName),
            getPartId(getPartSize(spaceName), srcId),
            srcId.getBytes(),
            edgeType,
            ranking,
            dstId.getBytes());
    }

    private byte[] getOutBoundEdgeKey(String spaceName,
                                      String edgeName,
                                      String srcId,
                                      String dstId,
                                      Long ranking) {
        int edgeType = cacheImplTest.getEdge(spaceName, edgeName).edge_type;
        return codec.edgeKeyByDefaultVer(getSpaceVidLen(spaceName),
            getPartId(getPartSize(spaceName), dstId),
            dstId.getBytes(),
            -edgeType,
            ranking,
            srcId.getBytes());
    }

    @Test()
    public void testVertexKey() {
        // test vertexKey
        byte[] vertexKey = getVertexKey("test_space","Tom", "person");
        String hexStr = Hex.encodeHexString(vertexKey);
        String expectResult = "01d80100546f6d00000000000000000000000000"
            + "0000000002000000";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // less than vidLen
        String vid = "0123456789";
        vertexKey = codec.vertexKey(10,123, vid.getBytes(), 2020);
        hexStr = Hex.encodeHexString(vertexKey);
        expectResult = "017b000030313233343536373839e4070000";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // large than vidLen
        vertexKey = codec.vertexKey(100,123, vid.getBytes(), 2020);
        hexStr = Hex.encodeHexString(vertexKey);
        expectResult = "017b00003031323334353637383900000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000000000000000"
            + "00000000000000000000000000000000000000000000000000000000000000000000000e407"
            + "0000";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());
    }

    @Test()
    public void testEdgeKey() {
        // test edgeKey
        byte[] edgeKey = getInBoundEdgeKey("test_space", "friend", "Kate","Lily", 0L);
        String hexStr = Hex.encodeHexString(edgeKey);
        String expectResult = "02ef00004b617465000000000000000000000000000000000300000080000"
            + "000000000004c696c790000000000000000000000000000000001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        String srcVid = "0123456789";
        String dstVid = "9876543210";

        // less than vidLen
        edgeKey = codec.edgeKeyByDefaultVer(
            10,123, srcVid.getBytes(), 1010, 10L, dstVid.getBytes());
        hexStr = Hex.encodeHexString(edgeKey);
        expectResult = "027b000030313233343536373839f2030000800000000000000a3938373635343332313001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // large than vidLen
        edgeKey = codec.edgeKeyByDefaultVer(
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
        edgeKey = codec.edgeKeyByDefaultVer(
            10,123, srcVid.getBytes(), -1010, 10L, dstVid.getBytes());
        hexStr = Hex.encodeHexString(edgeKey);
        expectResult = "027b0000303132333435363738390efcffff800000000000000a39"
            + "38373635343332313001";
        Assert.assertArrayEquals(expectResult.getBytes(), hexStr.getBytes());

        // large than vidLen with negative edgeType
        edgeKey = codec.edgeKeyByDefaultVer(
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
        // encode failed, loss filed value
        List<String> colNames = Arrays.asList("Col01", "Col02", "Col03", "Col04", "Col05");
        List<Object> colVals = Arrays.asList(true, 8, 16, 32, (long)100);
        TagItem tagItem1 = cacheImplTest.getTag("test", "tag_no_default");
        TagItem tagItem2 = cacheImplTest.getTag("test", "tag_with_empty_string");
        TagItem tagItem3 = cacheImplTest.getTag("test", "tag_with_default");
        try {
            codec.encodeTag(tagItem1, colNames, colVals);
            Assert.fail();
        } catch (Exception exception) {
            assert (true);
        }

        // encode all type succeeded
        try {
            byte[] encodeStr = codec.encodeTag(tagItem1, getCols(), getValues());
            String hexStr = Hex.encodeHexString(encodeStr);
            // write into file to use storage test to decode
            // File file = new File("encode_java.txt");
            // FileOutputStream fileOutputStream = new FileOutputStream(file);
            // fileOutputStream.write(encodeStr);
            Assert.assertArrayEquals(allTypeValueExpectResult.getBytes(),
                hexStr.substring(0, hexStr.length() - 16).getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }

        // encode empty string succeeded
        try {
            byte[] encodeStr = codec.encodeTag(tagItem2,
                Collections.singletonList("Col01"), Collections.singletonList(""));
            String hexStr = Hex.encodeHexString(encodeStr);
            String expectResult = "080900000000000000";
            Assert.assertArrayEquals(expectResult.getBytes(),
                hexStr.substring(0, hexStr.length() - 16).getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }

        // test with default
        try {
            codec.encodeTag(tagItem3, Arrays.asList("Col01", "Col02"), Arrays.asList(true, 8));
        } catch (RuntimeException e) {
            Assert.assertArrayEquals(e.getMessage().getBytes(),
                "Unsupported default value yet".getBytes());
            assert (true);
        }

        // test without string type
        try {
            byte[] encodeStr = codec.encodeTag(
                tagItem4, Arrays.asList("Col01"), Arrays.asList(1024));
            String hexStr = Hex.encodeHexString(encodeStr);
            String expectResult = "09070004000000000000";
            Assert.assertArrayEquals(expectResult.getBytes(),
                hexStr.substring(0, hexStr.length() - 16).getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }

        // test without empty property
        try {
            byte[] encodeStr = codec.encodeTag(tagItem5, new ArrayList<>(), new ArrayList<>());
            String hexStr = Hex.encodeHexString(encodeStr);
            String expectResult = "0907";
            Assert.assertArrayEquals(expectResult.getBytes(),
                hexStr.substring(0, hexStr.length() - 16).getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }

        // test with chinese value
        try {
            byte[] encodeStr = codec.encodeTag(tagItem2,
                                               Collections.singletonList("Col01"),
                                               Collections.singletonList("中国"));
            String hexStr = Hex.encodeHexString(encodeStr);
            String expectResult = "080900000006000000e4b8ade59bbd";
            Assert.assertArrayEquals(expectResult.getBytes(),
                hexStr.substring(0, hexStr.length() - 16).getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }
    }

    @Test()
    public void testEncodeEdge() {
        // encode failed, loss filed value
        List<String> colNames = Arrays.asList("Col01", "Col02", "Col03", "Col04", "Col05");
        List<Object> colVals = Arrays.asList(true, 8, 16, 32, (long)100);
        EdgeItem edgeItem1 = cacheImplTest.getEdge("test", "edge_no_default");
        EdgeItem edgeItem2 = cacheImplTest.getEdge("test", "edge_with_empty_string");
        try {
            codec.encodeEdge(edgeItem1, colNames, colVals);
            Assert.fail();
        } catch (Exception exception) {
            assert (true);
        }

        // encode succeeded
        try {
            byte[] encodeStr = codec.encodeEdge(edgeItem1, getCols(), getValues());
            String hexStr = Hex.encodeHexString(encodeStr);
            Assert.assertArrayEquals(
                allTypeValueExpectResult.getBytes(),
                hexStr.substring(0, hexStr.length() - 16).getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }
        // encode empty string succeeded
        try {
            byte[] encodeStr = codec.encodeEdge(edgeItem2,
                Collections.singletonList("Col01"), Collections.singletonList(""));
            String hexStr = Hex.encodeHexString(encodeStr);
            String expectResult = "080900000000000000";
            Assert.assertArrayEquals(expectResult.getBytes(),
                hexStr.substring(0, hexStr.length() - 16).getBytes());
        } catch (Exception exception) {
            exception.printStackTrace();
            Assert.fail(exception.getMessage());
        }
    }

    @Test()
    public void testEncodeWithNotExistedSchema() {
        List<String> colNames = Arrays.asList("Col01", "Col02", "Col03", "Col04", "Col05");
        List<Object> colVals = Arrays.asList(true, 8, 16, 32, (long) 100);
        try {
            codec.encodeTag(null, colNames, colVals);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertArrayEquals(e.getMessage().getBytes(),
                "TagItem is null".getBytes());
            assert (true);
        }

        try {
            codec.encodeEdge(null, colNames, colVals);
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertArrayEquals(e.getMessage().getBytes(),
                "EdgeItem is null".getBytes());
            assert (true);
        }
    }
}
