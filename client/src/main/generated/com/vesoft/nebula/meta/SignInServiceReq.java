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
public class SignInServiceReq implements TBase, java.io.Serializable, Cloneable, Comparable<SignInServiceReq> {
  private static final TStruct STRUCT_DESC = new TStruct("SignInServiceReq");
  private static final TField TYPE_FIELD_DESC = new TField("type", TType.I32, (short)1);
  private static final TField CLIENTS_FIELD_DESC = new TField("clients", TType.LIST, (short)2);

  /**
   * 
   * @see ExternalServiceType
   */
  public ExternalServiceType type;
  public List<ServiceClient> clients;
  public static final int TYPE = 1;
  public static final int CLIENTS = 2;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(TYPE, new FieldMetaData("type", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(CLIENTS, new FieldMetaData("clients", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, ServiceClient.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(SignInServiceReq.class, metaDataMap);
  }

  public SignInServiceReq() {
  }

  public SignInServiceReq(
      ExternalServiceType type,
      List<ServiceClient> clients) {
    this();
    this.type = type;
    this.clients = clients;
  }

  public static class Builder {
    private ExternalServiceType type;
    private List<ServiceClient> clients;

    public Builder() {
    }

    public Builder setType(final ExternalServiceType type) {
      this.type = type;
      return this;
    }

    public Builder setClients(final List<ServiceClient> clients) {
      this.clients = clients;
      return this;
    }

    public SignInServiceReq build() {
      SignInServiceReq result = new SignInServiceReq();
      result.setType(this.type);
      result.setClients(this.clients);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SignInServiceReq(SignInServiceReq other) {
    if (other.isSetType()) {
      this.type = TBaseHelper.deepCopy(other.type);
    }
    if (other.isSetClients()) {
      this.clients = TBaseHelper.deepCopy(other.clients);
    }
  }

  public SignInServiceReq deepCopy() {
    return new SignInServiceReq(this);
  }

  /**
   * 
   * @see ExternalServiceType
   */
  public ExternalServiceType getType() {
    return this.type;
  }

  /**
   * 
   * @see ExternalServiceType
   */
  public SignInServiceReq setType(ExternalServiceType type) {
    this.type = type;
    return this;
  }

  public void unsetType() {
    this.type = null;
  }

  // Returns true if field type is set (has been assigned a value) and false otherwise
  public boolean isSetType() {
    return this.type != null;
  }

  public void setTypeIsSet(boolean __value) {
    if (!__value) {
      this.type = null;
    }
  }

  public List<ServiceClient> getClients() {
    return this.clients;
  }

  public SignInServiceReq setClients(List<ServiceClient> clients) {
    this.clients = clients;
    return this;
  }

  public void unsetClients() {
    this.clients = null;
  }

  // Returns true if field clients is set (has been assigned a value) and false otherwise
  public boolean isSetClients() {
    return this.clients != null;
  }

  public void setClientsIsSet(boolean __value) {
    if (!__value) {
      this.clients = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case TYPE:
      if (__value == null) {
        unsetType();
      } else {
        setType((ExternalServiceType)__value);
      }
      break;

    case CLIENTS:
      if (__value == null) {
        unsetClients();
      } else {
        setClients((List<ServiceClient>)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case TYPE:
      return getType();

    case CLIENTS:
      return getClients();

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
    if (!(_that instanceof SignInServiceReq))
      return false;
    SignInServiceReq that = (SignInServiceReq)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetType(), that.isSetType(), this.type, that.type)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetClients(), that.isSetClients(), this.clients, that.clients)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {type, clients});
  }

  @Override
  public int compareTo(SignInServiceReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetType()).compareTo(other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(type, other.type);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetClients()).compareTo(other.isSetClients());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(clients, other.clients);
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
        case TYPE:
          if (__field.type == TType.I32) {
            this.type = ExternalServiceType.findByValue(iprot.readI32());
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case CLIENTS:
          if (__field.type == TType.LIST) {
            {
              TList _list339 = iprot.readListBegin();
              this.clients = new ArrayList<ServiceClient>(Math.max(0, _list339.size));
              for (int _i340 = 0; 
                   (_list339.size < 0) ? iprot.peekList() : (_i340 < _list339.size); 
                   ++_i340)
              {
                ServiceClient _elem341;
                _elem341 = new ServiceClient();
                _elem341.read(iprot);
                this.clients.add(_elem341);
              }
              iprot.readListEnd();
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
    if (this.type != null) {
      oprot.writeFieldBegin(TYPE_FIELD_DESC);
      oprot.writeI32(this.type == null ? 0 : this.type.getValue());
      oprot.writeFieldEnd();
    }
    if (this.clients != null) {
      oprot.writeFieldBegin(CLIENTS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.clients.size()));
        for (ServiceClient _iter342 : this.clients)        {
          _iter342.write(oprot);
        }
        oprot.writeListEnd();
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
    StringBuilder sb = new StringBuilder("SignInServiceReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("type");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getType() == null) {
      sb.append("null");
    } else {
      String type_name = this.getType() == null ? "null" : this.getType().name();
      if (type_name != null) {
        sb.append(type_name);
        sb.append(" (");
      }
      sb.append(this.getType());
      if (type_name != null) {
        sb.append(")");
      }
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("clients");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getClients() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getClients(), indent + 1, prettyPrint));
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

