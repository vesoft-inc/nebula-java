/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.meta;

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
public class PartItem implements TBase, java.io.Serializable, Cloneable, Comparable<PartItem> {
  private static final TStruct STRUCT_DESC = new TStruct("PartItem");
  private static final TField PART_ID_FIELD_DESC = new TField("part_id", TType.I32, (short)1);
  private static final TField LEADER_FIELD_DESC = new TField("leader", TType.STRUCT, (short)2);
  private static final TField PEERS_FIELD_DESC = new TField("peers", TType.LIST, (short)3);
  private static final TField LOSTS_FIELD_DESC = new TField("losts", TType.LIST, (short)4);

  public int part_id;
  public com.vesoft.nebula.HostAddr leader;
  public List<com.vesoft.nebula.HostAddr> peers;
  public List<com.vesoft.nebula.HostAddr> losts;
  public static final int PART_ID = 1;
  public static final int LEADER = 2;
  public static final int PEERS = 3;
  public static final int LOSTS = 4;
  public static boolean DEFAULT_PRETTY_PRINT = true;

  // isset id assignments
  private static final int __PART_ID_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<Integer, FieldMetaData> metaDataMap;
  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(PART_ID, new FieldMetaData("part_id", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(LEADER, new FieldMetaData("leader", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(PEERS, new FieldMetaData("peers", TFieldRequirementType.REQUIRED, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class))));
    tmpMetaDataMap.put(LOSTS, new FieldMetaData("losts", TFieldRequirementType.REQUIRED, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(PartItem.class, metaDataMap);
  }

  public PartItem() {
  }

  public PartItem(
    int part_id,
    List<com.vesoft.nebula.HostAddr> peers,
    List<com.vesoft.nebula.HostAddr> losts)
  {
    this();
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.peers = peers;
    this.losts = losts;
  }

  public PartItem(
    int part_id,
    com.vesoft.nebula.HostAddr leader,
    List<com.vesoft.nebula.HostAddr> peers,
    List<com.vesoft.nebula.HostAddr> losts)
  {
    this();
    this.part_id = part_id;
    setPart_idIsSet(true);
    this.leader = leader;
    this.peers = peers;
    this.losts = losts;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PartItem(PartItem other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.part_id = TBaseHelper.deepCopy(other.part_id);
    if (other.isSetLeader()) {
      this.leader = TBaseHelper.deepCopy(other.leader);
    }
    if (other.isSetPeers()) {
      this.peers = TBaseHelper.deepCopy(other.peers);
    }
    if (other.isSetLosts()) {
      this.losts = TBaseHelper.deepCopy(other.losts);
    }
  }

  public PartItem deepCopy() {
    return new PartItem(this);
  }

  @Deprecated
  public PartItem clone() {
    return new PartItem(this);
  }

  public int  getPart_id() {
    return this.part_id;
  }

  public PartItem setPart_id(int part_id) {
    this.part_id = part_id;
    setPart_idIsSet(true);
    return this;
  }

  public void unsetPart_id() {
    __isset_bit_vector.clear(__PART_ID_ISSET_ID);
  }

  // Returns true if field part_id is set (has been assigned a value) and false otherwise
  public boolean isSetPart_id() {
    return __isset_bit_vector.get(__PART_ID_ISSET_ID);
  }

  public void setPart_idIsSet(boolean value) {
    __isset_bit_vector.set(__PART_ID_ISSET_ID, value);
  }

  public com.vesoft.nebula.HostAddr  getLeader() {
    return this.leader;
  }

  public PartItem setLeader(com.vesoft.nebula.HostAddr leader) {
    this.leader = leader;
    return this;
  }

  public void unsetLeader() {
    this.leader = null;
  }

  // Returns true if field leader is set (has been assigned a value) and false otherwise
  public boolean isSetLeader() {
    return this.leader != null;
  }

  public void setLeaderIsSet(boolean value) {
    if (!value) {
      this.leader = null;
    }
  }

  public List<com.vesoft.nebula.HostAddr>  getPeers() {
    return this.peers;
  }

  public PartItem setPeers(List<com.vesoft.nebula.HostAddr> peers) {
    this.peers = peers;
    return this;
  }

  public void unsetPeers() {
    this.peers = null;
  }

  // Returns true if field peers is set (has been assigned a value) and false otherwise
  public boolean isSetPeers() {
    return this.peers != null;
  }

  public void setPeersIsSet(boolean value) {
    if (!value) {
      this.peers = null;
    }
  }

  public List<com.vesoft.nebula.HostAddr>  getLosts() {
    return this.losts;
  }

  public PartItem setLosts(List<com.vesoft.nebula.HostAddr> losts) {
    this.losts = losts;
    return this;
  }

  public void unsetLosts() {
    this.losts = null;
  }

  // Returns true if field losts is set (has been assigned a value) and false otherwise
  public boolean isSetLosts() {
    return this.losts != null;
  }

  public void setLostsIsSet(boolean value) {
    if (!value) {
      this.losts = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case PART_ID:
      if (value == null) {
        unsetPart_id();
      } else {
        setPart_id((Integer)value);
      }
      break;

    case LEADER:
      if (value == null) {
        unsetLeader();
      } else {
        setLeader((com.vesoft.nebula.HostAddr)value);
      }
      break;

    case PEERS:
      if (value == null) {
        unsetPeers();
      } else {
        setPeers((List<com.vesoft.nebula.HostAddr>)value);
      }
      break;

    case LOSTS:
      if (value == null) {
        unsetLosts();
      } else {
        setLosts((List<com.vesoft.nebula.HostAddr>)value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case PART_ID:
      return new Integer(getPart_id());

    case LEADER:
      return getLeader();

    case PEERS:
      return getPeers();

    case LOSTS:
      return getLosts();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  // Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise
  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case PART_ID:
      return isSetPart_id();
    case LEADER:
      return isSetLeader();
    case PEERS:
      return isSetPeers();
    case LOSTS:
      return isSetLosts();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof PartItem)
      return this.equals((PartItem)that);
    return false;
  }

  public boolean equals(PartItem that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_part_id = true;
    boolean that_present_part_id = true;
    if (this_present_part_id || that_present_part_id) {
      if (!(this_present_part_id && that_present_part_id))
        return false;
      if (!TBaseHelper.equalsNobinary(this.part_id, that.part_id))
        return false;
    }

    boolean this_present_leader = true && this.isSetLeader();
    boolean that_present_leader = true && that.isSetLeader();
    if (this_present_leader || that_present_leader) {
      if (!(this_present_leader && that_present_leader))
        return false;
      if (!TBaseHelper.equalsNobinary(this.leader, that.leader))
        return false;
    }

    boolean this_present_peers = true && this.isSetPeers();
    boolean that_present_peers = true && that.isSetPeers();
    if (this_present_peers || that_present_peers) {
      if (!(this_present_peers && that_present_peers))
        return false;
      if (!TBaseHelper.equalsNobinary(this.peers, that.peers))
        return false;
    }

    boolean this_present_losts = true && this.isSetLosts();
    boolean that_present_losts = true && that.isSetLosts();
    if (this_present_losts || that_present_losts) {
      if (!(this_present_losts && that_present_losts))
        return false;
      if (!TBaseHelper.equalsNobinary(this.losts, that.losts))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_part_id = true;
    builder.append(present_part_id);
    if (present_part_id)
      builder.append(part_id);

    boolean present_leader = true && (isSetLeader());
    builder.append(present_leader);
    if (present_leader)
      builder.append(leader);

    boolean present_peers = true && (isSetPeers());
    builder.append(present_peers);
    if (present_peers)
      builder.append(peers);

    boolean present_losts = true && (isSetLosts());
    builder.append(present_losts);
    if (present_losts)
      builder.append(losts);

    return builder.toHashCode();
  }

  @Override
  public int compareTo(PartItem other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetPart_id()).compareTo(other.isSetPart_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(part_id, other.part_id);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLeader()).compareTo(other.isSetLeader());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(leader, other.leader);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetPeers()).compareTo(other.isSetPeers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(peers, other.peers);
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLosts()).compareTo(other.isSetLosts());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(losts, other.losts);
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
        case PART_ID:
          if (field.type == TType.I32) {
            this.part_id = iprot.readI32();
            setPart_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case LEADER:
          if (field.type == TType.STRUCT) {
            this.leader = new com.vesoft.nebula.HostAddr();
            this.leader.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case PEERS:
          if (field.type == TType.LIST) {
            {
              TList _list66 = iprot.readListBegin();
              this.peers = new ArrayList<com.vesoft.nebula.HostAddr>(Math.max(0, _list66.size));
              for (int _i67 = 0; 
                   (_list66.size < 0) ? iprot.peekList() : (_i67 < _list66.size); 
                   ++_i67)
              {
                com.vesoft.nebula.HostAddr _elem68;
                _elem68 = new com.vesoft.nebula.HostAddr();
                _elem68.read(iprot);
                this.peers.add(_elem68);
              }
              iprot.readListEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case LOSTS:
          if (field.type == TType.LIST) {
            {
              TList _list69 = iprot.readListBegin();
              this.losts = new ArrayList<com.vesoft.nebula.HostAddr>(Math.max(0, _list69.size));
              for (int _i70 = 0; 
                   (_list69.size < 0) ? iprot.peekList() : (_i70 < _list69.size); 
                   ++_i70)
              {
                com.vesoft.nebula.HostAddr _elem71;
                _elem71 = new com.vesoft.nebula.HostAddr();
                _elem71.read(iprot);
                this.losts.add(_elem71);
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
    if (!isSetPart_id()) {
      throw new TProtocolException("Required field 'part_id' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(PART_ID_FIELD_DESC);
    oprot.writeI32(this.part_id);
    oprot.writeFieldEnd();
    if (this.leader != null) {
      if (isSetLeader()) {
        oprot.writeFieldBegin(LEADER_FIELD_DESC);
        this.leader.write(oprot);
        oprot.writeFieldEnd();
      }
    }
    if (this.peers != null) {
      oprot.writeFieldBegin(PEERS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.peers.size()));
        for (com.vesoft.nebula.HostAddr _iter72 : this.peers)        {
          _iter72.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.losts != null) {
      oprot.writeFieldBegin(LOSTS_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.losts.size()));
        for (com.vesoft.nebula.HostAddr _iter73 : this.losts)        {
          _iter73.write(oprot);
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
    StringBuilder sb = new StringBuilder("PartItem");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("part_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this. getPart_id(), indent + 1, prettyPrint));
    first = false;
    if (isSetLeader())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("leader");
      sb.append(space);
      sb.append(":").append(space);
      if (this. getLeader() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this. getLeader(), indent + 1, prettyPrint));
      }
      first = false;
    }
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("peers");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getPeers() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getPeers(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("losts");
    sb.append(space);
    sb.append(":").append(space);
    if (this. getLosts() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this. getLosts(), indent + 1, prettyPrint));
    }
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    // alas, we cannot check 'part_id' because it's a primitive and you chose the non-beans generator.
    if (peers == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'peers' was not present! Struct: " + toString());
    }
    if (losts == null) {
      throw new TProtocolException(TProtocolException.MISSING_REQUIRED_FIELD, "Required field 'losts' was not present! Struct: " + toString());
    }
    // check that fields of type enum have valid values
  }

}

