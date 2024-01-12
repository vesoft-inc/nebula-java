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
public class ListVariablesResp implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("ListVariablesResp");
  private static final TField CODE_FIELD_DESC = new TField("code", TType.I32, (short)1);
  private static final TField LEADER_FIELD_DESC = new TField("leader", TType.STRUCT, (short)2);
  private static final TField VARIABLES_FIELD_DESC = new TField("variables", TType.MAP, (short)3);

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public com.vesoft.nebula.ErrorCode code;
  public com.vesoft.nebula.HostAddr leader;
  public Map<byte[],com.vesoft.nebula.Value> variables;
  public static final int CODE = 1;
  public static final int LEADER = 2;
  public static final int VARIABLES = 3;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(CODE, new FieldMetaData("code", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(LEADER, new FieldMetaData("leader", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(VARIABLES, new FieldMetaData("variables", TFieldRequirementType.DEFAULT, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.STRING), 
            new StructMetaData(TType.STRUCT, com.vesoft.nebula.Value.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ListVariablesResp.class, metaDataMap);
  }

  public ListVariablesResp() {
  }

  public ListVariablesResp(
      com.vesoft.nebula.ErrorCode code,
      com.vesoft.nebula.HostAddr leader,
      Map<byte[],com.vesoft.nebula.Value> variables) {
    this();
    this.code = code;
    this.leader = leader;
    this.variables = variables;
  }

  public static class Builder {
    private com.vesoft.nebula.ErrorCode code;
    private com.vesoft.nebula.HostAddr leader;
    private Map<byte[],com.vesoft.nebula.Value> variables;

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

    public Builder setVariables(final Map<byte[],com.vesoft.nebula.Value> variables) {
      this.variables = variables;
      return this;
    }

    public ListVariablesResp build() {
      ListVariablesResp result = new ListVariablesResp();
      result.setCode(this.code);
      result.setLeader(this.leader);
      result.setVariables(this.variables);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ListVariablesResp(ListVariablesResp other) {
    if (other.isSetCode()) {
      this.code = TBaseHelper.deepCopy(other.code);
    }
    if (other.isSetLeader()) {
      this.leader = TBaseHelper.deepCopy(other.leader);
    }
    if (other.isSetVariables()) {
      this.variables = TBaseHelper.deepCopy(other.variables);
    }
  }

  public ListVariablesResp deepCopy() {
    return new ListVariablesResp(this);
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
  public ListVariablesResp setCode(com.vesoft.nebula.ErrorCode code) {
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

  public ListVariablesResp setLeader(com.vesoft.nebula.HostAddr leader) {
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

  public Map<byte[],com.vesoft.nebula.Value> getVariables() {
    return this.variables;
  }

  public ListVariablesResp setVariables(Map<byte[],com.vesoft.nebula.Value> variables) {
    this.variables = variables;
    return this;
  }

  public void unsetVariables() {
    this.variables = null;
  }

  // Returns true if field variables is set (has been assigned a value) and false otherwise
  public boolean isSetVariables() {
    return this.variables != null;
  }

  public void setVariablesIsSet(boolean __value) {
    if (!__value) {
      this.variables = null;
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

    case VARIABLES:
      if (__value == null) {
        unsetVariables();
      } else {
        setVariables((Map<byte[],com.vesoft.nebula.Value>)__value);
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

    case VARIABLES:
      return getVariables();

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
    if (!(_that instanceof ListVariablesResp))
      return false;
    ListVariablesResp that = (ListVariablesResp)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetCode(), that.isSetCode(), this.code, that.code)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetLeader(), that.isSetLeader(), this.leader, that.leader)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetVariables(), that.isSetVariables(), this.variables, that.variables)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {code, leader, variables});
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
        case VARIABLES:
          if (__field.type == TType.MAP) {
            {
              TMap _map432 = iprot.readMapBegin();
              this.variables = new HashMap<byte[],com.vesoft.nebula.Value>(Math.max(0, 2*_map432.size));
              for (int _i433 = 0; 
                   (_map432.size < 0) ? iprot.peekMap() : (_i433 < _map432.size); 
                   ++_i433)
              {
                byte[] _key434;
                com.vesoft.nebula.Value _val435;
                _key434 = iprot.readBinary();
                _val435 = new com.vesoft.nebula.Value();
                _val435.read(iprot);
                this.variables.put(_key434, _val435);
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
    if (this.variables != null) {
      oprot.writeFieldBegin(VARIABLES_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.STRING, TType.STRUCT, this.variables.size()));
        for (Map.Entry<byte[], com.vesoft.nebula.Value> _iter436 : this.variables.entrySet())        {
          oprot.writeBinary(_iter436.getKey());
          _iter436.getValue().write(oprot);
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
    StringBuilder sb = new StringBuilder("ListVariablesResp");
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
    sb.append("variables");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getVariables() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getVariables(), indent + 1, prettyPrint));
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

