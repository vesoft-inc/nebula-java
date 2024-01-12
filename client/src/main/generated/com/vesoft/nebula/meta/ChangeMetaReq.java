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
public class ChangeMetaReq implements TBase, java.io.Serializable, Cloneable, Comparable<ChangeMetaReq> {
  private static final TStruct STRUCT_DESC = new TStruct("ChangeMetaReq");
  private static final TField ADD_FIELD_DESC = new TField("add", TType.STRUCT, (short)1);
  private static final TField REMOVE_FIELD_DESC = new TField("remove", TType.STRUCT, (short)2);

  public com.vesoft.nebula.HostAddr add;
  public com.vesoft.nebula.HostAddr remove;
  public static final int ADD = 1;
  public static final int REMOVE = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ADD, new FieldMetaData("add", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(REMOVE, new FieldMetaData("remove", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ChangeMetaReq.class, metaDataMap);
  }

  public ChangeMetaReq() {
  }

  public ChangeMetaReq(
      com.vesoft.nebula.HostAddr add,
      com.vesoft.nebula.HostAddr remove) {
    this();
    this.add = add;
    this.remove = remove;
  }

  public static class Builder {
    private com.vesoft.nebula.HostAddr add;
    private com.vesoft.nebula.HostAddr remove;

    public Builder() {
    }

    public Builder setAdd(final com.vesoft.nebula.HostAddr add) {
      this.add = add;
      return this;
    }

    public Builder setRemove(final com.vesoft.nebula.HostAddr remove) {
      this.remove = remove;
      return this;
    }

    public ChangeMetaReq build() {
      ChangeMetaReq result = new ChangeMetaReq();
      result.setAdd(this.add);
      result.setRemove(this.remove);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ChangeMetaReq(ChangeMetaReq other) {
    if (other.isSetAdd()) {
      this.add = TBaseHelper.deepCopy(other.add);
    }
    if (other.isSetRemove()) {
      this.remove = TBaseHelper.deepCopy(other.remove);
    }
  }

  public ChangeMetaReq deepCopy() {
    return new ChangeMetaReq(this);
  }

  public com.vesoft.nebula.HostAddr getAdd() {
    return this.add;
  }

  public ChangeMetaReq setAdd(com.vesoft.nebula.HostAddr add) {
    this.add = add;
    return this;
  }

  public void unsetAdd() {
    this.add = null;
  }

  // Returns true if field add is set (has been assigned a value) and false otherwise
  public boolean isSetAdd() {
    return this.add != null;
  }

  public void setAddIsSet(boolean __value) {
    if (!__value) {
      this.add = null;
    }
  }

  public com.vesoft.nebula.HostAddr getRemove() {
    return this.remove;
  }

  public ChangeMetaReq setRemove(com.vesoft.nebula.HostAddr remove) {
    this.remove = remove;
    return this;
  }

  public void unsetRemove() {
    this.remove = null;
  }

  // Returns true if field remove is set (has been assigned a value) and false otherwise
  public boolean isSetRemove() {
    return this.remove != null;
  }

  public void setRemoveIsSet(boolean __value) {
    if (!__value) {
      this.remove = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case ADD:
      if (__value == null) {
        unsetAdd();
      } else {
        setAdd((com.vesoft.nebula.HostAddr)__value);
      }
      break;

    case REMOVE:
      if (__value == null) {
        unsetRemove();
      } else {
        setRemove((com.vesoft.nebula.HostAddr)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case ADD:
      return getAdd();

    case REMOVE:
      return getRemove();

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
    if (!(_that instanceof ChangeMetaReq))
      return false;
    ChangeMetaReq that = (ChangeMetaReq)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetAdd(), that.isSetAdd(), this.add, that.add)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetRemove(), that.isSetRemove(), this.remove, that.remove)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {add, remove});
  }

  @Override
  public int compareTo(ChangeMetaReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetAdd()).compareTo(other.isSetAdd());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(add, other.add);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetRemove()).compareTo(other.isSetRemove());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(remove, other.remove);
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
        case ADD:
          if (__field.type == TType.STRUCT) {
            this.add = new com.vesoft.nebula.HostAddr();
            this.add.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case REMOVE:
          if (__field.type == TType.STRUCT) {
            this.remove = new com.vesoft.nebula.HostAddr();
            this.remove.read(iprot);
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
    if (this.add != null) {
      oprot.writeFieldBegin(ADD_FIELD_DESC);
      this.add.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.remove != null) {
      oprot.writeFieldBegin(REMOVE_FIELD_DESC);
      this.remove.write(oprot);
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
    StringBuilder sb = new StringBuilder("ChangeMetaReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("add");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getAdd() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getAdd(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("remove");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getRemove() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getRemove(), indent + 1, prettyPrint));
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

