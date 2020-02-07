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
public class DropEdgeReq implements TBase, java.io.Serializable, Cloneable, Comparable<DropEdgeReq> {
  private static final TStruct STRUCT_DESC = new TStruct("DropEdgeReq");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField EDGE_NAME_FIELD_DESC = new TField("edge_name", TType.STRING, (short)2);
  private static final TField IF_EXISTS_FIELD_DESC = new TField("if_exists", TType.BOOL, (short)3);

  public int space_id;
  public String edge_name;
  public boolean if_exists;
  public static final int SPACE_ID = 1;
  public static final int EDGE_NAME = 2;
  public static final int IF_EXISTS = 3;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __IF_EXISTS_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(EDGE_NAME, new FieldMetaData("edge_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(IF_EXISTS, new FieldMetaData("if_exists", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(DropEdgeReq.class, metaDataMap);
  }

  public DropEdgeReq() {
  }

  public DropEdgeReq(
    int space_id,
    String edge_name,
    boolean if_exists)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.edge_name = edge_name;
    this.if_exists = if_exists;
    setIf_existsIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DropEdgeReq(DropEdgeReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetEdge_name()) {
      this.edge_name = TBaseHelper.deepCopy(other.edge_name);
    }
    this.if_exists = TBaseHelper.deepCopy(other.if_exists);
  }

  public DropEdgeReq deepCopy() {
    return new DropEdgeReq(this);
  }

  @Deprecated
  public DropEdgeReq clone() {
    return new DropEdgeReq(this);
  }

  public int  getSpace_id() {
    return this.space_id;
  }

  public DropEdgeReq setSpace_id(int space_id) {
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

  public String  getEdge_name() {
    return this.edge_name;
  }

  public DropEdgeReq setEdge_name(String edge_name) {
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

  public boolean  isIf_exists() {
    return this.if_exists;
  }

  public DropEdgeReq setIf_exists(boolean if_exists) {
    this.if_exists = if_exists;
    setIf_existsIsSet(true);
    return this;
  }

  public void unsetIf_exists() {
    __isset_bit_vector.clear(__IF_EXISTS_ISSET_ID);
  }

  // Returns true if field if_exists is set (has been assigned a value) and false otherwise
  public boolean isSetIf_exists() {
    return __isset_bit_vector.get(__IF_EXISTS_ISSET_ID);
  }

  public void setIf_existsIsSet(boolean value) {
    __isset_bit_vector.set(__IF_EXISTS_ISSET_ID, value);
  }

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
        setEdge_name((String)value);
      }
      break;

    case IF_EXISTS:
      if (value == null) {
        unsetIf_exists();
      } else {
        setIf_exists((Boolean)value);
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

    case IF_EXISTS:
      return new Boolean(isIf_exists());

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
    case IF_EXISTS:
      return isSetIf_exists();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof DropEdgeReq)
      return this.equals((DropEdgeReq)that);
    return false;
  }

  public boolean equals(DropEdgeReq that) {
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
      if (!TBaseHelper.equalsNobinary(this.edge_name, that.edge_name))
        return false;
    }

    boolean this_present_if_exists = true;
    boolean that_present_if_exists = true;
    if (this_present_if_exists || that_present_if_exists) {
      if (!(this_present_if_exists && that_present_if_exists))
        return false;
      if (!TBaseHelper.equalsNobinary(this.if_exists, that.if_exists))
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

    boolean present_if_exists = true;
    builder.append(present_if_exists);
    if (present_if_exists)
      builder.append(if_exists);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(DropEdgeReq other) {
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
    lastComparison = Boolean.valueOf(isSetIf_exists()).compareTo(other.isSetIf_exists());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(if_exists, other.if_exists);
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
            this.edge_name = iprot.readString();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case IF_EXISTS:
          if (field.type == TType.BOOL) {
            this.if_exists = iprot.readBool();
            setIf_existsIsSet(true);
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
      oprot.writeString(this.edge_name);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(IF_EXISTS_FIELD_DESC);
    oprot.writeBool(this.if_exists);
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
    StringBuilder sb = new StringBuilder("DropEdgeReq");
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
      sb.append(TBaseHelper.toString(this. getEdge_name(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("if_exists");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. isIf_exists(), indent + 1, prettyPrint));
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

