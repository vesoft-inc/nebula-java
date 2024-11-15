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
public class RequestCommon implements TBase, java.io.Serializable, Cloneable, Comparable<RequestCommon> {
  private static final TStruct STRUCT_DESC = new TStruct("RequestCommon");
  private static final TField SESSION_ID_FIELD_DESC = new TField("session_id", TType.I64, (short)1);
  private static final TField PLAN_ID_FIELD_DESC = new TField("plan_id", TType.I64, (short)2);
  private static final TField PROFILE_DETAIL_FIELD_DESC = new TField("profile_detail", TType.BOOL, (short)3);
  private static final TField TIMEOUT_FIELD_DESC = new TField("timeout", TType.I64, (short)4);

  public long session_id;
  public long plan_id;
  public boolean profile_detail;
  public long timeout;
  public static final int SESSION_ID = 1;
  public static final int PLAN_ID = 2;
  public static final int PROFILE_DETAIL = 3;
  public static final int TIMEOUT = 4;

  // isset id assignments
  private static final int __SESSION_ID_ISSET_ID = 0;
  private static final int __PLAN_ID_ISSET_ID = 1;
  private static final int __PROFILE_DETAIL_ISSET_ID = 2;
  private static final int __TIMEOUT_ISSET_ID = 3;
  private BitSet __isset_bit_vector = new BitSet(4);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SESSION_ID, new FieldMetaData("session_id", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(PLAN_ID, new FieldMetaData("plan_id", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(PROFILE_DETAIL, new FieldMetaData("profile_detail", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(TIMEOUT, new FieldMetaData("timeout", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(RequestCommon.class, metaDataMap);
  }

  public RequestCommon() {
  }

  public RequestCommon(
      long session_id,
      long plan_id,
      boolean profile_detail,
      long timeout) {
    this();
    this.session_id = session_id;
    setSession_idIsSet(true);
    this.plan_id = plan_id;
    setPlan_idIsSet(true);
    this.profile_detail = profile_detail;
    setProfile_detailIsSet(true);
    this.timeout = timeout;
    setTimeoutIsSet(true);
  }

  public static class Builder {
    private long session_id;
    private long plan_id;
    private boolean profile_detail;
    private long timeout;

    BitSet __optional_isset = new BitSet(4);

    public Builder() {
    }

    public Builder setSession_id(final long session_id) {
      this.session_id = session_id;
      __optional_isset.set(__SESSION_ID_ISSET_ID, true);
      return this;
    }

    public Builder setPlan_id(final long plan_id) {
      this.plan_id = plan_id;
      __optional_isset.set(__PLAN_ID_ISSET_ID, true);
      return this;
    }

    public Builder setProfile_detail(final boolean profile_detail) {
      this.profile_detail = profile_detail;
      __optional_isset.set(__PROFILE_DETAIL_ISSET_ID, true);
      return this;
    }

    public Builder setTimeout(final long timeout) {
      this.timeout = timeout;
      __optional_isset.set(__TIMEOUT_ISSET_ID, true);
      return this;
    }

    public RequestCommon build() {
      RequestCommon result = new RequestCommon();
      if (__optional_isset.get(__SESSION_ID_ISSET_ID)) {
        result.setSession_id(this.session_id);
      }
      if (__optional_isset.get(__PLAN_ID_ISSET_ID)) {
        result.setPlan_id(this.plan_id);
      }
      if (__optional_isset.get(__PROFILE_DETAIL_ISSET_ID)) {
        result.setProfile_detail(this.profile_detail);
      }
      if (__optional_isset.get(__TIMEOUT_ISSET_ID)) {
        result.setTimeout(this.timeout);
      }
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RequestCommon(RequestCommon other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.session_id = TBaseHelper.deepCopy(other.session_id);
    this.plan_id = TBaseHelper.deepCopy(other.plan_id);
    this.profile_detail = TBaseHelper.deepCopy(other.profile_detail);
    this.timeout = TBaseHelper.deepCopy(other.timeout);
  }

  public RequestCommon deepCopy() {
    return new RequestCommon(this);
  }

  public long getSession_id() {
    return this.session_id;
  }

  public RequestCommon setSession_id(long session_id) {
    this.session_id = session_id;
    setSession_idIsSet(true);
    return this;
  }

  public void unsetSession_id() {
    __isset_bit_vector.clear(__SESSION_ID_ISSET_ID);
  }

  // Returns true if field session_id is set (has been assigned a value) and false otherwise
  public boolean isSetSession_id() {
    return __isset_bit_vector.get(__SESSION_ID_ISSET_ID);
  }

  public void setSession_idIsSet(boolean __value) {
    __isset_bit_vector.set(__SESSION_ID_ISSET_ID, __value);
  }

  public long getPlan_id() {
    return this.plan_id;
  }

  public RequestCommon setPlan_id(long plan_id) {
    this.plan_id = plan_id;
    setPlan_idIsSet(true);
    return this;
  }

  public void unsetPlan_id() {
    __isset_bit_vector.clear(__PLAN_ID_ISSET_ID);
  }

  // Returns true if field plan_id is set (has been assigned a value) and false otherwise
  public boolean isSetPlan_id() {
    return __isset_bit_vector.get(__PLAN_ID_ISSET_ID);
  }

  public void setPlan_idIsSet(boolean __value) {
    __isset_bit_vector.set(__PLAN_ID_ISSET_ID, __value);
  }

  public boolean isProfile_detail() {
    return this.profile_detail;
  }

  public RequestCommon setProfile_detail(boolean profile_detail) {
    this.profile_detail = profile_detail;
    setProfile_detailIsSet(true);
    return this;
  }

  public void unsetProfile_detail() {
    __isset_bit_vector.clear(__PROFILE_DETAIL_ISSET_ID);
  }

  // Returns true if field profile_detail is set (has been assigned a value) and false otherwise
  public boolean isSetProfile_detail() {
    return __isset_bit_vector.get(__PROFILE_DETAIL_ISSET_ID);
  }

  public void setProfile_detailIsSet(boolean __value) {
    __isset_bit_vector.set(__PROFILE_DETAIL_ISSET_ID, __value);
  }

  public long getTimeout() {
    return this.timeout;
  }

  public RequestCommon setTimeout(long timeout) {
    this.timeout = timeout;
    setTimeoutIsSet(true);
    return this;
  }

  public void unsetTimeout() {
    __isset_bit_vector.clear(__TIMEOUT_ISSET_ID);
  }

  // Returns true if field timeout is set (has been assigned a value) and false otherwise
  public boolean isSetTimeout() {
    return __isset_bit_vector.get(__TIMEOUT_ISSET_ID);
  }

  public void setTimeoutIsSet(boolean __value) {
    __isset_bit_vector.set(__TIMEOUT_ISSET_ID, __value);
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SESSION_ID:
      if (__value == null) {
        unsetSession_id();
      } else {
        setSession_id((Long)__value);
      }
      break;

    case PLAN_ID:
      if (__value == null) {
        unsetPlan_id();
      } else {
        setPlan_id((Long)__value);
      }
      break;

    case PROFILE_DETAIL:
      if (__value == null) {
        unsetProfile_detail();
      } else {
        setProfile_detail((Boolean)__value);
      }
      break;

    case TIMEOUT:
      if (__value == null) {
        unsetTimeout();
      } else {
        setTimeout((Long)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SESSION_ID:
      return new Long(getSession_id());

    case PLAN_ID:
      return new Long(getPlan_id());

    case PROFILE_DETAIL:
      return new Boolean(isProfile_detail());

    case TIMEOUT:
      return new Long(getTimeout());

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
    if (!(_that instanceof RequestCommon))
      return false;
    RequestCommon that = (RequestCommon)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetSession_id(), that.isSetSession_id(), this.session_id, that.session_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetPlan_id(), that.isSetPlan_id(), this.plan_id, that.plan_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetProfile_detail(), that.isSetProfile_detail(), this.profile_detail, that.profile_detail)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetTimeout(), that.isSetTimeout(), this.timeout, that.timeout)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {session_id, plan_id, profile_detail, timeout});
  }

  @Override
  public int compareTo(RequestCommon other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSession_id()).compareTo(other.isSetSession_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(session_id, other.session_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPlan_id()).compareTo(other.isSetPlan_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(plan_id, other.plan_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetProfile_detail()).compareTo(other.isSetProfile_detail());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(profile_detail, other.profile_detail);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetTimeout()).compareTo(other.isSetTimeout());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(timeout, other.timeout);
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
        case SESSION_ID:
          if (__field.type == TType.I64) {
            this.session_id = iprot.readI64();
            setSession_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case PLAN_ID:
          if (__field.type == TType.I64) {
            this.plan_id = iprot.readI64();
            setPlan_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case PROFILE_DETAIL:
          if (__field.type == TType.BOOL) {
            this.profile_detail = iprot.readBool();
            setProfile_detailIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TIMEOUT:
          if (__field.type == TType.I64) {
            this.timeout = iprot.readI64();
            setTimeoutIsSet(true);
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
    if (isSetSession_id()) {
      oprot.writeFieldBegin(SESSION_ID_FIELD_DESC);
      oprot.writeI64(this.session_id);
      oprot.writeFieldEnd();
    }
    if (isSetPlan_id()) {
      oprot.writeFieldBegin(PLAN_ID_FIELD_DESC);
      oprot.writeI64(this.plan_id);
      oprot.writeFieldEnd();
    }
    if (isSetProfile_detail()) {
      oprot.writeFieldBegin(PROFILE_DETAIL_FIELD_DESC);
      oprot.writeBool(this.profile_detail);
      oprot.writeFieldEnd();
    }
    if (isSetTimeout()) {
      oprot.writeFieldBegin(TIMEOUT_FIELD_DESC);
      oprot.writeI64(this.timeout);
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
    StringBuilder sb = new StringBuilder("RequestCommon");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    if (isSetSession_id())
    {
      sb.append(indentStr);
      sb.append("session_id");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this.getSession_id(), indent + 1, prettyPrint));
      first = false;
    }
    if (isSetPlan_id())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("plan_id");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this.getPlan_id(), indent + 1, prettyPrint));
      first = false;
    }
    if (isSetProfile_detail())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("profile_detail");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this.isProfile_detail(), indent + 1, prettyPrint));
      first = false;
    }
    if (isSetTimeout())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("timeout");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this.getTimeout(), indent + 1, prettyPrint));
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

