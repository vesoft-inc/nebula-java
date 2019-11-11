/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.meta;

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
public class GetReq implements TBase, java.io.Serializable, Cloneable, Comparable<GetReq> {
  private static final TStruct STRUCT_DESC = new TStruct("GetReq");
  private static final TField SEGMENT_FIELD_DESC = new TField("segment", TType.STRING, (short)1);
  private static final TField KEY_FIELD_DESC = new TField("key", TType.STRING, (short)2);

  public String segment;
  public String key;
  public static final int SEGMENT = 1;
  public static final int KEY = 2;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SEGMENT, new FieldMetaData("segment", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(KEY, new FieldMetaData("key", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(GetReq.class, metaDataMap);
  }

  public GetReq() {
  }

  public GetReq(
    String segment,
    String key)
  {
    this();
    this.segment = segment;
    this.key = key;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetReq(GetReq other) {
    if (other.isSetSegment()) {
      this.segment = TBaseHelper.deepCopy(other.segment);
    }
    if (other.isSetKey()) {
      this.key = TBaseHelper.deepCopy(other.key);
    }
  }

  public GetReq deepCopy() {
    return new GetReq(this);
  }

  @Deprecated
  public GetReq clone() {
    return new GetReq(this);
  }

  public String  getSegment() {
    return this.segment;
  }

  public GetReq setSegment(String segment) {
    this.segment = segment;
    return this;
  }

  public void unsetSegment() {
    this.segment = null;
  }

  // Returns true if field segment is set (has been assigned a value) and false otherwise
  public boolean isSetSegment() {
    return this.segment != null;
  }

  public void setSegmentIsSet(boolean value) {
    if (!value) {
      this.segment = null;
    }
  }

  public String  getKey() {
    return this.key;
  }

  public GetReq setKey(String key) {
    this.key = key;
    return this;
  }

  public void unsetKey() {
    this.key = null;
  }

  // Returns true if field key is set (has been assigned a value) and false otherwise
  public boolean isSetKey() {
    return this.key != null;
  }

  public void setKeyIsSet(boolean value) {
    if (!value) {
      this.key = null;
    }
  }

  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case SEGMENT:
      if (value == null) {
        unsetSegment();
      } else {
        setSegment((String)value);
      }
      break;

    case KEY:
      if (value == null) {
        unsetKey();
      } else {
        setKey((String)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SEGMENT:
      return getSegment();

    case KEY:
      return getKey();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SEGMENT:
      return isSetSegment();
    case KEY:
      return isSetKey();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof GetReq)
      return this.equals((GetReq)that);
    return false;
  }

  public boolean equals(GetReq that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_segment = true && this.isSetSegment();
    boolean that_present_segment = true && that.isSetSegment();
    if (this_present_segment || that_present_segment) {
      if (!(this_present_segment && that_present_segment))
        return false;
      if (!TBaseHelper.equalsNobinary(this.segment, that.segment))
        return false;
    }

    boolean this_present_key = true && this.isSetKey();
    boolean that_present_key = true && that.isSetKey();
    if (this_present_key || that_present_key) {
      if (!(this_present_key && that_present_key))
        return false;
      if (!TBaseHelper.equalsNobinary(this.key, that.key))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_segment = true && (isSetSegment());
    builder.append(present_segment);
    if (present_segment)
      builder.append(segment);

    boolean present_key = true && (isSetKey());
    builder.append(present_key);
    if (present_key)
      builder.append(key);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(GetReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSegment()).compareTo(other.isSetSegment());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(segment, other.segment);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetKey()).compareTo(other.isSetKey());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(key, other.key);
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
        case SEGMENT:
          if (field.type == TType.STRING) {
            this.segment = iprot.readString();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case KEY:
          if (field.type == TType.STRING) {
            this.key = iprot.readString();
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
    if (this.segment != null) {
      oprot.writeFieldBegin(SEGMENT_FIELD_DESC);
      oprot.writeString(this.segment);
      oprot.writeFieldEnd();
    }
    if (this.key != null) {
      oprot.writeFieldBegin(KEY_FIELD_DESC);
      oprot.writeString(this.key);
      oprot.writeFieldEnd();
    }
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
    StringBuilder sb = new StringBuilder("GetReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("segment");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getSegment() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getSegment(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("key");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getKey() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getKey(), indent + 1, prettyPrint));
    }
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

