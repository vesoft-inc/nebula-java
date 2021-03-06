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
public class ListConfigsReq implements TBase, java.io.Serializable, Cloneable, Comparable<ListConfigsReq> {
  private static final TStruct STRUCT_DESC = new TStruct("ListConfigsReq");
  private static final TField SPACE_FIELD_DESC = new TField("space", TType.STRING, (short)1);
  private static final TField MODULE_FIELD_DESC = new TField("module", TType.I32, (short)2);

  public byte[] space;
  /**
   * 
   * @see ConfigModule
   */
  public ConfigModule module;
  public static final int SPACE = 1;
  public static final int MODULE = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE, new FieldMetaData("space", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(MODULE, new FieldMetaData("module", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(ListConfigsReq.class, metaDataMap);
  }

  public ListConfigsReq() {
  }

  public ListConfigsReq(
      byte[] space,
      ConfigModule module) {
    this();
    this.space = space;
    this.module = module;
  }

  public static class Builder {
    private byte[] space;
    private ConfigModule module;

    public Builder() {
    }

    public Builder setSpace(final byte[] space) {
      this.space = space;
      return this;
    }

    public Builder setModule(final ConfigModule module) {
      this.module = module;
      return this;
    }

    public ListConfigsReq build() {
      ListConfigsReq result = new ListConfigsReq();
      result.setSpace(this.space);
      result.setModule(this.module);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ListConfigsReq(ListConfigsReq other) {
    if (other.isSetSpace()) {
      this.space = TBaseHelper.deepCopy(other.space);
    }
    if (other.isSetModule()) {
      this.module = TBaseHelper.deepCopy(other.module);
    }
  }

  public ListConfigsReq deepCopy() {
    return new ListConfigsReq(this);
  }

  public byte[] getSpace() {
    return this.space;
  }

  public ListConfigsReq setSpace(byte[] space) {
    this.space = space;
    return this;
  }

  public void unsetSpace() {
    this.space = null;
  }

  // Returns true if field space is set (has been assigned a value) and false otherwise
  public boolean isSetSpace() {
    return this.space != null;
  }

  public void setSpaceIsSet(boolean __value) {
    if (!__value) {
      this.space = null;
    }
  }

  /**
   * 
   * @see ConfigModule
   */
  public ConfigModule getModule() {
    return this.module;
  }

  /**
   * 
   * @see ConfigModule
   */
  public ListConfigsReq setModule(ConfigModule module) {
    this.module = module;
    return this;
  }

  public void unsetModule() {
    this.module = null;
  }

  // Returns true if field module is set (has been assigned a value) and false otherwise
  public boolean isSetModule() {
    return this.module != null;
  }

  public void setModuleIsSet(boolean __value) {
    if (!__value) {
      this.module = null;
    }
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SPACE:
      if (__value == null) {
        unsetSpace();
      } else {
        setSpace((byte[])__value);
      }
      break;

    case MODULE:
      if (__value == null) {
        unsetModule();
      } else {
        setModule((ConfigModule)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SPACE:
      return getSpace();

    case MODULE:
      return getModule();

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
    if (!(_that instanceof ListConfigsReq))
      return false;
    ListConfigsReq that = (ListConfigsReq)_that;

    if (!TBaseHelper.equalsSlow(this.isSetSpace(), that.isSetSpace(), this.space, that.space)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetModule(), that.isSetModule(), this.module, that.module)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space, module});
  }

  @Override
  public int compareTo(ListConfigsReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSpace()).compareTo(other.isSetSpace());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space, other.space);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetModule()).compareTo(other.isSetModule());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(module, other.module);
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
        case SPACE:
          if (__field.type == TType.STRING) {
            this.space = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case MODULE:
          if (__field.type == TType.I32) {
            this.module = ConfigModule.findByValue(iprot.readI32());
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
    if (this.space != null) {
      oprot.writeFieldBegin(SPACE_FIELD_DESC);
      oprot.writeBinary(this.space);
      oprot.writeFieldEnd();
    }
    if (this.module != null) {
      oprot.writeFieldBegin(MODULE_FIELD_DESC);
      oprot.writeI32(this.module == null ? 0 : this.module.getValue());
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
    StringBuilder sb = new StringBuilder("ListConfigsReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("space");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getSpace() == null) {
      sb.append("null");
    } else {
        int __space_size = Math.min(this.getSpace().length, 128);
        for (int i = 0; i < __space_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getSpace()[i]).length() > 1 ? Integer.toHexString(this.getSpace()[i]).substring(Integer.toHexString(this.getSpace()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getSpace()[i]).toUpperCase());
        }
        if (this.getSpace().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("module");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getModule() == null) {
      sb.append("null");
    } else {
      String module_name = this.getModule() == null ? "null" : this.getModule().name();
      if (module_name != null) {
        sb.append(module_name);
        sb.append(" (");
      }
      sb.append(this.getModule());
      if (module_name != null) {
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

