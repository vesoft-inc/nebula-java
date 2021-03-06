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
public class HostPair implements TBase, java.io.Serializable, Cloneable, Comparable<HostPair> {
  private static final TStruct STRUCT_DESC = new TStruct("HostPair");
  private static final TField FROM_HOST_FIELD_DESC = new TField("from_host", TType.STRUCT, (short)1);
  private static final TField TO_HOST_FIELD_DESC = new TField("to_host", TType.STRUCT, (short)2);

  public com.vesoft.nebula.HostAddr from_host;
  public com.vesoft.nebula.HostAddr to_host;
  public static final int FROM_HOST = 1;
  public static final int TO_HOST = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(FROM_HOST, new FieldMetaData("from_host", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(TO_HOST, new FieldMetaData("to_host", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(HostPair.class, metaDataMap);
  }

  public HostPair() {
  }

  public HostPair(
      com.vesoft.nebula.HostAddr from_host,
      com.vesoft.nebula.HostAddr to_host) {
    this();
    this.from_host = from_host;
    this.to_host = to_host;
  }

  public static class Builder {
    private com.vesoft.nebula.HostAddr from_host;
    private com.vesoft.nebula.HostAddr to_host;

    public Builder() {
    }

    public Builder setFrom_host(final com.vesoft.nebula.HostAddr from_host) {
      this.from_host = from_host;
      return this;
    }

    public Builder setTo_host(final com.vesoft.nebula.HostAddr to_host) {
      this.to_host = to_host;
      return this;
    }

    public HostPair build() {
      HostPair result = new HostPair();
      result.setFrom_host(this.from_host);
      result.setTo_host(this.to_host);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public HostPair(HostPair other) {
    if (other.isSetFrom_host()) {
      this.from_host = TBaseHelper.deepCopy(other.from_host);
    }
    if (other.isSetTo_host()) {
      this.to_host = TBaseHelper.deepCopy(other.to_host);
    }
  }

  public HostPair deepCopy() {
    return new HostPair(this);
  }

  public com.vesoft.nebula.HostAddr getFrom_host() {
    return this.from_host;
  }

  public HostPair setFrom_host(com.vesoft.nebula.HostAddr from_host) {
    this.from_host = from_host;
    return this;
  }

  public void unsetFrom_host() {
    this.from_host = null;
  }

  // Returns true if field from_host is set (has been assigned a value) and false otherwise
  public boolean isSetFrom_host() {
    return this.from_host != null;
  }

  public void setFrom_hostIsSet(boolean __value) {
    if (!__value) {
      this.from_host = null;
    }
  }

  public com.vesoft.nebula.HostAddr getTo_host() {
    return this.to_host;
  }

  public HostPair setTo_host(com.vesoft.nebula.HostAddr to_host) {
    this.to_host = to_host;
    return this;
  }

  public void unsetTo_host() {
    this.to_host = null;
  }

  // Returns true if field to_host is set (has been assigned a value) and false otherwise
  public boolean isSetTo_host() {
    return this.to_host != null;
  }

  public void setTo_hostIsSet(boolean __value) {
    if (!__value) {
      this.to_host = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case FROM_HOST:
      if (__value == null) {
        unsetFrom_host();
      } else {
        setFrom_host((com.vesoft.nebula.HostAddr)__value);
      }
      break;

    case TO_HOST:
      if (__value == null) {
        unsetTo_host();
      } else {
        setTo_host((com.vesoft.nebula.HostAddr)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case FROM_HOST:
      return getFrom_host();

    case TO_HOST:
      return getTo_host();

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
    if (!(_that instanceof HostPair))
      return false;
    HostPair that = (HostPair)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetFrom_host(), that.isSetFrom_host(), this.from_host, that.from_host)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetTo_host(), that.isSetTo_host(), this.to_host, that.to_host)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {from_host, to_host});
  }

  @Override
  public int compareTo(HostPair other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetFrom_host()).compareTo(other.isSetFrom_host());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(from_host, other.from_host);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetTo_host()).compareTo(other.isSetTo_host());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(to_host, other.to_host);
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
        case FROM_HOST:
          if (__field.type == TType.STRUCT) {
            this.from_host = new com.vesoft.nebula.HostAddr();
            this.from_host.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TO_HOST:
          if (__field.type == TType.STRUCT) {
            this.to_host = new com.vesoft.nebula.HostAddr();
            this.to_host.read(iprot);
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
    if (this.from_host != null) {
      oprot.writeFieldBegin(FROM_HOST_FIELD_DESC);
      this.from_host.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.to_host != null) {
      oprot.writeFieldBegin(TO_HOST_FIELD_DESC);
      this.to_host.write(oprot);
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
    StringBuilder sb = new StringBuilder("HostPair");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("from_host");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getFrom_host() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getFrom_host(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("to_host");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getTo_host() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getTo_host(), indent + 1, prettyPrint));
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

