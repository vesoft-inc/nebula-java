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
public class ScanVertexRequest implements TBase, java.io.Serializable, Cloneable, Comparable<ScanVertexRequest> {
  private static final TStruct STRUCT_DESC = new TStruct("ScanVertexRequest");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField PART_ID_FIELD_DESC = new TField("part_id", TType.I32, (short)2);
  private static final TField CURSOR_FIELD_DESC = new TField("cursor", TType.STRING, (short)3);
  private static final TField RETURN_COLUMNS_FIELD_DESC = new TField("return_columns", TType.STRUCT, (short)4);
  private static final TField LIMIT_FIELD_DESC = new TField("limit", TType.I64, (short)5);
  private static final TField START_TIME_FIELD_DESC = new TField("start_time", TType.I64, (short)6);
  private static final TField END_TIME_FIELD_DESC = new TField("end_time", TType.I64, (short)7);
  private static final TField FILTER_FIELD_DESC = new TField("filter", TType.STRING, (short)8);
  private static final TField ONLY_LATEST_VERSION_FIELD_DESC = new TField("only_latest_version", TType.BOOL, (short)9);
  private static final TField ENABLE_READ_FROM_FOLLOWER_FIELD_DESC = new TField("enable_read_from_follower", TType.BOOL, (short)10);
  private static final TField COMMON_FIELD_DESC = new TField("common", TType.STRUCT, (short)11);

