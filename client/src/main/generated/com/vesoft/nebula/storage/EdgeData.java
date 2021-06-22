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
public class EdgeData implements TBase, java.io.Serializable, Cloneable, Comparable<EdgeData> {
  private static final TStruct STRUCT_DESC = new TStruct("EdgeData");
  private static final TField TYPE_FIELD_DESC = new TField("type", TType.I32, (short)1);
  private static final TField EDGES_FIELD_DESC = new TField("edges", TType.LIST, (short)3);

  public int type;
  public List<IdAndProp> edges;
  public static final int TYPE = 1;
  public static final int EDGES = 3;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __TYPE_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(TYPE, new FieldMetaData("type", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(EDGES, new FieldMetaData("edges", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, IdAndProp.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(EdgeData.class, metaDataMap);
  }

  public EdgeData() {
  }

  public EdgeData(
    int type,
    List<IdAndProp> edges)
  {
    this();
    this.type = type;
    setTypeIsSet(true);
    this.edges = edges;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EdgeData(EdgeData other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.type = TBaseHelper.deepCopy(other.type);
    if (other.isSetEdges()) {
      this.edges = TBaseHelper.deepCopy(other.edges);
    }
  }

  public EdgeData deepCopy() {
    return new EdgeData(this);
  }

  @Deprecated
  public EdgeData clone() {
    return new EdgeData(this);
  }

  public int  getType() {
    return this.type;
  }

  public EdgeData setType(int type) {
    this.type = type;
    setTypeIsSet(true);
    return this;
  }

  public void unsetType() {
    __isset_bit_vector.clear(__TYPE_ISSET_ID);
  }

  // Returns true if field type is set (has been assigned a value) and false otherwise
  public boolean isSetType() {
    return __isset_bit_vector.get(__TYPE_ISSET_ID);
  }

  public void setTypeIsSet(boolean value) {
    __isset_bit_vector.set(__TYPE_ISSET_ID, value);
  }

  public List<IdAndProp>  getEdges() {
    return this.edges;
  }

  public EdgeData setEdges(List<IdAndProp> edges) {
    this.edges = edges;
    return this;
  }

  public void unsetEdges() {
    this.edges = null;
  }

  // Returns true if field edges is set (has been assigned a value) and false otherwise
  public boolean isSetEdges() {
    return this.edges != null;
  }

  public void setEdgesIsSet(boolean value) {
    if (!value) {
      this.edges = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((Integer)value);
      }
      break;

    case EDGES:
      if (value == null) {
        unsetEdges();
      } else {
        setEdges((List<IdAndProp>)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case TYPE:
      return new Integer(getType());

    case EDGES:
      return getEdges();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case TYPE:
      return isSetType();
    case EDGES:
      return isSetEdges();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof EdgeData)
      return this.equals((EdgeData)that);
    return false;
  }

  public boolean equals(EdgeData that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_type = true;
    boolean that_present_type = true;
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (!TBaseHelper.equalsNobinary(this.type, that.type))
        return false;
    }

    boolean this_present_edges = true && this.isSetEdges();
    boolean that_present_edges = true && that.isSetEdges();
    if (this_present_edges || that_present_edges) {
      if (!(this_present_edges && that_present_edges))
        return false;
      if (!TBaseHelper.equalsNobinary(this.edges, that.edges))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_type = true;
    builder.append(present_type);
    if (present_type)
      builder.append(type);

    boolean present_edges = true && (isSetEdges());
    builder.append(present_edges);
    if (present_edges)
      builder.append(edges);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(EdgeData other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetType()).compareTo(other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(type, other.type);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetEdges()).compareTo(other.isSetEdges());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(edges, other.edges);
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
        case TYPE:
          if (field.type == TType.I32) {
            this.type = iprot.readI32();
            setTypeIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case EDGES:
          if (field.type == TType.LIST) {
            {
              TList _list0 = iprot.readListBegin();
              this.edges = new ArrayList<IdAndProp>(Math.max(0, _list0.size));
              for (int _i1 = 0; 
                   (_list0.size < 0) ? iprot.peekList() : (_i1 < _list0.size); 
                   ++_i1)
              {
                IdAndProp _elem2;
                _elem2 = new IdAndProp();
                _elem2.read(iprot);
                this.edges.add(_elem2);
              }
              iprot.readListEnd();
            }
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
    oprot.writeFieldBegin(TYPE_FIELD_DESC);
    oprot.writeI32(this.type);
    oprot.writeFieldEnd();
    if (this.edges != null) {
      oprot.writeFieldBegin(EDGES_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.edges.size()));
        for (IdAndProp _iter3 : this.edges)        {
          _iter3.write(oprot);
        }
        oprot.writeListEnd();
      }
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
    StringBuilder sb = new StringBuilder("EdgeData");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("type");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getType(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("edges");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getEdges() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getEdges(), indent + 1, prettyPrint));
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
