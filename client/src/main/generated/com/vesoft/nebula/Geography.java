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

@SuppressWarnings({ "unused", "serial", "unchecked" })
public class Geography extends TUnion<Geography> implements Comparable<Geography> {
  private static final TStruct STRUCT_DESC = new TStruct("Geography");
  private static final TField PT_VAL_FIELD_DESC = new TField("ptVal", TType.STRUCT, (short)1);
  private static final TField LS_VAL_FIELD_DESC = new TField("lsVal", TType.STRUCT, (short)2);
  private static final TField PG_VAL_FIELD_DESC = new TField("pgVal", TType.STRUCT, (short)3);

  public static final int PTVAL = 1;
  public static final int LSVAL = 2;
  public static final int PGVAL = 3;

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(PTVAL, new FieldMetaData("ptVal", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, Point.class)));
    tmpMetaDataMap.put(LSVAL, new FieldMetaData("lsVal", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, LineString.class)));
    tmpMetaDataMap.put(PGVAL, new FieldMetaData("pgVal", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, Polygon.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  public Geography() {
    super();
  }

  public Geography(int setField, Object __value) {
    super(setField, __value);
  }

  public Geography(Geography other) {
    super(other);
  }

  public Geography deepCopy() {
    return new Geography(this);
  }

  public static Geography ptVal(Point __value) {
    Geography x = new Geography();
    x.setPtVal(__value);
    return x;
  }

  public static Geography lsVal(LineString __value) {
    Geography x = new Geography();
    x.setLsVal(__value);
    return x;
  }

  public static Geography pgVal(Polygon __value) {
    Geography x = new Geography();
    x.setPgVal(__value);
    return x;
  }


  @Override
  protected void checkType(short setField, Object __value) throws ClassCastException {
    switch (setField) {
      case PTVAL:
        if (__value instanceof Point) {
          break;
        }
        throw new ClassCastException("Was expecting value of type Point for field 'ptVal', but got " + __value.getClass().getSimpleName());
      case LSVAL:
        if (__value instanceof LineString) {
          break;
        }
        throw new ClassCastException("Was expecting value of type LineString for field 'lsVal', but got " + __value.getClass().getSimpleName());
      case PGVAL:
        if (__value instanceof Polygon) {
          break;
        }
        throw new ClassCastException("Was expecting value of type Polygon for field 'pgVal', but got " + __value.getClass().getSimpleName());
      default:
        throw new IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  public void read(TProtocol iprot) throws TException {
    setField_ = 0;
    value_ = null;
    iprot.readStructBegin(metaDataMap);
    TField __field = iprot.readFieldBegin();
    if (__field.type != TType.STOP)
    {
      value_ = readValue(iprot, __field);
      if (value_ != null)
      {
        switch (__field.id) {
          case PTVAL:
            if (__field.type == PT_VAL_FIELD_DESC.type) {
              setField_ = __field.id;
            }
            break;
          case LSVAL:
            if (__field.type == LS_VAL_FIELD_DESC.type) {
              setField_ = __field.id;
            }
            break;
          case PGVAL:
            if (__field.type == PG_VAL_FIELD_DESC.type) {
              setField_ = __field.id;
            }
            break;
        }
      }
      iprot.readFieldEnd();
      TField __stopField = iprot.readFieldBegin();
      if (__stopField.type != TType.STOP) {
        throw new TProtocolException(TProtocolException.INVALID_DATA, "Union 'Geography' is missing a STOP byte");
      }
    }
    iprot.readStructEnd();
  }

  @Override
  protected Object readValue(TProtocol iprot, TField __field) throws TException {
    switch (__field.id) {
      case PTVAL:
        if (__field.type == PT_VAL_FIELD_DESC.type) {
          Point ptVal;
          ptVal = new Point();
          ptVal.read(iprot);
          return ptVal;
        }
        break;
      case LSVAL:
        if (__field.type == LS_VAL_FIELD_DESC.type) {
          LineString lsVal;
          lsVal = new LineString();
          lsVal.read(iprot);
          return lsVal;
        }
        break;
      case PGVAL:
        if (__field.type == PG_VAL_FIELD_DESC.type) {
          Polygon pgVal;
          pgVal = new Polygon();
          pgVal.read(iprot);
          return pgVal;
        }
        break;
    }
    TProtocolUtil.skip(iprot, __field.type);
    return null;
  }

  @Override
  protected void writeValue(TProtocol oprot, short setField, Object __value) throws TException {
    switch (setField) {
      case PTVAL:
        Point ptVal = (Point)getFieldValue();
        ptVal.write(oprot);
        return;
      case LSVAL:
        LineString lsVal = (LineString)getFieldValue();
        lsVal.write(oprot);
        return;
      case PGVAL:
        Polygon pgVal = (Polygon)getFieldValue();
        pgVal.write(oprot);
        return;
      default:
        throw new IllegalStateException("Cannot write union with unknown field " + setField);
    }
  }

  @Override
  protected TField getFieldDesc(int setField) {
    switch (setField) {
      case PTVAL:
        return PT_VAL_FIELD_DESC;
      case LSVAL:
        return LS_VAL_FIELD_DESC;
      case PGVAL:
        return PG_VAL_FIELD_DESC;
      default:
        throw new IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  protected TStruct getStructDesc() {
    return STRUCT_DESC;
  }

  @Override
  protected Map<Integer, FieldMetaData> getMetaDataMap() { return metaDataMap; }

  private Object __getValue(int expectedFieldId) {
    if (getSetField() == expectedFieldId) {
      return getFieldValue();
    } else {
      throw new RuntimeException("Cannot get field '" + getFieldDesc(expectedFieldId).name + "' because union is currently set to " + getFieldDesc(getSetField()).name);
    }
  }

  private void __setValue(int fieldId, Object __value) {
    if (__value == null) throw new NullPointerException();
    setField_ = fieldId;
    value_ = __value;
  }

  public Point getPtVal() {
    return (Point) __getValue(PTVAL);
  }

  public void setPtVal(Point __value) {
    __setValue(PTVAL, __value);
  }

  public LineString getLsVal() {
    return (LineString) __getValue(LSVAL);
  }

  public void setLsVal(LineString __value) {
    __setValue(LSVAL, __value);
  }

  public Polygon getPgVal() {
    return (Polygon) __getValue(PGVAL);
  }

  public void setPgVal(Polygon __value) {
    __setValue(PGVAL, __value);
  }

  public boolean equals(Object other) {
    if (other instanceof Geography) {
      return equals((Geography)other);
    } else {
      return false;
    }
  }

  public boolean equals(Geography other) {
    return equalsNobinaryImpl(other);
  }

  @Override
  public int compareTo(Geography other) {
    return compareToImpl(other);
  }


  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {getSetField(), getFieldValue()});
  }

}
