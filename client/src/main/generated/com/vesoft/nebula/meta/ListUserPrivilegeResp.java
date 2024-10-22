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
public class ListUserPrivilegeResp implements TBase, java.io.Serializable, Cloneable, Comparable<ListUserPrivilegeResp> {
  private static final TStruct STRUCT_DESC = new TStruct("ListUserPrivilegeResp");
  private static final TField CODE_FIELD_DESC = new TField("code", TType.I32, (short)1);
  private static final TField LEADER_FIELD_DESC = new TField("leader", TType.STRUCT, (short)2);
  private static final TField PRIVILEGES_FIELD_DESC = new TField("privileges", TType.MAP, (short)3);

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public com.vesoft.nebula.ErrorCode code;
  public com.vesoft.nebula.HostAddr leader;
  public Map<byte[],List<Privilege>> privileges;
  public static final int CODE = 1;
  public static final int LEADER = 2;
  public static final int PRIVILEGES = 3;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(CODE, new FieldMetaData("code", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(LEADER, new FieldMetaData("leader", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(PRIVILEGES, new FieldMetaData("privileges", TFieldRequirementType.DEFAULT, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.STRING), 
            new ListMetaData(TType.LIST, 
                new StructMetaData(TType.STRUCT, Privilege.class)))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ListUserPrivilegeResp.class, metaDataMap);
  }

  public ListUserPrivilegeResp() {
  }

  public ListUserPrivilegeResp(
      com.vesoft.nebula.ErrorCode code,
      com.vesoft.nebula.HostAddr leader,
      Map<byte[],List<Privilege>> privileges) {
    this();
    this.code = code;
    this.leader = leader;
    this.privileges = privileges;
  }

  public static class Builder {
    private com.vesoft.nebula.ErrorCode code;
    private com.vesoft.nebula.HostAddr leader;
    private Map<byte[],List<Privilege>> privileges;

    public Builder() {
    }

    public Builder setCode(final com.vesoft.nebula.ErrorCode code) {
      this.code = code;
      return this;
    }

    public Builder setLeader(final com.vesoft.nebula.HostAddr leader) {
      this.leader = leader;
      return this;
    }

    public Builder setPrivileges(final Map<byte[],List<Privilege>> privileges) {
      this.privileges = privileges;
      return this;
    }

    public ListUserPrivilegeResp build() {
      ListUserPrivilegeResp result = new ListUserPrivilegeResp();
      result.setCode(this.code);
      result.setLeader(this.leader);
      result.setPrivileges(this.privileges);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ListUserPrivilegeResp(ListUserPrivilegeResp other) {
    if (other.isSetCode()) {
      this.code = TBaseHelper.deepCopy(other.code);
    }
    if (other.isSetLeader()) {
      this.leader = TBaseHelper.deepCopy(other.leader);
    }
    if (other.isSetPrivileges()) {
      this.privileges = TBaseHelper.deepCopy(other.privileges);
    }
  }

  public ListUserPrivilegeResp deepCopy() {
    return new ListUserPrivilegeResp(this);
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
  public ListUserPrivilegeResp setCode(com.vesoft.nebula.ErrorCode code) {
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

  public com.vesoft.nebula.HostAddr getLeader() {
    return this.leader;
  }

  public ListUserPrivilegeResp setLeader(com.vesoft.nebula.HostAddr leader) {
    this.leader = leader;
    return this;
  }

  public void unsetLeader() {
    this.leader = null;
  }

  // Returns true if field leader is set (has been assigned a value) and false otherwise
  public boolean isSetLeader() {
    return this.leader != null;
  }

  public void setLeaderIsSet(boolean __value) {
    if (!__value) {
      this.leader = null;
    }
  }

  public Map<byte[],List<Privilege>> getPrivileges() {
    return this.privileges;
  }

  public ListUserPrivilegeResp setPrivileges(Map<byte[],List<Privilege>> privileges) {
    this.privileges = privileges;
    return this;
  }

  public void unsetPrivileges() {
    this.privileges = null;
  }

  // Returns true if field privileges is set (has been assigned a value) and false otherwise
  public boolean isSetPrivileges() {
    return this.privileges != null;
  }

  public void setPrivilegesIsSet(boolean __value) {
    if (!__value) {
      this.privileges = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case CODE:
      if (__value == null) {
        unsetCode();
      } else {
        setCode((com.vesoft.nebula.ErrorCode)__value);
      }
      break;

    case LEADER:
      if (__value == null) {
        unsetLeader();
      } else {
        setLeader((com.vesoft.nebula.HostAddr)__value);
      }
      break;

    case PRIVILEGES:
      if (__value == null) {
        unsetPrivileges();
      } else {
        setPrivileges((Map<byte[],List<Privilege>>)__value);
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

    case LEADER:
      return getLeader();

    case PRIVILEGES:
      return getPrivileges();

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
    if (!(_that instanceof ListUserPrivilegeResp))
      return false;
    ListUserPrivilegeResp that = (ListUserPrivilegeResp)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetCode(), that.isSetCode(), this.code, that.code)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetLeader(), that.isSetLeader(), this.leader, that.leader)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetPrivileges(), that.isSetPrivileges(), this.privileges, that.privileges)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {code, leader, privileges});
  }

  @Override
  public int compareTo(ListUserPrivilegeResp other) {
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
    lastComparison = Boolean.valueOf(isSetLeader()).compareTo(other.isSetLeader());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(leader, other.leader);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPrivileges()).compareTo(other.isSetPrivileges());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(privileges, other.privileges);
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
        case LEADER:
          if (__field.type == TType.STRUCT) {
            this.leader = new com.vesoft.nebula.HostAddr();
            this.leader.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case PRIVILEGES:
          if (__field.type == TType.MAP) {
            {
              TMap _map449 = iprot.readMapBegin();
              this.privileges = new HashMap<byte[],List<Privilege>>(Math.max(0, 2*_map449.size));
              for (int _i450 = 0; 
                   (_map449.size < 0) ? iprot.peekMap() : (_i450 < _map449.size); 
                   ++_i450)
              {
                byte[] _key451;
                List<Privilege> _val452;
                _key451 = iprot.readBinary();
                {
                  TList _list453 = iprot.readListBegin();
                  _val452 = new ArrayList<Privilege>(Math.max(0, _list453.size));
                  for (int _i454 = 0; 
                       (_list453.size < 0) ? iprot.peekList() : (_i454 < _list453.size); 
                       ++_i454)
                  {
                    Privilege _elem455;
                    _elem455 = new Privilege();
                    _elem455.read(iprot);
                    _val452.add(_elem455);
                  }
                  iprot.readListEnd();
                }
                this.privileges.put(_key451, _val452);
              }
              iprot.readMapEnd();
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
    if (this.code != null) {
      oprot.writeFieldBegin(CODE_FIELD_DESC);
      oprot.writeI32(this.code == null ? 0 : this.code.getValue());
      oprot.writeFieldEnd();
    }
    if (this.leader != null) {
      oprot.writeFieldBegin(LEADER_FIELD_DESC);
      this.leader.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.privileges != null) {
      oprot.writeFieldBegin(PRIVILEGES_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.STRING, TType.LIST, this.privileges.size()));
        for (Map.Entry<byte[], List<Privilege>> _iter456 : this.privileges.entrySet())        {
          oprot.writeBinary(_iter456.getKey());
          {
            oprot.writeListBegin(new TList(TType.STRUCT, _iter456.getValue().size()));
            for (Privilege _iter457 : _iter456.getValue())            {
              _iter457.write(oprot);
            }
            oprot.writeListEnd();
          }
        }
        oprot.writeMapEnd();
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
    StringBuilder sb = new StringBuilder("ListUserPrivilegeResp");
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
    sb.append("leader");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getLeader() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getLeader(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("privileges");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getPrivileges() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getPrivileges(), indent + 1, prettyPrint));
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
