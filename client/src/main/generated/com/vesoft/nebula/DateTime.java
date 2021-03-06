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
public class DateTime implements TBase, java.io.Serializable, Cloneable, Comparable<DateTime> {
  private static final TStruct STRUCT_DESC = new TStruct("DateTime");
  private static final TField YEAR_FIELD_DESC = new TField("year", TType.I16, (short)1);
  private static final TField MONTH_FIELD_DESC = new TField("month", TType.BYTE, (short)2);
  private static final TField DAY_FIELD_DESC = new TField("day", TType.BYTE, (short)3);
  private static final TField HOUR_FIELD_DESC = new TField("hour", TType.BYTE, (short)4);
  private static final TField MINUTE_FIELD_DESC = new TField("minute", TType.BYTE, (short)5);
  private static final TField SEC_FIELD_DESC = new TField("sec", TType.BYTE, (short)6);
  private static final TField MICROSEC_FIELD_DESC = new TField("microsec", TType.I32, (short)7);

  public short year;
  public byte month;
  public byte day;
  public byte hour;
  public byte minute;
  public byte sec;
  public int microsec;
  public static final int YEAR = 1;
  public static final int MONTH = 2;
  public static final int DAY = 3;
  public static final int HOUR = 4;
  public static final int MINUTE = 5;
  public static final int SEC = 6;
  public static final int MICROSEC = 7;

