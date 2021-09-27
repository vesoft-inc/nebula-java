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
public class AppendLogResponse implements TBase, java.io.Serializable, Cloneable, Comparable<AppendLogResponse> {
  private static final TStruct STRUCT_DESC = new TStruct("AppendLogResponse");
  private static final TField ERROR_CODE_FIELD_DESC = new TField("error_code", TType.I32, (short)1);
  private static final TField CURRENT_TERM_FIELD_DESC = new TField("current_term", TType.I64, (short)2);
  private static final TField LEADER_ADDR_FIELD_DESC = new TField("leader_addr", TType.STRING, (short)3);
  private static final TField LEADER_PORT_FIELD_DESC = new TField("leader_port", TType.I32, (short)4);
  private static final TField COMMITTED_LOG_ID_FIELD_DESC = new TField("committed_log_id", TType.I64, (short)5);
  private static final TField LAST_LOG_ID_FIELD_DESC = new TField("last_log_id", TType.I64, (short)6);
  private static final TField LAST_LOG_TERM_FIELD_DESC = new TField("last_log_term", TType.I64, (short)7);

  /**
   * 
   * @see ErrorCode
   */
  public ErrorCode error_code;
  public long current_term;
  public String leader_addr;
  public int leader_port;
  public long committed_log_id;
  public long last_log_id;
  public long last_log_term;
  public static final int ERROR_CODE = 1;
  public static final int CURRENT_TERM = 2;
  public static final int LEADER_ADDR = 3;
  public static final int LEADER_PORT = 4;
  public static final int COMMITTED_LOG_ID = 5;
  public static final int LAST_LOG_ID = 6;
  public static final int LAST_LOG_TERM = 7;

