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
public class GetMetaDirInfoResp implements TBase, java.io.Serializable, Cloneable, Comparable<GetMetaDirInfoResp> {
  private static final TStruct STRUCT_DESC = new TStruct("GetMetaDirInfoResp");
  private static final TField CODE_FIELD_DESC = new TField("code", TType.I32, (short)1);
  private static final TField DIR_FIELD_DESC = new TField("dir", TType.STRUCT, (short)2);

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public com.vesoft.nebula.ErrorCode code;
  public com.vesoft.nebula.DirInfo dir;
  public static final int CODE = 1;
  public static final int DIR = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(CODE, new FieldMetaData("code", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(DIR, new FieldMetaData("dir", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.DirInfo.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(GetMetaDirInfoResp.class, metaDataMap);
  }

  public GetMetaDirInfoResp() {
  }

  public GetMetaDirInfoResp(
      com.vesoft.nebula.ErrorCode code,
      com.vesoft.nebula.DirInfo dir) {
    this();
    this.code = code;
    this.dir = dir;
  }

  public static class Builder {
    private com.vesoft.nebula.ErrorCode code;
    private com.vesoft.nebula.DirInfo dir;

    public Builder() {
    }

    public Builder setCode(final com.vesoft.nebula.ErrorCode code) {
      this.code = code;
      return this;
    }

    public Builder setDir(final com.vesoft.nebula.DirInfo dir) {
      this.dir = dir;
      return this;
    }

    public GetMetaDirInfoResp build() {
      GetMetaDirInfoResp result = new GetMetaDirInfoResp();
      result.setCode(this.code);
      result.setDir(this.dir);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetMetaDirInfoResp(GetMetaDirInfoResp other) {
    if (other.isSetCode()) {
      this.code = TBaseHelper.deepCopy(other.code);
    }
    if (other.isSetDir()) {
      this.dir = TBaseHelper.deepCopy(other.dir);
    }
  }

  public GetMetaDirInfoResp deepCopy() {
    return new GetMetaDirInfoResp(this);
  }

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public com.vesoft.nebula.ErrorCode getCode() {
    return this.code;
  }

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public GetMetaDirInfoResp setCode(com.vesoft.nebula.ErrorCode code) {
    this.code = code;
    return this;
  }

  public void unsetCode() {
    this.code = null;
  }

  // Returns true if field code is set (has been assigned a value) and false otherwise
  public boolean isSetCode() {
    return this.code != null;
  }

  public void setCodeIsSet(boolean __value) {
    if (!__value) {
      this.code = null;
    }
  }

  public com.vesoft.nebula.DirInfo getDir() {
    return this.dir;
  }

  public GetMetaDirInfoResp setDir(com.vesoft.nebula.DirInfo dir) {
    this.dir = dir;
    return this;
  }

  public void unsetDir() {
    this.dir = null;
  }

  // Returns true if field dir is set (has been assigned a value) and false otherwise
  public boolean isSetDir() {
    return this.dir != null;
  }

  public void setDirIsSet(boolean __value) {
    if (!__value) {
      this.dir = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case CODE:
      if (__value == null) {
        unsetCode();
      } else {
        setCode((com.vesoft.nebula.ErrorCode)__value);
      }
      break;

    case DIR:
      if (__value == null) {
        unsetDir();
      } else {
        setDir((com.vesoft.nebula.DirInfo)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case CODE:
      return getCode();

    case DIR:
      return getDir();

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
    if (!(_that instanceof GetMetaDirInfoResp))
      return false;
    GetMetaDirInfoResp that = (GetMetaDirInfoResp)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetCode(), that.isSetCode(), this.code, that.code)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetDir(), that.isSetDir(), this.dir, that.dir)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {code, dir});
  }

  @Override
  public int compareTo(GetMetaDirInfoResp other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetCode()).compareTo(other.isSetCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(code, other.code);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetDir()).compareTo(other.isSetDir());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(dir, other.dir);
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
        case CODE:
          if (__field.type == TType.I32) {
            this.code = com.vesoft.nebula.ErrorCode.findByValue(iprot.readI32());
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case DIR:
          if (__field.type == TType.STRUCT) {
            this.dir = new com.vesoft.nebula.DirInfo();
            this.dir.read(iprot);
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
    if (this.code != null) {
      oprot.writeFieldBegin(CODE_FIELD_DESC);
      oprot.writeI32(this.code == null ? 0 : this.code.getValue());
      oprot.writeFieldEnd();
    }
    if (this.dir != null) {
      oprot.writeFieldBegin(DIR_FIELD_DESC);
      this.dir.write(oprot);
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
    StringBuilder sb = new StringBuilder("GetMetaDirInfoResp");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("code");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getCode() == null) {
      sb.append("null");
    } else {
      String code_name = this.getCode() == null ? "null" : this.getCode().name();
      if (code_name != null) {
        sb.append(code_name);
        sb.append(" (");
      }
      sb.append(this.getCode());
      if (code_name != null) {
        sb.append(")");
      }
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("dir");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getDir() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getDir(), indent + 1, prettyPrint));
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

