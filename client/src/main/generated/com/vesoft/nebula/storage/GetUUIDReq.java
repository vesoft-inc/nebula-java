/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.storage;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.BitSet;
import java.util.Arrays;
import com.facebook.thrift.*;
import com.facebook.thrift.annotations.*;
import com.facebook.thrift.async.*;
import com.facebook.thrift.meta_data.*;
import com.facebook.thrift.server.*;
import com.facebook.thrift.transport.*;
import com.facebook.thrift.protocol.*;

@SuppressWarnings({ "unused", "serial" })
public class GetUUIDReq implements TBase, java.io.Serializable, Cloneable, Comparable<GetUUIDReq> {
  private static final TStruct STRUCT_DESC = new TStruct("GetUUIDReq");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField PART_ID_FIELD_DESC = new TField("part_id", TType.I32, (short)2);
  private static final TField NAME_FIELD_DESC = new TField("name", TType.STRING, (short)3);
  private static final TField COMMON_FIELD_DESC = new TField("common", TType.STRUCT, (short)4);

  public int space_id;
  public int part_id;
  public byte[] name;
  public RequestCommon common;
  public static final int SPACE_ID = 1;
  public static final int PART_ID = 2;
  public static final int NAME = 3;
  public static final int COMMON = 4;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __PART_ID_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(PART_ID, new FieldMetaData("part_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(NAME, new FieldMetaData("name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(COMMON, new FieldMetaData("common", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, RequestCommon.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(GetUUIDReq.class, metaDataMap);
  }

  public GetUUIDReq() {
  }

  public GetUUIDReq(
      int space_id,
      int part_id,
      byte[] name) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.name = name;
  }

  public GetUUIDReq(
      int space_id,
      int part_id,
      byte[] name,
      RequestCommon common) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.name = name;
    this.common = common;
  }

  public static class Builder {
    private int space_id;
    private int part_id;
    private byte[] name;
    private RequestCommon common;

    BitSet __optional_isset = new BitSet(2);

    public Builder() {
    }

    public Builder setSpace_id(final int space_id) {
      this.space_id = space_id;
      __optional_isset.set(__SPACE_ID_ISSET_ID, true);
      return this;
    }

    public Builder setPart_id(final int part_id) {
      this.part_id = part_id;
      __optional_isset.set(__PART_ID_ISSET_ID, true);
      return this;
    }

    public Builder setName(final byte[] name) {
      this.name = name;
      return this;
    }

    public Builder setCommon(final RequestCommon common) {
      this.common = common;
      return this;
    }

    public GetUUIDReq build() {
      GetUUIDReq result = new GetUUIDReq();
      if (__optional_isset.get(__SPACE_ID_ISSET_ID)) {
        result.setSpace_id(this.space_id);
      }
      if (__optional_isset.get(__PART_ID_ISSET_ID)) {
        result.setPart_id(this.part_id);
      }
      result.setName(this.name);
      result.setCommon(this.common);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetUUIDReq(GetUUIDReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    this.part_id = TBaseHelper.deepCopy(other.part_id);
    if (other.isSetName()) {
      this.name = TBaseHelper.deepCopy(other.name);
    }
    if (other.isSetCommon()) {
      this.common = TBaseHelper.deepCopy(other.common);
    }
  }

  public GetUUIDReq deepCopy() {
    return new GetUUIDReq(this);
  }

  public int getSpace_id() {
    return this.space_id;
  }

  public GetUUIDReq setSpace_id(int space_id) {
    this.space_id = space_id;
    setSpace_idIsSet(true);
    return this;
  }

  public void unsetSpace_id() {
    __isset_bit_vector.clear(__SPACE_ID_ISSET_ID);
  }

  // Returns true if field space_id is set (has been assigned a value) and false otherwise
  public boolean isSetSpace_id() {
    return __isset_bit_vector.get(__SPACE_ID_ISSET_ID);
  }

  public void setSpace_idIsSet(boolean __value) {
    __isset_bit_vector.set(__SPACE_ID_ISSET_ID, __value);
  }

  public int getPart_id() {
    return this.part_id;
  }

  public GetUUIDReq setPart_id(int part_id) {
    this.part_id = part_id;
    setPart_idIsSet(true);
    return this;
  }

  public void unsetPart_id() {
    __isset_bit_vector.clear(__PART_ID_ISSET_ID);
  }

  // Returns true if field part_id is set (has been assigned a value) and false otherwise
  public boolean isSetPart_id() {
    return __isset_bit_vector.get(__PART_ID_ISSET_ID);
  }

  public void setPart_idIsSet(boolean __value) {
    __isset_bit_vector.set(__PART_ID_ISSET_ID, __value);
  }

  public byte[] getName() {
    return this.name;
  }

  public GetUUIDReq setName(byte[] name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  // Returns true if field name is set (has been assigned a value) and false otherwise
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean __value) {
    if (!__value) {
      this.name = null;
    }
  }

  public RequestCommon getCommon() {
    return this.common;
  }

  public GetUUIDReq setCommon(RequestCommon common) {
    this.common = common;
    return this;
  }

  public void unsetCommon() {
    this.common = null;
  }

  // Returns true if field common is set (has been assigned a value) and false otherwise
  public boolean isSetCommon() {
    return this.common != null;
  }

  public void setCommonIsSet(boolean __value) {
    if (!__value) {
      this.common = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SPACE_ID:
      if (__value == null) {
        unsetSpace_id();
      } else {
        setSpace_id((Integer)__value);
      }
      break;

    case PART_ID:
      if (__value == null) {
        unsetPart_id();
      } else {
        setPart_id((Integer)__value);
      }
      break;

    case NAME:
      if (__value == null) {
        unsetName();
      } else {
        setName((byte[])__value);
      }
      break;

    case COMMON:
      if (__value == null) {
        unsetCommon();
      } else {
        setCommon((RequestCommon)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return new Integer(getSpace_id());

    case PART_ID:
      return new Integer(getPart_id());

    case NAME:
      return getName();

    case COMMON:
      return getCommon();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object _that) {
    if (_that == null)
      return false;
    if (this == _that)
      return true;
    if (!(_that instanceof GetUUIDReq))
      return false;
    GetUUIDReq that = (GetUUIDReq)_that;

    if (!TBaseHelper.equalsNobinary(this.space_id, that.space_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.part_id, that.part_id)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetName(), that.isSetName(), this.name, that.name)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetCommon(), that.isSetCommon(), this.common, that.common)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space_id, part_id, name, common});
  }

  @Override
  public int compareTo(GetUUIDReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSpace_id()).compareTo(other.isSetSpace_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space_id, other.space_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPart_id()).compareTo(other.isSetPart_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(part_id, other.part_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(name, other.name);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCommon()).compareTo(other.isSetCommon());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(common, other.common);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    return 0;
  }

  public void read(TProtocol iprot) throws TException {
    TField __field;
    iprot.readStructBegin(metaDataMap);
    while (true)
    {
      __field = iprot.readFieldBegin();
      if (__field.type == TType.STOP) { 
        break;
      }
      switch (__field.id)
      {
        case SPACE_ID:
          if (__field.type == TType.I32) {
            this.space_id = iprot.readI32();
            setSpace_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case PART_ID:
          if (__field.type == TType.I32) {
            this.part_id = iprot.readI32();
            setPart_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case NAME:
          if (__field.type == TType.STRING) {
            this.name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case COMMON:
          if (__field.type == TType.STRUCT) {
            this.common = new RequestCommon();
            this.common.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        default:
          TProtocolUtil.skip(iprot, __field.type);
          break;
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();


    // check for required fields of primitive type, which can't be checked in the validate method
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(SPACE_ID_FIELD_DESC);
    oprot.writeI32(this.space_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(PART_ID_FIELD_DESC);
    oprot.writeI32(this.part_id);
    oprot.writeFieldEnd();
    if (this.name != null) {
      oprot.writeFieldBegin(NAME_FIELD_DESC);
      oprot.writeBinary(this.name);
      oprot.writeFieldEnd();
    }
    if (this.common != null) {
      if (isSetCommon()) {
        oprot.writeFieldBegin(COMMON_FIELD_DESC);
        this.common.write(oprot);
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    return toString(1, true);
  }

  @Override
  public String toString(int indent, boolean prettyPrint) {
    String indentStr = prettyPrint ? TBaseHelper.getIndentedString(indent) : "";
    String newLine = prettyPrint ? "\n" : "";
    String space = prettyPrint ? " " : "";
    StringBuilder sb = new StringBuilder("GetUUIDReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("space_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getSpace_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("part_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getPart_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getName() == null) {
      sb.append("null");
    } else {
        int __name_size = Math.min(this.getName().length, 128);
        for (int i = 0; i < __name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getName()[i]).length() > 1 ? Integer.toHexString(this.getName()[i]).substring(Integer.toHexString(this.getName()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getName()[i]).toUpperCase());
        }
        if (this.getName().length > 128) sb.append(" ...");
    }
    first = false;
    if (isSetCommon())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("common");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getCommon() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getCommon(), indent + 1, prettyPrint));
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

