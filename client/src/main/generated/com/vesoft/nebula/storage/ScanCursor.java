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
public class ScanCursor implements TBase, java.io.Serializable, Cloneable, Comparable<ScanCursor> {
  private static final TStruct STRUCT_DESC = new TStruct("ScanCursor");
  private static final TField HAS_NEXT_FIELD_DESC = new TField("has_next", TType.BOOL, (short)3);
  private static final TField NEXT_CURSOR_FIELD_DESC = new TField("next_cursor", TType.STRING, (short)4);

  public boolean has_next;
  public byte[] next_cursor;
  public static final int HAS_NEXT = 3;
  public static final int NEXT_CURSOR = 4;

  // isset id assignments
  private static final int __HAS_NEXT_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(HAS_NEXT, new FieldMetaData("has_next", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(NEXT_CURSOR, new FieldMetaData("next_cursor", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ScanCursor.class, metaDataMap);
  }

  public ScanCursor() {
  }

  public ScanCursor(
      boolean has_next) {
    this();
    this.has_next = has_next;
    setHas_nextIsSet(true);
  }

  public ScanCursor(
      boolean has_next,
      byte[] next_cursor) {
    this();
    this.has_next = has_next;
    setHas_nextIsSet(true);
    this.next_cursor = next_cursor;
  }

  public static class Builder {
    private boolean has_next;
    private byte[] next_cursor;

    BitSet __optional_isset = new BitSet(1);

    public Builder() {
    }

    public Builder setHas_next(final boolean has_next) {
      this.has_next = has_next;
      __optional_isset.set(__HAS_NEXT_ISSET_ID, true);
      return this;
    }

    public Builder setNext_cursor(final byte[] next_cursor) {
      this.next_cursor = next_cursor;
      return this;
    }

    public ScanCursor build() {
      ScanCursor result = new ScanCursor();
      if (__optional_isset.get(__HAS_NEXT_ISSET_ID)) {
        result.setHas_next(this.has_next);
      }
      result.setNext_cursor(this.next_cursor);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ScanCursor(ScanCursor other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.has_next = TBaseHelper.deepCopy(other.has_next);
    if (other.isSetNext_cursor()) {
      this.next_cursor = TBaseHelper.deepCopy(other.next_cursor);
    }
  }

  public ScanCursor deepCopy() {
    return new ScanCursor(this);
  }

  public boolean isHas_next() {
    return this.has_next;
  }

  public ScanCursor setHas_next(boolean has_next) {
    this.has_next = has_next;
    setHas_nextIsSet(true);
    return this;
  }

  public void unsetHas_next() {
    __isset_bit_vector.clear(__HAS_NEXT_ISSET_ID);
  }

  // Returns true if field has_next is set (has been assigned a value) and false otherwise
  public boolean isSetHas_next() {
    return __isset_bit_vector.get(__HAS_NEXT_ISSET_ID);
  }

  public void setHas_nextIsSet(boolean __value) {
    __isset_bit_vector.set(__HAS_NEXT_ISSET_ID, __value);
  }

  public byte[] getNext_cursor() {
    return this.next_cursor;
  }

  public ScanCursor setNext_cursor(byte[] next_cursor) {
    this.next_cursor = next_cursor;
    return this;
  }

  public void unsetNext_cursor() {
    this.next_cursor = null;
  }

  // Returns true if field next_cursor is set (has been assigned a value) and false otherwise
  public boolean isSetNext_cursor() {
    return this.next_cursor != null;
  }

  public void setNext_cursorIsSet(boolean __value) {
    if (!__value) {
      this.next_cursor = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case HAS_NEXT:
      if (__value == null) {
        unsetHas_next();
      } else {
        setHas_next((Boolean)__value);
      }
      break;

    case NEXT_CURSOR:
      if (__value == null) {
        unsetNext_cursor();
      } else {
        setNext_cursor((byte[])__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case HAS_NEXT:
      return new Boolean(isHas_next());

    case NEXT_CURSOR:
      return getNext_cursor();

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
    if (!(_that instanceof ScanCursor))
      return false;
    ScanCursor that = (ScanCursor)_that;

    if (!TBaseHelper.equalsNobinary(this.has_next, that.has_next)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetNext_cursor(), that.isSetNext_cursor(), this.next_cursor, that.next_cursor)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {has_next, next_cursor});
  }

  @Override
  public int compareTo(ScanCursor other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetHas_next()).compareTo(other.isSetHas_next());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(has_next, other.has_next);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetNext_cursor()).compareTo(other.isSetNext_cursor());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(next_cursor, other.next_cursor);
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
        case HAS_NEXT:
          if (__field.type == TType.BOOL) {
            this.has_next = iprot.readBool();
            setHas_nextIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case NEXT_CURSOR:
          if (__field.type == TType.STRING) {
            this.next_cursor = iprot.readBinary();
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
    oprot.writeFieldBegin(HAS_NEXT_FIELD_DESC);
    oprot.writeBool(this.has_next);
    oprot.writeFieldEnd();
    if (this.next_cursor != null) {
      if (isSetNext_cursor()) {
        oprot.writeFieldBegin(NEXT_CURSOR_FIELD_DESC);
        oprot.writeBinary(this.next_cursor);
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
    StringBuilder sb = new StringBuilder("ScanCursor");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("has_next");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isHas_next(), indent + 1, prettyPrint));
    first = false;
    if (isSetNext_cursor())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("next_cursor");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getNext_cursor() == null) {
        sb.append("null");
      } else {
          int __next_cursor_size = Math.min(this.getNext_cursor().length, 128);
          for (int i = 0; i < __next_cursor_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getNext_cursor()[i]).length() > 1 ? Integer.toHexString(this.getNext_cursor()[i]).substring(Integer.toHexString(this.getNext_cursor()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getNext_cursor()[i]).toUpperCase());
          }
          if (this.getNext_cursor().length > 128) sb.append(" ...");
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

