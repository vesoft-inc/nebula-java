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
public class ReportTaskReq implements TBase, java.io.Serializable, Cloneable, Comparable<ReportTaskReq> {
  private static final TStruct STRUCT_DESC = new TStruct("ReportTaskReq");
  private static final TField CODE_FIELD_DESC = new TField("code", TType.I32, (short)1);
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)2);
  private static final TField JOB_ID_FIELD_DESC = new TField("job_id", TType.I32, (short)3);
  private static final TField TASK_ID_FIELD_DESC = new TField("task_id", TType.I32, (short)4);
  private static final TField STATS_FIELD_DESC = new TField("stats", TType.STRUCT, (short)5);

  /**
   * 
   * @see com.vesoft.nebula.ErrorCode
   */
  public com.vesoft.nebula.ErrorCode code;
  public int space_id;
  public int job_id;
  public int task_id;
  public StatsItem stats;
  public static final int CODE = 1;
  public static final int SPACE_ID = 2;
  public static final int JOB_ID = 3;
  public static final int TASK_ID = 4;
  public static final int STATS = 5;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __JOB_ID_ISSET_ID = 1;
  private static final int __TASK_ID_ISSET_ID = 2;
  private BitSet __isset_bit_vector = new BitSet(3);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(CODE, new FieldMetaData("code", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(JOB_ID, new FieldMetaData("job_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(TASK_ID, new FieldMetaData("task_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(STATS, new FieldMetaData("stats", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, StatsItem.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ReportTaskReq.class, metaDataMap);
  }

  public ReportTaskReq() {
  }

  public ReportTaskReq(
      com.vesoft.nebula.ErrorCode code,
      int space_id,
      int job_id,
      int task_id) {
    this();
    this.code = code;
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.job_id = job_id;
    setJob_idIsSet(true);
    this.task_id = task_id;
    setTask_idIsSet(true);
  }

  public ReportTaskReq(
      com.vesoft.nebula.ErrorCode code,
      int space_id,
      int job_id,
      int task_id,
      StatsItem stats) {
    this();
    this.code = code;
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.job_id = job_id;
    setJob_idIsSet(true);
    this.task_id = task_id;
    setTask_idIsSet(true);
    this.stats = stats;
  }

  public static class Builder {
    private com.vesoft.nebula.ErrorCode code;
    private int space_id;
    private int job_id;
    private int task_id;
    private StatsItem stats;

    BitSet __optional_isset = new BitSet(3);

    public Builder() {
    }

    public Builder setCode(final com.vesoft.nebula.ErrorCode code) {
      this.code = code;
      return this;
    }

    public Builder setSpace_id(final int space_id) {
      this.space_id = space_id;
      __optional_isset.set(__SPACE_ID_ISSET_ID, true);
      return this;
    }

    public Builder setJob_id(final int job_id) {
      this.job_id = job_id;
      __optional_isset.set(__JOB_ID_ISSET_ID, true);
      return this;
    }

    public Builder setTask_id(final int task_id) {
      this.task_id = task_id;
      __optional_isset.set(__TASK_ID_ISSET_ID, true);
      return this;
    }

    public Builder setStats(final StatsItem stats) {
      this.stats = stats;
      return this;
    }

    public ReportTaskReq build() {
      ReportTaskReq result = new ReportTaskReq();
      result.setCode(this.code);
      if (__optional_isset.get(__SPACE_ID_ISSET_ID)) {
        result.setSpace_id(this.space_id);
      }
      if (__optional_isset.get(__JOB_ID_ISSET_ID)) {
        result.setJob_id(this.job_id);
      }
      if (__optional_isset.get(__TASK_ID_ISSET_ID)) {
        result.setTask_id(this.task_id);
      }
      result.setStats(this.stats);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ReportTaskReq(ReportTaskReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetCode()) {
      this.code = TBaseHelper.deepCopy(other.code);
    }
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    this.job_id = TBaseHelper.deepCopy(other.job_id);
    this.task_id = TBaseHelper.deepCopy(other.task_id);
    if (other.isSetStats()) {
      this.stats = TBaseHelper.deepCopy(other.stats);
    }
  }

  public ReportTaskReq deepCopy() {
    return new ReportTaskReq(this);
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
  public ReportTaskReq setCode(com.vesoft.nebula.ErrorCode code) {
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

  public int getSpace_id() {
    return this.space_id;
  }

  public ReportTaskReq setSpace_id(int space_id) {
    this.space_id = space_id;
    setSpace_idIsSet(true);
    return this;
  }

  public void unsetSpace_id() {
    __isset_bit_vector.clear(__SPACE_ID_ISSET_ID);
  }

  // Returns true if field space_id is set (has been assigned a value) and false otherwise
  public boolean isSetSpace_id() {
    return __isset_bit_vector.get(__SPACE_ID_ISSET_ID);
  }

  public void setSpace_idIsSet(boolean __value) {
    __isset_bit_vector.set(__SPACE_ID_ISSET_ID, __value);
  }

  public int getJob_id() {
    return this.job_id;
  }

  public ReportTaskReq setJob_id(int job_id) {
    this.job_id = job_id;
    setJob_idIsSet(true);
    return this;
  }

  public void unsetJob_id() {
    __isset_bit_vector.clear(__JOB_ID_ISSET_ID);
  }

  // Returns true if field job_id is set (has been assigned a value) and false otherwise
  public boolean isSetJob_id() {
    return __isset_bit_vector.get(__JOB_ID_ISSET_ID);
  }

  public void setJob_idIsSet(boolean __value) {
    __isset_bit_vector.set(__JOB_ID_ISSET_ID, __value);
  }

  public int getTask_id() {
    return this.task_id;
  }

  public ReportTaskReq setTask_id(int task_id) {
    this.task_id = task_id;
    setTask_idIsSet(true);
    return this;
  }

  public void unsetTask_id() {
    __isset_bit_vector.clear(__TASK_ID_ISSET_ID);
  }

  // Returns true if field task_id is set (has been assigned a value) and false otherwise
  public boolean isSetTask_id() {
    return __isset_bit_vector.get(__TASK_ID_ISSET_ID);
  }

  public void setTask_idIsSet(boolean __value) {
    __isset_bit_vector.set(__TASK_ID_ISSET_ID, __value);
  }

  public StatsItem getStats() {
    return this.stats;
  }

  public ReportTaskReq setStats(StatsItem stats) {
    this.stats = stats;
    return this;
  }

  public void unsetStats() {
    this.stats = null;
  }

  // Returns true if field stats is set (has been assigned a value) and false otherwise
  public boolean isSetStats() {
    return this.stats != null;
  }

  public void setStatsIsSet(boolean __value) {
    if (!__value) {
      this.stats = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case CODE:
      if (__value == null) {
        unsetCode();
      } else {
        setCode((com.vesoft.nebula.ErrorCode)__value);
      }
      break;

    case SPACE_ID:
      if (__value == null) {
        unsetSpace_id();
      } else {
        setSpace_id((Integer)__value);
      }
      break;

    case JOB_ID:
      if (__value == null) {
        unsetJob_id();
      } else {
        setJob_id((Integer)__value);
      }
      break;

    case TASK_ID:
      if (__value == null) {
        unsetTask_id();
      } else {
        setTask_id((Integer)__value);
      }
      break;

    case STATS:
      if (__value == null) {
        unsetStats();
      } else {
        setStats((StatsItem)__value);
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

    case SPACE_ID:
      return new Integer(getSpace_id());

    case JOB_ID:
      return new Integer(getJob_id());

    case TASK_ID:
      return new Integer(getTask_id());

    case STATS:
      return getStats();

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
    if (!(_that instanceof ReportTaskReq))
      return false;
    ReportTaskReq that = (ReportTaskReq)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetCode(), that.isSetCode(), this.code, that.code)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.space_id, that.space_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.job_id, that.job_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.task_id, that.task_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetStats(), that.isSetStats(), this.stats, that.stats)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {code, space_id, job_id, task_id, stats});
  }

  @Override
  public int compareTo(ReportTaskReq other) {
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
    lastComparison = Boolean.valueOf(isSetSpace_id()).compareTo(other.isSetSpace_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space_id, other.space_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetJob_id()).compareTo(other.isSetJob_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(job_id, other.job_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetTask_id()).compareTo(other.isSetTask_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(task_id, other.task_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetStats()).compareTo(other.isSetStats());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(stats, other.stats);
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
        case SPACE_ID:
          if (__field.type == TType.I32) {
            this.space_id = iprot.readI32();
            setSpace_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case JOB_ID:
          if (__field.type == TType.I32) {
            this.job_id = iprot.readI32();
            setJob_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TASK_ID:
          if (__field.type == TType.I32) {
            this.task_id = iprot.readI32();
            setTask_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case STATS:
          if (__field.type == TType.STRUCT) {
            this.stats = new StatsItem();
            this.stats.read(iprot);
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
    oprot.writeFieldBegin(SPACE_ID_FIELD_DESC);
    oprot.writeI32(this.space_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(JOB_ID_FIELD_DESC);
    oprot.writeI32(this.job_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(TASK_ID_FIELD_DESC);
    oprot.writeI32(this.task_id);
    oprot.writeFieldEnd();
    if (this.stats != null) {
      if (isSetStats()) {
        oprot.writeFieldBegin(STATS_FIELD_DESC);
        this.stats.write(oprot);
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
    StringBuilder sb = new StringBuilder("ReportTaskReq");
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
    sb.append("space_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getSpace_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("job_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getJob_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("task_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getTask_id(), indent + 1, prettyPrint));
    first = false;
    if (isSetStats())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("stats");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getStats() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getStats(), indent + 1, prettyPrint));
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

