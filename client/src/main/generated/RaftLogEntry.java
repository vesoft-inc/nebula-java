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
public class RaftLogEntry implements TBase, java.io.Serializable, Cloneable, Comparable<RaftLogEntry> {
  private static final TStruct STRUCT_DESC = new TStruct("RaftLogEntry");
  private static final TField CLUSTER_FIELD_DESC = new TField("cluster", TType.I64, (short)1);
  private static final TField LOG_STR_FIELD_DESC = new TField("log_str", TType.STRING, (short)2);
  private static final TField LOG_TERM_FIELD_DESC = new TField("log_term", TType.I64, (short)3);

  public long cluster;
  public byte[] log_str;
  public long log_term;
  public static final int CLUSTER = 1;
  public static final int LOG_STR = 2;
  public static final int LOG_TERM = 3;

  // isset id assignments
  private static final int __CLUSTER_ISSET_ID = 0;
  private static final int __LOG_TERM_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(CLUSTER, new FieldMetaData("cluster", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LOG_STR, new FieldMetaData("log_str", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(LOG_TERM, new FieldMetaData("log_term", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(RaftLogEntry.class, metaDataMap);
  }

  public RaftLogEntry() {
  }

  public RaftLogEntry(
      long cluster,
      byte[] log_str,
      long log_term) {
    this();
    this.cluster = cluster;
    setClusterIsSet(true);
    this.log_str = log_str;
    this.log_term = log_term;
    setLog_termIsSet(true);
  }

  public static class Builder {
    private long cluster;
    private byte[] log_str;
    private long log_term;

    BitSet __optional_isset = new BitSet(2);

    public Builder() {
    }

    public Builder setCluster(final long cluster) {
      this.cluster = cluster;
      __optional_isset.set(__CLUSTER_ISSET_ID, true);
      return this;
    }

    public Builder setLog_str(final byte[] log_str) {
      this.log_str = log_str;
      return this;
    }

    public Builder setLog_term(final long log_term) {
      this.log_term = log_term;
      __optional_isset.set(__LOG_TERM_ISSET_ID, true);
      return this;
    }

    public RaftLogEntry build() {
      RaftLogEntry result = new RaftLogEntry();
      if (__optional_isset.get(__CLUSTER_ISSET_ID)) {
        result.setCluster(this.cluster);
      }
      result.setLog_str(this.log_str);
      if (__optional_isset.get(__LOG_TERM_ISSET_ID)) {
        result.setLog_term(this.log_term);
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
  public RaftLogEntry(RaftLogEntry other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.cluster = TBaseHelper.deepCopy(other.cluster);
    if (other.isSetLog_str()) {
      this.log_str = TBaseHelper.deepCopy(other.log_str);
    }
    this.log_term = TBaseHelper.deepCopy(other.log_term);
  }

  public RaftLogEntry deepCopy() {
    return new RaftLogEntry(this);
  }

  public long getCluster() {
    return this.cluster;
  }

  public RaftLogEntry setCluster(long cluster) {
    this.cluster = cluster;
    setClusterIsSet(true);
    return this;
  }

  public void unsetCluster() {
    __isset_bit_vector.clear(__CLUSTER_ISSET_ID);
  }

  // Returns true if field cluster is set (has been assigned a value) and false otherwise
  public boolean isSetCluster() {
    return __isset_bit_vector.get(__CLUSTER_ISSET_ID);
  }

  public void setClusterIsSet(boolean __value) {
    __isset_bit_vector.set(__CLUSTER_ISSET_ID, __value);
  }

  public byte[] getLog_str() {
    return this.log_str;
  }

  public RaftLogEntry setLog_str(byte[] log_str) {
    this.log_str = log_str;
    return this;
  }

  public void unsetLog_str() {
    this.log_str = null;
  }

  // Returns true if field log_str is set (has been assigned a value) and false otherwise
  public boolean isSetLog_str() {
    return this.log_str != null;
  }

  public void setLog_strIsSet(boolean __value) {
    if (!__value) {
      this.log_str = null;
    }
  }

  public long getLog_term() {
    return this.log_term;
  }

  public RaftLogEntry setLog_term(long log_term) {
    this.log_term = log_term;
    setLog_termIsSet(true);
    return this;
  }

  public void unsetLog_term() {
    __isset_bit_vector.clear(__LOG_TERM_ISSET_ID);
  }

  // Returns true if field log_term is set (has been assigned a value) and false otherwise
  public boolean isSetLog_term() {
    return __isset_bit_vector.get(__LOG_TERM_ISSET_ID);
  }

  public void setLog_termIsSet(boolean __value) {
    __isset_bit_vector.set(__LOG_TERM_ISSET_ID, __value);
  }

  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case CLUSTER:
      if (__value == null) {
        unsetCluster();
      } else {
        setCluster((Long)__value);
      }
      break;

    case LOG_STR:
      if (__value == null) {
        unsetLog_str();
      } else {
        setLog_str((byte[])__value);
      }
      break;

    case LOG_TERM:
      if (__value == null) {
        unsetLog_term();
      } else {
        setLog_term((Long)__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case CLUSTER:
      return new Long(getCluster());

    case LOG_STR:
      return getLog_str();

    case LOG_TERM:
      return new Long(getLog_term());

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
    if (!(_that instanceof RaftLogEntry))
      return false;
    RaftLogEntry that = (RaftLogEntry)_that;

    if (!TBaseHelper.equalsNobinary(this.cluster, that.cluster)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetLog_str(), that.isSetLog_str(), this.log_str, that.log_str)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.log_term, that.log_term)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {cluster, log_str, log_term});
  }

  @Override
  public int compareTo(RaftLogEntry other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetCluster()).compareTo(other.isSetCluster());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(cluster, other.cluster);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLog_str()).compareTo(other.isSetLog_str());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(log_str, other.log_str);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLog_term()).compareTo(other.isSetLog_term());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(log_term, other.log_term);
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
        case CLUSTER:
          if (__field.type == TType.I64) {
            this.cluster = iprot.readI64();
            setClusterIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LOG_STR:
          if (__field.type == TType.STRING) {
            this.log_str = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LOG_TERM:
          if (__field.type == TType.I64) {
            this.log_term = iprot.readI64();
            setLog_termIsSet(true);
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
    oprot.writeFieldBegin(CLUSTER_FIELD_DESC);
    oprot.writeI64(this.cluster);
    oprot.writeFieldEnd();
    if (this.log_str != null) {
      oprot.writeFieldBegin(LOG_STR_FIELD_DESC);
      oprot.writeBinary(this.log_str);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(LOG_TERM_FIELD_DESC);
    oprot.writeI64(this.log_term);
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
    StringBuilder sb = new StringBuilder("RaftLogEntry");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

    sb.append(indentStr);
    sb.append("cluster");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCluster(), indent + 1, prettyPrint));
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("log_str");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getLog_str() == null) {
      sb.append("null");
    } else {
        int __log_str_size = Math.min(this.getLog_str().length, 128);
        for (int i = 0; i < __log_str_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getLog_str()[i]).length() > 1 ? Integer.toHexString(this.getLog_str()[i]).substring(Integer.toHexString(this.getLog_str()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getLog_str()[i]).toUpperCase());
        }
        if (this.getLog_str().length > 128) sb.append(" ...");
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("log_term");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getLog_term(), indent + 1, prettyPrint));
    first = false;
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

