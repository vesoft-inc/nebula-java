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
public class DrainerClientInfo implements TBase, java.io.Serializable, Cloneable, Comparable<DrainerClientInfo> {
  private static final TStruct STRUCT_DESC = new TStruct("DrainerClientInfo");
  private static final TField HOST_FIELD_DESC = new TField("host", TType.STRUCT, (short)1);
  private static final TField SPACE_NAME_FIELD_DESC = new TField("space_name", TType.STRING, (short)2);

  public com.vesoft.nebula.HostAddr host;
  public byte[] space_name;
  public static final int HOST = 1;
  public static final int SPACE_NAME = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(HOST, new FieldMetaData("host", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(SPACE_NAME, new FieldMetaData("space_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(DrainerClientInfo.class, metaDataMap);
  }

  public DrainerClientInfo() {
  }

  public DrainerClientInfo(
      com.vesoft.nebula.HostAddr host,
      byte[] space_name) {
    this();
    this.host = host;
    this.space_name = space_name;
  }

  public static class Builder {
    private com.vesoft.nebula.HostAddr host;
    private byte[] space_name;

    public Builder() {
    }

    public Builder setHost(final com.vesoft.nebula.HostAddr host) {
      this.host = host;
      return this;
    }

    public Builder setSpace_name(final byte[] space_name) {
      this.space_name = space_name;
      return this;
    }

    public DrainerClientInfo build() {
      DrainerClientInfo result = new DrainerClientInfo();
      result.setHost(this.host);
      result.setSpace_name(this.space_name);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DrainerClientInfo(DrainerClientInfo other) {
    if (other.isSetHost()) {
      this.host = TBaseHelper.deepCopy(other.host);
    }
    if (other.isSetSpace_name()) {
      this.space_name = TBaseHelper.deepCopy(other.space_name);
    }
  }

  public DrainerClientInfo deepCopy() {
    return new DrainerClientInfo(this);
  }

  public com.vesoft.nebula.HostAddr getHost() {
    return this.host;
  }

  public DrainerClientInfo setHost(com.vesoft.nebula.HostAddr host) {
    this.host = host;
    return this;
  }

  public void unsetHost() {
    this.host = null;
  }

  // Returns true if field host is set (has been assigned a value) and false otherwise
  public boolean isSetHost() {
    return this.host != null;
  }

  public void setHostIsSet(boolean __value) {
    if (!__value) {
      this.host = null;
    }
  }

  public byte[] getSpace_name() {
    return this.space_name;
  }

  public DrainerClientInfo setSpace_name(byte[] space_name) {
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

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case HOST:
      if (__value == null) {
        unsetHost();
      } else {
        setHost((com.vesoft.nebula.HostAddr)__value);
      }
      break;

    case SPACE_NAME:
      if (__value == null) {
        unsetSpace_name();
      } else {
        setSpace_name((byte[])__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case HOST:
      return getHost();

    case SPACE_NAME:
      return getSpace_name();

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
    if (!(_that instanceof DrainerClientInfo))
      return false;
    DrainerClientInfo that = (DrainerClientInfo)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetHost(), that.isSetHost(), this.host, that.host)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetSpace_name(), that.isSetSpace_name(), this.space_name, that.space_name)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {host, space_name});
  }

  @Override
  public int compareTo(DrainerClientInfo other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetHost()).compareTo(other.isSetHost());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(host, other.host);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetSpace_name()).compareTo(other.isSetSpace_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space_name, other.space_name);
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
        case HOST:
          if (__field.type == TType.STRUCT) {
            this.host = new com.vesoft.nebula.HostAddr();
            this.host.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case SPACE_NAME:
          if (__field.type == TType.STRING) {
            this.space_name = iprot.readBinary();
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
    if (this.host != null) {
      oprot.writeFieldBegin(HOST_FIELD_DESC);
      this.host.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.space_name != null) {
      oprot.writeFieldBegin(SPACE_NAME_FIELD_DESC);
      oprot.writeBinary(this.space_name);
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
    StringBuilder sb = new StringBuilder("DrainerClientInfo");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("host");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getHost() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getHost(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
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
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

