/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.graph;

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
public class VerifyClientVersionResp implements TBase, java.io.Serializable, Cloneable, Comparable<VerifyClientVersionResp> {
  private static final TStruct STRUCT_DESC = new TStruct("VerifyClientVersionResp");
  private static final TField ERROR_CODE_FIELD_DESC = new TField("error_code", TType.I32, (short)1);
  private static final TField ERROR_MSG_FIELD_DESC = new TField("error_msg", TType.STRING, (short)2);

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public com.vesoft.nebula.ErrorCode error_code;
  public byte[] error_msg;
  public static final int ERROR_CODE = 1;
  public static final int ERROR_MSG = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ERROR_CODE, new FieldMetaData("error_code", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(ERROR_MSG, new FieldMetaData("error_msg", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(VerifyClientVersionResp.class, metaDataMap);
  }

  public VerifyClientVersionResp() {
  }

  public VerifyClientVersionResp(
      com.vesoft.nebula.ErrorCode error_code) {
    this();
    this.error_code = error_code;
  }

  public VerifyClientVersionResp(
      com.vesoft.nebula.ErrorCode error_code,
      byte[] error_msg) {
    this();
    this.error_code = error_code;
    this.error_msg = error_msg;
  }

  public static class Builder {
    private com.vesoft.nebula.ErrorCode error_code;
    private byte[] error_msg;

    public Builder() {
    }

    public Builder setError_code(final com.vesoft.nebula.ErrorCode error_code) {
      this.error_code = error_code;
      return this;
    }

    public Builder setError_msg(final byte[] error_msg) {
      this.error_msg = error_msg;
      return this;
    }

    public VerifyClientVersionResp build() {
      VerifyClientVersionResp result = new VerifyClientVersionResp();
      result.setError_code(this.error_code);
      result.setError_msg(this.error_msg);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public VerifyClientVersionResp(VerifyClientVersionResp other) {
    if (other.isSetError_code()) {
      this.error_code = TBaseHelper.deepCopy(other.error_code);
    }
    if (other.isSetError_msg()) {
      this.error_msg = TBaseHelper.deepCopy(other.error_msg);
    }
  }

  public VerifyClientVersionResp deepCopy() {
    return new VerifyClientVersionResp(this);
  }

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public com.vesoft.nebula.ErrorCode getError_code() {
    return this.error_code;
  }

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public VerifyClientVersionResp setError_code(com.vesoft.nebula.ErrorCode error_code) {
    this.error_code = error_code;
    return this;
  }

  public void unsetError_code() {
    this.error_code = null;
  }

  // Returns true if field error_code is set (has been assigned a value) and false otherwise
  public boolean isSetError_code() {
    return this.error_code != null;
  }

  public void setError_codeIsSet(boolean __value) {
    if (!__value) {
      this.error_code = null;
    }
  }

  public byte[] getError_msg() {
    return this.error_msg;
  }

  public VerifyClientVersionResp setError_msg(byte[] error_msg) {
    this.error_msg = error_msg;
    return this;
  }

  public void unsetError_msg() {
    this.error_msg = null;
  }

  // Returns true if field error_msg is set (has been assigned a value) and false otherwise
  public boolean isSetError_msg() {
    return this.error_msg != null;
  }

  public void setError_msgIsSet(boolean __value) {
    if (!__value) {
      this.error_msg = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case ERROR_CODE:
      if (__value == null) {
        unsetError_code();
      } else {
        setError_code((com.vesoft.nebula.ErrorCode)__value);
      }
      break;

    case ERROR_MSG:
      if (__value == null) {
        unsetError_msg();
      } else {
        setError_msg((byte[])__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case ERROR_CODE:
      return getError_code();

    case ERROR_MSG:
      return getError_msg();

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
    if (!(_that instanceof VerifyClientVersionResp))
      return false;
    VerifyClientVersionResp that = (VerifyClientVersionResp)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetError_code(), that.isSetError_code(), this.error_code, that.error_code)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetError_msg(), that.isSetError_msg(), this.error_msg, that.error_msg)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {error_code, error_msg});
  }

  @Override
  public int compareTo(VerifyClientVersionResp other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetError_code()).compareTo(other.isSetError_code());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(error_code, other.error_code);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetError_msg()).compareTo(other.isSetError_msg());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(error_msg, other.error_msg);
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
        case ERROR_CODE:
          if (__field.type == TType.I32) {
            this.error_code = com.vesoft.nebula.ErrorCode.findByValue(iprot.readI32());
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case ERROR_MSG:
          if (__field.type == TType.STRING) {
            this.error_msg = iprot.readBinary();
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
    if (this.error_code != null) {
      oprot.writeFieldBegin(ERROR_CODE_FIELD_DESC);
      oprot.writeI32(this.error_code == null ? 0 : this.error_code.getValue());
      oprot.writeFieldEnd();
    }
    if (this.error_msg != null) {
      if (isSetError_msg()) {
        oprot.writeFieldBegin(ERROR_MSG_FIELD_DESC);
        oprot.writeBinary(this.error_msg);
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
    StringBuilder sb = new StringBuilder("VerifyClientVersionResp");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("error_code");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getError_code() == null) {
      sb.append("null");
    } else {
      String error_code_name = this.getError_code() == null ? "null" : this.getError_code().name();
      if (error_code_name != null) {
        sb.append(error_code_name);
        sb.append(" (");
      }
      sb.append(this.getError_code());
      if (error_code_name != null) {
        sb.append(")");
      }
    }
    first = false;
    if (isSetError_msg())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("error_msg");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getError_msg() == null) {
        sb.append("null");
      } else {
          int __error_msg_size = Math.min(this.getError_msg().length, 128);
          for (int i = 0; i < __error_msg_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getError_msg()[i]).length() > 1 ? Integer.toHexString(this.getError_msg()[i]).substring(Integer.toHexString(this.getError_msg()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getError_msg()[i]).toUpperCase());
          }
          if (this.getError_msg().length > 128) sb.append(" ...");
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    if (error_code == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'error_code' was not present! Struct: " + toString());
    }
  }

}

