/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.storage;

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
public class EdgeKeyResponse implements TBase, java.io.Serializable, Cloneable, Comparable<EdgeKeyResponse> {
  private static final TStruct STRUCT_DESC = new TStruct("EdgeKeyResponse");
  private static final TField RESULT_FIELD_DESC = new TField("result", TType.STRUCT, (short)1);
  private static final TField EDGE_KEYS_FIELD_DESC = new TField("edge_keys", TType.LIST, (short)2);

  public ResponseCommon result;
  public List<EdgeKey> edge_keys;
  public static final int RESULT = 1;
  public static final int EDGE_KEYS = 2;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(RESULT, new FieldMetaData("result", TFieldRequirementType.REQUIRED, 
        new StructMetaData(TType.STRUCT, ResponseCommon.class)));
    tmpMetaDataMap.put(EDGE_KEYS, new FieldMetaData("edge_keys", TFieldRequirementType.OPTIONAL, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, EdgeKey.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(EdgeKeyResponse.class, metaDataMap);
  }

  public EdgeKeyResponse() {
  }

  public EdgeKeyResponse(
    ResponseCommon result)
  {
    this();
    this.result = result;
  }

  public EdgeKeyResponse(
    ResponseCommon result,
    List<EdgeKey> edge_keys)
  {
    this();
    this.result = result;
    this.edge_keys = edge_keys;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EdgeKeyResponse(EdgeKeyResponse other) {
    if (other.isSetResult()) {
      this.result = TBaseHelper.deepCopy(other.result);
    }
    if (other.isSetEdge_keys()) {
      this.edge_keys = TBaseHelper.deepCopy(other.edge_keys);
    }
  }

  public EdgeKeyResponse deepCopy() {
    return new EdgeKeyResponse(this);
  }

  @Deprecated
  public EdgeKeyResponse clone() {
    return new EdgeKeyResponse(this);
  }

  public ResponseCommon  getResult() {
    return this.result;
  }

  public EdgeKeyResponse setResult(ResponseCommon result) {
    this.result = result;
    return this;
  }

  public void unsetResult() {
    this.result = null;
  }

  // Returns true if field result is set (has been assigned a value) and false otherwise
  public boolean isSetResult() {
    return this.result != null;
  }

  public void setResultIsSet(boolean value) {
    if (!value) {
      this.result = null;
    }
  }

  public List<EdgeKey>  getEdge_keys() {
    return this.edge_keys;
  }

  public EdgeKeyResponse setEdge_keys(List<EdgeKey> edge_keys) {
    this.edge_keys = edge_keys;
    return this;
  }

  public void unsetEdge_keys() {
    this.edge_keys = null;
  }

  // Returns true if field edge_keys is set (has been assigned a value) and false otherwise
  public boolean isSetEdge_keys() {
    return this.edge_keys != null;
  }

  public void setEdge_keysIsSet(boolean value) {
    if (!value) {
      this.edge_keys = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case RESULT:
      if (value == null) {
        unsetResult();
      } else {
        setResult((ResponseCommon)value);
      }
      break;

    case EDGE_KEYS:
      if (value == null) {
        unsetEdge_keys();
      } else {
        setEdge_keys((List<EdgeKey>)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case RESULT:
      return getResult();

    case EDGE_KEYS:
      return getEdge_keys();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case RESULT:
      return isSetResult();
    case EDGE_KEYS:
      return isSetEdge_keys();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof EdgeKeyResponse)
      return this.equals((EdgeKeyResponse)that);
    return false;
  }

  public boolean equals(EdgeKeyResponse that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_result = true && this.isSetResult();
    boolean that_present_result = true && that.isSetResult();
    if (this_present_result || that_present_result) {
      if (!(this_present_result && that_present_result))
        return false;
      if (!TBaseHelper.equalsNobinary(this.result, that.result))
        return false;
    }

    boolean this_present_edge_keys = true && this.isSetEdge_keys();
    boolean that_present_edge_keys = true && that.isSetEdge_keys();
    if (this_present_edge_keys || that_present_edge_keys) {
      if (!(this_present_edge_keys && that_present_edge_keys))
        return false;
      if (!TBaseHelper.equalsNobinary(this.edge_keys, that.edge_keys))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_result = true && (isSetResult());
    builder.append(present_result);
    if (present_result)
      builder.append(result);

    boolean present_edge_keys = true && (isSetEdge_keys());
    builder.append(present_edge_keys);
    if (present_edge_keys)
      builder.append(edge_keys);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(EdgeKeyResponse other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetResult()).compareTo(other.isSetResult());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(result, other.result);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetEdge_keys()).compareTo(other.isSetEdge_keys());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(edge_keys, other.edge_keys);
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
        case RESULT:
          if (field.type == TType.STRUCT) {
            this.result = new ResponseCommon();
            this.result.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case EDGE_KEYS:
          if (field.type == TType.LIST) {
            {
              TList _list30 = iprot.readListBegin();
              this.edge_keys = new ArrayList<EdgeKey>(Math.max(0, _list30.size));
              for (int _i31 = 0; 
                   (_list30.size < 0) ? iprot.peekList() : (_i31 < _list30.size); 
                   ++_i31)
              {
                EdgeKey _elem32;
                _elem32 = new EdgeKey();
                _elem32.read(iprot);
                this.edge_keys.add(_elem32);
              }
              iprot.readListEnd();
            }
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
    if (this.result != null) {
      oprot.writeFieldBegin(RESULT_FIELD_DESC);
      this.result.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.edge_keys != null) {
      if (isSetEdge_keys()) {
        oprot.writeFieldBegin(EDGE_KEYS_FIELD_DESC);
        {
          oprot.writeListBegin(new TList(TType.STRUCT, this.edge_keys.size()));
          for (EdgeKey _iter33 : this.edge_keys)          {
            _iter33.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
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
    StringBuilder sb = new StringBuilder("EdgeKeyResponse");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("result");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getResult() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getResult(), indent + 1, prettyPrint));
    }
    first = false;
    if (isSetEdge_keys())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("edge_keys");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getEdge_keys() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this. getEdge_keys(), indent + 1, prettyPrint));
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    if (result == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'result' was not present! Struct: " + toString());
    }
    // check that fields of type enum have valid values
  }

}