  // isset id assignments
  private static final int __CURRENT_TERM_ISSET_ID = 0;
  private static final int __LEADER_PORT_ISSET_ID = 1;
  private static final int __COMMITTED_LOG_ID_ISSET_ID = 2;
  private static final int __LAST_LOG_ID_ISSET_ID = 3;
  private static final int __LAST_LOG_TERM_ISSET_ID = 4;
  private BitSet __isset_bit_vector = new BitSet(5);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ERROR_CODE, new FieldMetaData("error_code", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(CURRENT_TERM, new FieldMetaData("current_term", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LEADER_ADDR, new FieldMetaData("leader_addr", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(LEADER_PORT, new FieldMetaData("leader_port", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(COMMITTED_LOG_ID, new FieldMetaData("committed_log_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LAST_LOG_ID, new FieldMetaData("last_log_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LAST_LOG_TERM, new FieldMetaData("last_log_term", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(AppendLogResponse.class, metaDataMap);
  }

  public AppendLogResponse() {
  }

  public AppendLogResponse(
      ErrorCode error_code,
      long current_term,
      String leader_addr,
      int leader_port,
      long committed_log_id,
      long last_log_id,
      long last_log_term) {
    this();
    this.error_code = error_code;
    this.current_term = current_term;
    setCurrent_termIsSet(true);
    this.leader_addr = leader_addr;
    this.leader_port = leader_port;
    setLeader_portIsSet(true);
    this.committed_log_id = committed_log_id;
    setCommitted_log_idIsSet(true);
    this.last_log_id = last_log_id;
    setLast_log_idIsSet(true);
    this.last_log_term = last_log_term;
    setLast_log_termIsSet(true);
  }

  public static class Builder {
    private ErrorCode error_code;
    private long current_term;
    private String leader_addr;
    private int leader_port;
    private long committed_log_id;
    private long last_log_id;
    private long last_log_term;

    BitSet __optional_isset = new BitSet(5);

    public Builder() {
    }

    public Builder setError_code(final ErrorCode error_code) {
      this.error_code = error_code;
      return this;
    }

    public Builder setCurrent_term(final long current_term) {
      this.current_term = current_term;
      __optional_isset.set(__CURRENT_TERM_ISSET_ID, true);
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

    public Builder setCommitted_log_id(final long committed_log_id) {
      this.committed_log_id = committed_log_id;
      __optional_isset.set(__COMMITTED_LOG_ID_ISSET_ID, true);
      return this;
    }

    public Builder setLast_log_id(final long last_log_id) {
      this.last_log_id = last_log_id;
      __optional_isset.set(__LAST_LOG_ID_ISSET_ID, true);
      return this;
    }

    public Builder setLast_log_term(final long last_log_term) {
      this.last_log_term = last_log_term;
      __optional_isset.set(__LAST_LOG_TERM_ISSET_ID, true);
      return this;
    }

    public AppendLogResponse build() {
      AppendLogResponse result = new AppendLogResponse();
      result.setError_code(this.error_code);
      if (__optional_isset.get(__CURRENT_TERM_ISSET_ID)) {
        result.setCurrent_term(this.current_term);
      }
      result.setLeader_addr(this.leader_addr);
      if (__optional_isset.get(__LEADER_PORT_ISSET_ID)) {
        result.setLeader_port(this.leader_port);
      }
      if (__optional_isset.get(__COMMITTED_LOG_ID_ISSET_ID)) {
        result.setCommitted_log_id(this.committed_log_id);
      }
      if (__optional_isset.get(__LAST_LOG_ID_ISSET_ID)) {
        result.setLast_log_id(this.last_log_id);
      }
      if (__optional_isset.get(__LAST_LOG_TERM_ISSET_ID)) {
        result.setLast_log_term(this.last_log_term);
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
  public AppendLogResponse(AppendLogResponse other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetError_code()) {
      this.error_code = TBaseHelper.deepCopy(other.error_code);
    }
    this.current_term = TBaseHelper.deepCopy(other.current_term);
    if (other.isSetLeader_addr()) {
      this.leader_addr = TBaseHelper.deepCopy(other.leader_addr);
    }
    this.leader_port = TBaseHelper.deepCopy(other.leader_port);
    this.committed_log_id = TBaseHelper.deepCopy(other.committed_log_id);
    this.last_log_id = TBaseHelper.deepCopy(other.last_log_id);
    this.last_log_term = TBaseHelper.deepCopy(other.last_log_term);
  }

  public AppendLogResponse deepCopy() {
    return new AppendLogResponse(this);
  }

  /**
   * 
   * @see ErrorCode
   */
  public ErrorCode getError_code() {
    return this.error_code;
  }

  /**
   * 
   * @see ErrorCode
   */
  public AppendLogResponse setError_code(ErrorCode error_code) {
    this.error_code = error_code;
    return this;
  }

  public void unsetError_code() {
    this.error_code = null;
  }

  // Returns true if field error_code is set (has been assigned a value) and false otherwise
  public boolean isSetError_code() {
    return this.error_code != null;
  }

  public void setError_codeIsSet(boolean __value) {
    if (!__value) {
      this.error_code = null;
    }
  }

  public long getCurrent_term() {
    return this.current_term;
  }

  public AppendLogResponse setCurrent_term(long current_term) {
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

  public String getLeader_addr() {
    return this.leader_addr;
  }

  public AppendLogResponse setLeader_addr(String leader_addr) {
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

  public AppendLogResponse setLeader_port(int leader_port) {
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

  public long getCommitted_log_id() {
    return this.committed_log_id;
  }

  public AppendLogResponse setCommitted_log_id(long committed_log_id) {
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

  public long getLast_log_id() {
    return this.last_log_id;
  }

  public AppendLogResponse setLast_log_id(long last_log_id) {
    this.last_log_id = last_log_id;
    setLast_log_idIsSet(true);
    return this;
  }

  public void unsetLast_log_id() {
    __isset_bit_vector.clear(__LAST_LOG_ID_ISSET_ID);
  }

  // Returns true if field last_log_id is set (has been assigned a value) and false otherwise
  public boolean isSetLast_log_id() {
    return __isset_bit_vector.get(__LAST_LOG_ID_ISSET_ID);
  }

  public void setLast_log_idIsSet(boolean __value) {
    __isset_bit_vector.set(__LAST_LOG_ID_ISSET_ID, __value);
  }

  public long getLast_log_term() {
    return this.last_log_term;
  }

  public AppendLogResponse setLast_log_term(long last_log_term) {
    this.last_log_term = last_log_term;
    setLast_log_termIsSet(true);
    return this;
  }

  public void unsetLast_log_term() {
    __isset_bit_vector.clear(__LAST_LOG_TERM_ISSET_ID);
  }

  // Returns true if field last_log_term is set (has been assigned a value) and false otherwise
  public boolean isSetLast_log_term() {
    return __isset_bit_vector.get(__LAST_LOG_TERM_ISSET_ID);
  }

  public void setLast_log_termIsSet(boolean __value) {
    __isset_bit_vector.set(__LAST_LOG_TERM_ISSET_ID, __value);
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case ERROR_CODE:
      if (__value == null) {
        unsetError_code();
      } else {
        setError_code((ErrorCode)__value);
      }
      break;

    case CURRENT_TERM:
      if (__value == null) {
        unsetCurrent_term();
      } else {
        setCurrent_term((Long)__value);
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

    case COMMITTED_LOG_ID:
      if (__value == null) {
        unsetCommitted_log_id();
      } else {
        setCommitted_log_id((Long)__value);
      }
      break;

    case LAST_LOG_ID:
      if (__value == null) {
        unsetLast_log_id();
      } else {
        setLast_log_id((Long)__value);
      }
      break;

    case LAST_LOG_TERM:
      if (__value == null) {
        unsetLast_log_term();
      } else {
        setLast_log_term((Long)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case ERROR_CODE:
      return getError_code();

    case CURRENT_TERM:
      return new Long(getCurrent_term());

    case LEADER_ADDR:
      return getLeader_addr();

    case LEADER_PORT:
      return new Integer(getLeader_port());

    case COMMITTED_LOG_ID:
      return new Long(getCommitted_log_id());

    case LAST_LOG_ID:
      return new Long(getLast_log_id());

    case LAST_LOG_TERM:
      return new Long(getLast_log_term());

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
    if (!(_that instanceof AppendLogResponse))
      return false;
    AppendLogResponse that = (AppendLogResponse)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetError_code(), that.isSetError_code(), this.error_code, that.error_code)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.current_term, that.current_term)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetLeader_addr(), that.isSetLeader_addr(), this.leader_addr, that.leader_addr)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.leader_port, that.leader_port)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.committed_log_id, that.committed_log_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.last_log_id, that.last_log_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.last_log_term, that.last_log_term)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {error_code, current_term, leader_addr, leader_port, committed_log_id, last_log_id, last_log_term});
  }

  @Override
  public int compareTo(AppendLogResponse other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetError_code()).compareTo(other.isSetError_code());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(error_code, other.error_code);
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
    lastComparison = Boolean.valueOf(isSetCommitted_log_id()).compareTo(other.isSetCommitted_log_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(committed_log_id, other.committed_log_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLast_log_id()).compareTo(other.isSetLast_log_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(last_log_id, other.last_log_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLast_log_term()).compareTo(other.isSetLast_log_term());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(last_log_term, other.last_log_term);
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
        case ERROR_CODE:
          if (__field.type == TType.I32) {
            this.error_code = ErrorCode.findByValue(iprot.readI32());
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
        case COMMITTED_LOG_ID:
          if (__field.type == TType.I64) {
            this.committed_log_id = iprot.readI64();
            setCommitted_log_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LAST_LOG_ID:
          if (__field.type == TType.I64) {
            this.last_log_id = iprot.readI64();
            setLast_log_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LAST_LOG_TERM:
          if (__field.type == TType.I64) {
            this.last_log_term = iprot.readI64();
            setLast_log_termIsSet(true);
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
    if (this.error_code != null) {
      oprot.writeFieldBegin(ERROR_CODE_FIELD_DESC);
      oprot.writeI32(this.error_code == null ? 0 : this.error_code.getValue());
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(CURRENT_TERM_FIELD_DESC);
    oprot.writeI64(this.current_term);
    oprot.writeFieldEnd();
    if (this.leader_addr != null) {
      oprot.writeFieldBegin(LEADER_ADDR_FIELD_DESC);
      oprot.writeString(this.leader_addr);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(LEADER_PORT_FIELD_DESC);
    oprot.writeI32(this.leader_port);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(COMMITTED_LOG_ID_FIELD_DESC);
    oprot.writeI64(this.committed_log_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(LAST_LOG_ID_FIELD_DESC);
    oprot.writeI64(this.last_log_id);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(LAST_LOG_TERM_FIELD_DESC);
    oprot.writeI64(this.last_log_term);
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
    StringBuilder sb = new StringBuilder("AppendLogResponse");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("error_code");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getError_code() == null) {
      sb.append("null");
    } else {
      String error_code_name = this.getError_code() == null ? "null" : this.getError_code().name();
      if (error_code_name != null) {
        sb.append(error_code_name);
        sb.append(" (");
      }
      sb.append(this.getError_code());
      if (error_code_name != null) {
        sb.append(")");
      }
    }
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
    sb.append("committed_log_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCommitted_log_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("last_log_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLast_log_id(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("last_log_term");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLast_log_term(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

