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
public class CreateEdgeIndexReq implements TBase, java.io.Serializable, Cloneable, Comparable<CreateEdgeIndexReq> {
  private static final TStruct STRUCT_DESC = new TStruct("CreateEdgeIndexReq");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField INDEX_NAME_FIELD_DESC = new TField("index_name", TType.STRING, (short)2);
  private static final TField EDGE_NAME_FIELD_DESC = new TField("edge_name", TType.STRING, (short)3);
  private static final TField FIELDS_FIELD_DESC = new TField("fields", TType.LIST, (short)4);
  private static final TField IF_NOT_EXISTS_FIELD_DESC = new TField("if_not_exists", TType.BOOL, (short)5);

  public int space_id;
  public byte[] index_name;
  public byte[] edge_name;
  public List<IndexFieldDef> fields;
  public boolean if_not_exists;
  public static final int SPACE_ID = 1;
  public static final int INDEX_NAME = 2;
  public static final int EDGE_NAME = 3;
  public static final int FIELDS = 4;
  public static final int IF_NOT_EXISTS = 5;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __IF_NOT_EXISTS_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(INDEX_NAME, new FieldMetaData("index_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(EDGE_NAME, new FieldMetaData("edge_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(FIELDS, new FieldMetaData("fields", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, IndexFieldDef.class))));
    tmpMetaDataMap.put(IF_NOT_EXISTS, new FieldMetaData("if_not_exists", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(CreateEdgeIndexReq.class, metaDataMap);
  }

  public CreateEdgeIndexReq() {
  }

  public CreateEdgeIndexReq(
    int space_id,
    byte[] index_name,
    byte[] edge_name,
    List<IndexFieldDef> fields,
    boolean if_not_exists)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.index_name = index_name;
    this.edge_name = edge_name;
    this.fields = fields;
    this.if_not_exists = if_not_exists;
    setIf_not_existsIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CreateEdgeIndexReq(CreateEdgeIndexReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetIndex_name()) {
      this.index_name = TBaseHelper.deepCopy(other.index_name);
    }
    if (other.isSetEdge_name()) {
      this.edge_name = TBaseHelper.deepCopy(other.edge_name);
    }
    if (other.isSetFields()) {
      this.fields = TBaseHelper.deepCopy(other.fields);
    }
    this.if_not_exists = TBaseHelper.deepCopy(other.if_not_exists);
  }

  public CreateEdgeIndexReq deepCopy() {
    return new CreateEdgeIndexReq(this);
  }

  @Deprecated
  public CreateEdgeIndexReq clone() {
    return new CreateEdgeIndexReq(this);
  }

  public int  getSpace_id() {
    return this.space_id;
  }

