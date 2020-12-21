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
public class UpdateVertexRequest implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("UpdateVertexRequest");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField PART_ID_FIELD_DESC = new TField("part_id", TType.I32, (short)2);
  private static final TField VERTEX_ID_FIELD_DESC = new TField("vertex_id", TType.STRUCT, (short)3);
  private static final TField TAG_ID_FIELD_DESC = new TField("tag_id", TType.I32, (short)4);
  private static final TField UPDATED_PROPS_FIELD_DESC = new TField("updated_props", TType.LIST, (short)5);
  private static final TField INSERTABLE_FIELD_DESC = new TField("insertable", TType.BOOL, (short)6);
  private static final TField RETURN_PROPS_FIELD_DESC = new TField("return_props", TType.LIST, (short)7);
  private static final TField CONDITION_FIELD_DESC = new TField("condition", TType.STRING, (short)8);

  public int space_id;
  public int part_id;
  public com.vesoft.nebula.Value vertex_id;
  public int tag_id;
  public List<UpdatedProp> updated_props;
  public boolean insertable;
  public List<byte[]> return_props;
  public byte[] condition;
  public static final int SPACE_ID = 1;
  public static final int PART_ID = 2;
  public static final int VERTEX_ID = 3;
  public static final int TAG_ID = 4;
  public static final int UPDATED_PROPS = 5;
  public static final int INSERTABLE = 6;
  public static final int RETURN_PROPS = 7;
  public static final int CONDITION = 8;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __PART_ID_ISSET_ID = 1;
  private static final int __TAG_ID_ISSET_ID = 2;
  private static final int __INSERTABLE_ISSET_ID = 3;
  private BitSet __isset_bit_vector = new BitSet(4);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(PART_ID, new FieldMetaData("part_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(VERTEX_ID, new FieldMetaData("vertex_id", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.Value.class)));
    tmpMetaDataMap.put(TAG_ID, new FieldMetaData("tag_id", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(UPDATED_PROPS, new FieldMetaData("updated_props", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, UpdatedProp.class))));
    tmpMetaDataMap.put(INSERTABLE, new FieldMetaData("insertable", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(RETURN_PROPS, new FieldMetaData("return_props", TFieldRequirementType.OPTIONAL, 
        new ListMetaData(TType.LIST, 
            new FieldValueMetaData(TType.STRING))));
    tmpMetaDataMap.put(CONDITION, new FieldMetaData("condition", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(UpdateVertexRequest.class, metaDataMap);
  }

  public UpdateVertexRequest() {
    this.insertable = false;

  }

  public UpdateVertexRequest(
    int tag_id)
  {
    this();
    this.tag_id = tag_id;
    setTag_idIsSet(true);
  }

  public UpdateVertexRequest(
    int space_id,
    int part_id,
    com.vesoft.nebula.Value vertex_id,
    int tag_id,
    List<UpdatedProp> updated_props)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.vertex_id = vertex_id;
    this.tag_id = tag_id;
    setTag_idIsSet(true);
    this.updated_props = updated_props;
  }

  public UpdateVertexRequest(
    int space_id,
    int part_id,
    com.vesoft.nebula.Value vertex_id,
    int tag_id,
    List<UpdatedProp> updated_props,
    boolean insertable,
    List<byte[]> return_props,
    byte[] condition)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.vertex_id = vertex_id;
    this.tag_id = tag_id;
    setTag_idIsSet(true);
    this.updated_props = updated_props;
    this.insertable = insertable;
    setInsertableIsSet(true);
    this.return_props = return_props;
    this.condition = condition;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public UpdateVertexRequest(UpdateVertexRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    this.part_id = TBaseHelper.deepCopy(other.part_id);
    if (other.isSetVertex_id()) {
      this.vertex_id = TBaseHelper.deepCopy(other.vertex_id);
    }
    this.tag_id = TBaseHelper.deepCopy(other.tag_id);
    if (other.isSetUpdated_props()) {
      this.updated_props = TBaseHelper.deepCopy(other.updated_props);
    }
    this.insertable = TBaseHelper.deepCopy(other.insertable);
    if (other.isSetReturn_props()) {
      this.return_props = TBaseHelper.deepCopy(other.return_props);
    }
    if (other.isSetCondition()) {
      this.condition = TBaseHelper.deepCopy(other.condition);
    }
  }

  public UpdateVertexRequest deepCopy() {
    return new UpdateVertexRequest(this);
  }

  @Deprecated
  public UpdateVertexRequest clone() {
    return new UpdateVertexRequest(this);
  }

  public int  getSpace_id() {
    return this.space_id;
  }

  public UpdateVertexRequest setSpace_id(int space_id) {
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

  public int  getPart_id() {
    return this.part_id;
  }

  public UpdateVertexRequest setPart_id(int part_id) {
    this.part_id = part_id;
    setPart_idIsSet(true);
    return this;
  }

  public void unsetPart_id() {
    __isset_bit_vector.clear(__PART_ID_ISSET_ID);
  }

  // Returns true if field part_id is set (has been assigned a value) and false otherwise
  public boolean isSetPart_id() {
    return __isset_bit_vector.get(__PART_ID_ISSET_ID);
  }

  public void setPart_idIsSet(boolean value) {
    __isset_bit_vector.set(__PART_ID_ISSET_ID, value);
  }

  public com.vesoft.nebula.Value  getVertex_id() {
    return this.vertex_id;
  }

  public UpdateVertexRequest setVertex_id(com.vesoft.nebula.Value vertex_id) {
    this.vertex_id = vertex_id;
    return this;
  }

  public void unsetVertex_id() {
    this.vertex_id = null;
  }

  // Returns true if field vertex_id is set (has been assigned a value) and false otherwise
  public boolean isSetVertex_id() {
    return this.vertex_id != null;
  }

  public void setVertex_idIsSet(boolean value) {
    if (!value) {
      this.vertex_id = null;
    }
  }

  public int  getTag_id() {
    return this.tag_id;
  }

  public UpdateVertexRequest setTag_id(int tag_id) {
    this.tag_id = tag_id;
    setTag_idIsSet(true);
    return this;
  }

  public void unsetTag_id() {
    __isset_bit_vector.clear(__TAG_ID_ISSET_ID);
  }

  // Returns true if field tag_id is set (has been assigned a value) and false otherwise
  public boolean isSetTag_id() {
    return __isset_bit_vector.get(__TAG_ID_ISSET_ID);
  }

  public void setTag_idIsSet(boolean value) {
    __isset_bit_vector.set(__TAG_ID_ISSET_ID, value);
  }

  public List<UpdatedProp>  getUpdated_props() {
    return this.updated_props;
  }

  public UpdateVertexRequest setUpdated_props(List<UpdatedProp> updated_props) {
    this.updated_props = updated_props;
    return this;
  }

  public void unsetUpdated_props() {
    this.updated_props = null;
  }

  // Returns true if field updated_props is set (has been assigned a value) and false otherwise
  public boolean isSetUpdated_props() {
    return this.updated_props != null;
  }

  public void setUpdated_propsIsSet(boolean value) {
    if (!value) {
      this.updated_props = null;
    }
  }

  public boolean  isInsertable() {
    return this.insertable;
  }

  public UpdateVertexRequest setInsertable(boolean insertable) {
    this.insertable = insertable;
    setInsertableIsSet(true);
    return this;
  }

  public void unsetInsertable() {
    __isset_bit_vector.clear(__INSERTABLE_ISSET_ID);
  }

  // Returns true if field insertable is set (has been assigned a value) and false otherwise
  public boolean isSetInsertable() {
    return __isset_bit_vector.get(__INSERTABLE_ISSET_ID);
  }

  public void setInsertableIsSet(boolean value) {
    __isset_bit_vector.set(__INSERTABLE_ISSET_ID, value);
  }

  public List<byte[]>  getReturn_props() {
    return this.return_props;
  }

  public UpdateVertexRequest setReturn_props(List<byte[]> return_props) {
    this.return_props = return_props;
    return this;
  }

  public void unsetReturn_props() {
    this.return_props = null;
  }

  // Returns true if field return_props is set (has been assigned a value) and false otherwise
  public boolean isSetReturn_props() {
    return this.return_props != null;
  }

  public void setReturn_propsIsSet(boolean value) {
    if (!value) {
      this.return_props = null;
    }
  }

  public byte[]  getCondition() {
    return this.condition;
  }

  public UpdateVertexRequest setCondition(byte[] condition) {
    this.condition = condition;
    return this;
  }

  public void unsetCondition() {
    this.condition = null;
  }

  // Returns true if field condition is set (has been assigned a value) and false otherwise
  public boolean isSetCondition() {
    return this.condition != null;
  }

  public void setConditionIsSet(boolean value) {
    if (!value) {
      this.condition = null;
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

    case PART_ID:
      if (value == null) {
        unsetPart_id();
      } else {
        setPart_id((Integer)value);
      }
      break;

    case VERTEX_ID:
      if (value == null) {
        unsetVertex_id();
      } else {
        setVertex_id((com.vesoft.nebula.Value)value);
      }
      break;

    case TAG_ID:
      if (value == null) {
        unsetTag_id();
      } else {
        setTag_id((Integer)value);
      }
      break;

    case UPDATED_PROPS:
      if (value == null) {
        unsetUpdated_props();
      } else {
        setUpdated_props((List<UpdatedProp>)value);
      }
      break;

    case INSERTABLE:
      if (value == null) {
        unsetInsertable();
      } else {
        setInsertable((Boolean)value);
      }
      break;

    case RETURN_PROPS:
      if (value == null) {
        unsetReturn_props();
      } else {
        setReturn_props((List<byte[]>)value);
      }
      break;

    case CONDITION:
      if (value == null) {
        unsetCondition();
      } else {
        setCondition((byte[])value);
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

    case PART_ID:
      return new Integer(getPart_id());

    case VERTEX_ID:
      return getVertex_id();

    case TAG_ID:
      return new Integer(getTag_id());

    case UPDATED_PROPS:
      return getUpdated_props();

    case INSERTABLE:
      return new Boolean(isInsertable());

    case RETURN_PROPS:
      return getReturn_props();

    case CONDITION:
      return getCondition();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return isSetSpace_id();
    case PART_ID:
      return isSetPart_id();
    case VERTEX_ID:
      return isSetVertex_id();
    case TAG_ID:
      return isSetTag_id();
    case UPDATED_PROPS:
      return isSetUpdated_props();
    case INSERTABLE:
      return isSetInsertable();
    case RETURN_PROPS:
      return isSetReturn_props();
    case CONDITION:
      return isSetCondition();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof UpdateVertexRequest)
      return this.equals((UpdateVertexRequest)that);
    return false;
  }

  public boolean equals(UpdateVertexRequest that) {
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

    boolean this_present_part_id = true;
    boolean that_present_part_id = true;
    if (this_present_part_id || that_present_part_id) {
      if (!(this_present_part_id && that_present_part_id))
        return false;
      if (!TBaseHelper.equalsNobinary(this.part_id, that.part_id))
        return false;
    }

    boolean this_present_vertex_id = true && this.isSetVertex_id();
    boolean that_present_vertex_id = true && that.isSetVertex_id();
    if (this_present_vertex_id || that_present_vertex_id) {
      if (!(this_present_vertex_id && that_present_vertex_id))
        return false;
      if (!TBaseHelper.equalsNobinary(this.vertex_id, that.vertex_id))
        return false;
    }

    boolean this_present_tag_id = true;
    boolean that_present_tag_id = true;
    if (this_present_tag_id || that_present_tag_id) {
      if (!(this_present_tag_id && that_present_tag_id))
        return false;
      if (!TBaseHelper.equalsNobinary(this.tag_id, that.tag_id))
        return false;
    }

    boolean this_present_updated_props = true && this.isSetUpdated_props();
    boolean that_present_updated_props = true && that.isSetUpdated_props();
    if (this_present_updated_props || that_present_updated_props) {
      if (!(this_present_updated_props && that_present_updated_props))
        return false;
      if (!TBaseHelper.equalsNobinary(this.updated_props, that.updated_props))
        return false;
    }

    boolean this_present_insertable = true && this.isSetInsertable();
    boolean that_present_insertable = true && that.isSetInsertable();
    if (this_present_insertable || that_present_insertable) {
      if (!(this_present_insertable && that_present_insertable))
        return false;
      if (!TBaseHelper.equalsNobinary(this.insertable, that.insertable))
        return false;
    }

    boolean this_present_return_props = true && this.isSetReturn_props();
    boolean that_present_return_props = true && that.isSetReturn_props();
    if (this_present_return_props || that_present_return_props) {
      if (!(this_present_return_props && that_present_return_props))
        return false;
      if (!TBaseHelper.equalsSlow(this.return_props, that.return_props))
        return false;
    }

    boolean this_present_condition = true && this.isSetCondition();
    boolean that_present_condition = true && that.isSetCondition();
    if (this_present_condition || that_present_condition) {
      if (!(this_present_condition && that_present_condition))
        return false;
      if (!TBaseHelper.equalsSlow(this.condition, that.condition))
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

    boolean present_part_id = true;
    builder.append(present_part_id);
    if (present_part_id)
      builder.append(part_id);

    boolean present_vertex_id = true && (isSetVertex_id());
    builder.append(present_vertex_id);
    if (present_vertex_id)
      builder.append(vertex_id);

    boolean present_tag_id = true;
    builder.append(present_tag_id);
    if (present_tag_id)
      builder.append(tag_id);

    boolean present_updated_props = true && (isSetUpdated_props());
    builder.append(present_updated_props);
    if (present_updated_props)
      builder.append(updated_props);

    boolean present_insertable = true && (isSetInsertable());
    builder.append(present_insertable);
    if (present_insertable)
      builder.append(insertable);

    boolean present_return_props = true && (isSetReturn_props());
    builder.append(present_return_props);
    if (present_return_props)
      builder.append(return_props);

    boolean present_condition = true && (isSetCondition());
    builder.append(present_condition);
    if (present_condition)
      builder.append(condition);

    return builder.toHashCode();
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
        case PART_ID:
          if (field.type == TType.I32) {
            this.part_id = iprot.readI32();
            setPart_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case VERTEX_ID:
          if (field.type == TType.STRUCT) {
            this.vertex_id = new com.vesoft.nebula.Value();
            this.vertex_id.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case TAG_ID:
          if (field.type == TType.I32) {
            this.tag_id = iprot.readI32();
            setTag_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case UPDATED_PROPS:
          if (field.type == TType.LIST) {
            {
              TList _list135 = iprot.readListBegin();
              this.updated_props = new ArrayList<UpdatedProp>(Math.max(0, _list135.size));
              for (int _i136 = 0; 
                   (_list135.size < 0) ? iprot.peekList() : (_i136 < _list135.size); 
                   ++_i136)
              {
                UpdatedProp _elem137;
                _elem137 = new UpdatedProp();
                _elem137.read(iprot);
                this.updated_props.add(_elem137);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case INSERTABLE:
          if (field.type == TType.BOOL) {
            this.insertable = iprot.readBool();
            setInsertableIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case RETURN_PROPS:
          if (field.type == TType.LIST) {
            {
              TList _list138 = iprot.readListBegin();
              this.return_props = new ArrayList<byte[]>(Math.max(0, _list138.size));
              for (int _i139 = 0; 
                   (_list138.size < 0) ? iprot.peekList() : (_i139 < _list138.size); 
                   ++_i139)
              {
                byte[] _elem140;
                _elem140 = iprot.readBinary();
                this.return_props.add(_elem140);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case CONDITION:
          if (field.type == TType.STRING) {
            this.condition = iprot.readBinary();
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
    if (!isSetTag_id()) {
      throw new TProtocolException("Required field 'tag_id' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(SPACE_ID_FIELD_DESC);
    oprot.writeI32(this.space_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(PART_ID_FIELD_DESC);
    oprot.writeI32(this.part_id);
    oprot.writeFieldEnd();
    if (this.vertex_id != null) {
      oprot.writeFieldBegin(VERTEX_ID_FIELD_DESC);
      this.vertex_id.write(oprot);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(TAG_ID_FIELD_DESC);
    oprot.writeI32(this.tag_id);
    oprot.writeFieldEnd();
    if (this.updated_props != null) {
      oprot.writeFieldBegin(UPDATED_PROPS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.updated_props.size()));
        for (UpdatedProp _iter141 : this.updated_props)        {
          _iter141.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (isSetInsertable()) {
      oprot.writeFieldBegin(INSERTABLE_FIELD_DESC);
      oprot.writeBool(this.insertable);
      oprot.writeFieldEnd();
    }
    if (this.return_props != null) {
      if (isSetReturn_props()) {
        oprot.writeFieldBegin(RETURN_PROPS_FIELD_DESC);
        {
          oprot.writeListBegin(new TList(TType.STRING, this.return_props.size()));
          for (byte[] _iter142 : this.return_props)          {
            oprot.writeBinary(_iter142);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
    }
    if (this.condition != null) {
      if (isSetCondition()) {
        oprot.writeFieldBegin(CONDITION_FIELD_DESC);
        oprot.writeBinary(this.condition);
        oprot.writeFieldEnd();
      }
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
    StringBuilder sb = new StringBuilder("UpdateVertexRequest");
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
    sb.append("part_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getPart_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("vertex_id");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getVertex_id() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getVertex_id(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("tag_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getTag_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("updated_props");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getUpdated_props() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getUpdated_props(), indent + 1, prettyPrint));
    }
    first = false;
    if (isSetInsertable())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("insertable");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this. isInsertable(), indent + 1, prettyPrint));
      first = false;
    }
    if (isSetReturn_props())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("return_props");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getReturn_props() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this. getReturn_props(), indent + 1, prettyPrint));
      }
      first = false;
    }
    if (isSetCondition())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("condition");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getCondition() == null) {
        sb.append("null");
      } else {
          int __condition_size = Math.min(this. getCondition().length, 128);
          for (int i = 0; i < __condition_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this. getCondition()[i]).length() > 1 ? Integer.toHexString(this. getCondition()[i]).substring(Integer.toHexString(this. getCondition()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getCondition()[i]).toUpperCase());
          }
          if (this. getCondition().length > 128) sb.append(" ...");
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // alas, we cannot check 'tag_id' because it's a primitive and you chose the non-beans generator.
    // check that fields of type enum have valid values
  }

}

