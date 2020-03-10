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
public class LookUpVertexIndexResp implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("LookUpVertexIndexResp");
  private static final TField RESULT_FIELD_DESC = new TField("result", TType.STRUCT, (short)1);
  private static final TField SCHEMA_FIELD_DESC = new TField("schema", TType.STRUCT, (short)2);
  private static final TField ROWS_FIELD_DESC = new TField("rows", TType.LIST, (short)3);

  public ResponseCommon result;
  public com.vesoft.nebula.Schema schema;
  public List<VertexIndexData> rows;
  public static final int RESULT = 1;
  public static final int SCHEMA = 2;
  public static final int ROWS = 3;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(RESULT, new FieldMetaData("result", TFieldRequirementType.REQUIRED, 
        new StructMetaData(TType.STRUCT, ResponseCommon.class)));
    tmpMetaDataMap.put(SCHEMA, new FieldMetaData("schema", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.Schema.class)));
    tmpMetaDataMap.put(ROWS, new FieldMetaData("rows", TFieldRequirementType.OPTIONAL, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, VertexIndexData.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(LookUpVertexIndexResp.class, metaDataMap);
  }

  public LookUpVertexIndexResp() {
  }

  public LookUpVertexIndexResp(
    ResponseCommon result)
  {
    this();
    this.result = result;
  }

  public LookUpVertexIndexResp(
    ResponseCommon result,
    com.vesoft.nebula.Schema schema,
    List<VertexIndexData> rows)
  {
    this();
    this.result = result;
    this.schema = schema;
    this.rows = rows;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LookUpVertexIndexResp(LookUpVertexIndexResp other) {
    if (other.isSetResult()) {
      this.result = TBaseHelper.deepCopy(other.result);
    }
    if (other.isSetSchema()) {
      this.schema = TBaseHelper.deepCopy(other.schema);
    }
    if (other.isSetRows()) {
      this.rows = TBaseHelper.deepCopy(other.rows);
    }
  }

  public LookUpVertexIndexResp deepCopy() {
    return new LookUpVertexIndexResp(this);
  }

  @Deprecated
  public LookUpVertexIndexResp clone() {
    return new LookUpVertexIndexResp(this);
  }

  public ResponseCommon  getResult() {
    return this.result;
  }

  public LookUpVertexIndexResp setResult(ResponseCommon result) {
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

  public com.vesoft.nebula.Schema  getSchema() {
    return this.schema;
  }

  public LookUpVertexIndexResp setSchema(com.vesoft.nebula.Schema schema) {
    this.schema = schema;
    return this;
  }

  public void unsetSchema() {
    this.schema = null;
  }

  // Returns true if field schema is set (has been assigned a value) and false otherwise
  public boolean isSetSchema() {
    return this.schema != null;
  }

  public void setSchemaIsSet(boolean value) {
    if (!value) {
      this.schema = null;
    }
  }

  public List<VertexIndexData>  getRows() {
    return this.rows;
  }

  public LookUpVertexIndexResp setRows(List<VertexIndexData> rows) {
    this.rows = rows;
    return this;
  }

  public void unsetRows() {
    this.rows = null;
  }

  // Returns true if field rows is set (has been assigned a value) and false otherwise
  public boolean isSetRows() {
    return this.rows != null;
  }

  public void setRowsIsSet(boolean value) {
    if (!value) {
      this.rows = null;
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

    case SCHEMA:
      if (value == null) {
        unsetSchema();
      } else {
        setSchema((com.vesoft.nebula.Schema)value);
      }
      break;

    case ROWS:
      if (value == null) {
        unsetRows();
      } else {
        setRows((List<VertexIndexData>)value);
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

    case SCHEMA:
      return getSchema();

    case ROWS:
      return getRows();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case RESULT:
      return isSetResult();
    case SCHEMA:
      return isSetSchema();
    case ROWS:
      return isSetRows();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof LookUpVertexIndexResp)
      return this.equals((LookUpVertexIndexResp)that);
    return false;
  }

  public boolean equals(LookUpVertexIndexResp that) {
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

    boolean this_present_schema = true && this.isSetSchema();
    boolean that_present_schema = true && that.isSetSchema();
    if (this_present_schema || that_present_schema) {
      if (!(this_present_schema && that_present_schema))
        return false;
      if (!TBaseHelper.equalsNobinary(this.schema, that.schema))
        return false;
    }

    boolean this_present_rows = true && this.isSetRows();
    boolean that_present_rows = true && that.isSetRows();
    if (this_present_rows || that_present_rows) {
      if (!(this_present_rows && that_present_rows))
        return false;
      if (!TBaseHelper.equalsNobinary(this.rows, that.rows))
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

    boolean present_schema = true && (isSetSchema());
    builder.append(present_schema);
    if (present_schema)
      builder.append(schema);

    boolean present_rows = true && (isSetRows());
    builder.append(present_rows);
    if (present_rows)
      builder.append(rows);

    return builder.toHashCode();
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
        case SCHEMA:
          if (field.type == TType.STRUCT) {
            this.schema = new com.vesoft.nebula.Schema();
            this.schema.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case ROWS:
          if (field.type == TType.LIST) {
            {
              TList _list247 = iprot.readListBegin();
              this.rows = new ArrayList<VertexIndexData>(Math.max(0, _list247.size));
              for (int _i248 = 0; 
                   (_list247.size < 0) ? iprot.peekList() : (_i248 < _list247.size); 
                   ++_i248)
              {
                VertexIndexData _elem249;
                _elem249 = new VertexIndexData();
                _elem249.read(iprot);
                this.rows.add(_elem249);
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
    if (this.schema != null) {
      if (isSetSchema()) {
        oprot.writeFieldBegin(SCHEMA_FIELD_DESC);
        this.schema.write(oprot);
        oprot.writeFieldEnd();
      }
    }
    if (this.rows != null) {
      if (isSetRows()) {
        oprot.writeFieldBegin(ROWS_FIELD_DESC);
        {
          oprot.writeListBegin(new TList(TType.STRUCT, this.rows.size()));
          for (VertexIndexData _iter250 : this.rows)          {
            _iter250.write(oprot);
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
    StringBuilder sb = new StringBuilder("LookUpVertexIndexResp");
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
    if (isSetSchema())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("schema");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getSchema() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this. getSchema(), indent + 1, prettyPrint));
      }
      first = false;
    }
    if (isSetRows())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("rows");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getRows() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this. getRows(), indent + 1, prettyPrint));
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

