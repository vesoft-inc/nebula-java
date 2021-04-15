/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.graph;

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
public class PlanDescription implements TBase, java.io.Serializable, Cloneable, Comparable<PlanDescription> {
  private static final TStruct STRUCT_DESC = new TStruct("PlanDescription");
  private static final TField PLAN_NODE_DESCS_FIELD_DESC = new TField("plan_node_descs", TType.LIST, (short)1);
  private static final TField NODE_INDEX_MAP_FIELD_DESC = new TField("node_index_map", TType.MAP, (short)2);
  private static final TField FORMAT_FIELD_DESC = new TField("format", TType.STRING, (short)3);
  private static final TField OPTIMIZE_TIME_IN_US_FIELD_DESC = new TField("optimize_time_in_us", TType.I32, (short)4);

  public List<PlanNodeDescription> plan_node_descs;
  public Map<Long,Long> node_index_map;
  public byte[] format;
  public int optimize_time_in_us;
  public static final int PLAN_NODE_DESCS = 1;
  public static final int NODE_INDEX_MAP = 2;
  public static final int FORMAT = 3;
  public static final int OPTIMIZE_TIME_IN_US = 4;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __OPTIMIZE_TIME_IN_US_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(PLAN_NODE_DESCS, new FieldMetaData("plan_node_descs", TFieldRequirementType.REQUIRED, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, PlanNodeDescription.class))));
    tmpMetaDataMap.put(NODE_INDEX_MAP, new FieldMetaData("node_index_map", TFieldRequirementType.REQUIRED, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.I64), 
            new FieldValueMetaData(TType.I64))));
    tmpMetaDataMap.put(FORMAT, new FieldMetaData("format", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(OPTIMIZE_TIME_IN_US, new FieldMetaData("optimize_time_in_us", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(PlanDescription.class, metaDataMap);
  }

  public PlanDescription() {
  }

  public PlanDescription(
    List<PlanNodeDescription> plan_node_descs,
    Map<Long,Long> node_index_map,
    byte[] format,
    int optimize_time_in_us)
  {
    this();
    this.plan_node_descs = plan_node_descs;
    this.node_index_map = node_index_map;
    this.format = format;
    this.optimize_time_in_us = optimize_time_in_us;
    setOptimize_time_in_usIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PlanDescription(PlanDescription other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetPlan_node_descs()) {
      this.plan_node_descs = TBaseHelper.deepCopy(other.plan_node_descs);
    }
    if (other.isSetNode_index_map()) {
      this.node_index_map = TBaseHelper.deepCopy(other.node_index_map);
    }
    if (other.isSetFormat()) {
      this.format = TBaseHelper.deepCopy(other.format);
    }
    this.optimize_time_in_us = TBaseHelper.deepCopy(other.optimize_time_in_us);
  }

  public PlanDescription deepCopy() {
    return new PlanDescription(this);
  }

  @Deprecated
  public PlanDescription clone() {
    return new PlanDescription(this);
  }

  public List<PlanNodeDescription>  getPlan_node_descs() {
    return this.plan_node_descs;
  }

  public PlanDescription setPlan_node_descs(List<PlanNodeDescription> plan_node_descs) {
    this.plan_node_descs = plan_node_descs;
    return this;
  }

  public void unsetPlan_node_descs() {
    this.plan_node_descs = null;
  }

  // Returns true if field plan_node_descs is set (has been assigned a value) and false otherwise
  public boolean isSetPlan_node_descs() {
    return this.plan_node_descs != null;
  }

  public void setPlan_node_descsIsSet(boolean value) {
    if (!value) {
      this.plan_node_descs = null;
    }
  }

  public Map<Long,Long>  getNode_index_map() {
    return this.node_index_map;
  }

  public PlanDescription setNode_index_map(Map<Long,Long> node_index_map) {
    this.node_index_map = node_index_map;
    return this;
  }

  public void unsetNode_index_map() {
    this.node_index_map = null;
  }

  // Returns true if field node_index_map is set (has been assigned a value) and false otherwise
  public boolean isSetNode_index_map() {
    return this.node_index_map != null;
  }

  public void setNode_index_mapIsSet(boolean value) {
    if (!value) {
      this.node_index_map = null;
    }
  }

  public byte[]  getFormat() {
    return this.format;
  }

  public PlanDescription setFormat(byte[] format) {
    this.format = format;
    return this;
  }

  public void unsetFormat() {
    this.format = null;
  }

  // Returns true if field format is set (has been assigned a value) and false otherwise
  public boolean isSetFormat() {
    return this.format != null;
  }

  public void setFormatIsSet(boolean value) {
    if (!value) {
      this.format = null;
    }
  }

  public int  getOptimize_time_in_us() {
    return this.optimize_time_in_us;
  }

  public PlanDescription setOptimize_time_in_us(int optimize_time_in_us) {
    this.optimize_time_in_us = optimize_time_in_us;
    setOptimize_time_in_usIsSet(true);
    return this;
  }

  public void unsetOptimize_time_in_us() {
    __isset_bit_vector.clear(__OPTIMIZE_TIME_IN_US_ISSET_ID);
  }

  // Returns true if field optimize_time_in_us is set (has been assigned a value) and false otherwise
  public boolean isSetOptimize_time_in_us() {
    return __isset_bit_vector.get(__OPTIMIZE_TIME_IN_US_ISSET_ID);
  }

  public void setOptimize_time_in_usIsSet(boolean value) {
    __isset_bit_vector.set(__OPTIMIZE_TIME_IN_US_ISSET_ID, value);
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case PLAN_NODE_DESCS:
      if (value == null) {
        unsetPlan_node_descs();
      } else {
        setPlan_node_descs((List<PlanNodeDescription>)value);
      }
      break;

    case NODE_INDEX_MAP:
      if (value == null) {
        unsetNode_index_map();
      } else {
        setNode_index_map((Map<Long,Long>)value);
      }
      break;

    case FORMAT:
      if (value == null) {
        unsetFormat();
      } else {
        setFormat((byte[])value);
      }
      break;

    case OPTIMIZE_TIME_IN_US:
      if (value == null) {
        unsetOptimize_time_in_us();
      } else {
        setOptimize_time_in_us((Integer)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case PLAN_NODE_DESCS:
      return getPlan_node_descs();

    case NODE_INDEX_MAP:
      return getNode_index_map();

    case FORMAT:
      return getFormat();

    case OPTIMIZE_TIME_IN_US:
      return new Integer(getOptimize_time_in_us());

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case PLAN_NODE_DESCS:
      return isSetPlan_node_descs();
    case NODE_INDEX_MAP:
      return isSetNode_index_map();
    case FORMAT:
      return isSetFormat();
    case OPTIMIZE_TIME_IN_US:
      return isSetOptimize_time_in_us();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof PlanDescription)
      return this.equals((PlanDescription)that);
    return false;
  }

  public boolean equals(PlanDescription that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_plan_node_descs = true && this.isSetPlan_node_descs();
    boolean that_present_plan_node_descs = true && that.isSetPlan_node_descs();
    if (this_present_plan_node_descs || that_present_plan_node_descs) {
      if (!(this_present_plan_node_descs && that_present_plan_node_descs))
        return false;
      if (!TBaseHelper.equalsNobinary(this.plan_node_descs, that.plan_node_descs))
        return false;
    }

    boolean this_present_node_index_map = true && this.isSetNode_index_map();
    boolean that_present_node_index_map = true && that.isSetNode_index_map();
    if (this_present_node_index_map || that_present_node_index_map) {
      if (!(this_present_node_index_map && that_present_node_index_map))
        return false;
      if (!TBaseHelper.equalsNobinary(this.node_index_map, that.node_index_map))
        return false;
    }

    boolean this_present_format = true && this.isSetFormat();
    boolean that_present_format = true && that.isSetFormat();
    if (this_present_format || that_present_format) {
      if (!(this_present_format && that_present_format))
        return false;
      if (!TBaseHelper.equalsSlow(this.format, that.format))
        return false;
    }

    boolean this_present_optimize_time_in_us = true;
    boolean that_present_optimize_time_in_us = true;
    if (this_present_optimize_time_in_us || that_present_optimize_time_in_us) {
      if (!(this_present_optimize_time_in_us && that_present_optimize_time_in_us))
        return false;
      if (!TBaseHelper.equalsNobinary(this.optimize_time_in_us, that.optimize_time_in_us))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_plan_node_descs = true && (isSetPlan_node_descs());
    builder.append(present_plan_node_descs);
    if (present_plan_node_descs)
      builder.append(plan_node_descs);

    boolean present_node_index_map = true && (isSetNode_index_map());
    builder.append(present_node_index_map);
    if (present_node_index_map)
      builder.append(node_index_map);

    boolean present_format = true && (isSetFormat());
    builder.append(present_format);
    if (present_format)
      builder.append(format);

    boolean present_optimize_time_in_us = true;
    builder.append(present_optimize_time_in_us);
    if (present_optimize_time_in_us)
      builder.append(optimize_time_in_us);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(PlanDescription other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetPlan_node_descs()).compareTo(other.isSetPlan_node_descs());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(plan_node_descs, other.plan_node_descs);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetNode_index_map()).compareTo(other.isSetNode_index_map());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(node_index_map, other.node_index_map);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetFormat()).compareTo(other.isSetFormat());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(format, other.format);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetOptimize_time_in_us()).compareTo(other.isSetOptimize_time_in_us());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(optimize_time_in_us, other.optimize_time_in_us);
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
        case PLAN_NODE_DESCS:
          if (field.type == TType.LIST) {
            {
              TList _list17 = iprot.readListBegin();
              this.plan_node_descs = new ArrayList<PlanNodeDescription>(Math.max(0, _list17.size));
              for (int _i18 = 0; 
                   (_list17.size < 0) ? iprot.peekList() : (_i18 < _list17.size); 
                   ++_i18)
              {
                PlanNodeDescription _elem19;
                _elem19 = new PlanNodeDescription();
                _elem19.read(iprot);
                this.plan_node_descs.add(_elem19);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case NODE_INDEX_MAP:
          if (field.type == TType.MAP) {
            {
              TMap _map20 = iprot.readMapBegin();
              this.node_index_map = new HashMap<Long,Long>(Math.max(0, 2*_map20.size));
              for (int _i21 = 0; 
                   (_map20.size < 0) ? iprot.peekMap() : (_i21 < _map20.size); 
                   ++_i21)
              {
                long _key22;
                long _val23;
                _key22 = iprot.readI64();
                _val23 = iprot.readI64();
                this.node_index_map.put(_key22, _val23);
              }
              iprot.readMapEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case FORMAT:
          if (field.type == TType.STRING) {
            this.format = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case OPTIMIZE_TIME_IN_US:
          if (field.type == TType.I32) {
            this.optimize_time_in_us = iprot.readI32();
            setOptimize_time_in_usIsSet(true);
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
    if (!isSetOptimize_time_in_us()) {
      throw new TProtocolException("Required field 'optimize_time_in_us' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.plan_node_descs != null) {
      oprot.writeFieldBegin(PLAN_NODE_DESCS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.plan_node_descs.size()));
        for (PlanNodeDescription _iter24 : this.plan_node_descs)        {
          _iter24.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.node_index_map != null) {
      oprot.writeFieldBegin(NODE_INDEX_MAP_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.I64, TType.I64, this.node_index_map.size()));
        for (Map.Entry<Long, Long> _iter25 : this.node_index_map.entrySet())        {
          oprot.writeI64(_iter25.getKey());
          oprot.writeI64(_iter25.getValue());
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.format != null) {
      oprot.writeFieldBegin(FORMAT_FIELD_DESC);
      oprot.writeBinary(this.format);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(OPTIMIZE_TIME_IN_US_FIELD_DESC);
    oprot.writeI32(this.optimize_time_in_us);
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
    StringBuilder sb = new StringBuilder("PlanDescription");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("plan_node_descs");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getPlan_node_descs() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getPlan_node_descs(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("node_index_map");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getNode_index_map() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getNode_index_map(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("format");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getFormat() == null) {
      sb.append("null");
    } else {
        int __format_size = Math.min(this. getFormat().length, 128);
        for (int i = 0; i < __format_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getFormat()[i]).length() > 1 ? Integer.toHexString(this. getFormat()[i]).substring(Integer.toHexString(this. getFormat()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getFormat()[i]).toUpperCase());
        }
        if (this. getFormat().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("optimize_time_in_us");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getOptimize_time_in_us(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    if (plan_node_descs == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'plan_node_descs' was not present! Struct: " + toString());
    }
    if (node_index_map == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'node_index_map' was not present! Struct: " + toString());
    }
    if (format == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'format' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'optimize_time_in_us' because it's a primitive and you chose the non-beans generator.
    // check that fields of type enum have valid values
  }

}
