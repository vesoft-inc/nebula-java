/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.meta;

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
public class ClearSpaceReq implements TBase, java.io.Serializable, Cloneable, Comparable<ClearSpaceReq> {
  private static final TStruct STRUCT_DESC = new TStruct("ClearSpaceReq");
  private static final TField SPACE_NAME_FIELD_DESC = new TField("space_name", TType.STRING, (short)1);
  private static final TField IF_EXISTS_FIELD_DESC = new TField("if_exists", TType.BOOL, (short)2);

  public byte[] space_name;
  public boolean if_exists;
  public static final int SPACE_NAME = 1;
  public static final int IF_EXISTS = 2;

  // isset id assignments
  private static final int __IF_EXISTS_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_NAME, new FieldMetaData("space_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(IF_EXISTS, new FieldMetaData("if_exists", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ClearSpaceReq.class, metaDataMap);
  }

  public ClearSpaceReq() {
  }

  public ClearSpaceReq(
      byte[] space_name,
      boolean if_exists) {
    this();
    this.space_name = space_name;
    this.if_exists = if_exists;
    setIf_existsIsSet(true);
  }

  public static class Builder {
    private byte[] space_name;
    private boolean if_exists;

    BitSet __optional_isset = new BitSet(1);

    public Builder() {
    }

    public Builder setSpace_name(final byte[] space_name) {
      this.space_name = space_name;
      return this;
    }

    public Builder setIf_exists(final boolean if_exists) {
      this.if_exists = if_exists;
      __optional_isset.set(__IF_EXISTS_ISSET_ID, true);
      return this;
    }

    public ClearSpaceReq build() {
      ClearSpaceReq result = new ClearSpaceReq();
      result.setSpace_name(this.space_name);
      if (__optional_isset.get(__IF_EXISTS_ISSET_ID)) {
        result.setIf_exists(this.if_exists);
      }
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ClearSpaceReq(ClearSpaceReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetSpace_name()) {
      this.space_name = TBaseHelper.deepCopy(other.space_name);
    }
    this.if_exists = TBaseHelper.deepCopy(other.if_exists);
  }

  public ClearSpaceReq deepCopy() {
    return new ClearSpaceReq(this);
  }

  public byte[] getSpace_name() {
    return this.space_name;
  }

  public ClearSpaceReq setSpace_name(byte[] space_name) {
    this.space_name = space_name;
    return this;
  }

  public void unsetSpace_name() {
    this.space_name = null;
  }

  // Returns true if field space_name is set (has been assigned a value) and false otherwise
  public boolean isSetSpace_name() {
    return this.space_name != null;
  }

  public void setSpace_nameIsSet(boolean __value) {
    if (!__value) {
      this.space_name = null;
    }
  }

  public boolean isIf_exists() {
    return this.if_exists;
  }

  public ClearSpaceReq setIf_exists(boolean if_exists) {
    this.if_exists = if_exists;
    setIf_existsIsSet(true);
    return this;
  }

  public void unsetIf_exists() {
    __isset_bit_vector.clear(__IF_EXISTS_ISSET_ID);
  }

  // Returns true if field if_exists is set (has been assigned a value) and false otherwise
  public boolean isSetIf_exists() {
    return __isset_bit_vector.get(__IF_EXISTS_ISSET_ID);
  }

  public void setIf_existsIsSet(boolean __value) {
    __isset_bit_vector.set(__IF_EXISTS_ISSET_ID, __value);
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SPACE_NAME:
      if (__value == null) {
        unsetSpace_name();
      } else {
        setSpace_name((byte[])__value);
      }
      break;

    case IF_EXISTS:
      if (__value == null) {
        unsetIf_exists();
      } else {
        setIf_exists((Boolean)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SPACE_NAME:
      return getSpace_name();

    case IF_EXISTS:
      return new Boolean(isIf_exists());

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
    if (!(_that instanceof ClearSpaceReq))
      return false;
    ClearSpaceReq that = (ClearSpaceReq)_that;

    if (!TBaseHelper.equalsSlow(this.isSetSpace_name(), that.isSetSpace_name(), this.space_name, that.space_name)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.if_exists, that.if_exists)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space_name, if_exists});
  }

  @Override
  public int compareTo(ClearSpaceReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSpace_name()).compareTo(other.isSetSpace_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space_name, other.space_name);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetIf_exists()).compareTo(other.isSetIf_exists());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(if_exists, other.if_exists);
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
        case SPACE_NAME:
          if (__field.type == TType.STRING) {
            this.space_name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case IF_EXISTS:
          if (__field.type == TType.BOOL) {
            this.if_exists = iprot.readBool();
            setIf_existsIsSet(true);
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
    if (this.space_name != null) {
      oprot.writeFieldBegin(SPACE_NAME_FIELD_DESC);
      oprot.writeBinary(this.space_name);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(IF_EXISTS_FIELD_DESC);
    oprot.writeBool(this.if_exists);
    oprot.writeFieldEnd();
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
    StringBuilder sb = new StringBuilder("ClearSpaceReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("space_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getSpace_name() == null) {
      sb.append("null");
    } else {
        int __space_name_size = Math.min(this.getSpace_name().length, 128);
        for (int i = 0; i < __space_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getSpace_name()[i]).length() > 1 ? Integer.toHexString(this.getSpace_name()[i]).substring(Integer.toHexString(this.getSpace_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getSpace_name()[i]).toUpperCase());
        }
        if (this.getSpace_name().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("if_exists");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isIf_exists(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