  public int space_id;
  public int part_id;
  public byte[] cursor;
  public VertexProp return_columns;
  public long limit;
  public long start_time;
  public long end_time;
  public byte[] filter;
  public boolean only_latest_version;
  public boolean enable_read_from_follower;
  public RequestCommon common;
  public static final int SPACE_ID = 1;
  public static final int PART_ID = 2;
  public static final int CURSOR = 3;
  public static final int RETURN_COLUMNS = 4;
  public static final int LIMIT = 5;
  public static final int START_TIME = 6;
  public static final int END_TIME = 7;
  public static final int FILTER = 8;
  public static final int ONLY_LATEST_VERSION = 9;
  public static final int ENABLE_READ_FROM_FOLLOWER = 10;
  public static final int COMMON = 11;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __PART_ID_ISSET_ID = 1;
  private static final int __LIMIT_ISSET_ID = 2;
  private static final int __START_TIME_ISSET_ID = 3;
  private static final int __END_TIME_ISSET_ID = 4;
  private static final int __ONLY_LATEST_VERSION_ISSET_ID = 5;
  private static final int __ENABLE_READ_FROM_FOLLOWER_ISSET_ID = 6;
  private BitSet __isset_bit_vector = new BitSet(7);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(PART_ID, new FieldMetaData("part_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(CURSOR, new FieldMetaData("cursor", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(RETURN_COLUMNS, new FieldMetaData("return_columns", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, VertexProp.class)));
    tmpMetaDataMap.put(LIMIT, new FieldMetaData("limit", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(START_TIME, new FieldMetaData("start_time", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(END_TIME, new FieldMetaData("end_time", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(FILTER, new FieldMetaData("filter", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(ONLY_LATEST_VERSION, new FieldMetaData("only_latest_version", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(ENABLE_READ_FROM_FOLLOWER, new FieldMetaData("enable_read_from_follower", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(COMMON, new FieldMetaData("common", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, RequestCommon.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ScanVertexRequest.class, metaDataMap);
  }

  public ScanVertexRequest() {
    this.only_latest_version = false;

    this.enable_read_from_follower = true;

  }

  public ScanVertexRequest(
      int space_id,
      int part_id,
      VertexProp return_columns,
      long limit,
      boolean only_latest_version,
      boolean enable_read_from_follower) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.return_columns = return_columns;
    this.limit = limit;
    setLimitIsSet(true);
    this.only_latest_version = only_latest_version;
    setOnly_latest_versionIsSet(true);
    this.enable_read_from_follower = enable_read_from_follower;
    setEnable_read_from_followerIsSet(true);
  }

  public ScanVertexRequest(
      int space_id,
      int part_id,
      byte[] cursor,
      VertexProp return_columns,
      long limit,
      long start_time,
      long end_time,
      byte[] filter,
      boolean only_latest_version,
      boolean enable_read_from_follower,
      RequestCommon common) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.cursor = cursor;
    this.return_columns = return_columns;
    this.limit = limit;
    setLimitIsSet(true);
    this.start_time = start_time;
    setStart_timeIsSet(true);
    this.end_time = end_time;
    setEnd_timeIsSet(true);
    this.filter = filter;
    this.only_latest_version = only_latest_version;
    setOnly_latest_versionIsSet(true);
    this.enable_read_from_follower = enable_read_from_follower;
    setEnable_read_from_followerIsSet(true);
    this.common = common;
  }

  public static class Builder {
    private int space_id;
    private int part_id;
    private byte[] cursor;
    private VertexProp return_columns;
    private long limit;
    private long start_time;
    private long end_time;
    private byte[] filter;
    private boolean only_latest_version;
    private boolean enable_read_from_follower;
    private RequestCommon common;

    BitSet __optional_isset = new BitSet(7);

    public Builder() {
    }

    public Builder setSpace_id(final int space_id) {
      this.space_id = space_id;
      __optional_isset.set(__SPACE_ID_ISSET_ID, true);
      return this;
    }

    public Builder setPart_id(final int part_id) {
      this.part_id = part_id;
      __optional_isset.set(__PART_ID_ISSET_ID, true);
      return this;
    }

    public Builder setCursor(final byte[] cursor) {
      this.cursor = cursor;
      return this;
    }

    public Builder setReturn_columns(final VertexProp return_columns) {
      this.return_columns = return_columns;
      return this;
    }

    public Builder setLimit(final long limit) {
      this.limit = limit;
      __optional_isset.set(__LIMIT_ISSET_ID, true);
      return this;
    }

    public Builder setStart_time(final long start_time) {
      this.start_time = start_time;
      __optional_isset.set(__START_TIME_ISSET_ID, true);
      return this;
    }

    public Builder setEnd_time(final long end_time) {
      this.end_time = end_time;
      __optional_isset.set(__END_TIME_ISSET_ID, true);
      return this;
    }

    public Builder setFilter(final byte[] filter) {
      this.filter = filter;
      return this;
    }

    public Builder setOnly_latest_version(final boolean only_latest_version) {
      this.only_latest_version = only_latest_version;
      __optional_isset.set(__ONLY_LATEST_VERSION_ISSET_ID, true);
      return this;
    }

    public Builder setEnable_read_from_follower(final boolean enable_read_from_follower) {
      this.enable_read_from_follower = enable_read_from_follower;
      __optional_isset.set(__ENABLE_READ_FROM_FOLLOWER_ISSET_ID, true);
      return this;
    }

    public Builder setCommon(final RequestCommon common) {
      this.common = common;
      return this;
    }

    public ScanVertexRequest build() {
      ScanVertexRequest result = new ScanVertexRequest();
      if (__optional_isset.get(__SPACE_ID_ISSET_ID)) {
        result.setSpace_id(this.space_id);
      }
      if (__optional_isset.get(__PART_ID_ISSET_ID)) {
        result.setPart_id(this.part_id);
      }
      result.setCursor(this.cursor);
      result.setReturn_columns(this.return_columns);
      if (__optional_isset.get(__LIMIT_ISSET_ID)) {
        result.setLimit(this.limit);
      }
      if (__optional_isset.get(__START_TIME_ISSET_ID)) {
        result.setStart_time(this.start_time);
      }
      if (__optional_isset.get(__END_TIME_ISSET_ID)) {
        result.setEnd_time(this.end_time);
      }
      result.setFilter(this.filter);
      if (__optional_isset.get(__ONLY_LATEST_VERSION_ISSET_ID)) {
        result.setOnly_latest_version(this.only_latest_version);
      }
      if (__optional_isset.get(__ENABLE_READ_FROM_FOLLOWER_ISSET_ID)) {
        result.setEnable_read_from_follower(this.enable_read_from_follower);
      }
      result.setCommon(this.common);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ScanVertexRequest(ScanVertexRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    this.part_id = TBaseHelper.deepCopy(other.part_id);
    if (other.isSetCursor()) {
      this.cursor = TBaseHelper.deepCopy(other.cursor);
    }
    if (other.isSetReturn_columns()) {
      this.return_columns = TBaseHelper.deepCopy(other.return_columns);
    }
    this.limit = TBaseHelper.deepCopy(other.limit);
    this.start_time = TBaseHelper.deepCopy(other.start_time);
    this.end_time = TBaseHelper.deepCopy(other.end_time);
    if (other.isSetFilter()) {
      this.filter = TBaseHelper.deepCopy(other.filter);
    }
    this.only_latest_version = TBaseHelper.deepCopy(other.only_latest_version);
    this.enable_read_from_follower = TBaseHelper.deepCopy(other.enable_read_from_follower);
    if (other.isSetCommon()) {
      this.common = TBaseHelper.deepCopy(other.common);
    }
  }

  public ScanVertexRequest deepCopy() {
    return new ScanVertexRequest(this);
  }

  public int getSpace_id() {
    return this.space_id;
  }

  public ScanVertexRequest setSpace_id(int space_id) {
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

  public int getPart_id() {
    return this.part_id;
  }

  public ScanVertexRequest setPart_id(int part_id) {
    this.part_id = part_id;
    setPart_idIsSet(true);
    return this;
  }

  public void unsetPart_id() {
    __isset_bit_vector.clear(__PART_ID_ISSET_ID);
  }

  // Returns true if field part_id is set (has been assigned a value) and false otherwise
  public boolean isSetPart_id() {
    return __isset_bit_vector.get(__PART_ID_ISSET_ID);
  }

  public void setPart_idIsSet(boolean __value) {
    __isset_bit_vector.set(__PART_ID_ISSET_ID, __value);
  }

  public byte[] getCursor() {
    return this.cursor;
  }

  public ScanVertexRequest setCursor(byte[] cursor) {
    this.cursor = cursor;
    return this;
  }

  public void unsetCursor() {
    this.cursor = null;
  }

  // Returns true if field cursor is set (has been assigned a value) and false otherwise
  public boolean isSetCursor() {
    return this.cursor != null;
  }

  public void setCursorIsSet(boolean __value) {
    if (!__value) {
      this.cursor = null;
    }
  }

  public VertexProp getReturn_columns() {
    return this.return_columns;
  }

  public ScanVertexRequest setReturn_columns(VertexProp return_columns) {
    this.return_columns = return_columns;
    return this;
  }

  public void unsetReturn_columns() {
    this.return_columns = null;
  }

  // Returns true if field return_columns is set (has been assigned a value) and false otherwise
  public boolean isSetReturn_columns() {
    return this.return_columns != null;
  }

  public void setReturn_columnsIsSet(boolean __value) {
    if (!__value) {
      this.return_columns = null;
    }
  }

  public long getLimit() {
    return this.limit;
  }

  public ScanVertexRequest setLimit(long limit) {
    this.limit = limit;
    setLimitIsSet(true);
    return this;
  }

  public void unsetLimit() {
    __isset_bit_vector.clear(__LIMIT_ISSET_ID);
  }

  // Returns true if field limit is set (has been assigned a value) and false otherwise
  public boolean isSetLimit() {
    return __isset_bit_vector.get(__LIMIT_ISSET_ID);
  }

  public void setLimitIsSet(boolean __value) {
    __isset_bit_vector.set(__LIMIT_ISSET_ID, __value);
  }

  public long getStart_time() {
    return this.start_time;
  }

  public ScanVertexRequest setStart_time(long start_time) {
    this.start_time = start_time;
    setStart_timeIsSet(true);
    return this;
  }

  public void unsetStart_time() {
    __isset_bit_vector.clear(__START_TIME_ISSET_ID);
  }

  // Returns true if field start_time is set (has been assigned a value) and false otherwise
  public boolean isSetStart_time() {
    return __isset_bit_vector.get(__START_TIME_ISSET_ID);
  }

  public void setStart_timeIsSet(boolean __value) {
    __isset_bit_vector.set(__START_TIME_ISSET_ID, __value);
  }

  public long getEnd_time() {
    return this.end_time;
  }

  public ScanVertexRequest setEnd_time(long end_time) {
    this.end_time = end_time;
    setEnd_timeIsSet(true);
    return this;
  }

  public void unsetEnd_time() {
    __isset_bit_vector.clear(__END_TIME_ISSET_ID);
  }

  // Returns true if field end_time is set (has been assigned a value) and false otherwise
  public boolean isSetEnd_time() {
    return __isset_bit_vector.get(__END_TIME_ISSET_ID);
  }

  public void setEnd_timeIsSet(boolean __value) {
    __isset_bit_vector.set(__END_TIME_ISSET_ID, __value);
  }

  public byte[] getFilter() {
    return this.filter;
  }

  public ScanVertexRequest setFilter(byte[] filter) {
    this.filter = filter;
    return this;
  }

  public void unsetFilter() {
    this.filter = null;
  }

  // Returns true if field filter is set (has been assigned a value) and false otherwise
  public boolean isSetFilter() {
    return this.filter != null;
  }

  public void setFilterIsSet(boolean __value) {
    if (!__value) {
      this.filter = null;
    }
  }

  public boolean isOnly_latest_version() {
    return this.only_latest_version;
  }

  public ScanVertexRequest setOnly_latest_version(boolean only_latest_version) {
    this.only_latest_version = only_latest_version;
    setOnly_latest_versionIsSet(true);
    return this;
  }

  public void unsetOnly_latest_version() {
    __isset_bit_vector.clear(__ONLY_LATEST_VERSION_ISSET_ID);
  }

  // Returns true if field only_latest_version is set (has been assigned a value) and false otherwise
  public boolean isSetOnly_latest_version() {
    return __isset_bit_vector.get(__ONLY_LATEST_VERSION_ISSET_ID);
  }

  public void setOnly_latest_versionIsSet(boolean __value) {
    __isset_bit_vector.set(__ONLY_LATEST_VERSION_ISSET_ID, __value);
  }

  public boolean isEnable_read_from_follower() {
    return this.enable_read_from_follower;
  }

  public ScanVertexRequest setEnable_read_from_follower(boolean enable_read_from_follower) {
    this.enable_read_from_follower = enable_read_from_follower;
    setEnable_read_from_followerIsSet(true);
    return this;
  }

  public void unsetEnable_read_from_follower() {
    __isset_bit_vector.clear(__ENABLE_READ_FROM_FOLLOWER_ISSET_ID);
  }

  // Returns true if field enable_read_from_follower is set (has been assigned a value) and false otherwise
  public boolean isSetEnable_read_from_follower() {
    return __isset_bit_vector.get(__ENABLE_READ_FROM_FOLLOWER_ISSET_ID);
  }

  public void setEnable_read_from_followerIsSet(boolean __value) {
    __isset_bit_vector.set(__ENABLE_READ_FROM_FOLLOWER_ISSET_ID, __value);
  }

  public RequestCommon getCommon() {
    return this.common;
  }

  public ScanVertexRequest setCommon(RequestCommon common) {
    this.common = common;
    return this;
  }

  public void unsetCommon() {
    this.common = null;
  }

  // Returns true if field common is set (has been assigned a value) and false otherwise
  public boolean isSetCommon() {
    return this.common != null;
  }

  public void setCommonIsSet(boolean __value) {
    if (!__value) {
      this.common = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SPACE_ID:
      if (__value == null) {
        unsetSpace_id();
      } else {
        setSpace_id((Integer)__value);
      }
      break;

    case PART_ID:
      if (__value == null) {
        unsetPart_id();
      } else {
        setPart_id((Integer)__value);
      }
      break;

    case CURSOR:
      if (__value == null) {
        unsetCursor();
      } else {
        setCursor((byte[])__value);
      }
      break;

    case RETURN_COLUMNS:
      if (__value == null) {
        unsetReturn_columns();
      } else {
        setReturn_columns((VertexProp)__value);
      }
      break;

    case LIMIT:
      if (__value == null) {
        unsetLimit();
      } else {
        setLimit((Long)__value);
      }
      break;

    case START_TIME:
      if (__value == null) {
        unsetStart_time();
      } else {
        setStart_time((Long)__value);
      }
      break;

    case END_TIME:
      if (__value == null) {
        unsetEnd_time();
      } else {
        setEnd_time((Long)__value);
      }
      break;

    case FILTER:
      if (__value == null) {
        unsetFilter();
      } else {
        setFilter((byte[])__value);
      }
      break;

    case ONLY_LATEST_VERSION:
      if (__value == null) {
        unsetOnly_latest_version();
      } else {
        setOnly_latest_version((Boolean)__value);
      }
      break;

    case ENABLE_READ_FROM_FOLLOWER:
      if (__value == null) {
        unsetEnable_read_from_follower();
      } else {
        setEnable_read_from_follower((Boolean)__value);
      }
      break;

    case COMMON:
      if (__value == null) {
        unsetCommon();
      } else {
        setCommon((RequestCommon)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return new Integer(getSpace_id());

    case PART_ID:
      return new Integer(getPart_id());

    case CURSOR:
      return getCursor();

    case RETURN_COLUMNS:
      return getReturn_columns();

    case LIMIT:
      return new Long(getLimit());

    case START_TIME:
      return new Long(getStart_time());

    case END_TIME:
      return new Long(getEnd_time());

    case FILTER:
      return getFilter();

    case ONLY_LATEST_VERSION:
      return new Boolean(isOnly_latest_version());

    case ENABLE_READ_FROM_FOLLOWER:
      return new Boolean(isEnable_read_from_follower());

    case COMMON:
      return getCommon();

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
    if (!(_that instanceof ScanVertexRequest))
      return false;
    ScanVertexRequest that = (ScanVertexRequest)_that;

    if (!TBaseHelper.equalsNobinary(this.space_id, that.space_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.part_id, that.part_id)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetCursor(), that.isSetCursor(), this.cursor, that.cursor)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetReturn_columns(), that.isSetReturn_columns(), this.return_columns, that.return_columns)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.limit, that.limit)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetStart_time(), that.isSetStart_time(), this.start_time, that.start_time)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetEnd_time(), that.isSetEnd_time(), this.end_time, that.end_time)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetFilter(), that.isSetFilter(), this.filter, that.filter)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.only_latest_version, that.only_latest_version)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.enable_read_from_follower, that.enable_read_from_follower)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetCommon(), that.isSetCommon(), this.common, that.common)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space_id, part_id, cursor, return_columns, limit, start_time, end_time, filter, only_latest_version, enable_read_from_follower, common});
  }

  @Override
  public int compareTo(ScanVertexRequest other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSpace_id()).compareTo(other.isSetSpace_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space_id, other.space_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPart_id()).compareTo(other.isSetPart_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(part_id, other.part_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCursor()).compareTo(other.isSetCursor());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(cursor, other.cursor);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetReturn_columns()).compareTo(other.isSetReturn_columns());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(return_columns, other.return_columns);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLimit()).compareTo(other.isSetLimit());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(limit, other.limit);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetStart_time()).compareTo(other.isSetStart_time());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(start_time, other.start_time);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetEnd_time()).compareTo(other.isSetEnd_time());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(end_time, other.end_time);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetFilter()).compareTo(other.isSetFilter());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(filter, other.filter);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetOnly_latest_version()).compareTo(other.isSetOnly_latest_version());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(only_latest_version, other.only_latest_version);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetEnable_read_from_follower()).compareTo(other.isSetEnable_read_from_follower());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(enable_read_from_follower, other.enable_read_from_follower);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCommon()).compareTo(other.isSetCommon());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(common, other.common);
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
        case SPACE_ID:
          if (__field.type == TType.I32) {
            this.space_id = iprot.readI32();
            setSpace_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case PART_ID:
          if (__field.type == TType.I32) {
            this.part_id = iprot.readI32();
            setPart_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case CURSOR:
          if (__field.type == TType.STRING) {
            this.cursor = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case RETURN_COLUMNS:
          if (__field.type == TType.STRUCT) {
            this.return_columns = new VertexProp();
            this.return_columns.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LIMIT:
          if (__field.type == TType.I64) {
            this.limit = iprot.readI64();
            setLimitIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case START_TIME:
          if (__field.type == TType.I64) {
            this.start_time = iprot.readI64();
            setStart_timeIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case END_TIME:
          if (__field.type == TType.I64) {
            this.end_time = iprot.readI64();
            setEnd_timeIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case FILTER:
          if (__field.type == TType.STRING) {
            this.filter = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case ONLY_LATEST_VERSION:
          if (__field.type == TType.BOOL) {
            this.only_latest_version = iprot.readBool();
            setOnly_latest_versionIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case ENABLE_READ_FROM_FOLLOWER:
          if (__field.type == TType.BOOL) {
            this.enable_read_from_follower = iprot.readBool();
            setEnable_read_from_followerIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case COMMON:
          if (__field.type == TType.STRUCT) {
            this.common = new RequestCommon();
            this.common.read(iprot);
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
    oprot.writeFieldBegin(SPACE_ID_FIELD_DESC);
    oprot.writeI32(this.space_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(PART_ID_FIELD_DESC);
    oprot.writeI32(this.part_id);
    oprot.writeFieldEnd();
    if (this.cursor != null) {
      if (isSetCursor()) {
        oprot.writeFieldBegin(CURSOR_FIELD_DESC);
        oprot.writeBinary(this.cursor);
        oprot.writeFieldEnd();
      }
    }
    if (this.return_columns != null) {
      oprot.writeFieldBegin(RETURN_COLUMNS_FIELD_DESC);
      this.return_columns.write(oprot);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(LIMIT_FIELD_DESC);
    oprot.writeI64(this.limit);
    oprot.writeFieldEnd();
    if (isSetStart_time()) {
      oprot.writeFieldBegin(START_TIME_FIELD_DESC);
      oprot.writeI64(this.start_time);
      oprot.writeFieldEnd();
    }
    if (isSetEnd_time()) {
      oprot.writeFieldBegin(END_TIME_FIELD_DESC);
      oprot.writeI64(this.end_time);
      oprot.writeFieldEnd();
    }
    if (this.filter != null) {
      if (isSetFilter()) {
        oprot.writeFieldBegin(FILTER_FIELD_DESC);
        oprot.writeBinary(this.filter);
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldBegin(ONLY_LATEST_VERSION_FIELD_DESC);
    oprot.writeBool(this.only_latest_version);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(ENABLE_READ_FROM_FOLLOWER_FIELD_DESC);
    oprot.writeBool(this.enable_read_from_follower);
    oprot.writeFieldEnd();
    if (this.common != null) {
      if (isSetCommon()) {
        oprot.writeFieldBegin(COMMON_FIELD_DESC);
        this.common.write(oprot);
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
    StringBuilder sb = new StringBuilder("ScanVertexRequest");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("space_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getSpace_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("part_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getPart_id(), indent + 1, prettyPrint));
    first = false;
    if (isSetCursor())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("cursor");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getCursor() == null) {
        sb.append("null");
      } else {
          int __cursor_size = Math.min(this.getCursor().length, 128);
          for (int i = 0; i < __cursor_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getCursor()[i]).length() > 1 ? Integer.toHexString(this.getCursor()[i]).substring(Integer.toHexString(this.getCursor()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getCursor()[i]).toUpperCase());
          }
          if (this.getCursor().length > 128) sb.append(" ...");
      }
      first = false;
    }
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("return_columns");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getReturn_columns() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getReturn_columns(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("limit");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLimit(), indent + 1, prettyPrint));
    first = false;
    if (isSetStart_time())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("start_time");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this.getStart_time(), indent + 1, prettyPrint));
      first = false;
    }
    if (isSetEnd_time())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("end_time");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this.getEnd_time(), indent + 1, prettyPrint));
      first = false;
    }
    if (isSetFilter())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("filter");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getFilter() == null) {
        sb.append("null");
      } else {
          int __filter_size = Math.min(this.getFilter().length, 128);
          for (int i = 0; i < __filter_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getFilter()[i]).length() > 1 ? Integer.toHexString(this.getFilter()[i]).substring(Integer.toHexString(this.getFilter()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getFilter()[i]).toUpperCase());
          }
          if (this.getFilter().length > 128) sb.append(" ...");
      }
      first = false;
    }
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("only_latest_version");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isOnly_latest_version(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("enable_read_from_follower");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isEnable_read_from_follower(), indent + 1, prettyPrint));
    first = false;
    if (isSetCommon())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("common");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getCommon() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getCommon(), indent + 1, prettyPrint));
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

