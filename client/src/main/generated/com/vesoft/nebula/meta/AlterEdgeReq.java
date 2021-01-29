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
public class AlterEdgeReq implements TBase, java.io.Serializable, Cloneable, Comparable<AlterEdgeReq> {
  private static final TStruct STRUCT_DESC = new TStruct("AlterEdgeReq");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField EDGE_NAME_FIELD_DESC = new TField("edge_name", TType.STRING, (short)2);
  private static final TField EDGE_ITEMS_FIELD_DESC = new TField("edge_items", TType.LIST, (short)3);
  private static final TField SCHEMA_PROP_FIELD_DESC = new TField("schema_prop", TType.STRUCT, (short)4);

  public int space_id;
  public byte[] edge_name;
  public List<AlterSchemaItem> edge_items;
  public SchemaProp schema_prop;
  public static final int SPACE_ID = 1;
  public static final int EDGE_NAME = 2;
  public static final int EDGE_ITEMS = 3;
  public static final int SCHEMA_PROP = 4;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(EDGE_NAME, new FieldMetaData("edge_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(EDGE_ITEMS, new FieldMetaData("edge_items", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, AlterSchemaItem.class))));
    tmpMetaDataMap.put(SCHEMA_PROP, new FieldMetaData("schema_prop", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, SchemaProp.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(AlterEdgeReq.class, metaDataMap);
  }

  public AlterEdgeReq() {
  }

  public AlterEdgeReq(
    int space_id,
    byte[] edge_name,
    List<AlterSchemaItem> edge_items,
    SchemaProp schema_prop)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.edge_name = edge_name;
    this.edge_items = edge_items;
    this.schema_prop = schema_prop;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AlterEdgeReq(AlterEdgeReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetEdge_name()) {
      this.edge_name = TBaseHelper.deepCopy(other.edge_name);
    }
    if (other.isSetEdge_items()) {
      this.edge_items = TBaseHelper.deepCopy(other.edge_items);
    }
    if (other.isSetSchema_prop()) {
      this.schema_prop = TBaseHelper.deepCopy(other.schema_prop);
    }
  }

  public AlterEdgeReq deepCopy() {
    return new AlterEdgeReq(this);
  }

  @Deprecated
  public AlterEdgeReq clone() {
    return new AlterEdgeReq(this);
  }

  public int  getSpace_id() {
    return this.space_id;
  }

  public AlterEdgeReq setSpace_id(int space_id) {
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

  public byte[]  getEdge_name() {
    return this.edge_name;
  }

  public AlterEdgeReq setEdge_name(byte[] edge_name) {
    this.edge_name = edge_name;
    return this;
  }

  public void unsetEdge_name() {
    this.edge_name = null;
  }

  // Returns true if field edge_name is set (has been assigned a value) and false otherwise
  public boolean isSetEdge_name() {
    return this.edge_name != null;
  }

  public void setEdge_nameIsSet(boolean value) {
    if (!value) {
      this.edge_name = null;
    }
  }

  public List<AlterSchemaItem>  getEdge_items() {
    return this.edge_items;
  }

  public AlterEdgeReq setEdge_items(List<AlterSchemaItem> edge_items) {
    this.edge_items = edge_items;
    return this;
  }

  public void unsetEdge_items() {
    this.edge_items = null;
  }

  // Returns true if field edge_items is set (has been assigned a value) and false otherwise
  public boolean isSetEdge_items() {
    return this.edge_items != null;
  }

  public void setEdge_itemsIsSet(boolean value) {
    if (!value) {
      this.edge_items = null;
    }
  }

  public SchemaProp  getSchema_prop() {
    return this.schema_prop;
  }

  public AlterEdgeReq setSchema_prop(SchemaProp schema_prop) {
    this.schema_prop = schema_prop;
    return this;
  }

  public void unsetSchema_prop() {
    this.schema_prop = null;
  }

  // Returns true if field schema_prop is set (has been assigned a value) and false otherwise
  public boolean isSetSchema_prop() {
    return this.schema_prop != null;
  }

  public void setSchema_propIsSet(boolean value) {
    if (!value) {
      this.schema_prop = null;
    }
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

    case EDGE_NAME:
      if (value == null) {
        unsetEdge_name();
      } else {
        setEdge_name((byte[])value);
      }
      break;

    case EDGE_ITEMS:
      if (value == null) {
        unsetEdge_items();
      } else {
        setEdge_items((List<AlterSchemaItem>)value);
      }
      break;

    case SCHEMA_PROP:
      if (value == null) {
        unsetSchema_prop();
      } else {
        setSchema_prop((SchemaProp)value);
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

    case EDGE_NAME:
      return getEdge_name();

    case EDGE_ITEMS:
      return getEdge_items();

    case SCHEMA_PROP:
      return getSchema_prop();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return isSetSpace_id();
    case EDGE_NAME:
      return isSetEdge_name();
    case EDGE_ITEMS:
      return isSetEdge_items();
    case SCHEMA_PROP:
      return isSetSchema_prop();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof AlterEdgeReq)
      return this.equals((AlterEdgeReq)that);
    return false;
  }

  public boolean equals(AlterEdgeReq that) {
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

    boolean this_present_edge_name = true && this.isSetEdge_name();
    boolean that_present_edge_name = true && that.isSetEdge_name();
    if (this_present_edge_name || that_present_edge_name) {
      if (!(this_present_edge_name && that_present_edge_name))
        return false;
      if (!TBaseHelper.equalsSlow(this.edge_name, that.edge_name))
        return false;
    }

    boolean this_present_edge_items = true && this.isSetEdge_items();
    boolean that_present_edge_items = true && that.isSetEdge_items();
    if (this_present_edge_items || that_present_edge_items) {
      if (!(this_present_edge_items && that_present_edge_items))
        return false;
      if (!TBaseHelper.equalsNobinary(this.edge_items, that.edge_items))
        return false;
    }

    boolean this_present_schema_prop = true && this.isSetSchema_prop();
    boolean that_present_schema_prop = true && that.isSetSchema_prop();
    if (this_present_schema_prop || that_present_schema_prop) {
      if (!(this_present_schema_prop && that_present_schema_prop))
        return false;
      if (!TBaseHelper.equalsNobinary(this.schema_prop, that.schema_prop))
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

    boolean present_edge_name = true && (isSetEdge_name());
    builder.append(present_edge_name);
    if (present_edge_name)
      builder.append(edge_name);

    boolean present_edge_items = true && (isSetEdge_items());
    builder.append(present_edge_items);
    if (present_edge_items)
      builder.append(edge_items);

    boolean present_schema_prop = true && (isSetSchema_prop());
    builder.append(present_schema_prop);
    if (present_schema_prop)
      builder.append(schema_prop);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(AlterEdgeReq other) {
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
    lastComparison = Boolean.valueOf(isSetEdge_name()).compareTo(other.isSetEdge_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(edge_name, other.edge_name);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetEdge_items()).compareTo(other.isSetEdge_items());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(edge_items, other.edge_items);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetSchema_prop()).compareTo(other.isSetSchema_prop());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(schema_prop, other.schema_prop);
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
        case EDGE_NAME:
          if (field.type == TType.STRING) {
            this.edge_name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case EDGE_ITEMS:
          if (field.type == TType.LIST) {
            {
              TList _list73 = iprot.readListBegin();
              this.edge_items = new ArrayList<AlterSchemaItem>(Math.max(0, _list73.size));
              for (int _i74 = 0; 
                   (_list73.size < 0) ? iprot.peekList() : (_i74 < _list73.size); 
                   ++_i74)
              {
                AlterSchemaItem _elem75;
                _elem75 = new AlterSchemaItem();
                _elem75.read(iprot);
                this.edge_items.add(_elem75);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case SCHEMA_PROP:
          if (field.type == TType.STRUCT) {
            this.schema_prop = new SchemaProp();
            this.schema_prop.read(iprot);
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
    if (this.edge_name != null) {
      oprot.writeFieldBegin(EDGE_NAME_FIELD_DESC);
      oprot.writeBinary(this.edge_name);
      oprot.writeFieldEnd();
    }
    if (this.edge_items != null) {
      oprot.writeFieldBegin(EDGE_ITEMS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.edge_items.size()));
        for (AlterSchemaItem _iter76 : this.edge_items)        {
          _iter76.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.schema_prop != null) {
      oprot.writeFieldBegin(SCHEMA_PROP_FIELD_DESC);
      this.schema_prop.write(oprot);
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
    StringBuilder sb = new StringBuilder("AlterEdgeReq");
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
    sb.append("edge_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getEdge_name() == null) {
      sb.append("null");
    } else {
        int __edge_name_size = Math.min(this. getEdge_name().length, 128);
        for (int i = 0; i < __edge_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getEdge_name()[i]).length() > 1 ? Integer.toHexString(this. getEdge_name()[i]).substring(Integer.toHexString(this. getEdge_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getEdge_name()[i]).toUpperCase());
        }
        if (this. getEdge_name().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("edge_items");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getEdge_items() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getEdge_items(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("schema_prop");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getSchema_prop() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getSchema_prop(), indent + 1, prettyPrint));
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

