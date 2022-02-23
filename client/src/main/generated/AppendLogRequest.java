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
public class AppendLogRequest implements TBase, java.io.Serializable, Cloneable, Comparable<AppendLogRequest> {
  private static final TStruct STRUCT_DESC = new TStruct("AppendLogRequest");
  private static final TField SPACE_FIELD_DESC = new TField("space", TType.I32, (short)1);
  private static final TField PART_FIELD_DESC = new TField("part", TType.I32, (short)2);
  private static final TField CURRENT_TERM_FIELD_DESC = new TField("current_term", TType.I64, (short)3);
  private static final TField COMMITTED_LOG_ID_FIELD_DESC = new TField("committed_log_id", TType.I64, (short)4);
  private static final TField LEADER_ADDR_FIELD_DESC = new TField("leader_addr", TType.STRING, (short)5);
  private static final TField LEADER_PORT_FIELD_DESC = new TField("leader_port", TType.I32, (short)6);
  private static final TField LAST_LOG_TERM_SENT_FIELD_DESC = new TField("last_log_term_sent", TType.I64, (short)7);
  private static final TField LAST_LOG_ID_SENT_FIELD_DESC = new TField("last_log_id_sent", TType.I64, (short)8);
  private static final TField LOG_STR_LIST_FIELD_DESC = new TField("log_str_list", TType.LIST, (short)9);

  public int space;
  public int part;
  public long current_term;
  public long committed_log_id;
  public String leader_addr;
  public int leader_port;
  public long last_log_term_sent;
  public long last_log_id_sent;
  public List<RaftLogEntry> log_str_list;
  public static final int SPACE = 1;
  public static final int PART = 2;
  public static final int CURRENT_TERM = 3;
  public static final int COMMITTED_LOG_ID = 4;
  public static final int LEADER_ADDR = 5;
  public static final int LEADER_PORT = 6;
  public static final int LAST_LOG_TERM_SENT = 7;
  public static final int LAST_LOG_ID_SENT = 8;
  public static final int LOG_STR_LIST = 9;

