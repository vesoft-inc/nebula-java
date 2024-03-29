/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula;

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
public class LogInfo implements TBase, java.io.Serializable, Cloneable, Comparable<LogInfo> {
  private static final TStruct STRUCT_DESC = new TStruct("LogInfo");
  private static final TField LOG_ID_FIELD_DESC = new TField("log_id", TType.I64, (short)1);
  private static final TField TERM_ID_FIELD_DESC = new TField("term_id", TType.I64, (short)2);
  private static final TField COMMIT_LOG_ID_FIELD_DESC = new TField("commit_log_id", TType.I64, (short)3);
  private static final TField CHECKPOINT_PATH_FIELD_DESC = new TField("checkpoint_path", TType.STRING, (short)4);

  public long log_id;
  public long term_id;
  public long commit_log_id;
  public byte[] checkpoint_path;
  public static final int LOG_ID = 1;
  public static final int TERM_ID = 2;
  public static final int COMMIT_LOG_ID = 3;
  public static final int CHECKPOINT_PATH = 4;

  // isset id assignments
  private static final int __LOG_ID_ISSET_ID = 0;
  private static final int __TERM_ID_ISSET_ID = 1;
  private static final int __COMMIT_LOG_ID_ISSET_ID = 2;
  private BitSet __isset_bit_vector = new BitSet(3);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(LOG_ID, new FieldMetaData("log_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(TERM_ID, new FieldMetaData("term_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(COMMIT_LOG_ID, new FieldMetaData("commit_log_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(CHECKPOINT_PATH, new FieldMetaData("checkpoint_path", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(LogInfo.class, metaDataMap);
  }

  public LogInfo() {
  }

  public LogInfo(
      long log_id,
      long term_id,
      long commit_log_id,
      byte[] checkpoint_path) {
    this();
    this.log_id = log_id;
    setLog_idIsSet(true);
    this.term_id = term_id;
    setTerm_idIsSet(true);
    this.commit_log_id = commit_log_id;
    setCommit_log_idIsSet(true);
    this.checkpoint_path = checkpoint_path;
  }

  public static class Builder {
    private long log_id;
    private long term_id;
    private long commit_log_id;
    private byte[] checkpoint_path;

    BitSet __optional_isset = new BitSet(3);

    public Builder() {
    }

    public Builder setLog_id(final long log_id) {
      this.log_id = log_id;
      __optional_isset.set(__LOG_ID_ISSET_ID, true);
      return this;
    }

    public Builder setTerm_id(final long term_id) {
      this.term_id = term_id;
      __optional_isset.set(__TERM_ID_ISSET_ID, true);
      return this;
    }

    public Builder setCommit_log_id(final long commit_log_id) {
      this.commit_log_id = commit_log_id;
      __optional_isset.set(__COMMIT_LOG_ID_ISSET_ID, true);
      return this;
    }

    public Builder setCheckpoint_path(final byte[] checkpoint_path) {
      this.checkpoint_path = checkpoint_path;
      return this;
    }

    public LogInfo build() {
      LogInfo result = new LogInfo();
      if (__optional_isset.get(__LOG_ID_ISSET_ID)) {
        result.setLog_id(this.log_id);
      }
      if (__optional_isset.get(__TERM_ID_ISSET_ID)) {
        result.setTerm_id(this.term_id);
      }
      if (__optional_isset.get(__COMMIT_LOG_ID_ISSET_ID)) {
        result.setCommit_log_id(this.commit_log_id);
      }
      result.setCheckpoint_path(this.checkpoint_path);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LogInfo(LogInfo other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.log_id = TBaseHelper.deepCopy(other.log_id);
    this.term_id = TBaseHelper.deepCopy(other.term_id);
    this.commit_log_id = TBaseHelper.deepCopy(other.commit_log_id);
    if (other.isSetCheckpoint_path()) {
      this.checkpoint_path = TBaseHelper.deepCopy(other.checkpoint_path);
    }
  }

  public LogInfo deepCopy() {
    return new LogInfo(this);
  }

  public long getLog_id() {
    return this.log_id;
  }

  public LogInfo setLog_id(long log_id) {
    this.log_id = log_id;
    setLog_idIsSet(true);
    return this;
  }

  public void unsetLog_id() {
    __isset_bit_vector.clear(__LOG_ID_ISSET_ID);
  }

  // Returns true if field log_id is set (has been assigned a value) and false otherwise
  public boolean isSetLog_id() {
    return __isset_bit_vector.get(__LOG_ID_ISSET_ID);
  }

  public void setLog_idIsSet(boolean __value) {
    __isset_bit_vector.set(__LOG_ID_ISSET_ID, __value);
  }

  public long getTerm_id() {
    return this.term_id;
  }

  public LogInfo setTerm_id(long term_id) {
    this.term_id = term_id;
    setTerm_idIsSet(true);
    return this;
  }

  public void unsetTerm_id() {
    __isset_bit_vector.clear(__TERM_ID_ISSET_ID);
  }

  // Returns true if field term_id is set (has been assigned a value) and false otherwise
  public boolean isSetTerm_id() {
    return __isset_bit_vector.get(__TERM_ID_ISSET_ID);
  }

  public void setTerm_idIsSet(boolean __value) {
    __isset_bit_vector.set(__TERM_ID_ISSET_ID, __value);
  }

  public long getCommit_log_id() {
    return this.commit_log_id;
  }

  public LogInfo setCommit_log_id(long commit_log_id) {
    this.commit_log_id = commit_log_id;
    setCommit_log_idIsSet(true);
    return this;
  }

  public void unsetCommit_log_id() {
    __isset_bit_vector.clear(__COMMIT_LOG_ID_ISSET_ID);
  }

  // Returns true if field commit_log_id is set (has been assigned a value) and false otherwise
  public boolean isSetCommit_log_id() {
    return __isset_bit_vector.get(__COMMIT_LOG_ID_ISSET_ID);
  }

  public void setCommit_log_idIsSet(boolean __value) {
    __isset_bit_vector.set(__COMMIT_LOG_ID_ISSET_ID, __value);
  }

  public byte[] getCheckpoint_path() {
    return this.checkpoint_path;
  }

  public LogInfo setCheckpoint_path(byte[] checkpoint_path) {
    this.checkpoint_path = checkpoint_path;
    return this;
  }

  public void unsetCheckpoint_path() {
    this.checkpoint_path = null;
  }

  // Returns true if field checkpoint_path is set (has been assigned a value) and false otherwise
  public boolean isSetCheckpoint_path() {
    return this.checkpoint_path != null;
  }

  public void setCheckpoint_pathIsSet(boolean __value) {
    if (!__value) {
      this.checkpoint_path = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case LOG_ID:
      if (__value == null) {
        unsetLog_id();
      } else {
        setLog_id((Long)__value);
      }
      break;

    case TERM_ID:
      if (__value == null) {
        unsetTerm_id();
      } else {
        setTerm_id((Long)__value);
      }
      break;

    case COMMIT_LOG_ID:
      if (__value == null) {
        unsetCommit_log_id();
      } else {
        setCommit_log_id((Long)__value);
      }
      break;

    case CHECKPOINT_PATH:
      if (__value == null) {
        unsetCheckpoint_path();
      } else {
        setCheckpoint_path((byte[])__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case LOG_ID:
      return new Long(getLog_id());

    case TERM_ID:
      return new Long(getTerm_id());

    case COMMIT_LOG_ID:
      return new Long(getCommit_log_id());

    case CHECKPOINT_PATH:
      return getCheckpoint_path();

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
    if (!(_that instanceof LogInfo))
      return false;
    LogInfo that = (LogInfo)_that;

    if (!TBaseHelper.equalsNobinary(this.log_id, that.log_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.term_id, that.term_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.commit_log_id, that.commit_log_id)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetCheckpoint_path(), that.isSetCheckpoint_path(), this.checkpoint_path, that.checkpoint_path)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {log_id, term_id, commit_log_id, checkpoint_path});
  }

  @Override
  public int compareTo(LogInfo other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetLog_id()).compareTo(other.isSetLog_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(log_id, other.log_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetTerm_id()).compareTo(other.isSetTerm_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(term_id, other.term_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCommit_log_id()).compareTo(other.isSetCommit_log_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(commit_log_id, other.commit_log_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCheckpoint_path()).compareTo(other.isSetCheckpoint_path());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(checkpoint_path, other.checkpoint_path);
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
        case LOG_ID:
          if (__field.type == TType.I64) {
            this.log_id = iprot.readI64();
            setLog_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TERM_ID:
          if (__field.type == TType.I64) {
            this.term_id = iprot.readI64();
            setTerm_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case COMMIT_LOG_ID:
          if (__field.type == TType.I64) {
            this.commit_log_id = iprot.readI64();
            setCommit_log_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case CHECKPOINT_PATH:
          if (__field.type == TType.STRING) {
            this.checkpoint_path = iprot.readBinary();
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
    oprot.writeFieldBegin(LOG_ID_FIELD_DESC);
    oprot.writeI64(this.log_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(TERM_ID_FIELD_DESC);
    oprot.writeI64(this.term_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(COMMIT_LOG_ID_FIELD_DESC);
    oprot.writeI64(this.commit_log_id);
    oprot.writeFieldEnd();
    if (this.checkpoint_path != null) {
      oprot.writeFieldBegin(CHECKPOINT_PATH_FIELD_DESC);
      oprot.writeBinary(this.checkpoint_path);
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
    StringBuilder sb = new StringBuilder("LogInfo");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("log_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLog_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("term_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getTerm_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("commit_log_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCommit_log_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("checkpoint_path");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getCheckpoint_path() == null) {
      sb.append("null");
    } else {
        int __checkpoint_path_size = Math.min(this.getCheckpoint_path().length, 128);
        for (int i = 0; i < __checkpoint_path_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getCheckpoint_path()[i]).length() > 1 ? Integer.toHexString(this.getCheckpoint_path()[i]).substring(Integer.toHexString(this.getCheckpoint_path()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getCheckpoint_path()[i]).toUpperCase());
        }
        if (this.getCheckpoint_path().length > 128) sb.append(" ...");
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

