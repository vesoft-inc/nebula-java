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
public class AddVerticesRequest implements TBase, java.io.Serializable, Cloneable, Comparable<AddVerticesRequest> {
  private static final TStruct STRUCT_DESC = new TStruct("AddVerticesRequest");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField PARTS_FIELD_DESC = new TField("parts", TType.MAP, (short)2);
  private static final TField OVERWRITABLE_FIELD_DESC = new TField("overwritable", TType.BOOL, (short)3);

  public int space_id;
  public Map<Integer,List<Vertex>> parts;
  public boolean overwritable;
  public static final int SPACE_ID = 1;
  public static final int PARTS = 2;
  public static final int OVERWRITABLE = 3;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __OVERWRITABLE_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(PARTS, new FieldMetaData("parts", TFieldRequirementType.DEFAULT, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.I32), 
            new ListMetaData(TType.LIST, 
                new StructMetaData(TType.STRUCT, Vertex.class)))));
    tmpMetaDataMap.put(OVERWRITABLE, new FieldMetaData("overwritable", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(AddVerticesRequest.class, metaDataMap);
  }

  public AddVerticesRequest() {
  }

  public AddVerticesRequest(
    int space_id,
    Map<Integer,List<Vertex>> parts,
    boolean overwritable)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.parts = parts;
    this.overwritable = overwritable;
    setOverwritableIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AddVerticesRequest(AddVerticesRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetParts()) {
      this.parts = TBaseHelper.deepCopy(other.parts);
    }
    this.overwritable = TBaseHelper.deepCopy(other.overwritable);
  }

  public AddVerticesRequest deepCopy() {
    return new AddVerticesRequest(this);
  }

  @Deprecated
  public AddVerticesRequest clone() {
    return new AddVerticesRequest(this);
  }

  public int  getSpace_id() {
    return this.space_id;
  }

  public AddVerticesRequest setSpace_id(int space_id) {
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

  public Map<Integer,List<Vertex>>  getParts() {
    return this.parts;
  }

  public AddVerticesRequest setParts(Map<Integer,List<Vertex>> parts) {
    this.parts = parts;
    return this;
  }

  public void unsetParts() {
    this.parts = null;
  }

  // Returns true if field parts is set (has been assigned a value) and false otherwise
  public boolean isSetParts() {
    return this.parts != null;
  }

  public void setPartsIsSet(boolean value) {
    if (!value) {
      this.parts = null;
    }
  }

  public boolean  isOverwritable() {
    return this.overwritable;
  }

  public AddVerticesRequest setOverwritable(boolean overwritable) {
    this.overwritable = overwritable;
    setOverwritableIsSet(true);
    return this;
  }

  public void unsetOverwritable() {
    __isset_bit_vector.clear(__OVERWRITABLE_ISSET_ID);
  }

  // Returns true if field overwritable is set (has been assigned a value) and false otherwise
  public boolean isSetOverwritable() {
    return __isset_bit_vector.get(__OVERWRITABLE_ISSET_ID);
  }

  public void setOverwritableIsSet(boolean value) {
    __isset_bit_vector.set(__OVERWRITABLE_ISSET_ID, value);
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

    case PARTS:
      if (value == null) {
        unsetParts();
      } else {
        setParts((Map<Integer,List<Vertex>>)value);
      }
      break;

    case OVERWRITABLE:
      if (value == null) {
        unsetOverwritable();
      } else {
        setOverwritable((Boolean)value);
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

    case PARTS:
      return getParts();

    case OVERWRITABLE:
      return new Boolean(isOverwritable());

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return isSetSpace_id();
    case PARTS:
      return isSetParts();
    case OVERWRITABLE:
      return isSetOverwritable();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof AddVerticesRequest)
      return this.equals((AddVerticesRequest)that);
    return false;
  }

  public boolean equals(AddVerticesRequest that) {
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

    boolean this_present_parts = true && this.isSetParts();
    boolean that_present_parts = true && that.isSetParts();
    if (this_present_parts || that_present_parts) {
      if (!(this_present_parts && that_present_parts))
        return false;
      if (!TBaseHelper.equalsNobinary(this.parts, that.parts))
        return false;
    }

    boolean this_present_overwritable = true;
    boolean that_present_overwritable = true;
    if (this_present_overwritable || that_present_overwritable) {
      if (!(this_present_overwritable && that_present_overwritable))
        return false;
      if (!TBaseHelper.equalsNobinary(this.overwritable, that.overwritable))
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

    boolean present_parts = true && (isSetParts());
    builder.append(present_parts);
    if (present_parts)
      builder.append(parts);

    boolean present_overwritable = true;
    builder.append(present_overwritable);
    if (present_overwritable)
      builder.append(overwritable);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(AddVerticesRequest other) {
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
    lastComparison = Boolean.valueOf(isSetParts()).compareTo(other.isSetParts());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(parts, other.parts);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetOverwritable()).compareTo(other.isSetOverwritable());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(overwritable, other.overwritable);
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
        case PARTS:
          if (field.type == TType.MAP) {
            {
              TMap _map77 = iprot.readMapBegin();
              this.parts = new HashMap<Integer,List<Vertex>>(Math.max(0, 2*_map77.size));
              for (int _i78 = 0; 
                   (_map77.size < 0) ? iprot.peekMap() : (_i78 < _map77.size); 
                   ++_i78)
              {
                int _key79;
                List<Vertex> _val80;
                _key79 = iprot.readI32();
                {
                  TList _list81 = iprot.readListBegin();
                  _val80 = new ArrayList<Vertex>(Math.max(0, _list81.size));
                  for (int _i82 = 0; 
                       (_list81.size < 0) ? iprot.peekList() : (_i82 < _list81.size); 
                       ++_i82)
                  {
                    Vertex _elem83;
                    _elem83 = new Vertex();
                    _elem83.read(iprot);
                    _val80.add(_elem83);
                  }
                  iprot.readListEnd();
                }
                this.parts.put(_key79, _val80);
              }
              iprot.readMapEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case OVERWRITABLE:
          if (field.type == TType.BOOL) {
            this.overwritable = iprot.readBool();
            setOverwritableIsSet(true);
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
    if (this.parts != null) {
      oprot.writeFieldBegin(PARTS_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.I32, TType.LIST, this.parts.size()));
        for (Map.Entry<Integer, List<Vertex>> _iter84 : this.parts.entrySet())        {
          oprot.writeI32(_iter84.getKey());
          {
            oprot.writeListBegin(new TList(TType.STRUCT, _iter84.getValue().size()));
            for (Vertex _iter85 : _iter84.getValue())            {
              _iter85.write(oprot);
            }
            oprot.writeListEnd();
          }
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(OVERWRITABLE_FIELD_DESC);
    oprot.writeBool(this.overwritable);
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
    StringBuilder sb = new StringBuilder("AddVerticesRequest");
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
    sb.append("parts");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getParts() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getParts(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("overwritable");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. isOverwritable(), indent + 1, prettyPrint));
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