  public CreateEdgeIndexReq setSpace_id(int space_id) {
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

  public byte[]  getIndex_name() {
    return this.index_name;
  }

  public CreateEdgeIndexReq setIndex_name(byte[] index_name) {
    this.index_name = index_name;
    return this;
  }

  public void unsetIndex_name() {
    this.index_name = null;
  }

  // Returns true if field index_name is set (has been assigned a value) and false otherwise
  public boolean isSetIndex_name() {
    return this.index_name != null;
  }

  public void setIndex_nameIsSet(boolean value) {
    if (!value) {
      this.index_name = null;
    }
  }

  public byte[]  getEdge_name() {
    return this.edge_name;
  }

  public CreateEdgeIndexReq setEdge_name(byte[] edge_name) {
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

  public List<IndexFieldDef>  getFields() {
    return this.fields;
  }

  public CreateEdgeIndexReq setFields(List<IndexFieldDef> fields) {
    this.fields = fields;
    return this;
  }

  public void unsetFields() {
    this.fields = null;
  }

  // Returns true if field fields is set (has been assigned a value) and false otherwise
  public boolean isSetFields() {
    return this.fields != null;
  }

  public void setFieldsIsSet(boolean value) {
    if (!value) {
      this.fields = null;
    }
  }

  public boolean  isIf_not_exists() {
    return this.if_not_exists;
  }

  public CreateEdgeIndexReq setIf_not_exists(boolean if_not_exists) {
    this.if_not_exists = if_not_exists;
    setIf_not_existsIsSet(true);
    return this;
  }

  public void unsetIf_not_exists() {
    __isset_bit_vector.clear(__IF_NOT_EXISTS_ISSET_ID);
  }

  // Returns true if field if_not_exists is set (has been assigned a value) and false otherwise
  public boolean isSetIf_not_exists() {
    return __isset_bit_vector.get(__IF_NOT_EXISTS_ISSET_ID);
  }

  public void setIf_not_existsIsSet(boolean value) {
    __isset_bit_vector.set(__IF_NOT_EXISTS_ISSET_ID, value);
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

    case INDEX_NAME:
      if (value == null) {
        unsetIndex_name();
      } else {
        setIndex_name((byte[])value);
      }
      break;

    case EDGE_NAME:
      if (value == null) {
        unsetEdge_name();
      } else {
        setEdge_name((byte[])value);
      }
      break;

    case FIELDS:
      if (value == null) {
        unsetFields();
      } else {
        setFields((List<IndexFieldDef>)value);
      }
      break;

    case IF_NOT_EXISTS:
      if (value == null) {
        unsetIf_not_exists();
      } else {
        setIf_not_exists((Boolean)value);
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

    case INDEX_NAME:
      return getIndex_name();

    case EDGE_NAME:
      return getEdge_name();

    case FIELDS:
      return getFields();

    case IF_NOT_EXISTS:
      return new Boolean(isIf_not_exists());

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return isSetSpace_id();
    case INDEX_NAME:
      return isSetIndex_name();
    case EDGE_NAME:
      return isSetEdge_name();
    case FIELDS:
      return isSetFields();
    case IF_NOT_EXISTS:
      return isSetIf_not_exists();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof CreateEdgeIndexReq)
      return this.equals((CreateEdgeIndexReq)that);
    return false;
  }

  public boolean equals(CreateEdgeIndexReq that) {
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

    boolean this_present_index_name = true && this.isSetIndex_name();
    boolean that_present_index_name = true && that.isSetIndex_name();
    if (this_present_index_name || that_present_index_name) {
      if (!(this_present_index_name && that_present_index_name))
        return false;
      if (!TBaseHelper.equalsSlow(this.index_name, that.index_name))
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

    boolean this_present_fields = true && this.isSetFields();
    boolean that_present_fields = true && that.isSetFields();
    if (this_present_fields || that_present_fields) {
      if (!(this_present_fields && that_present_fields))
        return false;
      if (!TBaseHelper.equalsNobinary(this.fields, that.fields))
        return false;
    }

    boolean this_present_if_not_exists = true;
    boolean that_present_if_not_exists = true;
    if (this_present_if_not_exists || that_present_if_not_exists) {
      if (!(this_present_if_not_exists && that_present_if_not_exists))
        return false;
      if (!TBaseHelper.equalsNobinary(this.if_not_exists, that.if_not_exists))
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

    boolean present_index_name = true && (isSetIndex_name());
    builder.append(present_index_name);
    if (present_index_name)
      builder.append(index_name);

    boolean present_edge_name = true && (isSetEdge_name());
    builder.append(present_edge_name);
    if (present_edge_name)
      builder.append(edge_name);

    boolean present_fields = true && (isSetFields());
    builder.append(present_fields);
    if (present_fields)
      builder.append(fields);

    boolean present_if_not_exists = true;
    builder.append(present_if_not_exists);
    if (present_if_not_exists)
      builder.append(if_not_exists);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(CreateEdgeIndexReq other) {
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
    lastComparison = Boolean.valueOf(isSetIndex_name()).compareTo(other.isSetIndex_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(index_name, other.index_name);
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
    lastComparison = Boolean.valueOf(isSetFields()).compareTo(other.isSetFields());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(fields, other.fields);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetIf_not_exists()).compareTo(other.isSetIf_not_exists());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(if_not_exists, other.if_not_exists);
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
        case INDEX_NAME:
          if (field.type == TType.STRING) {
            this.index_name = iprot.readBinary();
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
        case FIELDS:
          if (field.type == TType.LIST) {
            {
              TList _list134 = iprot.readListBegin();
              this.fields = new ArrayList<IndexFieldDef>(Math.max(0, _list134.size));
              for (int _i135 = 0; 
                   (_list134.size < 0) ? iprot.peekList() : (_i135 < _list134.size); 
                   ++_i135)
              {
                IndexFieldDef _elem136;
                _elem136 = new IndexFieldDef();
                _elem136.read(iprot);
                this.fields.add(_elem136);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case IF_NOT_EXISTS:
          if (field.type == TType.BOOL) {
            this.if_not_exists = iprot.readBool();
            setIf_not_existsIsSet(true);
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
    if (this.index_name != null) {
      oprot.writeFieldBegin(INDEX_NAME_FIELD_DESC);
      oprot.writeBinary(this.index_name);
      oprot.writeFieldEnd();
    }
    if (this.edge_name != null) {
      oprot.writeFieldBegin(EDGE_NAME_FIELD_DESC);
      oprot.writeBinary(this.edge_name);
      oprot.writeFieldEnd();
    }
    if (this.fields != null) {
      oprot.writeFieldBegin(FIELDS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.fields.size()));
        for (IndexFieldDef _iter137 : this.fields)        {
          _iter137.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(IF_NOT_EXISTS_FIELD_DESC);
    oprot.writeBool(this.if_not_exists);
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
    StringBuilder sb = new StringBuilder("CreateEdgeIndexReq");
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
    sb.append("index_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getIndex_name() == null) {
      sb.append("null");
    } else {
        int __index_name_size = Math.min(this. getIndex_name().length, 128);
        for (int i = 0; i < __index_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getIndex_name()[i]).length() > 1 ? Integer.toHexString(this. getIndex_name()[i]).substring(Integer.toHexString(this. getIndex_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getIndex_name()[i]).toUpperCase());
        }
        if (this. getIndex_name().length > 128) sb.append(" ...");
    }
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
    sb.append("fields");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getFields() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getFields(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("if_not_exists");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. isIf_not_exists(), indent + 1, prettyPrint));
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

