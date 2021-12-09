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
public class GetStateResponse implements TBase, java.io.Serializable, Cloneable, Comparable<GetStateResponse> {
  private static final TStruct STRUCT_DESC = new TStruct("GetStateResponse");
  private static final TField ERROR_CODE_FIELD_DESC = new TField("error_code", TType.I32, (short)1);
  private static final TField ROLE_FIELD_DESC = new TField("role", TType.I32, (short)2);
  private static final TField TERM_FIELD_DESC = new TField("term", TType.I64, (short)3);
  private static final TField IS_LEADER_FIELD_DESC = new TField("is_leader", TType.BOOL, (short)4);
  private static final TField COMMITTED_LOG_ID_FIELD_DESC = new TField("committed_log_id", TType.I64, (short)5);
  private static final TField LAST_LOG_ID_FIELD_DESC = new TField("last_log_id", TType.I64, (short)6);
  private static final TField LAST_LOG_TERM_FIELD_DESC = new TField("last_log_term", TType.I64, (short)7);
  private static final TField STATUS_FIELD_DESC = new TField("status", TType.I32, (short)8);

  /**
   * 
   * @see ErrorCode
   */
  public ErrorCode error_code;
  /**
   * 
   * @see Role
   */
  public Role role;
  public long term;
  public boolean is_leader;
  public long committed_log_id;
  public long last_log_id;
  public long last_log_term;
  /**
   * 
   * @see Status
   */
  public Status status;
  public static final int ERROR_CODE = 1;
  public static final int ROLE = 2;
  public static final int TERM = 3;
  public static final int IS_LEADER = 4;
  public static final int COMMITTED_LOG_ID = 5;
  public static final int LAST_LOG_ID = 6;
  public static final int LAST_LOG_TERM = 7;
  public static final int STATUS = 8;

