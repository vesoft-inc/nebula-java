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
public class GetNeighborsResponse implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("GetNeighborsResponse");
  private static final TField RESULT_FIELD_DESC = new TField("result", TType.STRUCT, (short)1);
  private static final TField VERTICES_FIELD_DESC = new TField("vertices", TType.STRUCT, (short)2);

  public ResponseCommon result;
  public com.vesoft.nebula.DataSet vertices;
  public static final int RESULT = 1;
  public static final int VERTICES = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(RESULT, new FieldMetaData("result", TFieldRequirementType.REQUIRED, 
        new StructMetaData(TType.STRUCT, ResponseCommon.class)));
    tmpMetaDataMap.put(VERTICES, new FieldMetaData("vertices", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.DataSet.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(GetNeighborsResponse.class, metaDataMap);
  }

  public GetNeighborsResponse() {
  }

  public GetNeighborsResponse(
      ResponseCommon result) {
    this();
    this.result = result;
  }

  public GetNeighborsResponse(
      ResponseCommon result,
      com.vesoft.nebula.DataSet vertices) {
    this();
    this.result = result;
    this.vertices = vertices;
  }

  public static class Builder {
    private ResponseCommon result;
    private com.vesoft.nebula.DataSet vertices;

    public Builder() {
    }

    public Builder setResult(final ResponseCommon result) {
      this.result = result;
      return this;
    }

    public Builder setVertices(final com.vesoft.nebula.DataSet vertices) {
      this.vertices = vertices;
      return this;
    }

    public GetNeighborsResponse build() {
      GetNeighborsResponse result = new GetNeighborsResponse();
      result.setResult(this.result);
      result.setVertices(this.vertices);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetNeighborsResponse(GetNeighborsResponse other) {
    if (other.isSetResult()) {
      this.result = TBaseHelper.deepCopy(other.result);
    }
    if (other.isSetVertices()) {
      this.vertices = TBaseHelper.deepCopy(other.vertices);
    }
  }

  public GetNeighborsResponse deepCopy() {
    return new GetNeighborsResponse(this);
  }

  public ResponseCommon getResult() {
    return this.result;
  }

  public GetNeighborsResponse setResult(ResponseCommon result) {
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

  public com.vesoft.nebula.DataSet getVertices() {
    return this.vertices;
  }

  public GetNeighborsResponse setVertices(com.vesoft.nebula.DataSet vertices) {
    this.vertices = vertices;
    return this;
  }

  public void unsetVertices() {
    this.vertices = null;
  }

  // Returns true if field vertices is set (has been assigned a value) and false otherwise
  public boolean isSetVertices() {
    return this.vertices != null;
  }

  public void setVerticesIsSet(boolean __value) {
    if (!__value) {
      this.vertices = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case RESULT:
      if (__value == null) {
        unsetResult();
      } else {
        setResult((ResponseCommon)__value);
      }
      break;

    case VERTICES:
      if (__value == null) {
        unsetVertices();
      } else {
        setVertices((com.vesoft.nebula.DataSet)__value);
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

    case VERTICES:
      return getVertices();

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
    if (!(_that instanceof GetNeighborsResponse))
      return false;
    GetNeighborsResponse that = (GetNeighborsResponse)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetResult(), that.isSetResult(), this.result, that.result)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetVertices(), that.isSetVertices(), this.vertices, that.vertices)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {result, vertices});
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
        case VERTICES:
          if (__field.type == TType.STRUCT) {
            this.vertices = new com.vesoft.nebula.DataSet();
            this.vertices.read(iprot);
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
    if (this.vertices != null) {
      if (isSetVertices()) {
        oprot.writeFieldBegin(VERTICES_FIELD_DESC);
        this.vertices.write(oprot);
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
    StringBuilder sb = new StringBuilder("GetNeighborsResponse");
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
    if (isSetVertices())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("vertices");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getVertices() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getVertices(), indent + 1, prettyPrint));
      }
      first = false;
    }
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