  // isset id assignments
  private static final int __YEAR_ISSET_ID = 0;
  private static final int __MONTH_ISSET_ID = 1;
  private static final int __DAY_ISSET_ID = 2;
  private static final int __HOUR_ISSET_ID = 3;
  private static final int __MINUTE_ISSET_ID = 4;
  private static final int __SEC_ISSET_ID = 5;
  private static final int __MICROSEC_ISSET_ID = 6;
  private BitSet __isset_bit_vector = new BitSet(7);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(YEAR, new FieldMetaData("year", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I16)));
    tmpMetaDataMap.put(MONTH, new FieldMetaData("month", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BYTE)));
    tmpMetaDataMap.put(DAY, new FieldMetaData("day", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BYTE)));
    tmpMetaDataMap.put(HOUR, new FieldMetaData("hour", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BYTE)));
    tmpMetaDataMap.put(MINUTE, new FieldMetaData("minute", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BYTE)));
    tmpMetaDataMap.put(SEC, new FieldMetaData("sec", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BYTE)));
    tmpMetaDataMap.put(MICROSEC, new FieldMetaData("microsec", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(DateTime.class, metaDataMap);
  }

  public DateTime() {
  }

  public DateTime(
      short year,
      byte month,
      byte day,
      byte hour,
      byte minute,
      byte sec,
      int microsec) {
    this();
    this.year = year;
    setYearIsSet(true);
    this.month = month;
    setMonthIsSet(true);
    this.day = day;
    setDayIsSet(true);
    this.hour = hour;
    setHourIsSet(true);
    this.minute = minute;
    setMinuteIsSet(true);
    this.sec = sec;
    setSecIsSet(true);
    this.microsec = microsec;
    setMicrosecIsSet(true);
  }

  public static class Builder {
    private short year;
    private byte month;
    private byte day;
    private byte hour;
    private byte minute;
    private byte sec;
    private int microsec;

    BitSet __optional_isset = new BitSet(7);

    public Builder() {
    }

    public Builder setYear(final short year) {
      this.year = year;
      __optional_isset.set(__YEAR_ISSET_ID, true);
      return this;
    }

    public Builder setMonth(final byte month) {
      this.month = month;
      __optional_isset.set(__MONTH_ISSET_ID, true);
      return this;
    }

    public Builder setDay(final byte day) {
      this.day = day;
      __optional_isset.set(__DAY_ISSET_ID, true);
      return this;
    }

    public Builder setHour(final byte hour) {
      this.hour = hour;
      __optional_isset.set(__HOUR_ISSET_ID, true);
      return this;
    }

    public Builder setMinute(final byte minute) {
      this.minute = minute;
      __optional_isset.set(__MINUTE_ISSET_ID, true);
      return this;
    }

    public Builder setSec(final byte sec) {
      this.sec = sec;
      __optional_isset.set(__SEC_ISSET_ID, true);
      return this;
    }

    public Builder setMicrosec(final int microsec) {
      this.microsec = microsec;
      __optional_isset.set(__MICROSEC_ISSET_ID, true);
      return this;
    }

    public DateTime build() {
      DateTime result = new DateTime();
      if (__optional_isset.get(__YEAR_ISSET_ID)) {
        result.setYear(this.year);
      }
      if (__optional_isset.get(__MONTH_ISSET_ID)) {
        result.setMonth(this.month);
      }
      if (__optional_isset.get(__DAY_ISSET_ID)) {
        result.setDay(this.day);
      }
      if (__optional_isset.get(__HOUR_ISSET_ID)) {
        result.setHour(this.hour);
      }
      if (__optional_isset.get(__MINUTE_ISSET_ID)) {
        result.setMinute(this.minute);
      }
      if (__optional_isset.get(__SEC_ISSET_ID)) {
        result.setSec(this.sec);
      }
      if (__optional_isset.get(__MICROSEC_ISSET_ID)) {
        result.setMicrosec(this.microsec);
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
  public DateTime(DateTime other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.year = TBaseHelper.deepCopy(other.year);
    this.month = TBaseHelper.deepCopy(other.month);
    this.day = TBaseHelper.deepCopy(other.day);
    this.hour = TBaseHelper.deepCopy(other.hour);
    this.minute = TBaseHelper.deepCopy(other.minute);
    this.sec = TBaseHelper.deepCopy(other.sec);
    this.microsec = TBaseHelper.deepCopy(other.microsec);
  }

  public DateTime deepCopy() {
    return new DateTime(this);
  }

  public short getYear() {
    return this.year;
  }

  public DateTime setYear(short year) {
    this.year = year;
    setYearIsSet(true);
    return this;
  }

  public void unsetYear() {
    __isset_bit_vector.clear(__YEAR_ISSET_ID);
  }

  // Returns true if field year is set (has been assigned a value) and false otherwise
  public boolean isSetYear() {
    return __isset_bit_vector.get(__YEAR_ISSET_ID);
  }

  public void setYearIsSet(boolean __value) {
    __isset_bit_vector.set(__YEAR_ISSET_ID, __value);
  }

  public byte getMonth() {
    return this.month;
  }

  public DateTime setMonth(byte month) {
    this.month = month;
    setMonthIsSet(true);
    return this;
  }

  public void unsetMonth() {
    __isset_bit_vector.clear(__MONTH_ISSET_ID);
  }

  // Returns true if field month is set (has been assigned a value) and false otherwise
  public boolean isSetMonth() {
    return __isset_bit_vector.get(__MONTH_ISSET_ID);
  }

  public void setMonthIsSet(boolean __value) {
    __isset_bit_vector.set(__MONTH_ISSET_ID, __value);
  }

  public byte getDay() {
    return this.day;
  }

  public DateTime setDay(byte day) {
    this.day = day;
    setDayIsSet(true);
    return this;
  }

  public void unsetDay() {
    __isset_bit_vector.clear(__DAY_ISSET_ID);
  }

  // Returns true if field day is set (has been assigned a value) and false otherwise
  public boolean isSetDay() {
    return __isset_bit_vector.get(__DAY_ISSET_ID);
  }

  public void setDayIsSet(boolean __value) {
    __isset_bit_vector.set(__DAY_ISSET_ID, __value);
  }

  public byte getHour() {
    return this.hour;
  }

  public DateTime setHour(byte hour) {
    this.hour = hour;
    setHourIsSet(true);
    return this;
  }

  public void unsetHour() {
    __isset_bit_vector.clear(__HOUR_ISSET_ID);
  }

  // Returns true if field hour is set (has been assigned a value) and false otherwise
  public boolean isSetHour() {
    return __isset_bit_vector.get(__HOUR_ISSET_ID);
  }

  public void setHourIsSet(boolean __value) {
    __isset_bit_vector.set(__HOUR_ISSET_ID, __value);
  }

  public byte getMinute() {
    return this.minute;
  }

  public DateTime setMinute(byte minute) {
    this.minute = minute;
    setMinuteIsSet(true);
    return this;
  }

  public void unsetMinute() {
    __isset_bit_vector.clear(__MINUTE_ISSET_ID);
  }

  // Returns true if field minute is set (has been assigned a value) and false otherwise
  public boolean isSetMinute() {
    return __isset_bit_vector.get(__MINUTE_ISSET_ID);
  }

  public void setMinuteIsSet(boolean __value) {
    __isset_bit_vector.set(__MINUTE_ISSET_ID, __value);
  }

  public byte getSec() {
    return this.sec;
  }

  public DateTime setSec(byte sec) {
    this.sec = sec;
    setSecIsSet(true);
    return this;
  }

  public void unsetSec() {
    __isset_bit_vector.clear(__SEC_ISSET_ID);
  }

  // Returns true if field sec is set (has been assigned a value) and false otherwise
  public boolean isSetSec() {
    return __isset_bit_vector.get(__SEC_ISSET_ID);
  }

  public void setSecIsSet(boolean __value) {
    __isset_bit_vector.set(__SEC_ISSET_ID, __value);
  }

  public int getMicrosec() {
    return this.microsec;
  }

  public DateTime setMicrosec(int microsec) {
    this.microsec = microsec;
    setMicrosecIsSet(true);
    return this;
  }

  public void unsetMicrosec() {
    __isset_bit_vector.clear(__MICROSEC_ISSET_ID);
  }

  // Returns true if field microsec is set (has been assigned a value) and false otherwise
  public boolean isSetMicrosec() {
    return __isset_bit_vector.get(__MICROSEC_ISSET_ID);
  }

  public void setMicrosecIsSet(boolean __value) {
    __isset_bit_vector.set(__MICROSEC_ISSET_ID, __value);
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case YEAR:
      if (__value == null) {
        unsetYear();
      } else {
        setYear((Short)__value);
      }
      break;

    case MONTH:
      if (__value == null) {
        unsetMonth();
      } else {
        setMonth((Byte)__value);
      }
      break;

    case DAY:
      if (__value == null) {
        unsetDay();
      } else {
        setDay((Byte)__value);
      }
      break;

    case HOUR:
      if (__value == null) {
        unsetHour();
      } else {
        setHour((Byte)__value);
      }
      break;

    case MINUTE:
      if (__value == null) {
        unsetMinute();
      } else {
        setMinute((Byte)__value);
      }
      break;

    case SEC:
      if (__value == null) {
        unsetSec();
      } else {
        setSec((Byte)__value);
      }
      break;

    case MICROSEC:
      if (__value == null) {
        unsetMicrosec();
      } else {
        setMicrosec((Integer)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case YEAR:
      return new Short(getYear());

    case MONTH:
      return new Byte(getMonth());

    case DAY:
      return new Byte(getDay());

    case HOUR:
      return new Byte(getHour());

    case MINUTE:
      return new Byte(getMinute());

    case SEC:
      return new Byte(getSec());

    case MICROSEC:
      return new Integer(getMicrosec());

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
    if (!(_that instanceof DateTime))
      return false;
    DateTime that = (DateTime)_that;

    if (!TBaseHelper.equalsNobinary(this.year, that.year)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.month, that.month)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.day, that.day)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.hour, that.hour)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.minute, that.minute)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.sec, that.sec)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.microsec, that.microsec)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {year, month, day, hour, minute, sec, microsec});
  }

  @Override
  public int compareTo(DateTime other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetYear()).compareTo(other.isSetYear());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(year, other.year);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetMonth()).compareTo(other.isSetMonth());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(month, other.month);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetDay()).compareTo(other.isSetDay());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(day, other.day);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetHour()).compareTo(other.isSetHour());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(hour, other.hour);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetMinute()).compareTo(other.isSetMinute());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(minute, other.minute);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetSec()).compareTo(other.isSetSec());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(sec, other.sec);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetMicrosec()).compareTo(other.isSetMicrosec());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(microsec, other.microsec);
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
        case YEAR:
          if (__field.type == TType.I16) {
            this.year = iprot.readI16();
            setYearIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case MONTH:
          if (__field.type == TType.BYTE) {
            this.month = iprot.readByte();
            setMonthIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case DAY:
          if (__field.type == TType.BYTE) {
            this.day = iprot.readByte();
            setDayIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case HOUR:
          if (__field.type == TType.BYTE) {
            this.hour = iprot.readByte();
            setHourIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case MINUTE:
          if (__field.type == TType.BYTE) {
            this.minute = iprot.readByte();
            setMinuteIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case SEC:
          if (__field.type == TType.BYTE) {
            this.sec = iprot.readByte();
            setSecIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case MICROSEC:
          if (__field.type == TType.I32) {
            this.microsec = iprot.readI32();
            setMicrosecIsSet(true);
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
    oprot.writeFieldBegin(YEAR_FIELD_DESC);
    oprot.writeI16(this.year);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(MONTH_FIELD_DESC);
    oprot.writeByte(this.month);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(DAY_FIELD_DESC);
    oprot.writeByte(this.day);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(HOUR_FIELD_DESC);
    oprot.writeByte(this.hour);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(MINUTE_FIELD_DESC);
    oprot.writeByte(this.minute);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(SEC_FIELD_DESC);
    oprot.writeByte(this.sec);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(MICROSEC_FIELD_DESC);
    oprot.writeI32(this.microsec);
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
    StringBuilder sb = new StringBuilder("DateTime");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("year");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getYear(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("month");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getMonth(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("day");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getDay(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("hour");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getHour(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("minute");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getMinute(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("sec");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getSec(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("microsec");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getMicrosec(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