  // isset id assignments
  private static final int __TERM_ISSET_ID = 0;
  private static final int __IS_LEADER_ISSET_ID = 1;
  private static final int __COMMITTED_LOG_ID_ISSET_ID = 2;
  private static final int __LAST_LOG_ID_ISSET_ID = 3;
  private static final int __LAST_LOG_TERM_ISSET_ID = 4;
  private BitSet __isset_bit_vector = new BitSet(5);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ERROR_CODE, new FieldMetaData("error_code", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(ROLE, new FieldMetaData("role", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(TERM, new FieldMetaData("term", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(IS_LEADER, new FieldMetaData("is_leader", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMetaDataMap.put(COMMITTED_LOG_ID, new FieldMetaData("committed_log_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LAST_LOG_ID, new FieldMetaData("last_log_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LAST_LOG_TERM, new FieldMetaData("last_log_term", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(STATUS, new FieldMetaData("status", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(GetStateResponse.class, metaDataMap);
  }

  public GetStateResponse() {
  }

  public GetStateResponse(
      ErrorCode error_code,
      Role role,
      long term,
      boolean is_leader,
      long committed_log_id,
      long last_log_id,
      long last_log_term,
      Status status) {
    this();
    this.error_code = error_code;
    this.role = role;
    this.term = term;
    setTermIsSet(true);
    this.is_leader = is_leader;
    setIs_leaderIsSet(true);
    this.committed_log_id = committed_log_id;
    setCommitted_log_idIsSet(true);
    this.last_log_id = last_log_id;
    setLast_log_idIsSet(true);
    this.last_log_term = last_log_term;
    setLast_log_termIsSet(true);
    this.status = status;
  }

  public static class Builder {
    private ErrorCode error_code;
    private Role role;
    private long term;
    private boolean is_leader;
    private long committed_log_id;
    private long last_log_id;
    private long last_log_term;
    private Status status;

    BitSet __optional_isset = new BitSet(5);

    public Builder() {
    }

    public Builder setError_code(final ErrorCode error_code) {
      this.error_code = error_code;
      return this;
    }

    public Builder setRole(final Role role) {
      this.role = role;
      return this;
    }

    public Builder setTerm(final long term) {
      this.term = term;
      __optional_isset.set(__TERM_ISSET_ID, true);
      return this;
    }

    public Builder setIs_leader(final boolean is_leader) {
      this.is_leader = is_leader;
      __optional_isset.set(__IS_LEADER_ISSET_ID, true);
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

    public Builder setStatus(final Status status) {
      this.status = status;
      return this;
    }

    public GetStateResponse build() {
      GetStateResponse result = new GetStateResponse();
      result.setError_code(this.error_code);
      result.setRole(this.role);
      if (__optional_isset.get(__TERM_ISSET_ID)) {
        result.setTerm(this.term);
      }
      if (__optional_isset.get(__IS_LEADER_ISSET_ID)) {
        result.setIs_leader(this.is_leader);
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
      result.setStatus(this.status);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetStateResponse(GetStateResponse other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetError_code()) {
      this.error_code = TBaseHelper.deepCopy(other.error_code);
    }
    if (other.isSetRole()) {
      this.role = TBaseHelper.deepCopy(other.role);
    }
    this.term = TBaseHelper.deepCopy(other.term);
    this.is_leader = TBaseHelper.deepCopy(other.is_leader);
    this.committed_log_id = TBaseHelper.deepCopy(other.committed_log_id);
    this.last_log_id = TBaseHelper.deepCopy(other.last_log_id);
    this.last_log_term = TBaseHelper.deepCopy(other.last_log_term);
    if (other.isSetStatus()) {
      this.status = TBaseHelper.deepCopy(other.status);
    }
  }

  public GetStateResponse deepCopy() {
    return new GetStateResponse(this);
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
  public GetStateResponse setError_code(ErrorCode error_code) {
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

  /**
   * 
   * @see Role
   */
  public Role getRole() {
    return this.role;
  }

  /**
   * 
   * @see Role
   */
  public GetStateResponse setRole(Role role) {
    this.role = role;
    return this;
  }

  public void unsetRole() {
    this.role = null;
  }

  // Returns true if field role is set (has been assigned a value) and false otherwise
  public boolean isSetRole() {
    return this.role != null;
  }

  public void setRoleIsSet(boolean __value) {
    if (!__value) {
      this.role = null;
    }
  }

  public long getTerm() {
    return this.term;
  }

  public GetStateResponse setTerm(long term) {
    this.term = term;
    setTermIsSet(true);
    return this;
  }

  public void unsetTerm() {
    __isset_bit_vector.clear(__TERM_ISSET_ID);
  }

  // Returns true if field term is set (has been assigned a value) and false otherwise
  public boolean isSetTerm() {
    return __isset_bit_vector.get(__TERM_ISSET_ID);
  }

  public void setTermIsSet(boolean __value) {
    __isset_bit_vector.set(__TERM_ISSET_ID, __value);
  }

  public boolean isIs_leader() {
    return this.is_leader;
  }

  public GetStateResponse setIs_leader(boolean is_leader) {
    this.is_leader = is_leader;
    setIs_leaderIsSet(true);
    return this;
  }

  public void unsetIs_leader() {
    __isset_bit_vector.clear(__IS_LEADER_ISSET_ID);
  }

  // Returns true if field is_leader is set (has been assigned a value) and false otherwise
  public boolean isSetIs_leader() {
    return __isset_bit_vector.get(__IS_LEADER_ISSET_ID);
  }

  public void setIs_leaderIsSet(boolean __value) {
    __isset_bit_vector.set(__IS_LEADER_ISSET_ID, __value);
  }

  public long getCommitted_log_id() {
    return this.committed_log_id;
  }

  public GetStateResponse setCommitted_log_id(long committed_log_id) {
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

  public GetStateResponse setLast_log_id(long last_log_id) {
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

  public GetStateResponse setLast_log_term(long last_log_term) {
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

  /**
   * 
   * @see Status
   */
  public Status getStatus() {
    return this.status;
  }

  /**
   * 
   * @see Status
   */
  public GetStateResponse setStatus(Status status) {
    this.status = status;
    return this;
  }

  public void unsetStatus() {
    this.status = null;
  }

  // Returns true if field status is set (has been assigned a value) and false otherwise
  public boolean isSetStatus() {
    return this.status != null;
  }

  public void setStatusIsSet(boolean __value) {
    if (!__value) {
      this.status = null;
    }
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

    case ROLE:
      if (__value == null) {
        unsetRole();
      } else {
        setRole((Role)__value);
      }
      break;

    case TERM:
      if (__value == null) {
        unsetTerm();
      } else {
        setTerm((Long)__value);
      }
      break;

    case IS_LEADER:
      if (__value == null) {
        unsetIs_leader();
      } else {
        setIs_leader((Boolean)__value);
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

    case STATUS:
      if (__value == null) {
        unsetStatus();
      } else {
        setStatus((Status)__value);
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

    case ROLE:
      return getRole();

    case TERM:
      return new Long(getTerm());

    case IS_LEADER:
      return new Boolean(isIs_leader());

    case COMMITTED_LOG_ID:
      return new Long(getCommitted_log_id());

    case LAST_LOG_ID:
      return new Long(getLast_log_id());

    case LAST_LOG_TERM:
      return new Long(getLast_log_term());

    case STATUS:
      return getStatus();

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
    if (!(_that instanceof GetStateResponse))
      return false;
    GetStateResponse that = (GetStateResponse)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetError_code(), that.isSetError_code(), this.error_code, that.error_code)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetRole(), that.isSetRole(), this.role, that.role)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.term, that.term)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.is_leader, that.is_leader)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.committed_log_id, that.committed_log_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.last_log_id, that.last_log_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.last_log_term, that.last_log_term)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetStatus(), that.isSetStatus(), this.status, that.status)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {error_code, role, term, is_leader, committed_log_id, last_log_id, last_log_term, status});
  }

  @Override
  public int compareTo(GetStateResponse other) {
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
    lastComparison = Boolean.valueOf(isSetRole()).compareTo(other.isSetRole());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(role, other.role);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetTerm()).compareTo(other.isSetTerm());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(term, other.term);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetIs_leader()).compareTo(other.isSetIs_leader());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(is_leader, other.is_leader);
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
    lastComparison = Boolean.valueOf(isSetStatus()).compareTo(other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(status, other.status);
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
        case ROLE:
          if (__field.type == TType.I32) {
            this.role = Role.findByValue(iprot.readI32());
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case TERM:
          if (__field.type == TType.I64) {
            this.term = iprot.readI64();
            setTermIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case IS_LEADER:
          if (__field.type == TType.BOOL) {
            this.is_leader = iprot.readBool();
            setIs_leaderIsSet(true);
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
        case STATUS:
          if (__field.type == TType.I32) {
            this.status = Status.findByValue(iprot.readI32());
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
    if (this.role != null) {
      oprot.writeFieldBegin(ROLE_FIELD_DESC);
      oprot.writeI32(this.role == null ? 0 : this.role.getValue());
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(TERM_FIELD_DESC);
    oprot.writeI64(this.term);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(IS_LEADER_FIELD_DESC);
    oprot.writeBool(this.is_leader);
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
    if (this.status != null) {
      oprot.writeFieldBegin(STATUS_FIELD_DESC);
      oprot.writeI32(this.status == null ? 0 : this.status.getValue());
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
    StringBuilder sb = new StringBuilder("GetStateResponse");
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
    sb.append("role");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getRole() == null) {
      sb.append("null");
    } else {
      String role_name = this.getRole() == null ? "null" : this.getRole().name();
      if (role_name != null) {
        sb.append(role_name);
        sb.append(" (");
      }
      sb.append(this.getRole());
      if (role_name != null) {
        sb.append(")");
      }
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("term");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getTerm(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("is_leader");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.isIs_leader(), indent + 1, prettyPrint));
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
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("status");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getStatus() == null) {
      sb.append("null");
    } else {
      String status_name = this.getStatus() == null ? "null" : this.getStatus().name();
      if (status_name != null) {
        sb.append(status_name);
        sb.append(" (");
      }
      sb.append(this.getStatus());
      if (status_name != null) {
        sb.append(")");
      }
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

