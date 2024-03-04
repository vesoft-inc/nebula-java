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
public class UpdateSessionsReq implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("UpdateSessionsReq");
  private static final TField SESSIONS_FIELD_DESC = new TField("sessions", TType.LIST, (short)1);

  public List<Session> sessions;
  public static final int SESSIONS = 1;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SESSIONS, new FieldMetaData("sessions", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, Session.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(UpdateSessionsReq.class, metaDataMap);
  }

  public UpdateSessionsReq() {
  }

  public UpdateSessionsReq(
      List<Session> sessions) {
    this();
    this.sessions = sessions;
  }

  public static class Builder {
    private List<Session> sessions;

    public Builder() {
    }

    public Builder setSessions(final List<Session> sessions) {
      this.sessions = sessions;
      return this;
    }

    public UpdateSessionsReq build() {
      UpdateSessionsReq result = new UpdateSessionsReq();
      result.setSessions(this.sessions);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public UpdateSessionsReq(UpdateSessionsReq other) {
    if (other.isSetSessions()) {
      this.sessions = TBaseHelper.deepCopy(other.sessions);
    }
  }

  public UpdateSessionsReq deepCopy() {
    return new UpdateSessionsReq(this);
  }

  public List<Session> getSessions() {
    return this.sessions;
  }

  public UpdateSessionsReq setSessions(List<Session> sessions) {
    this.sessions = sessions;
    return this;
  }

  public void unsetSessions() {
    this.sessions = null;
  }

  // Returns true if field sessions is set (has been assigned a value) and false otherwise
  public boolean isSetSessions() {
    return this.sessions != null;
  }

  public void setSessionsIsSet(boolean __value) {
    if (!__value) {
      this.sessions = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SESSIONS:
      if (__value == null) {
        unsetSessions();
      } else {
        setSessions((List<Session>)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SESSIONS:
      return getSessions();

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
    if (!(_that instanceof UpdateSessionsReq))
      return false;
    UpdateSessionsReq that = (UpdateSessionsReq)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetSessions(), that.isSetSessions(), this.sessions, that.sessions)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {sessions});
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
        case SESSIONS:
          if (__field.type == TType.LIST) {
            {
              TList _list384 = iprot.readListBegin();
              this.sessions = new ArrayList<Session>(Math.max(0, _list384.size));
              for (int _i385 = 0; 
                   (_list384.size < 0) ? iprot.peekList() : (_i385 < _list384.size); 
                   ++_i385)
              {
                Session _elem386;
                _elem386 = new Session();
                _elem386.read(iprot);
                this.sessions.add(_elem386);
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
    if (this.sessions != null) {
      oprot.writeFieldBegin(SESSIONS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.sessions.size()));
        for (Session _iter387 : this.sessions)        {
          _iter387.write(oprot);
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
    StringBuilder sb = new StringBuilder("UpdateSessionsReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("sessions");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getSessions() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getSessions(), indent + 1, prettyPrint));
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

