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
public class Snapshot implements TBase, java.io.Serializable, Cloneable, Comparable<Snapshot> {
  private static final TStruct STRUCT_DESC = new TStruct("Snapshot");
  private static final TField NAME_FIELD_DESC = new TField("name", TType.STRING, (short)1);
  private static final TField STATUS_FIELD_DESC = new TField("status", TType.I32, (short)2);
  private static final TField HOSTS_FIELD_DESC = new TField("hosts", TType.STRING, (short)3);

  public byte[] name;
  /**
   * 
   * @see SnapshotStatus
   */
  public int status;
  public byte[] hosts;
  public static final int NAME = 1;
  public static final int STATUS = 2;
  public static final int HOSTS = 3;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __STATUS_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(NAME, new FieldMetaData("name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(STATUS, new FieldMetaData("status", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(HOSTS, new FieldMetaData("hosts", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(Snapshot.class, metaDataMap);
  }

  public Snapshot() {
  }

  public Snapshot(
    byte[] name,
    int status,
    byte[] hosts)
  {
    this();
    this.name = name;
    this.status = status;
    setStatusIsSet(true);
    this.hosts = hosts;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Snapshot(Snapshot other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetName()) {
      this.name = TBaseHelper.deepCopy(other.name);
    }
    this.status = TBaseHelper.deepCopy(other.status);
    if (other.isSetHosts()) {
      this.hosts = TBaseHelper.deepCopy(other.hosts);
    }
  }

  public Snapshot deepCopy() {
    return new Snapshot(this);
  }

  @Deprecated
  public Snapshot clone() {
    return new Snapshot(this);
  }

  public byte[]  getName() {
    return this.name;
  }

  public Snapshot setName(byte[] name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  // Returns true if field name is set (has been assigned a value) and false otherwise
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  /**
   * 
   * @see SnapshotStatus
   */
  public int  getStatus() {
    return this.status;
  }

  /**
   * 
   * @see SnapshotStatus
   */
  public Snapshot setStatus(int status) {
    this.status = status;
    setStatusIsSet(true);
    return this;
  }

  public void unsetStatus() {
    __isset_bit_vector.clear(__STATUS_ISSET_ID);
  }

  // Returns true if field status is set (has been assigned a value) and false otherwise
  public boolean isSetStatus() {
    return __isset_bit_vector.get(__STATUS_ISSET_ID);
  }

  public void setStatusIsSet(boolean value) {
    __isset_bit_vector.set(__STATUS_ISSET_ID, value);
  }

  public byte[]  getHosts() {
    return this.hosts;
  }

  public Snapshot setHosts(byte[] hosts) {
    this.hosts = hosts;
    return this;
  }

  public void unsetHosts() {
    this.hosts = null;
  }

  // Returns true if field hosts is set (has been assigned a value) and false otherwise
  public boolean isSetHosts() {
    return this.hosts != null;
  }

  public void setHostsIsSet(boolean value) {
    if (!value) {
      this.hosts = null;
    }
  }

  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((byte[])value);
      }
      break;

    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((Integer)value);
      }
      break;

    case HOSTS:
      if (value == null) {
        unsetHosts();
      } else {
        setHosts((byte[])value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case NAME:
      return getName();

    case STATUS:
      return getStatus();

    case HOSTS:
      return getHosts();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case NAME:
      return isSetName();
    case STATUS:
      return isSetStatus();
    case HOSTS:
      return isSetHosts();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Snapshot)
      return this.equals((Snapshot)that);
    return false;
  }

  public boolean equals(Snapshot that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!TBaseHelper.equalsSlow(this.name, that.name))
        return false;
    }

    boolean this_present_status = true;
    boolean that_present_status = true;
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (!TBaseHelper.equalsNobinary(this.status, that.status))
        return false;
    }

    boolean this_present_hosts = true && this.isSetHosts();
    boolean that_present_hosts = true && that.isSetHosts();
    if (this_present_hosts || that_present_hosts) {
      if (!(this_present_hosts && that_present_hosts))
        return false;
      if (!TBaseHelper.equalsSlow(this.hosts, that.hosts))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_name = true && (isSetName());
    builder.append(present_name);
    if (present_name)
      builder.append(name);

    boolean present_status = true;
    builder.append(present_status);
    if (present_status)
      builder.append(status);

    boolean present_hosts = true && (isSetHosts());
    builder.append(present_hosts);
    if (present_hosts)
      builder.append(hosts);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(Snapshot other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetName()).compareTo(other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(name, other.name);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetStatus()).compareTo(other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(status, other.status);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetHosts()).compareTo(other.isSetHosts());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(hosts, other.hosts);
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
        case NAME:
          if (field.type == TType.STRING) {
            this.name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case STATUS:
          if (field.type == TType.I32) {
            this.status = iprot.readI32();
            setStatusIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case HOSTS:
          if (field.type == TType.STRING) {
            this.hosts = iprot.readBinary();
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
    if (this.name != null) {
      oprot.writeFieldBegin(NAME_FIELD_DESC);
      oprot.writeBinary(this.name);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(STATUS_FIELD_DESC);
    oprot.writeI32(this.status);
    oprot.writeFieldEnd();
    if (this.hosts != null) {
      oprot.writeFieldBegin(HOSTS_FIELD_DESC);
      oprot.writeBinary(this.hosts);
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
    StringBuilder sb = new StringBuilder("Snapshot");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("name");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getName() == null) {
      sb.append("null");
    } else {
        int __name_size = Math.min(this. getName().length, 128);
        for (int i = 0; i < __name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getName()[i]).length() > 1 ? Integer.toHexString(this. getName()[i]).substring(Integer.toHexString(this. getName()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getName()[i]).toUpperCase());
        }
        if (this. getName().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("status");
    sb.append(space);
    sb.append(":").append(space);
    String status_name = SnapshotStatus.VALUES_TO_NAMES.get(this. getStatus());
    if (status_name != null) {
      sb.append(status_name);
      sb.append(" (");
    }
    sb.append(this. getStatus());
    if (status_name != null) {
      sb.append(")");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("hosts");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getHosts() == null) {
      sb.append("null");
    } else {
        int __hosts_size = Math.min(this. getHosts().length, 128);
        for (int i = 0; i < __hosts_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getHosts()[i]).length() > 1 ? Integer.toHexString(this. getHosts()[i]).substring(Integer.toHexString(this. getHosts()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getHosts()[i]).toUpperCase());
        }
        if (this. getHosts().length > 128) sb.append(" ...");
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // check that fields of type enum have valid values
    if (isSetStatus() && !SnapshotStatus.VALID_VALUES.contains(status)){
      throw new TProtocolException("The field 'status' has been assigned the invalid value " + status);
    }
  }

}

