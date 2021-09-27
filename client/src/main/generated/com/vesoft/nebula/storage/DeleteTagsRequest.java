/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.storage;

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
public class DeleteTagsRequest implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("DeleteTagsRequest");
  private static final TField SPACE_ID_FIELD_DESC = new TField("space_id", TType.I32, (short)1);
  private static final TField PARTS_FIELD_DESC = new TField("parts", TType.MAP, (short)2);
  private static final TField COMMON_FIELD_DESC = new TField("common", TType.STRUCT, (short)3);

  public int space_id;
  public Map<Integer,List<DelTags>> parts;
  public RequestCommon common;
  public static final int SPACE_ID = 1;
  public static final int PARTS = 2;
  public static final int COMMON = 3;

  // isset id assignments
  private static final int __SPACE_ID_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE_ID, new FieldMetaData("space_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(PARTS, new FieldMetaData("parts", TFieldRequirementType.DEFAULT, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.I32), 
            new ListMetaData(TType.LIST, 
                new StructMetaData(TType.STRUCT, DelTags.class)))));
    tmpMetaDataMap.put(COMMON, new FieldMetaData("common", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, RequestCommon.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(DeleteTagsRequest.class, metaDataMap);
  }

  public DeleteTagsRequest() {
  }

  public DeleteTagsRequest(
      int space_id,
      Map<Integer,List<DelTags>> parts) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.parts = parts;
  }

  public DeleteTagsRequest(
      int space_id,
      Map<Integer,List<DelTags>> parts,
      RequestCommon common) {
    this();
    this.space_id = space_id;
    setSpace_idIsSet(true);
    this.parts = parts;
    this.common = common;
  }

  public static class Builder {
    private int space_id;
    private Map<Integer,List<DelTags>> parts;
    private RequestCommon common;

    BitSet __optional_isset = new BitSet(1);

    public Builder() {
    }

    public Builder setSpace_id(final int space_id) {
      this.space_id = space_id;
      __optional_isset.set(__SPACE_ID_ISSET_ID, true);
      return this;
    }

    public Builder setParts(final Map<Integer,List<DelTags>> parts) {
      this.parts = parts;
      return this;
    }

    public Builder setCommon(final RequestCommon common) {
      this.common = common;
      return this;
    }

    public DeleteTagsRequest build() {
      DeleteTagsRequest result = new DeleteTagsRequest();
      if (__optional_isset.get(__SPACE_ID_ISSET_ID)) {
        result.setSpace_id(this.space_id);
      }
      result.setParts(this.parts);
      result.setCommon(this.common);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DeleteTagsRequest(DeleteTagsRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space_id = TBaseHelper.deepCopy(other.space_id);
    if (other.isSetParts()) {
      this.parts = TBaseHelper.deepCopy(other.parts);
    }
    if (other.isSetCommon()) {
      this.common = TBaseHelper.deepCopy(other.common);
    }
  }

  public DeleteTagsRequest deepCopy() {
    return new DeleteTagsRequest(this);
  }

  public int getSpace_id() {
    return this.space_id;
  }

  public DeleteTagsRequest setSpace_id(int space_id) {
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

  public Map<Integer,List<DelTags>> getParts() {
    return this.parts;
  }

  public DeleteTagsRequest setParts(Map<Integer,List<DelTags>> parts) {
    this.parts = parts;
    return this;
  }

  public void unsetParts() {
    this.parts = null;
  }

  // Returns true if field parts is set (has been assigned a value) and false otherwise
  public boolean isSetParts() {
    return this.parts != null;
  }

  public void setPartsIsSet(boolean __value) {
    if (!__value) {
      this.parts = null;
    }
  }

  public RequestCommon getCommon() {
    return this.common;
  }

  public DeleteTagsRequest setCommon(RequestCommon common) {
    this.common = common;
    return this;
  }

  public void unsetCommon() {
    this.common = null;
  }

  // Returns true if field common is set (has been assigned a value) and false otherwise
  public boolean isSetCommon() {
    return this.common != null;
  }

  public void setCommonIsSet(boolean __value) {
    if (!__value) {
      this.common = null;
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

    case PARTS:
      if (__value == null) {
        unsetParts();
      } else {
        setParts((Map<Integer,List<DelTags>>)__value);
      }
      break;

    case COMMON:
      if (__value == null) {
        unsetCommon();
      } else {
        setCommon((RequestCommon)__value);
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

    case PARTS:
      return getParts();

    case COMMON:
      return getCommon();

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
    if (!(_that instanceof DeleteTagsRequest))
      return false;
    DeleteTagsRequest that = (DeleteTagsRequest)_that;

    if (!TBaseHelper.equalsNobinary(this.space_id, that.space_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetParts(), that.isSetParts(), this.parts, that.parts)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetCommon(), that.isSetCommon(), this.common, that.common)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space_id, parts, common});
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
        case PARTS:
          if (__field.type == TType.MAP) {
            {
              TMap _map144 = iprot.readMapBegin();
              this.parts = new HashMap<Integer,List<DelTags>>(Math.max(0, 2*_map144.size));
              for (int _i145 = 0; 
                   (_map144.size < 0) ? iprot.peekMap() : (_i145 < _map144.size); 
                   ++_i145)
              {
                int _key146;
                List<DelTags> _val147;
                _key146 = iprot.readI32();
                {
                  TList _list148 = iprot.readListBegin();
                  _val147 = new ArrayList<DelTags>(Math.max(0, _list148.size));
                  for (int _i149 = 0; 
                       (_list148.size < 0) ? iprot.peekList() : (_i149 < _list148.size); 
                       ++_i149)
                  {
                    DelTags _elem150;
                    _elem150 = new DelTags();
                    _elem150.read(iprot);
                    _val147.add(_elem150);
                  }
                  iprot.readListEnd();
                }
                this.parts.put(_key146, _val147);
              }
              iprot.readMapEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case COMMON:
          if (__field.type == TType.STRUCT) {
            this.common = new RequestCommon();
            this.common.read(iprot);
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
    if (this.parts != null) {
      oprot.writeFieldBegin(PARTS_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.I32, TType.LIST, this.parts.size()));
        for (Map.Entry<Integer, List<DelTags>> _iter151 : this.parts.entrySet())        {
          oprot.writeI32(_iter151.getKey());
          {
            oprot.writeListBegin(new TList(TType.STRUCT, _iter151.getValue().size()));
            for (DelTags _iter152 : _iter151.getValue())            {
              _iter152.write(oprot);
            }
            oprot.writeListEnd();
          }
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.common != null) {
      if (isSetCommon()) {
        oprot.writeFieldBegin(COMMON_FIELD_DESC);
        this.common.write(oprot);
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
    StringBuilder sb = new StringBuilder("DeleteTagsRequest");
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
    sb.append("parts");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getParts() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getParts(), indent + 1, prettyPrint));
    }
    first = false;
    if (isSetCommon())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("common");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getCommon() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getCommon(), indent + 1, prettyPrint));
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

