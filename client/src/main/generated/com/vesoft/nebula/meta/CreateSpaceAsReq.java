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
public class CreateSpaceAsReq implements TBase, java.io.Serializable, Cloneable, Comparable<CreateSpaceAsReq> {
  private static final TStruct STRUCT_DESC = new TStruct("CreateSpaceAsReq");
  private static final TField OLD_SPACE_NAME_FIELD_DESC = new TField("old_space_name", TType.STRING, (short)1);
  private static final TField NEW_SPACE_NAME_FIELD_DESC = new TField("new_space_name", TType.STRING, (short)2);

  public byte[] old_space_name;
  public byte[] new_space_name;
  public static final int OLD_SPACE_NAME = 1;
  public static final int NEW_SPACE_NAME = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(OLD_SPACE_NAME, new FieldMetaData("old_space_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(NEW_SPACE_NAME, new FieldMetaData("new_space_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(CreateSpaceAsReq.class, metaDataMap);
  }

  public CreateSpaceAsReq() {
  }

  public CreateSpaceAsReq(
      byte[] old_space_name,
      byte[] new_space_name) {
    this();
    this.old_space_name = old_space_name;
    this.new_space_name = new_space_name;
  }

  public static class Builder {
    private byte[] old_space_name;
    private byte[] new_space_name;

    public Builder() {
    }

    public Builder setOld_space_name(final byte[] old_space_name) {
      this.old_space_name = old_space_name;
      return this;
    }

    public Builder setNew_space_name(final byte[] new_space_name) {
      this.new_space_name = new_space_name;
      return this;
    }

    public CreateSpaceAsReq build() {
      CreateSpaceAsReq result = new CreateSpaceAsReq();
      result.setOld_space_name(this.old_space_name);
      result.setNew_space_name(this.new_space_name);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CreateSpaceAsReq(CreateSpaceAsReq other) {
    if (other.isSetOld_space_name()) {
      this.old_space_name = TBaseHelper.deepCopy(other.old_space_name);
    }
    if (other.isSetNew_space_name()) {
      this.new_space_name = TBaseHelper.deepCopy(other.new_space_name);
    }
  }

  public CreateSpaceAsReq deepCopy() {
    return new CreateSpaceAsReq(this);
  }

  public byte[] getOld_space_name() {
    return this.old_space_name;
  }

  public CreateSpaceAsReq setOld_space_name(byte[] old_space_name) {
    this.old_space_name = old_space_name;
    return this;
  }

  public void unsetOld_space_name() {
    this.old_space_name = null;
  }

  // Returns true if field old_space_name is set (has been assigned a value) and false otherwise
  public boolean isSetOld_space_name() {
    return this.old_space_name != null;
  }

  public void setOld_space_nameIsSet(boolean __value) {
    if (!__value) {
      this.old_space_name = null;
    }
  }

  public byte[] getNew_space_name() {
    return this.new_space_name;
  }

  public CreateSpaceAsReq setNew_space_name(byte[] new_space_name) {
    this.new_space_name = new_space_name;
    return this;
  }

  public void unsetNew_space_name() {
    this.new_space_name = null;
  }

  // Returns true if field new_space_name is set (has been assigned a value) and false otherwise
  public boolean isSetNew_space_name() {
    return this.new_space_name != null;
  }

  public void setNew_space_nameIsSet(boolean __value) {
    if (!__value) {
      this.new_space_name = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case OLD_SPACE_NAME:
      if (__value == null) {
        unsetOld_space_name();
      } else {
        setOld_space_name((byte[])__value);
      }
      break;

    case NEW_SPACE_NAME:
      if (__value == null) {
        unsetNew_space_name();
      } else {
        setNew_space_name((byte[])__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case OLD_SPACE_NAME:
      return getOld_space_name();

    case NEW_SPACE_NAME:
      return getNew_space_name();

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
    if (!(_that instanceof CreateSpaceAsReq))
      return false;
    CreateSpaceAsReq that = (CreateSpaceAsReq)_that;

    if (!TBaseHelper.equalsSlow(this.isSetOld_space_name(), that.isSetOld_space_name(), this.old_space_name, that.old_space_name)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetNew_space_name(), that.isSetNew_space_name(), this.new_space_name, that.new_space_name)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {old_space_name, new_space_name});
  }

  @Override
  public int compareTo(CreateSpaceAsReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetOld_space_name()).compareTo(other.isSetOld_space_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(old_space_name, other.old_space_name);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetNew_space_name()).compareTo(other.isSetNew_space_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(new_space_name, other.new_space_name);
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
        case OLD_SPACE_NAME:
          if (__field.type == TType.STRING) {
            this.old_space_name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case NEW_SPACE_NAME:
          if (__field.type == TType.STRING) {
            this.new_space_name = iprot.readBinary();
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
    if (this.old_space_name != null) {
      oprot.writeFieldBegin(OLD_SPACE_NAME_FIELD_DESC);
      oprot.writeBinary(this.old_space_name);
      oprot.writeFieldEnd();
    }
    if (this.new_space_name != null) {
      oprot.writeFieldBegin(NEW_SPACE_NAME_FIELD_DESC);
      oprot.writeBinary(this.new_space_name);
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
    StringBuilder sb = new StringBuilder("CreateSpaceAsReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("old_space_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getOld_space_name() == null) {
      sb.append("null");
    } else {
        int __old_space_name_size = Math.min(this.getOld_space_name().length, 128);
        for (int i = 0; i < __old_space_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getOld_space_name()[i]).length() > 1 ? Integer.toHexString(this.getOld_space_name()[i]).substring(Integer.toHexString(this.getOld_space_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getOld_space_name()[i]).toUpperCase());
        }
        if (this.getOld_space_name().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("new_space_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getNew_space_name() == null) {
      sb.append("null");
    } else {
        int __new_space_name_size = Math.min(this.getNew_space_name().length, 128);
        for (int i = 0; i < __new_space_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getNew_space_name()[i]).length() > 1 ? Integer.toHexString(this.getNew_space_name()[i]).substring(Integer.toHexString(this.getNew_space_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getNew_space_name()[i]).toUpperCase());
        }
        if (this.getNew_space_name().length > 128) sb.append(" ...");
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

