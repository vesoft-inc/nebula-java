/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
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
public class GetStateRequest implements TBase, java.io.Serializable, Cloneable, Comparable<GetStateRequest> {
  private static final TStruct STRUCT_DESC = new TStruct("GetStateRequest");
  private static final TField SPACE_FIELD_DESC = new TField("space", TType.I32, (short)1);
  private static final TField PART_FIELD_DESC = new TField("part", TType.I32, (short)2);

  public int space;
  public int part;
  public static final int SPACE = 1;
  public static final int PART = 2;

  // isset id assignments
  private static final int __SPACE_ISSET_ID = 0;
  private static final int __PART_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE, new FieldMetaData("space", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(PART, new FieldMetaData("part", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(GetStateRequest.class, metaDataMap);
  }

  public GetStateRequest() {
  }

  public GetStateRequest(
      int space,
      int part) {
    this();
    this.space = space;
    setSpaceIsSet(true);
    this.part = part;
    setPartIsSet(true);
  }

  public static class Builder {
    private int space;
    private int part;

    BitSet __optional_isset = new BitSet(2);

    public Builder() {
    }

    public Builder setSpace(final int space) {
      this.space = space;
      __optional_isset.set(__SPACE_ISSET_ID, true);
      return this;
    }

    public Builder setPart(final int part) {
      this.part = part;
      __optional_isset.set(__PART_ISSET_ID, true);
      return this;
    }

    public GetStateRequest build() {
      GetStateRequest result = new GetStateRequest();
      if (__optional_isset.get(__SPACE_ISSET_ID)) {
        result.setSpace(this.space);
      }
      if (__optional_isset.get(__PART_ISSET_ID)) {
        result.setPart(this.part);
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
  public GetStateRequest(GetStateRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space = TBaseHelper.deepCopy(other.space);
    this.part = TBaseHelper.deepCopy(other.part);
  }

  public GetStateRequest deepCopy() {
    return new GetStateRequest(this);
  }

  public int getSpace() {
    return this.space;
  }

  public GetStateRequest setSpace(int space) {
    this.space = space;
    setSpaceIsSet(true);
    return this;
  }

  public void unsetSpace() {
    __isset_bit_vector.clear(__SPACE_ISSET_ID);
  }

  // Returns true if field space is set (has been assigned a value) and false otherwise
  public boolean isSetSpace() {
    return __isset_bit_vector.get(__SPACE_ISSET_ID);
  }

  public void setSpaceIsSet(boolean __value) {
    __isset_bit_vector.set(__SPACE_ISSET_ID, __value);
  }

  public int getPart() {
    return this.part;
  }

  public GetStateRequest setPart(int part) {
    this.part = part;
    setPartIsSet(true);
    return this;
  }

  public void unsetPart() {
    __isset_bit_vector.clear(__PART_ISSET_ID);
  }

  // Returns true if field part is set (has been assigned a value) and false otherwise
  public boolean isSetPart() {
    return __isset_bit_vector.get(__PART_ISSET_ID);
  }

  public void setPartIsSet(boolean __value) {
    __isset_bit_vector.set(__PART_ISSET_ID, __value);
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case SPACE:
      if (__value == null) {
        unsetSpace();
      } else {
        setSpace((Integer)__value);
      }
      break;

    case PART:
      if (__value == null) {
        unsetPart();
      } else {
        setPart((Integer)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case SPACE:
      return new Integer(getSpace());

    case PART:
      return new Integer(getPart());

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
    if (!(_that instanceof GetStateRequest))
      return false;
    GetStateRequest that = (GetStateRequest)_that;

    if (!TBaseHelper.equalsNobinary(this.space, that.space)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.part, that.part)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space, part});
  }

  @Override
  public int compareTo(GetStateRequest other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSpace()).compareTo(other.isSetSpace());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(space, other.space);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPart()).compareTo(other.isSetPart());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(part, other.part);
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
        case SPACE:
          if (__field.type == TType.I32) {
            this.space = iprot.readI32();
            setSpaceIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case PART:
          if (__field.type == TType.I32) {
            this.part = iprot.readI32();
            setPartIsSet(true);
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
    oprot.writeFieldBegin(SPACE_FIELD_DESC);
    oprot.writeI32(this.space);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(PART_FIELD_DESC);
    oprot.writeI32(this.part);
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
    StringBuilder sb = new StringBuilder("GetStateRequest");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("space");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getSpace(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("part");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getPart(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

