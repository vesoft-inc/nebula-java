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
public class BackupMeta implements TBase, java.io.Serializable, Cloneable, Comparable<BackupMeta> {
  private static final TStruct STRUCT_DESC = new TStruct("BackupMeta");
  private static final TField BACKUP_INFO_FIELD_DESC = new TField("backup_info", TType.MAP, (short)1);
  private static final TField META_FILES_FIELD_DESC = new TField("meta_files", TType.LIST, (short)2);
  private static final TField BACKUP_NAME_FIELD_DESC = new TField("backup_name", TType.STRING, (short)3);
  private static final TField FULL_FIELD_DESC = new TField("full", TType.BOOL, (short)4);
  private static final TField INCLUDE_SYSTEM_SPACE_FIELD_DESC = new TField("include_system_space", TType.BOOL, (short)5);
  private static final TField CREATE_TIME_FIELD_DESC = new TField("create_time", TType.I64, (short)6);

  public Map<Integer,SpaceBackupInfo> backup_info;
  public List<byte[]> meta_files;
  public byte[] backup_name;
  public boolean full;
  public boolean include_system_space;
  public long create_time;
  public static final int BACKUP_INFO = 1;
  public static final int META_FILES = 2;
  public static final int BACKUP_NAME = 3;
  public static final int FULL = 4;
  public static final int INCLUDE_SYSTEM_SPACE = 5;
  public static final int CREATE_TIME = 6;

