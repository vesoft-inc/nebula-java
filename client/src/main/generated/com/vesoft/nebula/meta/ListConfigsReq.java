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
public class ListConfigsReq implements TBase, java.io.Serializable, Cloneable, Comparable<ListConfigsReq> {
  private static final TStruct STRUCT_DESC = new TStruct("ListConfigsReq");
  private static final TField SPACE_FIELD_DESC = new TField("space", TType.STRING, (short)1);
  private static final TField MODULE_FIELD_DESC = new TField("module", TType.I32, (short)2);

  public byte[] space;
  /**
   * 
   * @see ConfigModule
   */
  public int module;
  public static final int SPACE = 1;
  public static final int MODULE = 2;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __MODULE_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

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
    int module)
  {
    this();
    this.space = space;
    this.module = module;
    setModuleIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ListConfigsReq(ListConfigsReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetSpace()) {
      this.space = TBaseHelper.deepCopy(other.space);
    }
    this.module = TBaseHelper.deepCopy(other.module);
  }

  public ListConfigsReq deepCopy() {
    return new ListConfigsReq(this);
  }

  @Deprecated
  public ListConfigsReq clone() {
    return new ListConfigsReq(this);
  }

  public byte[]  getSpace() {
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

  public void setSpaceIsSet(boolean value) {
    if (!value) {
      this.space = null;
    }
  }

  /**
   * 
   * @see ConfigModule
   */
  public int  getModule() {
    return this.module;
  }

  /**
   * 
   * @see ConfigModule
   */
  public ListConfigsReq setModule(int module) {
    this.module = module;
    setModuleIsSet(true);
    return this;
  }

  public void unsetModule() {
    __isset_bit_vector.clear(__MODULE_ISSET_ID);
  }

  // Returns true if field module is set (has been assigned a value) and false otherwise
  public boolean isSetModule() {
    return __isset_bit_vector.get(__MODULE_ISSET_ID);
  }

  public void setModuleIsSet(boolean value) {
    __isset_bit_vector.set(__MODULE_ISSET_ID, value);
  }

  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case SPACE:
      if (value == null) {
        unsetSpace();
      } else {
        setSpace((byte[])value);
      }
      break;

    case MODULE:
      if (value == null) {
        unsetModule();
      } else {
        setModule((Integer)value);
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

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case SPACE:
      return isSetSpace();
    case MODULE:
      return isSetModule();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ListConfigsReq)
      return this.equals((ListConfigsReq)that);
    return false;
  }

  public boolean equals(ListConfigsReq that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_space = true && this.isSetSpace();
    boolean that_present_space = true && that.isSetSpace();
    if (this_present_space || that_present_space) {
      if (!(this_present_space && that_present_space))
        return false;
      if (!TBaseHelper.equalsSlow(this.space, that.space))
        return false;
    }

    boolean this_present_module = true;
    boolean that_present_module = true;
    if (this_present_module || that_present_module) {
      if (!(this_present_module && that_present_module))
        return false;
      if (!TBaseHelper.equalsNobinary(this.module, that.module))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_space = true && (isSetSpace());
    builder.append(present_space);
    if (present_space)
      builder.append(space);

    boolean present_module = true;
    builder.append(present_module);
    if (present_module)
      builder.append(module);

    return builder.toHashCode();
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
        case SPACE:
          if (field.type == TType.STRING) {
            this.space = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case MODULE:
          if (field.type == TType.I32) {
            this.module = iprot.readI32();
            setModuleIsSet(true);
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
    if (this.space != null) {
      oprot.writeFieldBegin(SPACE_FIELD_DESC);
      oprot.writeBinary(this.space);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(MODULE_FIELD_DESC);
    oprot.writeI32(this.module);
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
    StringBuilder sb = new StringBuilder("ListConfigsReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("space");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getSpace() == null) {
      sb.append("null");
    } else {
        int __space_size = Math.min(this. getSpace().length, 128);
        for (int i = 0; i < __space_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getSpace()[i]).length() > 1 ? Integer.toHexString(this. getSpace()[i]).substring(Integer.toHexString(this. getSpace()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getSpace()[i]).toUpperCase());
        }
        if (this. getSpace().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("module");
    sb.append(space);
    sb.append(":").append(space);
    String module_name = ConfigModule.VALUES_TO_NAMES.get(this. getModule());
    if (module_name != null) {
      sb.append(module_name);
      sb.append(" (");
    }
    sb.append(this. getModule());
    if (module_name != null) {
      sb.append(")");
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // check that fields of type enum have valid values
    if (isSetModule() && !ConfigModule.VALID_VALUES.contains(module)){
      throw new TProtocolException("The field 'module' has been assigned the invalid value " + module);
    }
  }

}

