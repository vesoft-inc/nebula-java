/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.meta;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.BitSet;
import java.util.Arrays;
import com.facebook.thrift.*;
import com.facebook.thrift.annotations.*;
import com.facebook.thrift.async.*;
import com.facebook.thrift.meta_data.*;
import com.facebook.thrift.server.*;
import com.facebook.thrift.transport.*;
import com.facebook.thrift.protocol.*;

@SuppressWarnings({ "unused", "serial" })
public class RoleItem implements TBase, java.io.Serializable, Cloneable, Comparable<RoleItem> {
  private static final TStruct STRUCT_DESC = new TStruct("RoleItem");
  private static final TField USER_ID_FIELD_DESC = new TField("user_id", TType.STRING, (short)1);
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)2);
  private static final TField ROLE_TYPE_FIELD_DESC = new TField("role_type", TType.I32, (short)3);

  public byte[] user_id;
  public int space_id;
  /**
   * 
   * @see RoleType
   */
  public RoleType role_type;
  public static final int USER_ID = 1;
  public static final int SPACE_ID = 2;
  public static final int ROLE_TYPE = 3;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(USER_ID, new FieldMetaData("user_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(ROLE_TYPE, new FieldMetaData("role_type", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(RoleItem.class, metaDataMap);
  }

  public RoleItem() {
  }

  public RoleItem(
      byte[] user_id,
      int space_id,
      RoleType role_type) {
    this();
    this.user_id = user_id;
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.role_type = role_type;
  }

  public static class Builder {
    private byte[] user_id;
    private int space_id;
    private RoleType role_type;

    BitSet __optional_isset = new BitSet(1);

    public Builder() {
    }

    public Builder setUser_id(final byte[] user_id) {
      this.user_id = user_id;
      return this;
    }

    public Builder setSpace_id(final int space_id) {
      this.space_id = space_id;
      __optional_isset.set(__SPACE_ID_ISSET_ID, true);
      return this;
    }

    public Builder setRole_type(final RoleType role_type) {
      this.role_type = role_type;
      return this;
    }

    public RoleItem build() {
      RoleItem result = new RoleItem();
      result.setUser_id(this.user_id);
      if (__optional_isset.get(__SPACE_ID_ISSET_ID)) {
        result.setSpace_id(this.space_id);
      }
      result.setRole_type(this.role_type);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RoleItem(RoleItem other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetUser_id()) {
      this.user_id = TBaseHelper.deepCopy(other.user_id);
    }
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetRole_type()) {
      this.role_type = TBaseHelper.deepCopy(other.role_type);
    }
  }

  public RoleItem deepCopy() {
    return new RoleItem(this);
  }

  public byte[] getUser_id() {
    return this.user_id;
  }

  public RoleItem setUser_id(byte[] user_id) {
    this.user_id = user_id;
    return this;
  }

  public void unsetUser_id() {
    this.user_id = null;
  }

  // Returns true if field user_id is set (has been assigned a value) and false otherwise
  public boolean isSetUser_id() {
    return this.user_id != null;
  }

  public void setUser_idIsSet(boolean __value) {
    if (!__value) {
      this.user_id = null;
    }
  }

  public int getSpace_id() {
    return this.space_id;
  }

  public RoleItem setSpace_id(int space_id) {
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

  public void setSpace_idIsSet(boolean __value) {
    __isset_bit_vector.set(__SPACE_ID_ISSET_ID, __value);
  }

  /**
   * 
   * @see RoleType
   */
  public RoleType getRole_type() {
    return this.role_type;
  }

  /**
   * 
   * @see RoleType
   */
  public RoleItem setRole_type(RoleType role_type) {
    this.role_type = role_type;
    return this;
  }

  public void unsetRole_type() {
    this.role_type = null;
  }

  // Returns true if field role_type is set (has been assigned a value) and false otherwise
  public boolean isSetRole_type() {
    return this.role_type != null;
  }

  public void setRole_typeIsSet(boolean __value) {
    if (!__value) {
      this.role_type = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case USER_ID:
      if (__value == null) {
        unsetUser_id();
      } else {
        setUser_id((byte[])__value);
      }
      break;

    case SPACE_ID:
      if (__value == null) {
        unsetSpace_id();
      } else {
        setSpace_id((Integer)__value);
      }
      break;

    case ROLE_TYPE:
      if (__value == null) {
        unsetRole_type();
      } else {
        setRole_type((RoleType)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case USER_ID:
      return getUser_id();

    case SPACE_ID:
      return new Integer(getSpace_id());

    case ROLE_TYPE:
      return getRole_type();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object _that) {
    if (_that == null)
      return false;
    if (this == _that)
      return true;
    if (!(_that instanceof RoleItem))
      return false;
    RoleItem that = (RoleItem)_that;

    if (!TBaseHelper.equalsSlow(this.isSetUser_id(), that.isSetUser_id(), this.user_id, that.user_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.space_id, that.space_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetRole_type(), that.isSetRole_type(), this.role_type, that.role_type)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {user_id, space_id, role_type});
  }

  @Override
  public int compareTo(RoleItem other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetUser_id()).compareTo(other.isSetUser_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(user_id, other.user_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetSpace_id()).compareTo(other.isSetSpace_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space_id, other.space_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetRole_type()).compareTo(other.isSetRole_type());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(role_type, other.role_type);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    return 0;
  }

  public void read(TProtocol iprot) throws TException {
    TField __field;
    iprot.readStructBegin(metaDataMap);
    while (true)
    {
      __field = iprot.readFieldBegin();
      if (__field.type == TType.STOP) { 
        break;
      }
      switch (__field.id)
      {
        case USER_ID:
          if (__field.type == TType.STRING) {
            this.user_id = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case SPACE_ID:
          if (__field.type == TType.I32) {
            this.space_id = iprot.readI32();
            setSpace_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case ROLE_TYPE:
          if (__field.type == TType.I32) {
            this.role_type = RoleType.findByValue(iprot.readI32());
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        default:
          TProtocolUtil.skip(iprot, __field.type);
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
    if (this.user_id != null) {
      oprot.writeFieldBegin(USER_ID_FIELD_DESC);
      oprot.writeBinary(this.user_id);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(SPACE_ID_FIELD_DESC);
    oprot.writeI32(this.space_id);
    oprot.writeFieldEnd();
    if (this.role_type != null) {
      oprot.writeFieldBegin(ROLE_TYPE_FIELD_DESC);
      oprot.writeI32(this.role_type == null ? 0 : this.role_type.getValue());
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    return toString(1, true);
  }

  @Override
  public String toString(int indent, boolean prettyPrint) {
    String indentStr = prettyPrint ? TBaseHelper.getIndentedString(indent) : "";
    String newLine = prettyPrint ? "\n" : "";
    String space = prettyPrint ? " " : "";
    StringBuilder sb = new StringBuilder("RoleItem");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("user_id");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getUser_id() == null) {
      sb.append("null");
    } else {
        int __user_id_size = Math.min(this.getUser_id().length, 128);
        for (int i = 0; i < __user_id_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getUser_id()[i]).length() > 1 ? Integer.toHexString(this.getUser_id()[i]).substring(Integer.toHexString(this.getUser_id()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getUser_id()[i]).toUpperCase());
        }
        if (this.getUser_id().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("space_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getSpace_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("role_type");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getRole_type() == null) {
      sb.append("null");
    } else {
      String role_type_name = this.getRole_type() == null ? "null" : this.getRole_type().name();
      if (role_type_name != null) {
        sb.append(role_type_name);
        sb.append(" (");
      }
      sb.append(this.getRole_type());
      if (role_type_name != null) {
        sb.append(")");
      }
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

