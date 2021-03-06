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
public class PlanNodeBranchInfo implements TBase, java.io.Serializable, Cloneable, Comparable<PlanNodeBranchInfo> {
  private static final TStruct STRUCT_DESC = new TStruct("PlanNodeBranchInfo");
  private static final TField IS_DO_BRANCH_FIELD_DESC = new TField("is_do_branch", TType.BOOL, (short)1);
  private static final TField CONDITION_NODE_ID_FIELD_DESC = new TField("condition_node_id", TType.I64, (short)2);

  public boolean is_do_branch;
  public long condition_node_id;
  public static final int IS_DO_BRANCH = 1;
  public static final int CONDITION_NODE_ID = 2;

  // isset id assignments
  private static final int __IS_DO_BRANCH_ISSET_ID = 0;
  private static final int __CONDITION_NODE_ID_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(IS_DO_BRANCH, new FieldMetaData("is_do_branch", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(CONDITION_NODE_ID, new FieldMetaData("condition_node_id", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(PlanNodeBranchInfo.class, metaDataMap);
  }

  public PlanNodeBranchInfo() {
  }

  public PlanNodeBranchInfo(
      boolean is_do_branch,
      long condition_node_id) {
    this();
    this.is_do_branch = is_do_branch;
    setIs_do_branchIsSet(true);
    this.condition_node_id = condition_node_id;
    setCondition_node_idIsSet(true);
  }

  public static class Builder {
    private boolean is_do_branch;
    private long condition_node_id;

    BitSet __optional_isset = new BitSet(2);

    public Builder() {
    }

    public Builder setIs_do_branch(final boolean is_do_branch) {
      this.is_do_branch = is_do_branch;
      __optional_isset.set(__IS_DO_BRANCH_ISSET_ID, true);
      return this;
    }

    public Builder setCondition_node_id(final long condition_node_id) {
      this.condition_node_id = condition_node_id;
      __optional_isset.set(__CONDITION_NODE_ID_ISSET_ID, true);
      return this;
    }

    public PlanNodeBranchInfo build() {
      PlanNodeBranchInfo result = new PlanNodeBranchInfo();
      if (__optional_isset.get(__IS_DO_BRANCH_ISSET_ID)) {
        result.setIs_do_branch(this.is_do_branch);
      }
      if (__optional_isset.get(__CONDITION_NODE_ID_ISSET_ID)) {
        result.setCondition_node_id(this.condition_node_id);
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
  public PlanNodeBranchInfo(PlanNodeBranchInfo other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.is_do_branch = TBaseHelper.deepCopy(other.is_do_branch);
    this.condition_node_id = TBaseHelper.deepCopy(other.condition_node_id);
  }

  public PlanNodeBranchInfo deepCopy() {
    return new PlanNodeBranchInfo(this);
  }

  public boolean isIs_do_branch() {
    return this.is_do_branch;
  }

  public PlanNodeBranchInfo setIs_do_branch(boolean is_do_branch) {
    this.is_do_branch = is_do_branch;
    setIs_do_branchIsSet(true);
    return this;
  }

  public void unsetIs_do_branch() {
    __isset_bit_vector.clear(__IS_DO_BRANCH_ISSET_ID);
  }

  // Returns true if field is_do_branch is set (has been assigned a value) and false otherwise
  public boolean isSetIs_do_branch() {
    return __isset_bit_vector.get(__IS_DO_BRANCH_ISSET_ID);
  }

  public void setIs_do_branchIsSet(boolean __value) {
    __isset_bit_vector.set(__IS_DO_BRANCH_ISSET_ID, __value);
  }

  public long getCondition_node_id() {
    return this.condition_node_id;
  }

  public PlanNodeBranchInfo setCondition_node_id(long condition_node_id) {
    this.condition_node_id = condition_node_id;
    setCondition_node_idIsSet(true);
    return this;
  }

  public void unsetCondition_node_id() {
    __isset_bit_vector.clear(__CONDITION_NODE_ID_ISSET_ID);
  }

  // Returns true if field condition_node_id is set (has been assigned a value) and false otherwise
  public boolean isSetCondition_node_id() {
    return __isset_bit_vector.get(__CONDITION_NODE_ID_ISSET_ID);
  }

  public void setCondition_node_idIsSet(boolean __value) {
    __isset_bit_vector.set(__CONDITION_NODE_ID_ISSET_ID, __value);
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case IS_DO_BRANCH:
      if (__value == null) {
        unsetIs_do_branch();
      } else {
        setIs_do_branch((Boolean)__value);
      }
      break;

    case CONDITION_NODE_ID:
      if (__value == null) {
        unsetCondition_node_id();
      } else {
        setCondition_node_id((Long)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case IS_DO_BRANCH:
      return new Boolean(isIs_do_branch());

    case CONDITION_NODE_ID:
      return new Long(getCondition_node_id());

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
    if (!(_that instanceof PlanNodeBranchInfo))
      return false;
    PlanNodeBranchInfo that = (PlanNodeBranchInfo)_that;

    if (!TBaseHelper.equalsNobinary(this.is_do_branch, that.is_do_branch)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.condition_node_id, that.condition_node_id)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {is_do_branch, condition_node_id});
  }

  @Override
  public int compareTo(PlanNodeBranchInfo other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetIs_do_branch()).compareTo(other.isSetIs_do_branch());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(is_do_branch, other.is_do_branch);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCondition_node_id()).compareTo(other.isSetCondition_node_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(condition_node_id, other.condition_node_id);
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
        case IS_DO_BRANCH:
          if (__field.type == TType.BOOL) {
            this.is_do_branch = iprot.readBool();
            setIs_do_branchIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case CONDITION_NODE_ID:
          if (__field.type == TType.I64) {
            this.condition_node_id = iprot.readI64();
            setCondition_node_idIsSet(true);
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
    if (!isSetIs_do_branch()) {
      throw new TProtocolException("Required field 'is_do_branch' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetCondition_node_id()) {
      throw new TProtocolException("Required field 'condition_node_id' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(IS_DO_BRANCH_FIELD_DESC);
    oprot.writeBool(this.is_do_branch);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(CONDITION_NODE_ID_FIELD_DESC);
    oprot.writeI64(this.condition_node_id);
    oprot.writeFieldEnd();
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
    StringBuilder sb = new StringBuilder("PlanNodeBranchInfo");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("is_do_branch");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isIs_do_branch(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("condition_node_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCondition_node_id(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // alas, we cannot check 'is_do_branch' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'condition_node_id' because it's a primitive and you chose the non-beans generator.
  }

}

