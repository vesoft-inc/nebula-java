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
public class CreateCPResp implements TBase, java.io.Serializable, Cloneable, Comparable<CreateCPResp> {
  private static final TStruct STRUCT_DESC = new TStruct("CreateCPResp");
  private static final TField RESULT_FIELD_DESC = new TField("result", TType.STRUCT, (short)1);
  private static final TField INFO_FIELD_DESC = new TField("info", TType.LIST, (short)2);

  public ResponseCommon result;
  public List<com.vesoft.nebula.CheckpointInfo> info;
  public static final int RESULT = 1;
  public static final int INFO = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(RESULT, new FieldMetaData("result", TFieldRequirementType.REQUIRED, 
        new StructMetaData(TType.STRUCT, ResponseCommon.class)));
    tmpMetaDataMap.put(INFO, new FieldMetaData("info", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, com.vesoft.nebula.CheckpointInfo.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(CreateCPResp.class, metaDataMap);
  }

  public CreateCPResp() {
  }

  public CreateCPResp(
      ResponseCommon result) {
    this();
    this.result = result;
  }

  public CreateCPResp(
      ResponseCommon result,
      List<com.vesoft.nebula.CheckpointInfo> info) {
    this();
    this.result = result;
    this.info = info;
  }

  public static class Builder {
    private ResponseCommon result;
    private List<com.vesoft.nebula.CheckpointInfo> info;

    public Builder() {
    }

    public Builder setResult(final ResponseCommon result) {
      this.result = result;
      return this;
    }

    public Builder setInfo(final List<com.vesoft.nebula.CheckpointInfo> info) {
      this.info = info;
      return this;
    }

    public CreateCPResp build() {
      CreateCPResp result = new CreateCPResp();
      result.setResult(this.result);
      result.setInfo(this.info);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CreateCPResp(CreateCPResp other) {
    if (other.isSetResult()) {
      this.result = TBaseHelper.deepCopy(other.result);
    }
    if (other.isSetInfo()) {
      this.info = TBaseHelper.deepCopy(other.info);
    }
  }

  public CreateCPResp deepCopy() {
    return new CreateCPResp(this);
  }

  public ResponseCommon getResult() {
    return this.result;
  }

  public CreateCPResp setResult(ResponseCommon result) {
    this.result = result;
    return this;
  }

  public void unsetResult() {
    this.result = null;
  }

  // Returns true if field result is set (has been assigned a value) and false otherwise
  public boolean isSetResult() {
    return this.result != null;
  }

  public void setResultIsSet(boolean __value) {
    if (!__value) {
      this.result = null;
    }
  }

  public List<com.vesoft.nebula.CheckpointInfo> getInfo() {
    return this.info;
  }

  public CreateCPResp setInfo(List<com.vesoft.nebula.CheckpointInfo> info) {
    this.info = info;
    return this;
  }

  public void unsetInfo() {
    this.info = null;
  }

  // Returns true if field info is set (has been assigned a value) and false otherwise
  public boolean isSetInfo() {
    return this.info != null;
  }

  public void setInfoIsSet(boolean __value) {
    if (!__value) {
      this.info = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case RESULT:
      if (__value == null) {
        unsetResult();
      } else {
        setResult((ResponseCommon)__value);
      }
      break;

    case INFO:
      if (__value == null) {
        unsetInfo();
      } else {
        setInfo((List<com.vesoft.nebula.CheckpointInfo>)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case RESULT:
      return getResult();

    case INFO:
      return getInfo();

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
    if (!(_that instanceof CreateCPResp))
      return false;
    CreateCPResp that = (CreateCPResp)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetResult(), that.isSetResult(), this.result, that.result)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetInfo(), that.isSetInfo(), this.info, that.info)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {result, info});
  }

  @Override
  public int compareTo(CreateCPResp other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetResult()).compareTo(other.isSetResult());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(result, other.result);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetInfo()).compareTo(other.isSetInfo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(info, other.info);
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
        case RESULT:
          if (__field.type == TType.STRUCT) {
            this.result = new ResponseCommon();
            this.result.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case INFO:
          if (__field.type == TType.LIST) {
            {
              TList _list293 = iprot.readListBegin();
              this.info = new ArrayList<com.vesoft.nebula.CheckpointInfo>(Math.max(0, _list293.size));
              for (int _i294 = 0; 
                   (_list293.size < 0) ? iprot.peekList() : (_i294 < _list293.size); 
                   ++_i294)
              {
                com.vesoft.nebula.CheckpointInfo _elem295;
                _elem295 = new com.vesoft.nebula.CheckpointInfo();
                _elem295.read(iprot);
                this.info.add(_elem295);
              }
              iprot.readListEnd();
            }
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
    if (this.result != null) {
      oprot.writeFieldBegin(RESULT_FIELD_DESC);
      this.result.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.info != null) {
      oprot.writeFieldBegin(INFO_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.info.size()));
        for (com.vesoft.nebula.CheckpointInfo _iter296 : this.info)        {
          _iter296.write(oprot);
        }
        oprot.writeListEnd();
      }
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
    StringBuilder sb = new StringBuilder("CreateCPResp");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("result");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getResult() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getResult(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("info");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getInfo() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getInfo(), indent + 1, prettyPrint));
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    if (result == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'result' was not present! Struct: " + toString());
    }
  }

}

