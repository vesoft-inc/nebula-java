/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula;

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
public class Vertex implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("Vertex");
  private static final TField VID_FIELD_DESC = new TField("vid", TType.STRING, (short)1);
  private static final TField TAGS_FIELD_DESC = new TField("tags", TType.LIST, (short)2);

  public byte[] vid;
  public List<Tag> tags;
  public static final int VID = 1;
  public static final int TAGS = 2;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(VID, new FieldMetaData("vid", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(TAGS, new FieldMetaData("tags", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, Tag.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(Vertex.class, metaDataMap);
  }

  public Vertex() {
  }

  public Vertex(
    byte[] vid,
    List<Tag> tags)
  {
    this();
    this.vid = vid;
    this.tags = tags;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Vertex(Vertex other) {
    if (other.isSetVid()) {
      this.vid = TBaseHelper.deepCopy(other.vid);
    }
    if (other.isSetTags()) {
      this.tags = TBaseHelper.deepCopy(other.tags);
    }
  }

  public Vertex deepCopy() {
    return new Vertex(this);
  }

  @Deprecated
  public Vertex clone() {
    return new Vertex(this);
  }

  public byte[]  getVid() {
    return this.vid;
  }

  public Vertex setVid(byte[] vid) {
    this.vid = vid;
    return this;
  }

  public void unsetVid() {
    this.vid = null;
  }

  // Returns true if field vid is set (has been assigned a value) and false otherwise
  public boolean isSetVid() {
    return this.vid != null;
  }

  public void setVidIsSet(boolean value) {
    if (!value) {
      this.vid = null;
    }
  }

  public List<Tag>  getTags() {
    return this.tags;
  }

  public Vertex setTags(List<Tag> tags) {
    this.tags = tags;
    return this;
  }

  public void unsetTags() {
    this.tags = null;
  }

  // Returns true if field tags is set (has been assigned a value) and false otherwise
  public boolean isSetTags() {
    return this.tags != null;
  }

  public void setTagsIsSet(boolean value) {
    if (!value) {
      this.tags = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case VID:
      if (value == null) {
        unsetVid();
      } else {
        setVid((byte[])value);
      }
      break;

    case TAGS:
      if (value == null) {
        unsetTags();
      } else {
        setTags((List<Tag>)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case VID:
      return getVid();

    case TAGS:
      return getTags();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case VID:
      return isSetVid();
    case TAGS:
      return isSetTags();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Vertex)
      return this.equals((Vertex)that);
    return false;
  }

  public boolean equals(Vertex that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_vid = true && this.isSetVid();
    boolean that_present_vid = true && that.isSetVid();
    if (this_present_vid || that_present_vid) {
      if (!(this_present_vid && that_present_vid))
        return false;
      if (!TBaseHelper.equalsSlow(this.vid, that.vid))
        return false;
    }

    boolean this_present_tags = true && this.isSetTags();
    boolean that_present_tags = true && that.isSetTags();
    if (this_present_tags || that_present_tags) {
      if (!(this_present_tags && that_present_tags))
        return false;
      if (!TBaseHelper.equalsNobinary(this.tags, that.tags))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_vid = true && (isSetVid());
    builder.append(present_vid);
    if (present_vid)
      builder.append(vid);

    boolean present_tags = true && (isSetTags());
    builder.append(present_tags);
    if (present_tags)
      builder.append(tags);

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
        case VID:
          if (field.type == TType.STRING) {
            this.vid = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case TAGS:
          if (field.type == TType.LIST) {
            {
              TList _list30 = iprot.readListBegin();
              this.tags = new ArrayList<Tag>(Math.max(0, _list30.size));
              for (int _i31 = 0; 
                   (_list30.size < 0) ? iprot.peekList() : (_i31 < _list30.size); 
                   ++_i31)
              {
                Tag _elem32;
                _elem32 = new Tag();
                _elem32.read(iprot);
                this.tags.add(_elem32);
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
    if (this.vid != null) {
      oprot.writeFieldBegin(VID_FIELD_DESC);
      oprot.writeBinary(this.vid);
      oprot.writeFieldEnd();
    }
    if (this.tags != null) {
      oprot.writeFieldBegin(TAGS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.tags.size()));
        for (Tag _iter33 : this.tags)        {
          _iter33.write(oprot);
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
    StringBuilder sb = new StringBuilder("Vertex");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("vid");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getVid() == null) {
      sb.append("null");
    } else {
        int __vid_size = Math.min(this. getVid().length, 128);
        for (int i = 0; i < __vid_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this. getVid()[i]).length() > 1 ? Integer.toHexString(this. getVid()[i]).substring(Integer.toHexString(this. getVid()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this. getVid()[i]).toUpperCase());
        }
        if (this. getVid().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("tags");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getTags() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getTags(), indent + 1, prettyPrint));
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // check that fields of type enum have valid values
  }

}

