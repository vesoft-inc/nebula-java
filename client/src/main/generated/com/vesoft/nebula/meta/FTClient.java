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
public class FTClient implements TBase, java.io.Serializable, Cloneable, Comparable<FTClient> {
  private static final TStruct STRUCT_DESC = new TStruct("FTClient");
  private static final TField HOST_FIELD_DESC = new TField("host", TType.STRUCT, (short)1);
  private static final TField USER_FIELD_DESC = new TField("user", TType.STRING, (short)2);
  private static final TField PWD_FIELD_DESC = new TField("pwd", TType.STRING, (short)3);

  public com.vesoft.nebula.HostAddr host;
  public byte[] user;
  public byte[] pwd;
  public static final int HOST = 1;
  public static final int USER = 2;
  public static final int PWD = 3;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(HOST, new FieldMetaData("host", TFieldRequirementType.REQUIRED, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(USER, new FieldMetaData("user", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(PWD, new FieldMetaData("pwd", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(FTClient.class, metaDataMap);
  }

  public FTClient() {
  }

  public FTClient(
      com.vesoft.nebula.HostAddr host) {
    this();
    this.host = host;
  }

  public FTClient(
      com.vesoft.nebula.HostAddr host,
      byte[] user,
      byte[] pwd) {
    this();
    this.host = host;
    this.user = user;
    this.pwd = pwd;
  }

  public static class Builder {
    private com.vesoft.nebula.HostAddr host;
    private byte[] user;
    private byte[] pwd;

    public Builder() {
    }

    public Builder setHost(final com.vesoft.nebula.HostAddr host) {
      this.host = host;
      return this;
    }

    public Builder setUser(final byte[] user) {
      this.user = user;
      return this;
    }

    public Builder setPwd(final byte[] pwd) {
      this.pwd = pwd;
      return this;
    }

    public FTClient build() {
      FTClient result = new FTClient();
      result.setHost(this.host);
      result.setUser(this.user);
      result.setPwd(this.pwd);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FTClient(FTClient other) {
    if (other.isSetHost()) {
      this.host = TBaseHelper.deepCopy(other.host);
    }
    if (other.isSetUser()) {
      this.user = TBaseHelper.deepCopy(other.user);
    }
    if (other.isSetPwd()) {
      this.pwd = TBaseHelper.deepCopy(other.pwd);
    }
  }

  public FTClient deepCopy() {
    return new FTClient(this);
  }

  public com.vesoft.nebula.HostAddr getHost() {
    return this.host;
  }

  public FTClient setHost(com.vesoft.nebula.HostAddr host) {
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

  public byte[] getUser() {
    return this.user;
  }

  public FTClient setUser(byte[] user) {
    this.user = user;
    return this;
  }

  public void unsetUser() {
    this.user = null;
  }

  // Returns true if field user is set (has been assigned a value) and false otherwise
  public boolean isSetUser() {
    return this.user != null;
  }

  public void setUserIsSet(boolean __value) {
    if (!__value) {
      this.user = null;
    }
  }

  public byte[] getPwd() {
    return this.pwd;
  }

  public FTClient setPwd(byte[] pwd) {
    this.pwd = pwd;
    return this;
  }

  public void unsetPwd() {
    this.pwd = null;
  }

  // Returns true if field pwd is set (has been assigned a value) and false otherwise
  public boolean isSetPwd() {
    return this.pwd != null;
  }

  public void setPwdIsSet(boolean __value) {
    if (!__value) {
      this.pwd = null;
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

    case USER:
      if (__value == null) {
        unsetUser();
      } else {
        setUser((byte[])__value);
      }
      break;

    case PWD:
      if (__value == null) {
        unsetPwd();
      } else {
        setPwd((byte[])__value);
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

    case USER:
      return getUser();

    case PWD:
      return getPwd();

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
    if (!(_that instanceof FTClient))
      return false;
    FTClient that = (FTClient)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetHost(), that.isSetHost(), this.host, that.host)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetUser(), that.isSetUser(), this.user, that.user)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetPwd(), that.isSetPwd(), this.pwd, that.pwd)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {host, user, pwd});
  }

  @Override
  public int compareTo(FTClient other) {
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
    lastComparison = Boolean.valueOf(isSetUser()).compareTo(other.isSetUser());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(user, other.user);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPwd()).compareTo(other.isSetPwd());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(pwd, other.pwd);
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
        case USER:
          if (__field.type == TType.STRING) {
            this.user = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case PWD:
          if (__field.type == TType.STRING) {
            this.pwd = iprot.readBinary();
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
    if (this.user != null) {
      if (isSetUser()) {
        oprot.writeFieldBegin(USER_FIELD_DESC);
        oprot.writeBinary(this.user);
        oprot.writeFieldEnd();
      }
    }
    if (this.pwd != null) {
      if (isSetPwd()) {
        oprot.writeFieldBegin(PWD_FIELD_DESC);
        oprot.writeBinary(this.pwd);
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
    StringBuilder sb = new StringBuilder("FTClient");
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
    if (isSetUser())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("user");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getUser() == null) {
        sb.append("null");
      } else {
          int __user_size = Math.min(this.getUser().length, 128);
          for (int i = 0; i < __user_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getUser()[i]).length() > 1 ? Integer.toHexString(this.getUser()[i]).substring(Integer.toHexString(this.getUser()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getUser()[i]).toUpperCase());
          }
          if (this.getUser().length > 128) sb.append(" ...");
      }
      first = false;
    }
    if (isSetPwd())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("pwd");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getPwd() == null) {
        sb.append("null");
      } else {
          int __pwd_size = Math.min(this.getPwd().length, 128);
          for (int i = 0; i < __pwd_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getPwd()[i]).length() > 1 ? Integer.toHexString(this.getPwd()[i]).substring(Integer.toHexString(this.getPwd()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getPwd()[i]).toUpperCase());
          }
          if (this.getPwd().length > 128) sb.append(" ...");
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    if (host == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'host' was not present! Struct: " + toString());
    }
  }

}

