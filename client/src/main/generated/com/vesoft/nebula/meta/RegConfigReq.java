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
public class RegConfigReq implements TBase, java.io.Serializable, Cloneable, Comparable<RegConfigReq> {
  private static final TStruct STRUCT_DESC = new TStruct("RegConfigReq");
  private static final TField ITEMS_FIELD_DESC = new TField("items", TType.LIST, (short)1);

  public List<ConfigItem> items;
  public static final int ITEMS = 1;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ITEMS, new FieldMetaData("items", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, ConfigItem.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(RegConfigReq.class, metaDataMap);
  }

  public RegConfigReq() {
  }

  public RegConfigReq(
    List<ConfigItem> items)
  {
    this();
    this.items = items;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RegConfigReq(RegConfigReq other) {
    if (other.isSetItems()) {
      this.items = TBaseHelper.deepCopy(other.items);
    }
  }

  public RegConfigReq deepCopy() {
    return new RegConfigReq(this);
  }

  @Deprecated
  public RegConfigReq clone() {
    return new RegConfigReq(this);
  }

  public List<ConfigItem>  getItems() {
    return this.items;
  }

  public RegConfigReq setItems(List<ConfigItem> items) {
    this.items = items;
    return this;
  }

  public void unsetItems() {
    this.items = null;
  }

  // Returns true if field items is set (has been assigned a value) and false otherwise
  public boolean isSetItems() {
    return this.items != null;
  }

  public void setItemsIsSet(boolean value) {
    if (!value) {
      this.items = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case ITEMS:
      if (value == null) {
        unsetItems();
      } else {
        setItems((List<ConfigItem>)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case ITEMS:
      return getItems();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case ITEMS:
      return isSetItems();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof RegConfigReq)
      return this.equals((RegConfigReq)that);
    return false;
  }

  public boolean equals(RegConfigReq that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_items = true && this.isSetItems();
    boolean that_present_items = true && that.isSetItems();
    if (this_present_items || that_present_items) {
      if (!(this_present_items && that_present_items))
        return false;
      if (!TBaseHelper.equalsNobinary(this.items, that.items))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_items = true && (isSetItems());
    builder.append(present_items);
    if (present_items)
      builder.append(items);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(RegConfigReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetItems()).compareTo(other.isSetItems());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(items, other.items);
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
        case ITEMS:
          if (field.type == TType.LIST) {
            {
              TList _list125 = iprot.readListBegin();
              this.items = new ArrayList<ConfigItem>(Math.max(0, _list125.size));
              for (int _i126 = 0; 
                   (_list125.size < 0) ? iprot.peekList() : (_i126 < _list125.size); 
                   ++_i126)
              {
                ConfigItem _elem127;
                _elem127 = new ConfigItem();
                _elem127.read(iprot);
                this.items.add(_elem127);
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
    if (this.items != null) {
      oprot.writeFieldBegin(ITEMS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.items.size()));
        for (ConfigItem _iter128 : this.items)        {
          _iter128.write(oprot);
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
    StringBuilder sb = new StringBuilder("RegConfigReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("items");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getItems() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getItems(), indent + 1, prettyPrint));
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

