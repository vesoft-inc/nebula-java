/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula;

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
public class NMap implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("NMap");
  private static final TField KVS_FIELD_DESC = new TField("kvs", TType.MAP, (short)1);

  public Map<byte[],Value> kvs;
  public static final int KVS = 1;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(KVS, new FieldMetaData("kvs", TFieldRequirementType.DEFAULT, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.STRING), 
            new StructMetaData(TType.STRUCT, Value.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(NMap.class, metaDataMap);
  }

  public NMap() {
  }

  public NMap(
      Map<byte[],Value> kvs) {
    this();
    this.kvs = kvs;
  }

  public static class Builder {
    private Map<byte[],Value> kvs;

    public Builder() {
    }

    public Builder setKvs(final Map<byte[],Value> kvs) {
      this.kvs = kvs;
      return this;
    }

    public NMap build() {
      NMap result = new NMap();
      result.setKvs(this.kvs);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NMap(NMap other) {
    if (other.isSetKvs()) {
      this.kvs = TBaseHelper.deepCopy(other.kvs);
    }
  }

  public NMap deepCopy() {
    return new NMap(this);
  }

  public Map<byte[],Value> getKvs() {
    return this.kvs;
  }

  public NMap setKvs(Map<byte[],Value> kvs) {
    this.kvs = kvs;
    return this;
  }

  public void unsetKvs() {
    this.kvs = null;
  }

  // Returns true if field kvs is set (has been assigned a value) and false otherwise
  public boolean isSetKvs() {
    return this.kvs != null;
  }

  public void setKvsIsSet(boolean __value) {
    if (!__value) {
      this.kvs = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case KVS:
      if (__value == null) {
        unsetKvs();
      } else {
        setKvs((Map<byte[],Value>)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case KVS:
      return getKvs();

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
    if (!(_that instanceof NMap))
      return false;
    NMap that = (NMap)_that;

    if (!TBaseHelper.equalsSlow(this.isSetKvs(), that.isSetKvs(), this.kvs, that.kvs)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {kvs});
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
        case KVS:
          if (__field.type == TType.MAP) {
            {
              TMap _map4 = iprot.readMapBegin();
              this.kvs = new HashMap<byte[],Value>(Math.max(0, 2*_map4.size));
              for (int _i5 = 0; 
                   (_map4.size < 0) ? iprot.peekMap() : (_i5 < _map4.size); 
                   ++_i5)
              {
                byte[] _key6;
                Value _val7;
                _key6 = iprot.readBinary();
                _val7 = new Value();
                _val7.read(iprot);
                this.kvs.put(_key6, _val7);
              }
              iprot.readMapEnd();
            }
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
    if (this.kvs != null) {
      oprot.writeFieldBegin(KVS_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.STRING, TType.STRUCT, this.kvs.size()));
        for (Map.Entry<byte[], Value> _iter8 : this.kvs.entrySet())        {
          oprot.writeBinary(_iter8.getKey());
          _iter8.getValue().write(oprot);
        }
        oprot.writeMapEnd();
      }
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
    StringBuilder sb = new StringBuilder("NMap");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("kvs");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getKvs() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getKvs(), indent + 1, prettyPrint));
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

