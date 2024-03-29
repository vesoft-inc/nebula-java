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
public class AlterUserReq implements TBase, java.io.Serializable, Cloneable, Comparable<AlterUserReq> {
  private static final TStruct STRUCT_DESC = new TStruct("AlterUserReq");
  private static final TField ACCOUNT_FIELD_DESC = new TField("account", TType.STRING, (short)1);
  private static final TField ENCODED_PWD_FIELD_DESC = new TField("encoded_pwd", TType.STRING, (short)2);
  private static final TField IP_WHITELIST_FIELD_DESC = new TField("ip_whitelist", TType.SET, (short)3);

  public byte[] account;
  public byte[] encoded_pwd;
  public Set<byte[]> ip_whitelist;
  public static final int ACCOUNT = 1;
  public static final int ENCODED_PWD = 2;
  public static final int IP_WHITELIST = 3;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ACCOUNT, new FieldMetaData("account", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(ENCODED_PWD, new FieldMetaData("encoded_pwd", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(IP_WHITELIST, new FieldMetaData("ip_whitelist", TFieldRequirementType.OPTIONAL, 
        new SetMetaData(TType.SET, 
            new FieldValueMetaData(TType.STRING))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(AlterUserReq.class, metaDataMap);
  }

  public AlterUserReq() {
  }

  public AlterUserReq(
      byte[] account) {
    this();
    this.account = account;
  }

  public AlterUserReq(
      byte[] account,
      byte[] encoded_pwd,
      Set<byte[]> ip_whitelist) {
    this();
    this.account = account;
    this.encoded_pwd = encoded_pwd;
    this.ip_whitelist = ip_whitelist;
  }

  public static class Builder {
    private byte[] account;
    private byte[] encoded_pwd;
    private Set<byte[]> ip_whitelist;

    public Builder() {
    }

    public Builder setAccount(final byte[] account) {
      this.account = account;
      return this;
    }

    public Builder setEncoded_pwd(final byte[] encoded_pwd) {
      this.encoded_pwd = encoded_pwd;
      return this;
    }

    public Builder setIp_whitelist(final Set<byte[]> ip_whitelist) {
      this.ip_whitelist = ip_whitelist;
      return this;
    }

    public AlterUserReq build() {
      AlterUserReq result = new AlterUserReq();
      result.setAccount(this.account);
      result.setEncoded_pwd(this.encoded_pwd);
      result.setIp_whitelist(this.ip_whitelist);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AlterUserReq(AlterUserReq other) {
    if (other.isSetAccount()) {
      this.account = TBaseHelper.deepCopy(other.account);
    }
    if (other.isSetEncoded_pwd()) {
      this.encoded_pwd = TBaseHelper.deepCopy(other.encoded_pwd);
    }
    if (other.isSetIp_whitelist()) {
      this.ip_whitelist = TBaseHelper.deepCopy(other.ip_whitelist);
    }
  }

  public AlterUserReq deepCopy() {
    return new AlterUserReq(this);
  }

  public byte[] getAccount() {
    return this.account;
  }

  public AlterUserReq setAccount(byte[] account) {
    this.account = account;
    return this;
  }

  public void unsetAccount() {
    this.account = null;
  }

  // Returns true if field account is set (has been assigned a value) and false otherwise
  public boolean isSetAccount() {
    return this.account != null;
  }

  public void setAccountIsSet(boolean __value) {
    if (!__value) {
      this.account = null;
    }
  }

  public byte[] getEncoded_pwd() {
    return this.encoded_pwd;
  }

  public AlterUserReq setEncoded_pwd(byte[] encoded_pwd) {
    this.encoded_pwd = encoded_pwd;
    return this;
  }

  public void unsetEncoded_pwd() {
    this.encoded_pwd = null;
  }

  // Returns true if field encoded_pwd is set (has been assigned a value) and false otherwise
  public boolean isSetEncoded_pwd() {
    return this.encoded_pwd != null;
  }

  public void setEncoded_pwdIsSet(boolean __value) {
    if (!__value) {
      this.encoded_pwd = null;
    }
  }

  public Set<byte[]> getIp_whitelist() {
    return this.ip_whitelist;
  }

  public AlterUserReq setIp_whitelist(Set<byte[]> ip_whitelist) {
    this.ip_whitelist = ip_whitelist;
    return this;
  }

  public void unsetIp_whitelist() {
    this.ip_whitelist = null;
  }

  // Returns true if field ip_whitelist is set (has been assigned a value) and false otherwise
  public boolean isSetIp_whitelist() {
    return this.ip_whitelist != null;
  }

  public void setIp_whitelistIsSet(boolean __value) {
    if (!__value) {
      this.ip_whitelist = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case ACCOUNT:
      if (__value == null) {
        unsetAccount();
      } else {
        setAccount((byte[])__value);
      }
      break;

    case ENCODED_PWD:
      if (__value == null) {
        unsetEncoded_pwd();
      } else {
        setEncoded_pwd((byte[])__value);
      }
      break;

    case IP_WHITELIST:
      if (__value == null) {
        unsetIp_whitelist();
      } else {
        setIp_whitelist((Set<byte[]>)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case ACCOUNT:
      return getAccount();

    case ENCODED_PWD:
      return getEncoded_pwd();

    case IP_WHITELIST:
      return getIp_whitelist();

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
    if (!(_that instanceof AlterUserReq))
      return false;
    AlterUserReq that = (AlterUserReq)_that;

    if (!TBaseHelper.equalsSlow(this.isSetAccount(), that.isSetAccount(), this.account, that.account)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetEncoded_pwd(), that.isSetEncoded_pwd(), this.encoded_pwd, that.encoded_pwd)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetIp_whitelist(), that.isSetIp_whitelist(), this.ip_whitelist, that.ip_whitelist)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {account, encoded_pwd, ip_whitelist});
  }

  @Override
  public int compareTo(AlterUserReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetAccount()).compareTo(other.isSetAccount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(account, other.account);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetEncoded_pwd()).compareTo(other.isSetEncoded_pwd());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(encoded_pwd, other.encoded_pwd);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetIp_whitelist()).compareTo(other.isSetIp_whitelist());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(ip_whitelist, other.ip_whitelist);
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
        case ACCOUNT:
          if (__field.type == TType.STRING) {
            this.account = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case ENCODED_PWD:
          if (__field.type == TType.STRING) {
            this.encoded_pwd = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case IP_WHITELIST:
          if (__field.type == TType.SET) {
            {
              TSet _set187 = iprot.readSetBegin();
              this.ip_whitelist = new HashSet<byte[]>(Math.max(0, 2*_set187.size));
              for (int _i188 = 0; 
                   (_set187.size < 0) ? iprot.peekSet() : (_i188 < _set187.size); 
                   ++_i188)
              {
                byte[] _elem189;
                _elem189 = iprot.readBinary();
                this.ip_whitelist.add(_elem189);
              }
              iprot.readSetEnd();
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
    if (this.account != null) {
      oprot.writeFieldBegin(ACCOUNT_FIELD_DESC);
      oprot.writeBinary(this.account);
      oprot.writeFieldEnd();
    }
    if (this.encoded_pwd != null) {
      if (isSetEncoded_pwd()) {
        oprot.writeFieldBegin(ENCODED_PWD_FIELD_DESC);
        oprot.writeBinary(this.encoded_pwd);
        oprot.writeFieldEnd();
      }
    }
    if (this.ip_whitelist != null) {
      if (isSetIp_whitelist()) {
        oprot.writeFieldBegin(IP_WHITELIST_FIELD_DESC);
        {
          oprot.writeSetBegin(new TSet(TType.STRING, this.ip_whitelist.size()));
          for (byte[] _iter190 : this.ip_whitelist)          {
            oprot.writeBinary(_iter190);
          }
          oprot.writeSetEnd();
        }
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
    StringBuilder sb = new StringBuilder("AlterUserReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("account");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getAccount() == null) {
      sb.append("null");
    } else {
        int __account_size = Math.min(this.getAccount().length, 128);
        for (int i = 0; i < __account_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getAccount()[i]).length() > 1 ? Integer.toHexString(this.getAccount()[i]).substring(Integer.toHexString(this.getAccount()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getAccount()[i]).toUpperCase());
        }
        if (this.getAccount().length > 128) sb.append(" ...");
    }
    first = false;
    if (isSetEncoded_pwd())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("encoded_pwd");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getEncoded_pwd() == null) {
        sb.append("null");
      } else {
          int __encoded_pwd_size = Math.min(this.getEncoded_pwd().length, 128);
          for (int i = 0; i < __encoded_pwd_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getEncoded_pwd()[i]).length() > 1 ? Integer.toHexString(this.getEncoded_pwd()[i]).substring(Integer.toHexString(this.getEncoded_pwd()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getEncoded_pwd()[i]).toUpperCase());
          }
          if (this.getEncoded_pwd().length > 128) sb.append(" ...");
      }
      first = false;
    }
    if (isSetIp_whitelist())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("ip_whitelist");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getIp_whitelist() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getIp_whitelist(), indent + 1, prettyPrint));
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

