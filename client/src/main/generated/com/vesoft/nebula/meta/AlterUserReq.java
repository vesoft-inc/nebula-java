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
public class AlterUserReq implements TBase, java.io.Serializable, Cloneable, Comparable<AlterUserReq> {
  private static final TStruct STRUCT_DESC = new TStruct("AlterUserReq");
  private static final TField USER_ITEM_FIELD_DESC = new TField("user_item", TType.STRUCT, (short)1);

  public UserItem user_item;
  public static final int USER_ITEM = 1;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(USER_ITEM, new FieldMetaData("user_item", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, UserItem.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(AlterUserReq.class, metaDataMap);
  }

  public AlterUserReq() {
  }

  public AlterUserReq(
    UserItem user_item)
  {
    this();
    this.user_item = user_item;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AlterUserReq(AlterUserReq other) {
    if (other.isSetUser_item()) {
      this.user_item = TBaseHelper.deepCopy(other.user_item);
    }
  }

  public AlterUserReq deepCopy() {
    return new AlterUserReq(this);
  }

  @Deprecated
  public AlterUserReq clone() {
    return new AlterUserReq(this);
  }

  public UserItem  getUser_item() {
    return this.user_item;
  }

  public AlterUserReq setUser_item(UserItem user_item) {
    this.user_item = user_item;
    return this;
  }

  public void unsetUser_item() {
    this.user_item = null;
  }

  // Returns true if field user_item is set (has been assigned a value) and false otherwise
  public boolean isSetUser_item() {
    return this.user_item != null;
  }

  public void setUser_itemIsSet(boolean value) {
    if (!value) {
      this.user_item = null;
    }
  }

  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case USER_ITEM:
      if (value == null) {
        unsetUser_item();
      } else {
        setUser_item((UserItem)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case USER_ITEM:
      return getUser_item();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case USER_ITEM:
      return isSetUser_item();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof AlterUserReq)
      return this.equals((AlterUserReq)that);
    return false;
  }

  public boolean equals(AlterUserReq that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_user_item = true && this.isSetUser_item();
    boolean that_present_user_item = true && that.isSetUser_item();
    if (this_present_user_item || that_present_user_item) {
      if (!(this_present_user_item && that_present_user_item))
        return false;
      if (!TBaseHelper.equalsNobinary(this.user_item, that.user_item))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_user_item = true && (isSetUser_item());
    builder.append(present_user_item);
    if (present_user_item)
      builder.append(user_item);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(AlterUserReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetUser_item()).compareTo(other.isSetUser_item());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(user_item, other.user_item);
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
        case USER_ITEM:
          if (field.type == TType.STRUCT) {
            this.user_item = new UserItem();
            this.user_item.read(iprot);
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
    if (this.user_item != null) {
      oprot.writeFieldBegin(USER_ITEM_FIELD_DESC);
      this.user_item.write(oprot);
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
    StringBuilder sb = new StringBuilder("AlterUserReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("user_item");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getUser_item() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getUser_item(), indent + 1, prettyPrint));
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

