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
public class CreateTagIndexReq implements TBase, java.io.Serializable, Cloneable, Comparable<CreateTagIndexReq> {
  private static final TStruct STRUCT_DESC = new TStruct("CreateTagIndexReq");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField INDEX_NAME_FIELD_DESC = new TField("index_name", TType.STRING, (short)2);
  private static final TField TAG_NAME_FIELD_DESC = new TField("tag_name", TType.STRING, (short)3);
  private static final TField FIELDS_FIELD_DESC = new TField("fields", TType.LIST, (short)4);
  private static final TField IF_NOT_EXISTS_FIELD_DESC = new TField("if_not_exists", TType.BOOL, (short)5);
  private static final TField COMMENT_FIELD_DESC = new TField("comment", TType.STRING, (short)6);
  private static final TField INDEX_PARAMS_FIELD_DESC = new TField("index_params", TType.STRUCT, (short)7);

  public int space_id;
  public byte[] index_name;
  public byte[] tag_name;
  public List<IndexFieldDef> fields;
  public boolean if_not_exists;
  public byte[] comment;
  public IndexParams index_params;
  public static final int SPACE_ID = 1;
  public static final int INDEX_NAME = 2;
  public static final int TAG_NAME = 3;
  public static final int FIELDS = 4;
  public static final int IF_NOT_EXISTS = 5;
  public static final int COMMENT = 6;
  public static final int INDEX_PARAMS = 7;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private static final int __IF_NOT_EXISTS_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(INDEX_NAME, new FieldMetaData("index_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(TAG_NAME, new FieldMetaData("tag_name", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(FIELDS, new FieldMetaData("fields", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, IndexFieldDef.class))));
    tmpMetaDataMap.put(IF_NOT_EXISTS, new FieldMetaData("if_not_exists", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(COMMENT, new FieldMetaData("comment", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(INDEX_PARAMS, new FieldMetaData("index_params", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, IndexParams.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(CreateTagIndexReq.class, metaDataMap);
  }

  public CreateTagIndexReq() {
  }

  public CreateTagIndexReq(
      int space_id,
      byte[] index_name,
      byte[] tag_name,
      List<IndexFieldDef> fields,
      boolean if_not_exists) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.index_name = index_name;
    this.tag_name = tag_name;
    this.fields = fields;
    this.if_not_exists = if_not_exists;
    setIf_not_existsIsSet(true);
  }

  public CreateTagIndexReq(
      int space_id,
      byte[] index_name,
      byte[] tag_name,
      List<IndexFieldDef> fields,
      boolean if_not_exists,
      byte[] comment,
      IndexParams index_params) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.index_name = index_name;
    this.tag_name = tag_name;
    this.fields = fields;
    this.if_not_exists = if_not_exists;
    setIf_not_existsIsSet(true);
    this.comment = comment;
    this.index_params = index_params;
  }

  public static class Builder {
    private int space_id;
    private byte[] index_name;
    private byte[] tag_name;
    private List<IndexFieldDef> fields;
    private boolean if_not_exists;
    private byte[] comment;
    private IndexParams index_params;

    BitSet __optional_isset = new BitSet(2);

    public Builder() {
    }

    public Builder setSpace_id(final int space_id) {
      this.space_id = space_id;
      __optional_isset.set(__SPACE_ID_ISSET_ID, true);
      return this;
    }

    public Builder setIndex_name(final byte[] index_name) {
      this.index_name = index_name;
      return this;
    }

    public Builder setTag_name(final byte[] tag_name) {
      this.tag_name = tag_name;
      return this;
    }

    public Builder setFields(final List<IndexFieldDef> fields) {
      this.fields = fields;
      return this;
    }

    public Builder setIf_not_exists(final boolean if_not_exists) {
      this.if_not_exists = if_not_exists;
      __optional_isset.set(__IF_NOT_EXISTS_ISSET_ID, true);
      return this;
    }

    public Builder setComment(final byte[] comment) {
      this.comment = comment;
      return this;
    }

    public Builder setIndex_params(final IndexParams index_params) {
      this.index_params = index_params;
      return this;
    }

    public CreateTagIndexReq build() {
      CreateTagIndexReq result = new CreateTagIndexReq();
      if (__optional_isset.get(__SPACE_ID_ISSET_ID)) {
        result.setSpace_id(this.space_id);
      }
      result.setIndex_name(this.index_name);
      result.setTag_name(this.tag_name);
      result.setFields(this.fields);
      if (__optional_isset.get(__IF_NOT_EXISTS_ISSET_ID)) {
        result.setIf_not_exists(this.if_not_exists);
      }
      result.setComment(this.comment);
      result.setIndex_params(this.index_params);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CreateTagIndexReq(CreateTagIndexReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetIndex_name()) {
      this.index_name = TBaseHelper.deepCopy(other.index_name);
    }
    if (other.isSetTag_name()) {
      this.tag_name = TBaseHelper.deepCopy(other.tag_name);
    }
    if (other.isSetFields()) {
      this.fields = TBaseHelper.deepCopy(other.fields);
    }
    this.if_not_exists = TBaseHelper.deepCopy(other.if_not_exists);
    if (other.isSetComment()) {
      this.comment = TBaseHelper.deepCopy(other.comment);
    }
    if (other.isSetIndex_params()) {
      this.index_params = TBaseHelper.deepCopy(other.index_params);
    }
  }

  public CreateTagIndexReq deepCopy() {
    return new CreateTagIndexReq(this);
  }

  public int getSpace_id() {
    return this.space_id;
  }

  public CreateTagIndexReq setSpace_id(int space_id) {
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

  public byte[] getIndex_name() {
    return this.index_name;
  }

  public CreateTagIndexReq setIndex_name(byte[] index_name) {
    this.index_name = index_name;
    return this;
  }

  public void unsetIndex_name() {
    this.index_name = null;
  }

  // Returns true if field index_name is set (has been assigned a value) and false otherwise
  public boolean isSetIndex_name() {
    return this.index_name != null;
  }

  public void setIndex_nameIsSet(boolean __value) {
    if (!__value) {
      this.index_name = null;
    }
  }

  public byte[] getTag_name() {
    return this.tag_name;
  }

  public CreateTagIndexReq setTag_name(byte[] tag_name) {
    this.tag_name = tag_name;
    return this;
  }

  public void unsetTag_name() {
    this.tag_name = null;
  }

  // Returns true if field tag_name is set (has been assigned a value) and false otherwise
  public boolean isSetTag_name() {
    return this.tag_name != null;
  }

  public void setTag_nameIsSet(boolean __value) {
    if (!__value) {
      this.tag_name = null;
    }
  }

  public List<IndexFieldDef> getFields() {
    return this.fields;
  }

  public CreateTagIndexReq setFields(List<IndexFieldDef> fields) {
    this.fields = fields;
    return this;
  }

  public void unsetFields() {
    this.fields = null;
  }

  // Returns true if field fields is set (has been assigned a value) and false otherwise
  public boolean isSetFields() {
    return this.fields != null;
  }

  public void setFieldsIsSet(boolean __value) {
    if (!__value) {
      this.fields = null;
    }
  }

  public boolean isIf_not_exists() {
    return this.if_not_exists;
  }

  public CreateTagIndexReq setIf_not_exists(boolean if_not_exists) {
    this.if_not_exists = if_not_exists;
    setIf_not_existsIsSet(true);
    return this;
  }

  public void unsetIf_not_exists() {
    __isset_bit_vector.clear(__IF_NOT_EXISTS_ISSET_ID);
  }

  // Returns true if field if_not_exists is set (has been assigned a value) and false otherwise
  public boolean isSetIf_not_exists() {
    return __isset_bit_vector.get(__IF_NOT_EXISTS_ISSET_ID);
  }

  public void setIf_not_existsIsSet(boolean __value) {
    __isset_bit_vector.set(__IF_NOT_EXISTS_ISSET_ID, __value);
  }

  public byte[] getComment() {
    return this.comment;
  }

  public CreateTagIndexReq setComment(byte[] comment) {
    this.comment = comment;
    return this;
  }

  public void unsetComment() {
    this.comment = null;
  }

  // Returns true if field comment is set (has been assigned a value) and false otherwise
  public boolean isSetComment() {
    return this.comment != null;
  }

  public void setCommentIsSet(boolean __value) {
    if (!__value) {
      this.comment = null;
    }
  }

  public IndexParams getIndex_params() {
    return this.index_params;
  }

  public CreateTagIndexReq setIndex_params(IndexParams index_params) {
    this.index_params = index_params;
    return this;
  }

  public void unsetIndex_params() {
    this.index_params = null;
  }

  // Returns true if field index_params is set (has been assigned a value) and false otherwise
  public boolean isSetIndex_params() {
    return this.index_params != null;
  }

  public void setIndex_paramsIsSet(boolean __value) {
    if (!__value) {
      this.index_params = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SPACE_ID:
      if (__value == null) {
        unsetSpace_id();
      } else {
        setSpace_id((Integer)__value);
      }
      break;

    case INDEX_NAME:
      if (__value == null) {
        unsetIndex_name();
      } else {
        setIndex_name((byte[])__value);
      }
      break;

    case TAG_NAME:
      if (__value == null) {
        unsetTag_name();
      } else {
        setTag_name((byte[])__value);
      }
      break;

    case FIELDS:
      if (__value == null) {
        unsetFields();
      } else {
        setFields((List<IndexFieldDef>)__value);
      }
      break;

    case IF_NOT_EXISTS:
      if (__value == null) {
        unsetIf_not_exists();
      } else {
        setIf_not_exists((Boolean)__value);
      }
      break;

    case COMMENT:
      if (__value == null) {
        unsetComment();
      } else {
        setComment((byte[])__value);
      }
      break;

    case INDEX_PARAMS:
      if (__value == null) {
        unsetIndex_params();
      } else {
        setIndex_params((IndexParams)__value);
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

    case INDEX_NAME:
      return getIndex_name();

    case TAG_NAME:
      return getTag_name();

    case FIELDS:
      return getFields();

    case IF_NOT_EXISTS:
      return new Boolean(isIf_not_exists());

    case COMMENT:
      return getComment();

    case INDEX_PARAMS:
      return getIndex_params();

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
    if (!(_that instanceof CreateTagIndexReq))
      return false;
    CreateTagIndexReq that = (CreateTagIndexReq)_that;

    if (!TBaseHelper.equalsNobinary(this.space_id, that.space_id)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetIndex_name(), that.isSetIndex_name(), this.index_name, that.index_name)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetTag_name(), that.isSetTag_name(), this.tag_name, that.tag_name)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetFields(), that.isSetFields(), this.fields, that.fields)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.if_not_exists, that.if_not_exists)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetComment(), that.isSetComment(), this.comment, that.comment)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetIndex_params(), that.isSetIndex_params(), this.index_params, that.index_params)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space_id, index_name, tag_name, fields, if_not_exists, comment, index_params});
  }

  @Override
  public int compareTo(CreateTagIndexReq other) {
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
    lastComparison = Boolean.valueOf(isSetIndex_name()).compareTo(other.isSetIndex_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(index_name, other.index_name);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetTag_name()).compareTo(other.isSetTag_name());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(tag_name, other.tag_name);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetFields()).compareTo(other.isSetFields());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(fields, other.fields);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetIf_not_exists()).compareTo(other.isSetIf_not_exists());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(if_not_exists, other.if_not_exists);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetComment()).compareTo(other.isSetComment());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(comment, other.comment);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetIndex_params()).compareTo(other.isSetIndex_params());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(index_params, other.index_params);
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
        case SPACE_ID:
          if (__field.type == TType.I32) {
            this.space_id = iprot.readI32();
            setSpace_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case INDEX_NAME:
          if (__field.type == TType.STRING) {
            this.index_name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TAG_NAME:
          if (__field.type == TType.STRING) {
            this.tag_name = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case FIELDS:
          if (__field.type == TType.LIST) {
            {
              TList _list179 = iprot.readListBegin();
              this.fields = new ArrayList<IndexFieldDef>(Math.max(0, _list179.size));
              for (int _i180 = 0; 
                   (_list179.size < 0) ? iprot.peekList() : (_i180 < _list179.size); 
                   ++_i180)
              {
                IndexFieldDef _elem181;
                _elem181 = new IndexFieldDef();
                _elem181.read(iprot);
                this.fields.add(_elem181);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case IF_NOT_EXISTS:
          if (__field.type == TType.BOOL) {
            this.if_not_exists = iprot.readBool();
            setIf_not_existsIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case COMMENT:
          if (__field.type == TType.STRING) {
            this.comment = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case INDEX_PARAMS:
          if (__field.type == TType.STRUCT) {
            this.index_params = new IndexParams();
            this.index_params.read(iprot);
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
    oprot.writeFieldBegin(SPACE_ID_FIELD_DESC);
    oprot.writeI32(this.space_id);
    oprot.writeFieldEnd();
    if (this.index_name != null) {
      oprot.writeFieldBegin(INDEX_NAME_FIELD_DESC);
      oprot.writeBinary(this.index_name);
      oprot.writeFieldEnd();
    }
    if (this.tag_name != null) {
      oprot.writeFieldBegin(TAG_NAME_FIELD_DESC);
      oprot.writeBinary(this.tag_name);
      oprot.writeFieldEnd();
    }
    if (this.fields != null) {
      oprot.writeFieldBegin(FIELDS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.fields.size()));
        for (IndexFieldDef _iter182 : this.fields)        {
          _iter182.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(IF_NOT_EXISTS_FIELD_DESC);
    oprot.writeBool(this.if_not_exists);
    oprot.writeFieldEnd();
    if (this.comment != null) {
      if (isSetComment()) {
        oprot.writeFieldBegin(COMMENT_FIELD_DESC);
        oprot.writeBinary(this.comment);
        oprot.writeFieldEnd();
      }
    }
    if (this.index_params != null) {
      if (isSetIndex_params()) {
        oprot.writeFieldBegin(INDEX_PARAMS_FIELD_DESC);
        this.index_params.write(oprot);
        oprot.writeFieldEnd();
      }
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
    StringBuilder sb = new StringBuilder("CreateTagIndexReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("space_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getSpace_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("index_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getIndex_name() == null) {
      sb.append("null");
    } else {
        int __index_name_size = Math.min(this.getIndex_name().length, 128);
        for (int i = 0; i < __index_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getIndex_name()[i]).length() > 1 ? Integer.toHexString(this.getIndex_name()[i]).substring(Integer.toHexString(this.getIndex_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getIndex_name()[i]).toUpperCase());
        }
        if (this.getIndex_name().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("tag_name");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getTag_name() == null) {
      sb.append("null");
    } else {
        int __tag_name_size = Math.min(this.getTag_name().length, 128);
        for (int i = 0; i < __tag_name_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getTag_name()[i]).length() > 1 ? Integer.toHexString(this.getTag_name()[i]).substring(Integer.toHexString(this.getTag_name()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getTag_name()[i]).toUpperCase());
        }
        if (this.getTag_name().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("fields");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getFields() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getFields(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("if_not_exists");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isIf_not_exists(), indent + 1, prettyPrint));
    first = false;
    if (isSetComment())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("comment");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getComment() == null) {
        sb.append("null");
      } else {
          int __comment_size = Math.min(this.getComment().length, 128);
          for (int i = 0; i < __comment_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getComment()[i]).length() > 1 ? Integer.toHexString(this.getComment()[i]).substring(Integer.toHexString(this.getComment()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getComment()[i]).toUpperCase());
          }
          if (this.getComment().length > 128) sb.append(" ...");
      }
      first = false;
    }
    if (isSetIndex_params())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("index_params");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getIndex_params() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getIndex_params(), indent + 1, prettyPrint));
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