  // isset id assignments
  private static final int __FULL_ISSET_ID = 0;
  private static final int __INCLUDE_SYSTEM_SPACE_ISSET_ID = 1;
  private static final int __CREATE_TIME_ISSET_ID = 2;
  private BitSet __isset_bit_vector = new BitSet(3);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(BACKUP_INFO, new FieldMetaData("backup_info", TFieldRequirementType.DEFAULT, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.I32), 
            new StructMetaData(TType.STRUCT, SpaceBackupInfo.class))));
    tmpMetaDataMap.put(META_FILES, new FieldMetaData("meta_files", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new FieldValueMetaData(TType.STRING))));
    tmpMetaDataMap.put(BACKUP_NAME, new FieldMetaData("backup_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(FULL, new FieldMetaData("full", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(INCLUDE_SYSTEM_SPACE, new FieldMetaData("include_system_space", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(CREATE_TIME, new FieldMetaData("create_time", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(BackupMeta.class, metaDataMap);
  }

  public BackupMeta() {
  }

  public BackupMeta(
      Map<Integer,SpaceBackupInfo> backup_info,
      List<byte[]> meta_files,
      byte[] backup_name,
      boolean full,
      boolean include_system_space,
      long create_time) {
    this();
    this.backup_info = backup_info;
    this.meta_files = meta_files;
    this.backup_name = backup_name;
    this.full = full;
    setFullIsSet(true);
    this.include_system_space = include_system_space;
    setInclude_system_spaceIsSet(true);
    this.create_time = create_time;
    setCreate_timeIsSet(true);
  }

  public static class Builder {
    private Map<Integer,SpaceBackupInfo> backup_info;
    private List<byte[]> meta_files;
    private byte[] backup_name;
    private boolean full;
    private boolean include_system_space;
    private long create_time;

    BitSet __optional_isset = new BitSet(3);

    public Builder() {
    }

    public Builder setBackup_info(final Map<Integer,SpaceBackupInfo> backup_info) {
      this.backup_info = backup_info;
      return this;
    }

    public Builder setMeta_files(final List<byte[]> meta_files) {
      this.meta_files = meta_files;
      return this;
    }

    public Builder setBackup_name(final byte[] backup_name) {
      this.backup_name = backup_name;
      return this;
    }

    public Builder setFull(final boolean full) {
      this.full = full;
      __optional_isset.set(__FULL_ISSET_ID, true);
      return this;
    }

    public Builder setInclude_system_space(final boolean include_system_space) {
      this.include_system_space = include_system_space;
      __optional_isset.set(__INCLUDE_SYSTEM_SPACE_ISSET_ID, true);
      return this;
    }

    public Builder setCreate_time(final long create_time) {
      this.create_time = create_time;
      __optional_isset.set(__CREATE_TIME_ISSET_ID, true);
      return this;
    }

    public BackupMeta build() {
      BackupMeta result = new BackupMeta();
      result.setBackup_info(this.backup_info);
      result.setMeta_files(this.meta_files);
      result.setBackup_name(this.backup_name);
      if (__optional_isset.get(__FULL_ISSET_ID)) {
        result.setFull(this.full);
      }
      if (__optional_isset.get(__INCLUDE_SYSTEM_SPACE_ISSET_ID)) {
        result.setInclude_system_space(this.include_system_space);
      }
      if (__optional_isset.get(__CREATE_TIME_ISSET_ID)) {
        result.setCreate_time(this.create_time);
      }
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public BackupMeta(BackupMeta other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetBackup_info()) {
      this.backup_info = TBaseHelper.deepCopy(other.backup_info);
    }
    if (other.isSetMeta_files()) {
      this.meta_files = TBaseHelper.deepCopy(other.meta_files);
    }
    if (other.isSetBackup_name()) {
      this.backup_name = TBaseHelper.deepCopy(other.backup_name);
    }
    this.full = TBaseHelper.deepCopy(other.full);
    this.include_system_space = TBaseHelper.deepCopy(other.include_system_space);
    this.create_time = TBaseHelper.deepCopy(other.create_time);
  }

  public BackupMeta deepCopy() {
    return new BackupMeta(this);
  }

  public Map<Integer,SpaceBackupInfo> getBackup_info() {
    return this.backup_info;
  }

  public BackupMeta setBackup_info(Map<Integer,SpaceBackupInfo> backup_info) {
    this.backup_info = backup_info;
    return this;
  }

  public void unsetBackup_info() {
    this.backup_info = null;
  }

  // Returns true if field backup_info is set (has been assigned a value) and false otherwise
  public boolean isSetBackup_info() {
    return this.backup_info != null;
  }

  public void setBackup_infoIsSet(boolean __value) {
    if (!__value) {
      this.backup_info = null;
    }
  }

  public List<byte[]> getMeta_files() {
    return this.meta_files;
  }

  public BackupMeta setMeta_files(List<byte[]> meta_files) {
    this.meta_files = meta_files;
    return this;
  }

  public void unsetMeta_files() {
    this.meta_files = null;
  }

  // Returns true if field meta_files is set (has been assigned a value) and false otherwise
  public boolean isSetMeta_files() {
    return this.meta_files != null;
  }

  public void setMeta_filesIsSet(boolean __value) {
    if (!__value) {
      this.meta_files = null;
    }
  }

  public byte[] getBackup_name() {
    return this.backup_name;
  }

  public BackupMeta setBackup_name(byte[] backup_name) {
    this.backup_name = backup_name;
    return this;
  }

  public void unsetBackup_name() {
    this.backup_name = null;
  }

  // Returns true if field backup_name is set (has been assigned a value) and false otherwise
  public boolean isSetBackup_name() {
    return this.backup_name != null;
  }

  public void setBackup_nameIsSet(boolean __value) {
    if (!__value) {
      this.backup_name = null;
    }
  }

  public boolean isFull() {
    return this.full;
  }

  public BackupMeta setFull(boolean full) {
    this.full = full;
    setFullIsSet(true);
    return this;
  }

  public void unsetFull() {
    __isset_bit_vector.clear(__FULL_ISSET_ID);
  }

  // Returns true if field full is set (has been assigned a value) and false otherwise
  public boolean isSetFull() {
    return __isset_bit_vector.get(__FULL_ISSET_ID);
  }

  public void setFullIsSet(boolean __value) {
    __isset_bit_vector.set(__FULL_ISSET_ID, __value);
  }

  public boolean isInclude_system_space() {
    return this.include_system_space;
  }

  public BackupMeta setInclude_system_space(boolean include_system_space) {
    this.include_system_space = include_system_space;
    setInclude_system_spaceIsSet(true);
    return this;
  }

  public void unsetInclude_system_space() {
    __isset_bit_vector.clear(__INCLUDE_SYSTEM_SPACE_ISSET_ID);
  }

  // Returns true if field include_system_space is set (has been assigned a value) and false otherwise
  public boolean isSetInclude_system_space() {
    return __isset_bit_vector.get(__INCLUDE_SYSTEM_SPACE_ISSET_ID);
  }

  public void setInclude_system_spaceIsSet(boolean __value) {
    __isset_bit_vector.set(__INCLUDE_SYSTEM_SPACE_ISSET_ID, __value);
  }

  public long getCreate_time() {
    return this.create_time;
  }

  public BackupMeta setCreate_time(long create_time) {
    this.create_time = create_time;
    setCreate_timeIsSet(true);
    return this;
  }

  public void unsetCreate_time() {
    __isset_bit_vector.clear(__CREATE_TIME_ISSET_ID);
  }

  // Returns true if field create_time is set (has been assigned a value) and false otherwise
  public boolean isSetCreate_time() {
    return __isset_bit_vector.get(__CREATE_TIME_ISSET_ID);
  }

  public void setCreate_timeIsSet(boolean __value) {
    __isset_bit_vector.set(__CREATE_TIME_ISSET_ID, __value);
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case BACKUP_INFO:
      if (__value == null) {
        unsetBackup_info();
      } else {
        setBackup_info((Map<Integer,SpaceBackupInfo>)__value);
      }
      break;

    case META_FILES:
      if (__value == null) {
        unsetMeta_files();
      } else {
        setMeta_files((List<byte[]>)__value);
      }
      break;

    case BACKUP_NAME:
      if (__value == null) {
        unsetBackup_name();
      } else {
        setBackup_name((byte[])__value);
      }
      break;

    case FULL:
      if (__value == null) {
        unsetFull();
      } else {
        setFull((Boolean)__value);
      }
      break;

    case INCLUDE_SYSTEM_SPACE:
      if (__value == null) {
        unsetInclude_system_space();
      } else {
        setInclude_system_space((Boolean)__value);
      }
      break;

    case CREATE_TIME:
      if (__value == null) {
        unsetCreate_time();
      } else {
        setCreate_time((Long)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case BACKUP_INFO:
      return getBackup_info();

    case META_FILES:
      return getMeta_files();

    case BACKUP_NAME:
      return getBackup_name();

    case FULL:
      return new Boolean(isFull());

    case INCLUDE_SYSTEM_SPACE:
      return new Boolean(isInclude_system_space());

    case CREATE_TIME:
      return new Long(getCreate_time());

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
    if (!(_that instanceof BackupMeta))
      return false;
    BackupMeta that = (BackupMeta)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetBackup_info(), that.isSetBackup_info(), this.backup_info, that.backup_info)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetMeta_files(), that.isSetMeta_files(), this.meta_files, that.meta_files)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetBackup_name(), that.isSetBackup_name(), this.backup_name, that.backup_name)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.full, that.full)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.include_system_space, that.include_system_space)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.create_time, that.create_time)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {backup_info, meta_files, backup_name, full, include_system_space, create_time});
  }

  @Override
  public int compareTo(BackupMeta other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetBackup_info()).compareTo(other.isSetBackup_info());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(backup_info, other.backup_info);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetMeta_files()).compareTo(other.isSetMeta_files());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(meta_files, other.meta_files);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetBackup_name()).compareTo(other.isSetBackup_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(backup_name, other.backup_name);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetFull()).compareTo(other.isSetFull());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(full, other.full);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetInclude_system_space()).compareTo(other.isSetInclude_system_space());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(include_system_space, other.include_system_space);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCreate_time()).compareTo(other.isSetCreate_time());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(create_time, other.create_time);
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
        case BACKUP_INFO:
          if (__field.type == TType.MAP) {
            {
              TMap _map242 = iprot.readMapBegin();
              this.backup_info = new HashMap<Integer,SpaceBackupInfo>(Math.max(0, 2*_map242.size));
              for (int _i243 = 0; 
                   (_map242.size < 0) ? iprot.peekMap() : (_i243 < _map242.size); 
                   ++_i243)
              {
                int _key244;
                SpaceBackupInfo _val245;
                _key244 = iprot.readI32();
                _val245 = new SpaceBackupInfo();
                _val245.read(iprot);
                this.backup_info.put(_key244, _val245);
              }
              iprot.readMapEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case META_FILES:
          if (__field.type == TType.LIST) {
            {
              TList _list246 = iprot.readListBegin();
              this.meta_files = new ArrayList<byte[]>(Math.max(0, _list246.size));
              for (int _i247 = 0; 
                   (_list246.size < 0) ? iprot.peekList() : (_i247 < _list246.size); 
                   ++_i247)
              {
                byte[] _elem248;
                _elem248 = iprot.readBinary();
                this.meta_files.add(_elem248);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case BACKUP_NAME:
          if (__field.type == TType.STRING) {
            this.backup_name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case FULL:
          if (__field.type == TType.BOOL) {
            this.full = iprot.readBool();
            setFullIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case INCLUDE_SYSTEM_SPACE:
          if (__field.type == TType.BOOL) {
            this.include_system_space = iprot.readBool();
            setInclude_system_spaceIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case CREATE_TIME:
          if (__field.type == TType.I64) {
            this.create_time = iprot.readI64();
            setCreate_timeIsSet(true);
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
    if (this.backup_info != null) {
      oprot.writeFieldBegin(BACKUP_INFO_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.I32, TType.STRUCT, this.backup_info.size()));
        for (Map.Entry<Integer, SpaceBackupInfo> _iter249 : this.backup_info.entrySet())        {
          oprot.writeI32(_iter249.getKey());
          _iter249.getValue().write(oprot);
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.meta_files != null) {
      oprot.writeFieldBegin(META_FILES_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRING, this.meta_files.size()));
        for (byte[] _iter250 : this.meta_files)        {
          oprot.writeBinary(_iter250);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.backup_name != null) {
      oprot.writeFieldBegin(BACKUP_NAME_FIELD_DESC);
      oprot.writeBinary(this.backup_name);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(FULL_FIELD_DESC);
    oprot.writeBool(this.full);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(INCLUDE_SYSTEM_SPACE_FIELD_DESC);
    oprot.writeBool(this.include_system_space);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(CREATE_TIME_FIELD_DESC);
    oprot.writeI64(this.create_time);
    oprot.writeFieldEnd();
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
    StringBuilder sb = new StringBuilder("BackupMeta");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("backup_info");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getBackup_info() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getBackup_info(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("meta_files");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getMeta_files() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getMeta_files(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("backup_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getBackup_name() == null) {
      sb.append("null");
    } else {
        int __backup_name_size = Math.min(this.getBackup_name().length, 128);
        for (int i = 0; i < __backup_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getBackup_name()[i]).length() > 1 ? Integer.toHexString(this.getBackup_name()[i]).substring(Integer.toHexString(this.getBackup_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getBackup_name()[i]).toUpperCase());
        }
        if (this.getBackup_name().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("full");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isFull(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("include_system_space");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isInclude_system_space(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("create_time");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCreate_time(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

