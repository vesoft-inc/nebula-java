/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.graph;

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

@SuppressWarnings({ "unused", "serial", "unchecked" })
public class PathEntry extends TUnion<PathEntry> implements Comparable<PathEntry> {
  public static boolean DEFAULT_PRETTY_PRINT = true;
  private static final TStruct STRUCT_DESC = new TStruct("PathEntry");
  private static final TField VERTEX_FIELD_DESC = new TField("vertex", TType.STRUCT, (short)1);
  private static final TField EDGE_FIELD_DESC = new TField("edge", TType.STRUCT, (short)2);

  public static final int VERTEX = 1;
  public static final int EDGE = 2;

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(VERTEX, new FieldMetaData("vertex", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, Vertex.class)));
    tmpMetaDataMap.put(EDGE, new FieldMetaData("edge", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, Edge.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  public PathEntry() {
    super();
  }

  public PathEntry(int setField, Object value) {
    super(setField, value);
  }

  public PathEntry(PathEntry other) {
    super(other);
  }
  public PathEntry deepCopy() {
    return new PathEntry(this);
  }

  public static PathEntry vertex(Vertex value) {
    PathEntry x = new PathEntry();
    x.setVertex(value);
    return x;
  }

  public static PathEntry edge(Edge value) {
    PathEntry x = new PathEntry();
    x.setEdge(value);
    return x;
  }


  @Override
  protected void checkType(short setField, Object value) throws ClassCastException {
    switch (setField) {
      case VERTEX:
        if (value instanceof Vertex) {
          break;
        }
        throw new ClassCastException("Was expecting value of type Vertex for field 'vertex', but got " + value.getClass().getSimpleName());
      case EDGE:
        if (value instanceof Edge) {
          break;
        }
        throw new ClassCastException("Was expecting value of type Edge for field 'edge', but got " + value.getClass().getSimpleName());
      default:
        throw new IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  public void read(TProtocol iprot) throws TException {
    setField_ = 0;
    value_ = null;
    iprot.readStructBegin(metaDataMap);
    TField field = iprot.readFieldBegin();
    if (field.type != TType.STOP)
    {
      value_ = readValue(iprot, field);
      if (value_ != null)
      {
        switch (field.id) {
          case VERTEX:
            if (field.type == VERTEX_FIELD_DESC.type) {
              setField_ = field.id;
            }
            break;
          case EDGE:
            if (field.type == EDGE_FIELD_DESC.type) {
              setField_ = field.id;
            }
            break;
        }
      }
      iprot.readFieldEnd();
      iprot.readFieldBegin();
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();
  }

  @Override
  protected Object readValue(TProtocol iprot, TField field) throws TException {
    switch (field.id) {
      case VERTEX:
        if (field.type == VERTEX_FIELD_DESC.type) {
          Vertex vertex;
          vertex = new Vertex();
          vertex.read(iprot);
          return vertex;
        } else {
          TProtocolUtil.skip(iprot, field.type);
          return null;
        }
      case EDGE:
        if (field.type == EDGE_FIELD_DESC.type) {
          Edge edge;
          edge = new Edge();
          edge.read(iprot);
          return edge;
        } else {
          TProtocolUtil.skip(iprot, field.type);
          return null;
        }
      default:
        TProtocolUtil.skip(iprot, field.type);
        return null;
    }
  }

  @Override
  protected void writeValue(TProtocol oprot, short setField, Object value) throws TException {
    switch (setField) {
      case VERTEX:
        Vertex vertex = (Vertex)getFieldValue();
        vertex.write(oprot);
        return;
      case EDGE:
        Edge edge = (Edge)getFieldValue();
        edge.write(oprot);
        return;
      default:
        throw new IllegalStateException("Cannot write union with unknown field " + setField);
    }
  }

  @Override
  protected TField getFieldDesc(int setField) {
    switch (setField) {
      case VERTEX:
        return VERTEX_FIELD_DESC;
      case EDGE:
        return EDGE_FIELD_DESC;
      default:
        throw new IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  protected TStruct getStructDesc() {
    return STRUCT_DESC;
  }

  public Vertex  getVertex() {
    if (getSetField() == VERTEX) {
      return (Vertex)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'vertex' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setVertex(Vertex value) {
    if (value == null) throw new NullPointerException();
    setField_ = VERTEX;
    value_ = value;
  }

  public Edge  getEdge() {
    if (getSetField() == EDGE) {
      return (Edge)getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field 'edge' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  public void setEdge(Edge value) {
    if (value == null) throw new NullPointerException();
    setField_ = EDGE;
    value_ = value;
  }

  public boolean equals(Object other) {
    if (other instanceof PathEntry) {
      return equals((PathEntry)other);
    } else {
      return false;
    }
  }

  public boolean equals(PathEntry other) {
    return equalsNobinaryImpl(other);
  }

  @Override
  public int compareTo(PathEntry other) {
    return compareToImpl(other);
  }


  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getSetField()).append(getFieldValue()).toHashCode();
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
    StringBuilder sb = new StringBuilder("PathEntry");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    // Only print this field if it is the set field
    if (getSetField() == VERTEX)
    {
      sb.append(indentStr);
      sb.append("vertex");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getVertex() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this. getVertex(), indent + 1, prettyPrint));
      }
      first = false;
    }
    // Only print this field if it is the set field
    if (getSetField() == EDGE)
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("edge");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getEdge() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this. getEdge(), indent + 1, prettyPrint));
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }


}
