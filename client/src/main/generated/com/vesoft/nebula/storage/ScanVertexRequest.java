/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.storage;

import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.BitSet;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facebook.thrift.*;
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
  private static final TField RETURN_COLUMNS_FIELD_DESC = new TField("return_columns", TType.MAP, (short)4);
  private static final TField ALL_COLUMNS_FIELD_DESC = new TField("all_columns", TType.BOOL, (short)5);
  private static final TField LIMIT_FIELD_DESC = new TField("limit", TType.I32, (short)6);
  private static final TField START_TIME_FIELD_DESC = new TField("start_time", TType.I64, (short)7);
  private static final TField END_TIME_FIELD_DESC = new TField("end_time", TType.I64, (short)8);

  public int space_id;
  public int part_id;
  public byte[] cursor;
  public Map<Integer,List<PropDef>> return_columns;
  public boolean all_columns;
  public int limit;
  public long start_time;
  public long end_time;
  public static final int SPACE_ID = 1;
  public static final int PART_ID = 2;
  public static final int CURSOR = 3;
  public static final int RETURN_COLUMNS = 4;
  public static final int ALL_COLUMNS = 5;
  public static final int LIMIT = 6;
  public static final int START_TIME = 7;
  public static final int END_TIME = 8;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __PART_ID_ISSET_ID = 1;
  private static final int __ALL_COLUMNS_ISSET_ID = 2;
  private static final int __LIMIT_ISSET_ID = 3;
  private static final int __START_TIME_ISSET_ID = 4;
  private static final int __END_TIME_ISSET_ID = 5;
  private BitSet __isset_bit_vector = new BitSet(6);

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
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.I32), 
            new ListMetaData(TType.LIST, 
                new StructMetaData(TType.STRUCT, PropDef.class)))));
    tmpMetaDataMap.put(ALL_COLUMNS, new FieldMetaData("all_columns", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(LIMIT, new FieldMetaData("limit", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(START_TIME, new FieldMetaData("start_time", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(END_TIME, new FieldMetaData("end_time", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ScanVertexRequest.class, metaDataMap);
  }

  public ScanVertexRequest() {
  }

  public ScanVertexRequest(
    int space_id,
    int part_id,
    Map<Integer,List<PropDef>> return_columns,
    boolean all_columns,
    int limit,
    long start_time,
    long end_time)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.return_columns = return_columns;
    this.all_columns = all_columns;
    setAll_columnsIsSet(true);
    this.limit = limit;
    setLimitIsSet(true);
    this.start_time = start_time;
    setStart_timeIsSet(true);
    this.end_time = end_time;
    setEnd_timeIsSet(true);
  }

  public ScanVertexRequest(
    int space_id,
    int part_id,
    byte[] cursor,
    Map<Integer,List<PropDef>> return_columns,
    boolean all_columns,
    int limit,
    long start_time,
    long end_time)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.cursor = cursor;
    this.return_columns = return_columns;
    this.all_columns = all_columns;
    setAll_columnsIsSet(true);
    this.limit = limit;
    setLimitIsSet(true);
    this.start_time = start_time;
    setStart_timeIsSet(true);
    this.end_time = end_time;
    setEnd_timeIsSet(true);
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
    this.all_columns = TBaseHelper.deepCopy(other.all_columns);
    this.limit = TBaseHelper.deepCopy(other.limit);
    this.start_time = TBaseHelper.deepCopy(other.start_time);
    this.end_time = TBaseHelper.deepCopy(other.end_time);
  }

  public ScanVertexRequest deepCopy() {
    return new ScanVertexRequest(this);
  }

  @Deprecated
  public ScanVertexRequest clone() {
    return new ScanVertexRequest(this);
  }

  public int  getSpace_id() {
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

  public void setSpace_idIsSet(boolean value) {
    __isset_bit_vector.set(__SPACE_ID_ISSET_ID, value);
  }

  public int  getPart_id() {
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

  public void setPart_idIsSet(boolean value) {
    __isset_bit_vector.set(__PART_ID_ISSET_ID, value);
  }

  public byte[]  getCursor() {
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

  public void setCursorIsSet(boolean value) {
    if (!value) {
      this.cursor = null;
    }
  }

  public Map<Integer,List<PropDef>>  getReturn_columns() {
    return this.return_columns;
  }

  public ScanVertexRequest setReturn_columns(Map<Integer,List<PropDef>> return_columns) {
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

  public void setReturn_columnsIsSet(boolean value) {
    if (!value) {
      this.return_columns = null;
    }
  }

  public boolean  isAll_columns() {
    return this.all_columns;
  }

  public ScanVertexRequest setAll_columns(boolean all_columns) {
    this.all_columns = all_columns;
    setAll_columnsIsSet(true);
    return this;
  }

  public void unsetAll_columns() {
    __isset_bit_vector.clear(__ALL_COLUMNS_ISSET_ID);
  }

  // Returns true if field all_columns is set (has been assigned a value) and false otherwise
  public boolean isSetAll_columns() {
    return __isset_bit_vector.get(__ALL_COLUMNS_ISSET_ID);
  }

  public void setAll_columnsIsSet(boolean value) {
    __isset_bit_vector.set(__ALL_COLUMNS_ISSET_ID, value);
  }

  public int  getLimit() {
    return this.limit;
  }

  public ScanVertexRequest setLimit(int limit) {
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

  public void setLimitIsSet(boolean value) {
    __isset_bit_vector.set(__LIMIT_ISSET_ID, value);
  }

  public long  getStart_time() {
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

  public void setStart_timeIsSet(boolean value) {
    __isset_bit_vector.set(__START_TIME_ISSET_ID, value);
  }

  public long  getEnd_time() {
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

  public void setEnd_timeIsSet(boolean value) {
    __isset_bit_vector.set(__END_TIME_ISSET_ID, value);
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case SPACE_ID:
      if (value == null) {
        unsetSpace_id();
      } else {
        setSpace_id((Integer)value);
      }
      break;

    case PART_ID:
      if (value == null) {
        unsetPart_id();
      } else {
        setPart_id((Integer)value);
      }
      break;

    case CURSOR:
      if (value == null) {
        unsetCursor();
      } else {
        setCursor((byte[])value);
      }
      break;

    case RETURN_COLUMNS:
      if (value == null) {
        unsetReturn_columns();
      } else {
        setReturn_columns((Map<Integer,List<PropDef>>)value);
      }
      break;

    case ALL_COLUMNS:
      if (value == null) {
        unsetAll_columns();
      } else {
        setAll_columns((Boolean)value);
      }
      break;

    case LIMIT:
      if (value == null) {
        unsetLimit();
      } else {
        setLimit((Integer)value);
      }
      break;

    case START_TIME:
      if (value == null) {
        unsetStart_time();
      } else {
        setStart_time((Long)value);
      }
      break;

    case END_TIME:
      if (value == null) {
        unsetEnd_time();
      } else {
        setEnd_time((Long)value);
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

    case ALL_COLUMNS:
      return new Boolean(isAll_columns());

    case LIMIT:
      return new Integer(getLimit());

    case START_TIME:
      return new Long(getStart_time());

    case END_TIME:
      return new Long(getEnd_time());

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return isSetSpace_id();
    case PART_ID:
      return isSetPart_id();
    case CURSOR:
      return isSetCursor();
    case RETURN_COLUMNS:
      return isSetReturn_columns();
    case ALL_COLUMNS:
      return isSetAll_columns();
    case LIMIT:
      return isSetLimit();
    case START_TIME:
      return isSetStart_time();
    case END_TIME:
      return isSetEnd_time();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ScanVertexRequest)
      return this.equals((ScanVertexRequest)that);
    return false;
  }

  public boolean equals(ScanVertexRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_space_id = true;
    boolean that_present_space_id = true;
    if (this_present_space_id || that_present_space_id) {
      if (!(this_present_space_id && that_present_space_id))
        return false;
      if (!TBaseHelper.equalsNobinary(this.space_id, that.space_id))
        return false;
    }

    boolean this_present_part_id = true;
    boolean that_present_part_id = true;
    if (this_present_part_id || that_present_part_id) {
      if (!(this_present_part_id && that_present_part_id))
        return false;
      if (!TBaseHelper.equalsNobinary(this.part_id, that.part_id))
        return false;
    }

    boolean this_present_cursor = true && this.isSetCursor();
    boolean that_present_cursor = true && that.isSetCursor();
    if (this_present_cursor || that_present_cursor) {
      if (!(this_present_cursor && that_present_cursor))
        return false;
      if (!TBaseHelper.equalsSlow(this.cursor, that.cursor))
        return false;
    }

    boolean this_present_return_columns = true && this.isSetReturn_columns();
    boolean that_present_return_columns = true && that.isSetReturn_columns();
    if (this_present_return_columns || that_present_return_columns) {
      if (!(this_present_return_columns && that_present_return_columns))
        return false;
      if (!TBaseHelper.equalsNobinary(this.return_columns, that.return_columns))
        return false;
    }

    boolean this_present_all_columns = true;
    boolean that_present_all_columns = true;
    if (this_present_all_columns || that_present_all_columns) {
      if (!(this_present_all_columns && that_present_all_columns))
        return false;
      if (!TBaseHelper.equalsNobinary(this.all_columns, that.all_columns))
        return false;
    }

    boolean this_present_limit = true;
    boolean that_present_limit = true;
    if (this_present_limit || that_present_limit) {
      if (!(this_present_limit && that_present_limit))
        return false;
      if (!TBaseHelper.equalsNobinary(this.limit, that.limit))
        return false;
    }

    boolean this_present_start_time = true;
    boolean that_present_start_time = true;
    if (this_present_start_time || that_present_start_time) {
      if (!(this_present_start_time && that_present_start_time))
        return false;
      if (!TBaseHelper.equalsNobinary(this.start_time, that.start_time))
        return false;
    }

    boolean this_present_end_time = true;
    boolean that_present_end_time = true;
    if (this_present_end_time || that_present_end_time) {
      if (!(this_present_end_time && that_present_end_time))
        return false;
      if (!TBaseHelper.equalsNobinary(this.end_time, that.end_time))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_space_id = true;
    builder.append(present_space_id);
    if (present_space_id)
      builder.append(space_id);

    boolean present_part_id = true;
    builder.append(present_part_id);
    if (present_part_id)
      builder.append(part_id);

    boolean present_cursor = true && (isSetCursor());
    builder.append(present_cursor);
    if (present_cursor)
      builder.append(cursor);

    boolean present_return_columns = true && (isSetReturn_columns());
    builder.append(present_return_columns);
    if (present_return_columns)
      builder.append(return_columns);

    boolean present_all_columns = true;
    builder.append(present_all_columns);
    if (present_all_columns)
      builder.append(all_columns);

    boolean present_limit = true;
    builder.append(present_limit);
    if (present_limit)
      builder.append(limit);

    boolean present_start_time = true;
    builder.append(present_start_time);
    if (present_start_time)
      builder.append(start_time);

    boolean present_end_time = true;
    builder.append(present_end_time);
    if (present_end_time)
      builder.append(end_time);

    return builder.toHashCode();
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
    lastComparison = Boolean.valueOf(isSetAll_columns()).compareTo(other.isSetAll_columns());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(all_columns, other.all_columns);
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
    return 0;
  }

  public void read(TProtocol iprot) throws TException {
    TField field;
    iprot.readStructBegin(metaDataMap);
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == TType.STOP) { 
        break;
      }
      switch (field.id)
      {
        case SPACE_ID:
          if (field.type == TType.I32) {
            this.space_id = iprot.readI32();
            setSpace_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case PART_ID:
          if (field.type == TType.I32) {
            this.part_id = iprot.readI32();
            setPart_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case CURSOR:
          if (field.type == TType.STRING) {
            this.cursor = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case RETURN_COLUMNS:
          if (field.type == TType.MAP) {
            {
              TMap _map151 = iprot.readMapBegin();
              this.return_columns = new HashMap<Integer,List<PropDef>>(Math.max(0, 2*_map151.size));
              for (int _i152 = 0; 
                   (_map151.size < 0) ? iprot.peekMap() : (_i152 < _map151.size); 
                   ++_i152)
              {
                int _key153;
                List<PropDef> _val154;
                _key153 = iprot.readI32();
                {
                  TList _list155 = iprot.readListBegin();
                  _val154 = new ArrayList<PropDef>(Math.max(0, _list155.size));
                  for (int _i156 = 0; 
                       (_list155.size < 0) ? iprot.peekList() : (_i156 < _list155.size); 
                       ++_i156)
                  {
                    PropDef _elem157;
                    _elem157 = new PropDef();
                    _elem157.read(iprot);
                    _val154.add(_elem157);
                  }
                  iprot.readListEnd();
                }
                this.return_columns.put(_key153, _val154);
              }
              iprot.readMapEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case ALL_COLUMNS:
          if (field.type == TType.BOOL) {
            this.all_columns = iprot.readBool();
            setAll_columnsIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case LIMIT:
          if (field.type == TType.I32) {
            this.limit = iprot.readI32();
            setLimitIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case START_TIME:
          if (field.type == TType.I64) {
            this.start_time = iprot.readI64();
            setStart_timeIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case END_TIME:
          if (field.type == TType.I64) {
            this.end_time = iprot.readI64();
            setEnd_timeIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        default:
          TProtocolUtil.skip(iprot, field.type);
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
      {
        oprot.writeMapBegin(new TMap(TType.I32, TType.LIST, this.return_columns.size()));
        for (Map.Entry<Integer, List<PropDef>> _iter158 : this.return_columns.entrySet())        {
          oprot.writeI32(_iter158.getKey());
          {
            oprot.writeListBegin(new TList(TType.STRUCT, _iter158.getValue().size()));
            for (PropDef _iter159 : _iter158.getValue())            {
              _iter159.write(oprot);
            }
            oprot.writeListEnd();
          }
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(ALL_COLUMNS_FIELD_DESC);
    oprot.writeBool(this.all_columns);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(LIMIT_FIELD_DESC);
    oprot.writeI32(this.limit);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(START_TIME_FIELD_DESC);
    oprot.writeI64(this.start_time);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(END_TIME_FIELD_DESC);
    oprot.writeI64(this.end_time);
    oprot.writeFieldEnd();
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    return toString(DEFAULT_PRETTY_PRINT);
  }

  @Override
  public String toString(boolean prettyPrint) {
    return toString(1, prettyPrint);
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
    sb.append(TBaseHelper.toString(this. getSpace_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("part_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getPart_id(), indent + 1, prettyPrint));
    first = false;
    if (isSetCursor())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("cursor");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getCursor() == null) {
        sb.append("null");
      } else {
          int __cursor_size = Math.min(this. getCursor().length, 128);
          for (int i = 0; i < __cursor_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this. getCursor()[i]).length() > 1 ? Integer.toHexString(this. getCursor()[i]).substring(Integer.toHexString(this. getCursor()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getCursor()[i]).toUpperCase());
          }
          if (this. getCursor().length > 128) sb.append(" ...");
      }
      first = false;
    }
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("return_columns");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getReturn_columns() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getReturn_columns(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("all_columns");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. isAll_columns(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("limit");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getLimit(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("start_time");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getStart_time(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("end_time");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getEnd_time(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // check that fields of type enum have valid values
  }

}

