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
public class ResponseCommon implements TBase, java.io.Serializable, Cloneable, Comparable<ResponseCommon> {
  private static final TStruct STRUCT_DESC = new TStruct("ResponseCommon");
  private static final TField FAILED_PARTS_FIELD_DESC = new TField("failed_parts", TType.LIST, (short)1);
  private static final TField LATENCY_IN_US_FIELD_DESC = new TField("latency_in_us", TType.I32, (short)2);

  public List<PartitionResult> failed_parts;
  public int latency_in_us;
  public static final int FAILED_PARTS = 1;
  public static final int LATENCY_IN_US = 2;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __LATENCY_IN_US_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(FAILED_PARTS, new FieldMetaData("failed_parts", TFieldRequirementType.REQUIRED, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, PartitionResult.class))));
    tmpMetaDataMap.put(LATENCY_IN_US, new FieldMetaData("latency_in_us", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ResponseCommon.class, metaDataMap);
  }

  public ResponseCommon() {
  }

  public ResponseCommon(
    List<PartitionResult> failed_parts,
    int latency_in_us)
  {
    this();
    this.failed_parts = failed_parts;
    this.latency_in_us = latency_in_us;
    setLatency_in_usIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ResponseCommon(ResponseCommon other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetFailed_parts()) {
      this.failed_parts = TBaseHelper.deepCopy(other.failed_parts);
    }
    this.latency_in_us = TBaseHelper.deepCopy(other.latency_in_us);
  }

  public ResponseCommon deepCopy() {
    return new ResponseCommon(this);
  }

  @Deprecated
  public ResponseCommon clone() {
    return new ResponseCommon(this);
  }

  public List<PartitionResult>  getFailed_parts() {
    return this.failed_parts;
  }

  public ResponseCommon setFailed_parts(List<PartitionResult> failed_parts) {
    this.failed_parts = failed_parts;
    return this;
  }

  public void unsetFailed_parts() {
    this.failed_parts = null;
  }

  // Returns true if field failed_parts is set (has been assigned a value) and false otherwise
  public boolean isSetFailed_parts() {
    return this.failed_parts != null;
  }

  public void setFailed_partsIsSet(boolean value) {
    if (!value) {
      this.failed_parts = null;
    }
  }

  public int  getLatency_in_us() {
    return this.latency_in_us;
  }

  public ResponseCommon setLatency_in_us(int latency_in_us) {
    this.latency_in_us = latency_in_us;
    setLatency_in_usIsSet(true);
    return this;
  }

  public void unsetLatency_in_us() {
    __isset_bit_vector.clear(__LATENCY_IN_US_ISSET_ID);
  }

  // Returns true if field latency_in_us is set (has been assigned a value) and false otherwise
  public boolean isSetLatency_in_us() {
    return __isset_bit_vector.get(__LATENCY_IN_US_ISSET_ID);
  }

  public void setLatency_in_usIsSet(boolean value) {
    __isset_bit_vector.set(__LATENCY_IN_US_ISSET_ID, value);
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case FAILED_PARTS:
      if (value == null) {
        unsetFailed_parts();
      } else {
        setFailed_parts((List<PartitionResult>)value);
      }
      break;

    case LATENCY_IN_US:
      if (value == null) {
        unsetLatency_in_us();
      } else {
        setLatency_in_us((Integer)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case FAILED_PARTS:
      return getFailed_parts();

    case LATENCY_IN_US:
      return new Integer(getLatency_in_us());

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case FAILED_PARTS:
      return isSetFailed_parts();
    case LATENCY_IN_US:
      return isSetLatency_in_us();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ResponseCommon)
      return this.equals((ResponseCommon)that);
    return false;
  }

  public boolean equals(ResponseCommon that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_failed_parts = true && this.isSetFailed_parts();
    boolean that_present_failed_parts = true && that.isSetFailed_parts();
    if (this_present_failed_parts || that_present_failed_parts) {
      if (!(this_present_failed_parts && that_present_failed_parts))
        return false;
      if (!TBaseHelper.equalsNobinary(this.failed_parts, that.failed_parts))
        return false;
    }

    boolean this_present_latency_in_us = true;
    boolean that_present_latency_in_us = true;
    if (this_present_latency_in_us || that_present_latency_in_us) {
      if (!(this_present_latency_in_us && that_present_latency_in_us))
        return false;
      if (!TBaseHelper.equalsNobinary(this.latency_in_us, that.latency_in_us))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_failed_parts = true && (isSetFailed_parts());
    builder.append(present_failed_parts);
    if (present_failed_parts)
      builder.append(failed_parts);

    boolean present_latency_in_us = true;
    builder.append(present_latency_in_us);
    if (present_latency_in_us)
      builder.append(latency_in_us);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(ResponseCommon other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetFailed_parts()).compareTo(other.isSetFailed_parts());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(failed_parts, other.failed_parts);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLatency_in_us()).compareTo(other.isSetLatency_in_us());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(latency_in_us, other.latency_in_us);
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
        case FAILED_PARTS:
          if (field.type == TType.LIST) {
            {
              TList _list0 = iprot.readListBegin();
              this.failed_parts = new ArrayList<PartitionResult>(Math.max(0, _list0.size));
              for (int _i1 = 0; 
                   (_list0.size < 0) ? iprot.peekList() : (_i1 < _list0.size); 
                   ++_i1)
              {
                PartitionResult _elem2;
                _elem2 = new PartitionResult();
                _elem2.read(iprot);
                this.failed_parts.add(_elem2);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case LATENCY_IN_US:
          if (field.type == TType.I32) {
            this.latency_in_us = iprot.readI32();
            setLatency_in_usIsSet(true);
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
    if (!isSetLatency_in_us()) {
      throw new TProtocolException("Required field 'latency_in_us' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.failed_parts != null) {
      oprot.writeFieldBegin(FAILED_PARTS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.failed_parts.size()));
        for (PartitionResult _iter3 : this.failed_parts)        {
          _iter3.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(LATENCY_IN_US_FIELD_DESC);
    oprot.writeI32(this.latency_in_us);
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
    StringBuilder sb = new StringBuilder("ResponseCommon");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("failed_parts");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getFailed_parts() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getFailed_parts(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("latency_in_us");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getLatency_in_us(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    if (failed_parts == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'failed_parts' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'latency_in_us' because it's a primitive and you chose the non-beans generator.
    // check that fields of type enum have valid values
  }

}

