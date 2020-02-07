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
public class UpdateEdgeRequest implements TBase, java.io.Serializable, Cloneable, Comparable<UpdateEdgeRequest> {
  private static final TStruct STRUCT_DESC = new TStruct("UpdateEdgeRequest");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField EDGE_KEY_FIELD_DESC = new TField("edge_key", TType.STRUCT, (short)2);
  private static final TField PART_ID_FIELD_DESC = new TField("part_id", TType.I32, (short)3);
  private static final TField FILTER_FIELD_DESC = new TField("filter", TType.STRING, (short)4);
  private static final TField UPDATE_ITEMS_FIELD_DESC = new TField("update_items", TType.LIST, (short)5);
  private static final TField RETURN_COLUMNS_FIELD_DESC = new TField("return_columns", TType.LIST, (short)6);
  private static final TField INSERTABLE_FIELD_DESC = new TField("insertable", TType.BOOL, (short)7);

  public int space_id;
  public EdgeKey edge_key;
  public int part_id;
  public byte[] filter;
  public List<UpdateItem> update_items;
  public List<byte[]> return_columns;
  public boolean insertable;
  public static final int SPACE_ID = 1;
  public static final int EDGE_KEY = 2;
  public static final int PART_ID = 3;
  public static final int FILTER = 4;
  public static final int UPDATE_ITEMS = 5;
  public static final int RETURN_COLUMNS = 6;
  public static final int INSERTABLE = 7;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __PART_ID_ISSET_ID = 1;
  private static final int __INSERTABLE_ISSET_ID = 2;
  private BitSet __isset_bit_vector = new BitSet(3);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(EDGE_KEY, new FieldMetaData("edge_key", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, EdgeKey.class)));
    tmpMetaDataMap.put(PART_ID, new FieldMetaData("part_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(FILTER, new FieldMetaData("filter", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(UPDATE_ITEMS, new FieldMetaData("update_items", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, UpdateItem.class))));
    tmpMetaDataMap.put(RETURN_COLUMNS, new FieldMetaData("return_columns", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new FieldValueMetaData(TType.STRING))));
    tmpMetaDataMap.put(INSERTABLE, new FieldMetaData("insertable", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(UpdateEdgeRequest.class, metaDataMap);
  }

  public UpdateEdgeRequest() {
  }

  public UpdateEdgeRequest(
    int space_id,
    EdgeKey edge_key,
    int part_id,
    byte[] filter,
    List<UpdateItem> update_items,
    List<byte[]> return_columns,
    boolean insertable)
  {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.edge_key = edge_key;
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.filter = filter;
    this.update_items = update_items;
    this.return_columns = return_columns;
    this.insertable = insertable;
    setInsertableIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public UpdateEdgeRequest(UpdateEdgeRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetEdge_key()) {
      this.edge_key = TBaseHelper.deepCopy(other.edge_key);
    }
    this.part_id = TBaseHelper.deepCopy(other.part_id);
    if (other.isSetFilter()) {
      this.filter = TBaseHelper.deepCopy(other.filter);
    }
    if (other.isSetUpdate_items()) {
      this.update_items = TBaseHelper.deepCopy(other.update_items);
    }
    if (other.isSetReturn_columns()) {
      this.return_columns = TBaseHelper.deepCopy(other.return_columns);
    }
    this.insertable = TBaseHelper.deepCopy(other.insertable);
  }

  public UpdateEdgeRequest deepCopy() {
    return new UpdateEdgeRequest(this);
  }

  @Deprecated
  public UpdateEdgeRequest clone() {
    return new UpdateEdgeRequest(this);
  }

  public int  getSpace_id() {
    return this.space_id;
  }

  public UpdateEdgeRequest setSpace_id(int space_id) {
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

  public EdgeKey  getEdge_key() {
    return this.edge_key;
  }

  public UpdateEdgeRequest setEdge_key(EdgeKey edge_key) {
    this.edge_key = edge_key;
    return this;
  }

  public void unsetEdge_key() {
    this.edge_key = null;
  }

  // Returns true if field edge_key is set (has been assigned a value) and false otherwise
  public boolean isSetEdge_key() {
    return this.edge_key != null;
  }

  public void setEdge_keyIsSet(boolean value) {
    if (!value) {
      this.edge_key = null;
    }
  }

  public int  getPart_id() {
    return this.part_id;
  }

  public UpdateEdgeRequest setPart_id(int part_id) {
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

  public byte[]  getFilter() {
    return this.filter;
  }

  public UpdateEdgeRequest setFilter(byte[] filter) {
    this.filter = filter;
    return this;
  }

  public void unsetFilter() {
    this.filter = null;
  }

  // Returns true if field filter is set (has been assigned a value) and false otherwise
  public boolean isSetFilter() {
    return this.filter != null;
  }

  public void setFilterIsSet(boolean value) {
    if (!value) {
      this.filter = null;
    }
  }

  public List<UpdateItem>  getUpdate_items() {
    return this.update_items;
  }

  public UpdateEdgeRequest setUpdate_items(List<UpdateItem> update_items) {
    this.update_items = update_items;
    return this;
  }

  public void unsetUpdate_items() {
    this.update_items = null;
  }

  // Returns true if field update_items is set (has been assigned a value) and false otherwise
  public boolean isSetUpdate_items() {
    return this.update_items != null;
  }

  public void setUpdate_itemsIsSet(boolean value) {
    if (!value) {
      this.update_items = null;
    }
  }

  public List<byte[]>  getReturn_columns() {
    return this.return_columns;
  }

  public UpdateEdgeRequest setReturn_columns(List<byte[]> return_columns) {
    this.return_columns = return_columns;
    return this;
  }

  public void unsetReturn_columns() {
    this.return_columns = null;
  }

  // Returns true if field return_columns is set (has been assigned a value) and false otherwise
  public boolean isSetReturn_columns() {
    return this.return_columns != null;
  }

  public void setReturn_columnsIsSet(boolean value) {
    if (!value) {
      this.return_columns = null;
    }
  }

  public boolean  isInsertable() {
    return this.insertable;
  }

  public UpdateEdgeRequest setInsertable(boolean insertable) {
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

    case EDGE_KEY:
      if (value == null) {
        unsetEdge_key();
      } else {
        setEdge_key((EdgeKey)value);
      }
      break;

    case PART_ID:
      if (value == null) {
        unsetPart_id();
      } else {
        setPart_id((Integer)value);
      }
      break;

    case FILTER:
      if (value == null) {
        unsetFilter();
      } else {
        setFilter((byte[])value);
      }
      break;

    case UPDATE_ITEMS:
      if (value == null) {
        unsetUpdate_items();
      } else {
        setUpdate_items((List<UpdateItem>)value);
      }
      break;

    case RETURN_COLUMNS:
      if (value == null) {
        unsetReturn_columns();
      } else {
        setReturn_columns((List<byte[]>)value);
      }
      break;

    case INSERTABLE:
      if (value == null) {
        unsetInsertable();
      } else {
        setInsertable((Boolean)value);
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

    case EDGE_KEY:
      return getEdge_key();

    case PART_ID:
      return new Integer(getPart_id());

    case FILTER:
      return getFilter();

    case UPDATE_ITEMS:
      return getUpdate_items();

    case RETURN_COLUMNS:
      return getReturn_columns();

    case INSERTABLE:
      return new Boolean(isInsertable());

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SPACE_ID:
      return isSetSpace_id();
    case EDGE_KEY:
      return isSetEdge_key();
    case PART_ID:
      return isSetPart_id();
    case FILTER:
      return isSetFilter();
    case UPDATE_ITEMS:
      return isSetUpdate_items();
    case RETURN_COLUMNS:
      return isSetReturn_columns();
    case INSERTABLE:
      return isSetInsertable();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof UpdateEdgeRequest)
      return this.equals((UpdateEdgeRequest)that);
    return false;
  }

  public boolean equals(UpdateEdgeRequest that) {
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

    boolean this_present_edge_key = true && this.isSetEdge_key();
    boolean that_present_edge_key = true && that.isSetEdge_key();
    if (this_present_edge_key || that_present_edge_key) {
      if (!(this_present_edge_key && that_present_edge_key))
        return false;
      if (!TBaseHelper.equalsNobinary(this.edge_key, that.edge_key))
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

    boolean this_present_filter = true && this.isSetFilter();
    boolean that_present_filter = true && that.isSetFilter();
    if (this_present_filter || that_present_filter) {
      if (!(this_present_filter && that_present_filter))
        return false;
      if (!TBaseHelper.equalsSlow(this.filter, that.filter))
        return false;
    }

    boolean this_present_update_items = true && this.isSetUpdate_items();
    boolean that_present_update_items = true && that.isSetUpdate_items();
    if (this_present_update_items || that_present_update_items) {
      if (!(this_present_update_items && that_present_update_items))
        return false;
      if (!TBaseHelper.equalsNobinary(this.update_items, that.update_items))
        return false;
    }

    boolean this_present_return_columns = true && this.isSetReturn_columns();
    boolean that_present_return_columns = true && that.isSetReturn_columns();
    if (this_present_return_columns || that_present_return_columns) {
      if (!(this_present_return_columns && that_present_return_columns))
        return false;
      if (!TBaseHelper.equalsSlow(this.return_columns, that.return_columns))
        return false;
    }

    boolean this_present_insertable = true;
    boolean that_present_insertable = true;
    if (this_present_insertable || that_present_insertable) {
      if (!(this_present_insertable && that_present_insertable))
        return false;
      if (!TBaseHelper.equalsNobinary(this.insertable, that.insertable))
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

    boolean present_edge_key = true && (isSetEdge_key());
    builder.append(present_edge_key);
    if (present_edge_key)
      builder.append(edge_key);

    boolean present_part_id = true;
    builder.append(present_part_id);
    if (present_part_id)
      builder.append(part_id);

    boolean present_filter = true && (isSetFilter());
    builder.append(present_filter);
    if (present_filter)
      builder.append(filter);

    boolean present_update_items = true && (isSetUpdate_items());
    builder.append(present_update_items);
    if (present_update_items)
      builder.append(update_items);

    boolean present_return_columns = true && (isSetReturn_columns());
    builder.append(present_return_columns);
    if (present_return_columns)
      builder.append(return_columns);

    boolean present_insertable = true;
    builder.append(present_insertable);
    if (present_insertable)
      builder.append(insertable);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(UpdateEdgeRequest other) {
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
    lastComparison = Boolean.valueOf(isSetEdge_key()).compareTo(other.isSetEdge_key());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(edge_key, other.edge_key);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPart_id()).compareTo(other.isSetPart_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(part_id, other.part_id);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetFilter()).compareTo(other.isSetFilter());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(filter, other.filter);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetUpdate_items()).compareTo(other.isSetUpdate_items());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(update_items, other.update_items);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetReturn_columns()).compareTo(other.isSetReturn_columns());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(return_columns, other.return_columns);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetInsertable()).compareTo(other.isSetInsertable());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(insertable, other.insertable);
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
        case EDGE_KEY:
          if (field.type == TType.STRUCT) {
            this.edge_key = new EdgeKey();
            this.edge_key.read(iprot);
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
        case FILTER:
          if (field.type == TType.STRING) {
            this.filter = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case UPDATE_ITEMS:
          if (field.type == TType.LIST) {
            {
              TList _list129 = iprot.readListBegin();
              this.update_items = new ArrayList<UpdateItem>(Math.max(0, _list129.size));
              for (int _i130 = 0; 
                   (_list129.size < 0) ? iprot.peekList() : (_i130 < _list129.size); 
                   ++_i130)
              {
                UpdateItem _elem131;
                _elem131 = new UpdateItem();
                _elem131.read(iprot);
                this.update_items.add(_elem131);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case RETURN_COLUMNS:
          if (field.type == TType.LIST) {
            {
              TList _list132 = iprot.readListBegin();
              this.return_columns = new ArrayList<byte[]>(Math.max(0, _list132.size));
              for (int _i133 = 0; 
                   (_list132.size < 0) ? iprot.peekList() : (_i133 < _list132.size); 
                   ++_i133)
              {
                byte[] _elem134;
                _elem134 = iprot.readBinary();
                this.return_columns.add(_elem134);
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
    if (this.edge_key != null) {
      oprot.writeFieldBegin(EDGE_KEY_FIELD_DESC);
      this.edge_key.write(oprot);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(PART_ID_FIELD_DESC);
    oprot.writeI32(this.part_id);
    oprot.writeFieldEnd();
    if (this.filter != null) {
      oprot.writeFieldBegin(FILTER_FIELD_DESC);
      oprot.writeBinary(this.filter);
      oprot.writeFieldEnd();
    }
    if (this.update_items != null) {
      oprot.writeFieldBegin(UPDATE_ITEMS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.update_items.size()));
        for (UpdateItem _iter135 : this.update_items)        {
          _iter135.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.return_columns != null) {
      oprot.writeFieldBegin(RETURN_COLUMNS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRING, this.return_columns.size()));
        for (byte[] _iter136 : this.return_columns)        {
          oprot.writeBinary(_iter136);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(INSERTABLE_FIELD_DESC);
    oprot.writeBool(this.insertable);
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
    StringBuilder sb = new StringBuilder("UpdateEdgeRequest");
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
    sb.append("edge_key");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getEdge_key() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getEdge_key(), indent + 1, prettyPrint));
    }
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
    sb.append("filter");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getFilter() == null) {
      sb.append("null");
    } else {
        int __filter_size = Math.min(this. getFilter().length, 128);
        for (int i = 0; i < __filter_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getFilter()[i]).length() > 1 ? Integer.toHexString(this. getFilter()[i]).substring(Integer.toHexString(this. getFilter()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getFilter()[i]).toUpperCase());
        }
        if (this. getFilter().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("update_items");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getUpdate_items() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getUpdate_items(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("return_columns");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getReturn_columns() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getReturn_columns(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("insertable");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. isInsertable(), indent + 1, prettyPrint));
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