  // isset id assignments
  private static final int __SPACE_ISSET_ID = 0;
  private static final int __PART_ISSET_ID = 1;
  private static final int __CURRENT_TERM_ISSET_ID = 2;
  private static final int __COMMITTED_LOG_ID_ISSET_ID = 3;
  private static final int __LEADER_PORT_ISSET_ID = 4;
  private static final int __LAST_LOG_TERM_SENT_ISSET_ID = 5;
  private static final int __LAST_LOG_ID_SENT_ISSET_ID = 6;
  private BitSet __isset_bit_vector = new BitSet(7);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(SPACE, new FieldMetaData("space", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(PART, new FieldMetaData("part", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(CURRENT_TERM, new FieldMetaData("current_term", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(COMMITTED_LOG_ID, new FieldMetaData("committed_log_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LEADER_ADDR, new FieldMetaData("leader_addr", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(LEADER_PORT, new FieldMetaData("leader_port", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(LAST_LOG_TERM_SENT, new FieldMetaData("last_log_term_sent", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LAST_LOG_ID_SENT, new FieldMetaData("last_log_id_sent", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LOG_STR_LIST, new FieldMetaData("log_str_list", TFieldRequirementType.DEFAULT, 
        new ListMetaData(TType.LIST, 
            new StructMetaData(TType.STRUCT, RaftLogEntry.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(AppendLogRequest.class, metaDataMap);
  }

  public AppendLogRequest() {
  }

  public AppendLogRequest(
      int space,
      int part,
      long current_term,
      long committed_log_id,
      String leader_addr,
      int leader_port,
      long last_log_term_sent,
      long last_log_id_sent,
      List<RaftLogEntry> log_str_list) {
    this();
    this.space = space;
    setSpaceIsSet(true);
    this.part = part;
    setPartIsSet(true);
    this.current_term = current_term;
    setCurrent_termIsSet(true);
    this.committed_log_id = committed_log_id;
    setCommitted_log_idIsSet(true);
    this.leader_addr = leader_addr;
    this.leader_port = leader_port;
    setLeader_portIsSet(true);
    this.last_log_term_sent = last_log_term_sent;
    setLast_log_term_sentIsSet(true);
    this.last_log_id_sent = last_log_id_sent;
    setLast_log_id_sentIsSet(true);
    this.log_str_list = log_str_list;
  }

  public static class Builder {
    private int space;
    private int part;
    private long current_term;
    private long committed_log_id;
    private String leader_addr;
    private int leader_port;
    private long last_log_term_sent;
    private long last_log_id_sent;
    private List<RaftLogEntry> log_str_list;

    BitSet __optional_isset = new BitSet(7);

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

    public Builder setCurrent_term(final long current_term) {
      this.current_term = current_term;
      __optional_isset.set(__CURRENT_TERM_ISSET_ID, true);
      return this;
    }

    public Builder setCommitted_log_id(final long committed_log_id) {
      this.committed_log_id = committed_log_id;
      __optional_isset.set(__COMMITTED_LOG_ID_ISSET_ID, true);
      return this;
    }

    public Builder setLeader_addr(final String leader_addr) {
      this.leader_addr = leader_addr;
      return this;
    }

    public Builder setLeader_port(final int leader_port) {
      this.leader_port = leader_port;
      __optional_isset.set(__LEADER_PORT_ISSET_ID, true);
      return this;
    }

    public Builder setLast_log_term_sent(final long last_log_term_sent) {
      this.last_log_term_sent = last_log_term_sent;
      __optional_isset.set(__LAST_LOG_TERM_SENT_ISSET_ID, true);
      return this;
    }

    public Builder setLast_log_id_sent(final long last_log_id_sent) {
      this.last_log_id_sent = last_log_id_sent;
      __optional_isset.set(__LAST_LOG_ID_SENT_ISSET_ID, true);
      return this;
    }

    public Builder setLog_str_list(final List<RaftLogEntry> log_str_list) {
      this.log_str_list = log_str_list;
      return this;
    }

    public AppendLogRequest build() {
      AppendLogRequest result = new AppendLogRequest();
      if (__optional_isset.get(__SPACE_ISSET_ID)) {
        result.setSpace(this.space);
      }
      if (__optional_isset.get(__PART_ISSET_ID)) {
        result.setPart(this.part);
      }
      if (__optional_isset.get(__CURRENT_TERM_ISSET_ID)) {
        result.setCurrent_term(this.current_term);
      }
      if (__optional_isset.get(__COMMITTED_LOG_ID_ISSET_ID)) {
        result.setCommitted_log_id(this.committed_log_id);
      }
      result.setLeader_addr(this.leader_addr);
      if (__optional_isset.get(__LEADER_PORT_ISSET_ID)) {
        result.setLeader_port(this.leader_port);
      }
      if (__optional_isset.get(__LAST_LOG_TERM_SENT_ISSET_ID)) {
        result.setLast_log_term_sent(this.last_log_term_sent);
      }
      if (__optional_isset.get(__LAST_LOG_ID_SENT_ISSET_ID)) {
        result.setLast_log_id_sent(this.last_log_id_sent);
      }
      result.setLog_str_list(this.log_str_list);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AppendLogRequest(AppendLogRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.space = TBaseHelper.deepCopy(other.space);
    this.part = TBaseHelper.deepCopy(other.part);
    this.current_term = TBaseHelper.deepCopy(other.current_term);
    this.committed_log_id = TBaseHelper.deepCopy(other.committed_log_id);
    if (other.isSetLeader_addr()) {
      this.leader_addr = TBaseHelper.deepCopy(other.leader_addr);
    }
    this.leader_port = TBaseHelper.deepCopy(other.leader_port);
    this.last_log_term_sent = TBaseHelper.deepCopy(other.last_log_term_sent);
    this.last_log_id_sent = TBaseHelper.deepCopy(other.last_log_id_sent);
    if (other.isSetLog_str_list()) {
      this.log_str_list = TBaseHelper.deepCopy(other.log_str_list);
    }
  }

  public AppendLogRequest deepCopy() {
    return new AppendLogRequest(this);
  }

  public int getSpace() {
    return this.space;
  }

  public AppendLogRequest setSpace(int space) {
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

  public AppendLogRequest setPart(int part) {
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

  public long getCurrent_term() {
    return this.current_term;
  }

  public AppendLogRequest setCurrent_term(long current_term) {
    this.current_term = current_term;
    setCurrent_termIsSet(true);
    return this;
  }

  public void unsetCurrent_term() {
    __isset_bit_vector.clear(__CURRENT_TERM_ISSET_ID);
  }

  // Returns true if field current_term is set (has been assigned a value) and false otherwise
  public boolean isSetCurrent_term() {
    return __isset_bit_vector.get(__CURRENT_TERM_ISSET_ID);
  }

  public void setCurrent_termIsSet(boolean __value) {
    __isset_bit_vector.set(__CURRENT_TERM_ISSET_ID, __value);
  }

  public long getCommitted_log_id() {
    return this.committed_log_id;
  }

  public AppendLogRequest setCommitted_log_id(long committed_log_id) {
    this.committed_log_id = committed_log_id;
    setCommitted_log_idIsSet(true);
    return this;
  }

  public void unsetCommitted_log_id() {
    __isset_bit_vector.clear(__COMMITTED_LOG_ID_ISSET_ID);
  }

  // Returns true if field committed_log_id is set (has been assigned a value) and false otherwise
  public boolean isSetCommitted_log_id() {
    return __isset_bit_vector.get(__COMMITTED_LOG_ID_ISSET_ID);
  }

  public void setCommitted_log_idIsSet(boolean __value) {
    __isset_bit_vector.set(__COMMITTED_LOG_ID_ISSET_ID, __value);
  }

  public String getLeader_addr() {
    return this.leader_addr;
  }

  public AppendLogRequest setLeader_addr(String leader_addr) {
    this.leader_addr = leader_addr;
    return this;
  }

  public void unsetLeader_addr() {
    this.leader_addr = null;
  }

  // Returns true if field leader_addr is set (has been assigned a value) and false otherwise
  public boolean isSetLeader_addr() {
    return this.leader_addr != null;
  }

  public void setLeader_addrIsSet(boolean __value) {
    if (!__value) {
      this.leader_addr = null;
    }
  }

  public int getLeader_port() {
    return this.leader_port;
  }

  public AppendLogRequest setLeader_port(int leader_port) {
    this.leader_port = leader_port;
    setLeader_portIsSet(true);
    return this;
  }

  public void unsetLeader_port() {
    __isset_bit_vector.clear(__LEADER_PORT_ISSET_ID);
  }

  // Returns true if field leader_port is set (has been assigned a value) and false otherwise
  public boolean isSetLeader_port() {
    return __isset_bit_vector.get(__LEADER_PORT_ISSET_ID);
  }

  public void setLeader_portIsSet(boolean __value) {
    __isset_bit_vector.set(__LEADER_PORT_ISSET_ID, __value);
  }

  public long getLast_log_term_sent() {
    return this.last_log_term_sent;
  }

  public AppendLogRequest setLast_log_term_sent(long last_log_term_sent) {
    this.last_log_term_sent = last_log_term_sent;
    setLast_log_term_sentIsSet(true);
    return this;
  }

  public void unsetLast_log_term_sent() {
    __isset_bit_vector.clear(__LAST_LOG_TERM_SENT_ISSET_ID);
  }

  // Returns true if field last_log_term_sent is set (has been assigned a value) and false otherwise
  public boolean isSetLast_log_term_sent() {
    return __isset_bit_vector.get(__LAST_LOG_TERM_SENT_ISSET_ID);
  }

  public void setLast_log_term_sentIsSet(boolean __value) {
    __isset_bit_vector.set(__LAST_LOG_TERM_SENT_ISSET_ID, __value);
  }

  public long getLast_log_id_sent() {
    return this.last_log_id_sent;
  }

  public AppendLogRequest setLast_log_id_sent(long last_log_id_sent) {
    this.last_log_id_sent = last_log_id_sent;
    setLast_log_id_sentIsSet(true);
    return this;
  }

  public void unsetLast_log_id_sent() {
    __isset_bit_vector.clear(__LAST_LOG_ID_SENT_ISSET_ID);
  }

  // Returns true if field last_log_id_sent is set (has been assigned a value) and false otherwise
  public boolean isSetLast_log_id_sent() {
    return __isset_bit_vector.get(__LAST_LOG_ID_SENT_ISSET_ID);
  }

  public void setLast_log_id_sentIsSet(boolean __value) {
    __isset_bit_vector.set(__LAST_LOG_ID_SENT_ISSET_ID, __value);
  }

  public List<RaftLogEntry> getLog_str_list() {
    return this.log_str_list;
  }

  public AppendLogRequest setLog_str_list(List<RaftLogEntry> log_str_list) {
    this.log_str_list = log_str_list;
    return this;
  }

  public void unsetLog_str_list() {
    this.log_str_list = null;
  }

  // Returns true if field log_str_list is set (has been assigned a value) and false otherwise
  public boolean isSetLog_str_list() {
    return this.log_str_list != null;
  }

  public void setLog_str_listIsSet(boolean __value) {
    if (!__value) {
      this.log_str_list = null;
    }
  }

  @SuppressWarnings("unchecked")
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

    case CURRENT_TERM:
      if (__value == null) {
        unsetCurrent_term();
      } else {
        setCurrent_term((Long)__value);
      }
      break;

    case COMMITTED_LOG_ID:
      if (__value == null) {
        unsetCommitted_log_id();
      } else {
        setCommitted_log_id((Long)__value);
      }
      break;

    case LEADER_ADDR:
      if (__value == null) {
        unsetLeader_addr();
      } else {
        setLeader_addr((String)__value);
      }
      break;

    case LEADER_PORT:
      if (__value == null) {
        unsetLeader_port();
      } else {
        setLeader_port((Integer)__value);
      }
      break;

    case LAST_LOG_TERM_SENT:
      if (__value == null) {
        unsetLast_log_term_sent();
      } else {
        setLast_log_term_sent((Long)__value);
      }
      break;

    case LAST_LOG_ID_SENT:
      if (__value == null) {
        unsetLast_log_id_sent();
      } else {
        setLast_log_id_sent((Long)__value);
      }
      break;

    case LOG_STR_LIST:
      if (__value == null) {
        unsetLog_str_list();
      } else {
        setLog_str_list((List<RaftLogEntry>)__value);
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

    case CURRENT_TERM:
      return new Long(getCurrent_term());

    case COMMITTED_LOG_ID:
      return new Long(getCommitted_log_id());

    case LEADER_ADDR:
      return getLeader_addr();

    case LEADER_PORT:
      return new Integer(getLeader_port());

    case LAST_LOG_TERM_SENT:
      return new Long(getLast_log_term_sent());

    case LAST_LOG_ID_SENT:
      return new Long(getLast_log_id_sent());

    case LOG_STR_LIST:
      return getLog_str_list();

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
    if (!(_that instanceof AppendLogRequest))
      return false;
    AppendLogRequest that = (AppendLogRequest)_that;

    if (!TBaseHelper.equalsNobinary(this.space, that.space)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.part, that.part)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.current_term, that.current_term)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.committed_log_id, that.committed_log_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetLeader_addr(), that.isSetLeader_addr(), this.leader_addr, that.leader_addr)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.leader_port, that.leader_port)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.last_log_term_sent, that.last_log_term_sent)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.last_log_id_sent, that.last_log_id_sent)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetLog_str_list(), that.isSetLog_str_list(), this.log_str_list, that.log_str_list)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {space, part, current_term, committed_log_id, leader_addr, leader_port, last_log_term_sent, last_log_id_sent, log_str_list});
  }

  @Override
  public int compareTo(AppendLogRequest other) {
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
    lastComparison = Boolean.valueOf(isSetCurrent_term()).compareTo(other.isSetCurrent_term());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(current_term, other.current_term);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCommitted_log_id()).compareTo(other.isSetCommitted_log_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(committed_log_id, other.committed_log_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLeader_addr()).compareTo(other.isSetLeader_addr());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(leader_addr, other.leader_addr);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLeader_port()).compareTo(other.isSetLeader_port());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(leader_port, other.leader_port);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLast_log_term_sent()).compareTo(other.isSetLast_log_term_sent());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(last_log_term_sent, other.last_log_term_sent);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLast_log_id_sent()).compareTo(other.isSetLast_log_id_sent());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(last_log_id_sent, other.last_log_id_sent);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLog_str_list()).compareTo(other.isSetLog_str_list());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(log_str_list, other.log_str_list);
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
        case CURRENT_TERM:
          if (__field.type == TType.I64) {
            this.current_term = iprot.readI64();
            setCurrent_termIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case COMMITTED_LOG_ID:
          if (__field.type == TType.I64) {
            this.committed_log_id = iprot.readI64();
            setCommitted_log_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LEADER_ADDR:
          if (__field.type == TType.STRING) {
            this.leader_addr = iprot.readString();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LEADER_PORT:
          if (__field.type == TType.I32) {
            this.leader_port = iprot.readI32();
            setLeader_portIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LAST_LOG_TERM_SENT:
          if (__field.type == TType.I64) {
            this.last_log_term_sent = iprot.readI64();
            setLast_log_term_sentIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LAST_LOG_ID_SENT:
          if (__field.type == TType.I64) {
            this.last_log_id_sent = iprot.readI64();
            setLast_log_id_sentIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LOG_STR_LIST:
          if (__field.type == TType.LIST) {
            {
              TList _list0 = iprot.readListBegin();
              this.log_str_list = new ArrayList<RaftLogEntry>(Math.max(0, _list0.size));
              for (int _i1 = 0; 
                   (_list0.size < 0) ? iprot.peekList() : (_i1 < _list0.size); 
                   ++_i1)
              {
                RaftLogEntry _elem2;
                _elem2 = new RaftLogEntry();
                _elem2.read(iprot);
                this.log_str_list.add(_elem2);
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
    oprot.writeFieldBegin(SPACE_FIELD_DESC);
    oprot.writeI32(this.space);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(PART_FIELD_DESC);
    oprot.writeI32(this.part);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(CURRENT_TERM_FIELD_DESC);
    oprot.writeI64(this.current_term);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(COMMITTED_LOG_ID_FIELD_DESC);
    oprot.writeI64(this.committed_log_id);
    oprot.writeFieldEnd();
    if (this.leader_addr != null) {
      oprot.writeFieldBegin(LEADER_ADDR_FIELD_DESC);
      oprot.writeString(this.leader_addr);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(LEADER_PORT_FIELD_DESC);
    oprot.writeI32(this.leader_port);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(LAST_LOG_TERM_SENT_FIELD_DESC);
    oprot.writeI64(this.last_log_term_sent);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(LAST_LOG_ID_SENT_FIELD_DESC);
    oprot.writeI64(this.last_log_id_sent);
    oprot.writeFieldEnd();
    if (this.log_str_list != null) {
      oprot.writeFieldBegin(LOG_STR_LIST_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.log_str_list.size()));
        for (RaftLogEntry _iter3 : this.log_str_list)        {
          _iter3.write(oprot);
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
    StringBuilder sb = new StringBuilder("AppendLogRequest");
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
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("current_term");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCurrent_term(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("committed_log_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCommitted_log_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("leader_addr");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getLeader_addr() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getLeader_addr(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("leader_port");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLeader_port(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("last_log_term_sent");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLast_log_term_sent(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("last_log_id_sent");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLast_log_id_sent(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("log_str_list");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getLog_str_list() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getLog_str_list(), indent + 1, prettyPrint));
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

