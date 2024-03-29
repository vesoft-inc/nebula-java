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
public class PrivilegeTag implements TBase, java.io.Serializable, Cloneable, Comparable<PrivilegeTag> {
  private static final TStruct STRUCT_DESC = new TStruct("PrivilegeTag");
  private static final TField TAG_ID_FIELD_DESC = new TField("tag_id", TType.I32, (short)1);
  private static final TField TAG_NAME_FIELD_DESC = new TField("tag_name", TType.STRING, (short)2);

  public int tag_id;
  public byte[] tag_name;
  public static final int TAG_ID = 1;
  public static final int TAG_NAME = 2;

  // isset id assignments
  private static final int __TAG_ID_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(TAG_ID, new FieldMetaData("tag_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(TAG_NAME, new FieldMetaData("tag_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(PrivilegeTag.class, metaDataMap);
  }

  public PrivilegeTag() {
  }

  public PrivilegeTag(
      int tag_id,
      byte[] tag_name) {
    this();
    this.tag_id = tag_id;
    setTag_idIsSet(true);
    this.tag_name = tag_name;
  }

  public static class Builder {
    private int tag_id;
    private byte[] tag_name;

    BitSet __optional_isset = new BitSet(1);

    public Builder() {
    }

    public Builder setTag_id(final int tag_id) {
      this.tag_id = tag_id;
      __optional_isset.set(__TAG_ID_ISSET_ID, true);
      return this;
    }

    public Builder setTag_name(final byte[] tag_name) {
      this.tag_name = tag_name;
      return this;
    }

    public PrivilegeTag build() {
      PrivilegeTag result = new PrivilegeTag();
      if (__optional_isset.get(__TAG_ID_ISSET_ID)) {
        result.setTag_id(this.tag_id);
      }
      result.setTag_name(this.tag_name);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PrivilegeTag(PrivilegeTag other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.tag_id = TBaseHelper.deepCopy(other.tag_id);
    if (other.isSetTag_name()) {
      this.tag_name = TBaseHelper.deepCopy(other.tag_name);
    }
  }

  public PrivilegeTag deepCopy() {
    return new PrivilegeTag(this);
  }

  public int getTag_id() {
    return this.tag_id;
  }

  public PrivilegeTag setTag_id(int tag_id) {
    this.tag_id = tag_id;
    setTag_idIsSet(true);
    return this;
  }

  public void unsetTag_id() {
    __isset_bit_vector.clear(__TAG_ID_ISSET_ID);
  }

  // Returns true if field tag_id is set (has been assigned a value) and false otherwise
  public boolean isSetTag_id() {
    return __isset_bit_vector.get(__TAG_ID_ISSET_ID);
  }

  public void setTag_idIsSet(boolean __value) {
    __isset_bit_vector.set(__TAG_ID_ISSET_ID, __value);
  }

  public byte[] getTag_name() {
    return this.tag_name;
  }

  public PrivilegeTag setTag_name(byte[] tag_name) {
    this.tag_name = tag_name;
    return this;
  }

  public void unsetTag_name() {
    this.tag_name = null;
  }

  // Returns true if field tag_name is set (has been assigned a value) and false otherwise
  public boolean isSetTag_name() {
    return this.tag_name != null;
  }

  public void setTag_nameIsSet(boolean __value) {
    if (!__value) {
      this.tag_name = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case TAG_ID:
      if (__value == null) {
        unsetTag_id();
      } else {
        setTag_id((Integer)__value);
      }
      break;

    case TAG_NAME:
      if (__value == null) {
        unsetTag_name();
      } else {
        setTag_name((byte[])__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case TAG_ID:
      return new Integer(getTag_id());

    case TAG_NAME:
      return getTag_name();

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
    if (!(_that instanceof PrivilegeTag))
      return false;
    PrivilegeTag that = (PrivilegeTag)_that;

    if (!TBaseHelper.equalsNobinary(this.tag_id, that.tag_id)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetTag_name(), that.isSetTag_name(), this.tag_name, that.tag_name)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {tag_id, tag_name});
  }

  @Override
  public int compareTo(PrivilegeTag other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetTag_id()).compareTo(other.isSetTag_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(tag_id, other.tag_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetTag_name()).compareTo(other.isSetTag_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(tag_name, other.tag_name);
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
        case TAG_ID:
          if (__field.type == TType.I32) {
            this.tag_id = iprot.readI32();
            setTag_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TAG_NAME:
          if (__field.type == TType.STRING) {
            this.tag_name = iprot.readBinary();
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
    oprot.writeFieldBegin(TAG_ID_FIELD_DESC);
    oprot.writeI32(this.tag_id);
    oprot.writeFieldEnd();
    if (this.tag_name != null) {
      oprot.writeFieldBegin(TAG_NAME_FIELD_DESC);
      oprot.writeBinary(this.tag_name);
      oprot.writeFieldEnd();
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
    StringBuilder sb = new StringBuilder("PrivilegeTag");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("tag_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getTag_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("tag_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getTag_name() == null) {
      sb.append("null");
    } else {
        int __tag_name_size = Math.min(this.getTag_name().length, 128);
        for (int i = 0; i < __tag_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getTag_name()[i]).length() > 1 ? Integer.toHexString(this.getTag_name()[i]).substring(Integer.toHexString(this.getTag_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getTag_name()[i]).toUpperCase());
        }
        if (this.getTag_name().length > 128) sb.append(" ...");
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

