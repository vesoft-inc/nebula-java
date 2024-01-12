/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.meta;

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
public class HBReq implements TBase, java.io.Serializable, Cloneable, Comparable<HBReq> {
  private static final TStruct STRUCT_DESC = new TStruct("HBReq");
  private static final TField ROLE_FIELD_DESC = new TField("role", TType.I32, (short)1);
  private static final TField HOST_FIELD_DESC = new TField("host", TType.STRUCT, (short)2);
  private static final TField CLUSTER_ID_FIELD_DESC = new TField("cluster_id", TType.I64, (short)3);
  private static final TField LEADER_PART_IDS_FIELD_DESC = new TField("leader_partIds", TType.MAP, (short)4);
  private static final TField GIT_INFO_SHA_FIELD_DESC = new TField("git_info_sha", TType.STRING, (short)5);
  private static final TField DISK_PARTS_FIELD_DESC = new TField("disk_parts", TType.MAP, (short)6);
  private static final TField DIR_FIELD_DESC = new TField("dir", TType.STRUCT, (short)7);
  private static final TField VERSION_FIELD_DESC = new TField("version", TType.STRING, (short)8);
  private static final TField CPU_CORES_FIELD_DESC = new TField("cpu_cores", TType.I32, (short)9);
  private static final TField ZONE_FIELD_DESC = new TField("zone", TType.STRING, (short)10);

  /**
   * 
   * @see HostRole
   */
  public HostRole role;
  public com.vesoft.nebula.HostAddr host;
  public long cluster_id;
  public Map<Integer,List<LeaderInfo>> leader_partIds;
  public byte[] git_info_sha;
  public Map<Integer,Map<byte[],PartitionList>> disk_parts;
  public com.vesoft.nebula.DirInfo dir;
  public byte[] version;
  public int cpu_cores;
  public byte[] zone;
  public static final int ROLE = 1;
  public static final int HOST = 2;
  public static final int CLUSTER_ID = 3;
  public static final int LEADER_PARTIDS = 4;
  public static final int GIT_INFO_SHA = 5;
  public static final int DISK_PARTS = 6;
  public static final int DIR = 7;
  public static final int VERSION = 8;
  public static final int CPU_CORES = 9;
  public static final int ZONE = 10;

  // isset id assignments
  private static final int __CLUSTER_ID_ISSET_ID = 0;
  private static final int __CPU_CORES_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<Integer, FieldMetaData> metaDataMap;

  static {
    Map<Integer, FieldMetaData> tmpMetaDataMap = new HashMap<Integer, FieldMetaData>();
    tmpMetaDataMap.put(ROLE, new FieldMetaData("role", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(HOST, new FieldMetaData("host", TFieldRequirementType.DEFAULT, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.HostAddr.class)));
    tmpMetaDataMap.put(CLUSTER_ID, new FieldMetaData("cluster_id", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I64)));
    tmpMetaDataMap.put(LEADER_PARTIDS, new FieldMetaData("leader_partIds", TFieldRequirementType.OPTIONAL, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.I32), 
            new ListMetaData(TType.LIST, 
                new StructMetaData(TType.STRUCT, LeaderInfo.class)))));
    tmpMetaDataMap.put(GIT_INFO_SHA, new FieldMetaData("git_info_sha", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(DISK_PARTS, new FieldMetaData("disk_parts", TFieldRequirementType.OPTIONAL, 
        new MapMetaData(TType.MAP, 
            new FieldValueMetaData(TType.I32), 
            new MapMetaData(TType.MAP, 
                new FieldValueMetaData(TType.STRING), 
                new StructMetaData(TType.STRUCT, PartitionList.class)))));
    tmpMetaDataMap.put(DIR, new FieldMetaData("dir", TFieldRequirementType.OPTIONAL, 
        new StructMetaData(TType.STRUCT, com.vesoft.nebula.DirInfo.class)));
    tmpMetaDataMap.put(VERSION, new FieldMetaData("version", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    tmpMetaDataMap.put(CPU_CORES, new FieldMetaData("cpu_cores", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.I32)));
    tmpMetaDataMap.put(ZONE, new FieldMetaData("zone", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMetaDataMap);
  }

  static {
    FieldMetaData.addStructMetaDataMap(HBReq.class, metaDataMap);
  }

  public HBReq() {
    this.cpu_cores = -1;

  }

  public HBReq(
      HostRole role,
      com.vesoft.nebula.HostAddr host,
      long cluster_id,
      byte[] git_info_sha) {
    this();
    this.role = role;
    this.host = host;
    this.cluster_id = cluster_id;
    setCluster_idIsSet(true);
    this.git_info_sha = git_info_sha;
  }

  public HBReq(
      HostRole role,
      com.vesoft.nebula.HostAddr host,
      long cluster_id,
      Map<Integer,List<LeaderInfo>> leader_partIds,
      byte[] git_info_sha,
      Map<Integer,Map<byte[],PartitionList>> disk_parts,
      com.vesoft.nebula.DirInfo dir,
      byte[] version,
      int cpu_cores,
      byte[] zone) {
    this();
    this.role = role;
    this.host = host;
    this.cluster_id = cluster_id;
    setCluster_idIsSet(true);
    this.leader_partIds = leader_partIds;
    this.git_info_sha = git_info_sha;
    this.disk_parts = disk_parts;
    this.dir = dir;
    this.version = version;
    this.cpu_cores = cpu_cores;
    setCpu_coresIsSet(true);
    this.zone = zone;
  }

  public static class Builder {
    private HostRole role;
    private com.vesoft.nebula.HostAddr host;
    private long cluster_id;
    private Map<Integer,List<LeaderInfo>> leader_partIds;
    private byte[] git_info_sha;
    private Map<Integer,Map<byte[],PartitionList>> disk_parts;
    private com.vesoft.nebula.DirInfo dir;
    private byte[] version;
    private int cpu_cores;
    private byte[] zone;

    BitSet __optional_isset = new BitSet(2);

    public Builder() {
    }

    public Builder setRole(final HostRole role) {
      this.role = role;
      return this;
    }

    public Builder setHost(final com.vesoft.nebula.HostAddr host) {
      this.host = host;
      return this;
    }

    public Builder setCluster_id(final long cluster_id) {
      this.cluster_id = cluster_id;
      __optional_isset.set(__CLUSTER_ID_ISSET_ID, true);
      return this;
    }

    public Builder setLeader_partIds(final Map<Integer,List<LeaderInfo>> leader_partIds) {
      this.leader_partIds = leader_partIds;
      return this;
    }

    public Builder setGit_info_sha(final byte[] git_info_sha) {
      this.git_info_sha = git_info_sha;
      return this;
    }

    public Builder setDisk_parts(final Map<Integer,Map<byte[],PartitionList>> disk_parts) {
      this.disk_parts = disk_parts;
      return this;
    }

    public Builder setDir(final com.vesoft.nebula.DirInfo dir) {
      this.dir = dir;
      return this;
    }

    public Builder setVersion(final byte[] version) {
      this.version = version;
      return this;
    }

    public Builder setCpu_cores(final int cpu_cores) {
      this.cpu_cores = cpu_cores;
      __optional_isset.set(__CPU_CORES_ISSET_ID, true);
      return this;
    }

    public Builder setZone(final byte[] zone) {
      this.zone = zone;
      return this;
    }

    public HBReq build() {
      HBReq result = new HBReq();
      result.setRole(this.role);
      result.setHost(this.host);
      if (__optional_isset.get(__CLUSTER_ID_ISSET_ID)) {
        result.setCluster_id(this.cluster_id);
      }
      result.setLeader_partIds(this.leader_partIds);
      result.setGit_info_sha(this.git_info_sha);
      result.setDisk_parts(this.disk_parts);
      result.setDir(this.dir);
      result.setVersion(this.version);
      if (__optional_isset.get(__CPU_CORES_ISSET_ID)) {
        result.setCpu_cores(this.cpu_cores);
      }
      result.setZone(this.zone);
      return result;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public HBReq(HBReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetRole()) {
      this.role = TBaseHelper.deepCopy(other.role);
    }
    if (other.isSetHost()) {
      this.host = TBaseHelper.deepCopy(other.host);
    }
    this.cluster_id = TBaseHelper.deepCopy(other.cluster_id);
    if (other.isSetLeader_partIds()) {
      this.leader_partIds = TBaseHelper.deepCopy(other.leader_partIds);
    }
    if (other.isSetGit_info_sha()) {
      this.git_info_sha = TBaseHelper.deepCopy(other.git_info_sha);
    }
    if (other.isSetDisk_parts()) {
      this.disk_parts = TBaseHelper.deepCopy(other.disk_parts);
    }
    if (other.isSetDir()) {
      this.dir = TBaseHelper.deepCopy(other.dir);
    }
    if (other.isSetVersion()) {
      this.version = TBaseHelper.deepCopy(other.version);
    }
    this.cpu_cores = TBaseHelper.deepCopy(other.cpu_cores);
    if (other.isSetZone()) {
      this.zone = TBaseHelper.deepCopy(other.zone);
    }
  }

  public HBReq deepCopy() {
    return new HBReq(this);
  }

  /**
   * 
   * @see HostRole
   */
  public HostRole getRole() {
    return this.role;
  }

  /**
   * 
   * @see HostRole
   */
  public HBReq setRole(HostRole role) {
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

  public com.vesoft.nebula.HostAddr getHost() {
    return this.host;
  }

  public HBReq setHost(com.vesoft.nebula.HostAddr host) {
    this.host = host;
    return this;
  }

  public void unsetHost() {
    this.host = null;
  }

  // Returns true if field host is set (has been assigned a value) and false otherwise
  public boolean isSetHost() {
    return this.host != null;
  }

  public void setHostIsSet(boolean __value) {
    if (!__value) {
      this.host = null;
    }
  }

  public long getCluster_id() {
    return this.cluster_id;
  }

  public HBReq setCluster_id(long cluster_id) {
    this.cluster_id = cluster_id;
    setCluster_idIsSet(true);
    return this;
  }

  public void unsetCluster_id() {
    __isset_bit_vector.clear(__CLUSTER_ID_ISSET_ID);
  }

  // Returns true if field cluster_id is set (has been assigned a value) and false otherwise
  public boolean isSetCluster_id() {
    return __isset_bit_vector.get(__CLUSTER_ID_ISSET_ID);
  }

  public void setCluster_idIsSet(boolean __value) {
    __isset_bit_vector.set(__CLUSTER_ID_ISSET_ID, __value);
  }

  public Map<Integer,List<LeaderInfo>> getLeader_partIds() {
    return this.leader_partIds;
  }

  public HBReq setLeader_partIds(Map<Integer,List<LeaderInfo>> leader_partIds) {
    this.leader_partIds = leader_partIds;
    return this;
  }

  public void unsetLeader_partIds() {
    this.leader_partIds = null;
  }

  // Returns true if field leader_partIds is set (has been assigned a value) and false otherwise
  public boolean isSetLeader_partIds() {
    return this.leader_partIds != null;
  }

  public void setLeader_partIdsIsSet(boolean __value) {
    if (!__value) {
      this.leader_partIds = null;
    }
  }

  public byte[] getGit_info_sha() {
    return this.git_info_sha;
  }

  public HBReq setGit_info_sha(byte[] git_info_sha) {
    this.git_info_sha = git_info_sha;
    return this;
  }

  public void unsetGit_info_sha() {
    this.git_info_sha = null;
  }

  // Returns true if field git_info_sha is set (has been assigned a value) and false otherwise
  public boolean isSetGit_info_sha() {
    return this.git_info_sha != null;
  }

  public void setGit_info_shaIsSet(boolean __value) {
    if (!__value) {
      this.git_info_sha = null;
    }
  }

  public Map<Integer,Map<byte[],PartitionList>> getDisk_parts() {
    return this.disk_parts;
  }

  public HBReq setDisk_parts(Map<Integer,Map<byte[],PartitionList>> disk_parts) {
    this.disk_parts = disk_parts;
    return this;
  }

  public void unsetDisk_parts() {
    this.disk_parts = null;
  }

  // Returns true if field disk_parts is set (has been assigned a value) and false otherwise
  public boolean isSetDisk_parts() {
    return this.disk_parts != null;
  }

  public void setDisk_partsIsSet(boolean __value) {
    if (!__value) {
      this.disk_parts = null;
    }
  }

  public com.vesoft.nebula.DirInfo getDir() {
    return this.dir;
  }

  public HBReq setDir(com.vesoft.nebula.DirInfo dir) {
    this.dir = dir;
    return this;
  }

  public void unsetDir() {
    this.dir = null;
  }

  // Returns true if field dir is set (has been assigned a value) and false otherwise
  public boolean isSetDir() {
    return this.dir != null;
  }

  public void setDirIsSet(boolean __value) {
    if (!__value) {
      this.dir = null;
    }
  }

  public byte[] getVersion() {
    return this.version;
  }

  public HBReq setVersion(byte[] version) {
    this.version = version;
    return this;
  }

  public void unsetVersion() {
    this.version = null;
  }

  // Returns true if field version is set (has been assigned a value) and false otherwise
  public boolean isSetVersion() {
    return this.version != null;
  }

  public void setVersionIsSet(boolean __value) {
    if (!__value) {
      this.version = null;
    }
  }

  public int getCpu_cores() {
    return this.cpu_cores;
  }

  public HBReq setCpu_cores(int cpu_cores) {
    this.cpu_cores = cpu_cores;
    setCpu_coresIsSet(true);
    return this;
  }

  public void unsetCpu_cores() {
    __isset_bit_vector.clear(__CPU_CORES_ISSET_ID);
  }

  // Returns true if field cpu_cores is set (has been assigned a value) and false otherwise
  public boolean isSetCpu_cores() {
    return __isset_bit_vector.get(__CPU_CORES_ISSET_ID);
  }

  public void setCpu_coresIsSet(boolean __value) {
    __isset_bit_vector.set(__CPU_CORES_ISSET_ID, __value);
  }

  public byte[] getZone() {
    return this.zone;
  }

  public HBReq setZone(byte[] zone) {
    this.zone = zone;
    return this;
  }

  public void unsetZone() {
    this.zone = null;
  }

  // Returns true if field zone is set (has been assigned a value) and false otherwise
  public boolean isSetZone() {
    return this.zone != null;
  }

  public void setZoneIsSet(boolean __value) {
    if (!__value) {
      this.zone = null;
    }
  }

  @SuppressWarnings("unchecked")
  public void setFieldValue(int fieldID, Object __value) {
    switch (fieldID) {
    case ROLE:
      if (__value == null) {
        unsetRole();
      } else {
        setRole((HostRole)__value);
      }
      break;

    case HOST:
      if (__value == null) {
        unsetHost();
      } else {
        setHost((com.vesoft.nebula.HostAddr)__value);
      }
      break;

    case CLUSTER_ID:
      if (__value == null) {
        unsetCluster_id();
      } else {
        setCluster_id((Long)__value);
      }
      break;

    case LEADER_PARTIDS:
      if (__value == null) {
        unsetLeader_partIds();
      } else {
        setLeader_partIds((Map<Integer,List<LeaderInfo>>)__value);
      }
      break;

    case GIT_INFO_SHA:
      if (__value == null) {
        unsetGit_info_sha();
      } else {
        setGit_info_sha((byte[])__value);
      }
      break;

    case DISK_PARTS:
      if (__value == null) {
        unsetDisk_parts();
      } else {
        setDisk_parts((Map<Integer,Map<byte[],PartitionList>>)__value);
      }
      break;

    case DIR:
      if (__value == null) {
        unsetDir();
      } else {
        setDir((com.vesoft.nebula.DirInfo)__value);
      }
      break;

    case VERSION:
      if (__value == null) {
        unsetVersion();
      } else {
        setVersion((byte[])__value);
      }
      break;

    case CPU_CORES:
      if (__value == null) {
        unsetCpu_cores();
      } else {
        setCpu_cores((Integer)__value);
      }
      break;

    case ZONE:
      if (__value == null) {
        unsetZone();
      } else {
        setZone((byte[])__value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case ROLE:
      return getRole();

    case HOST:
      return getHost();

    case CLUSTER_ID:
      return new Long(getCluster_id());

    case LEADER_PARTIDS:
      return getLeader_partIds();

    case GIT_INFO_SHA:
      return getGit_info_sha();

    case DISK_PARTS:
      return getDisk_parts();

    case DIR:
      return getDir();

    case VERSION:
      return getVersion();

    case CPU_CORES:
      return new Integer(getCpu_cores());

    case ZONE:
      return getZone();

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
    if (!(_that instanceof HBReq))
      return false;
    HBReq that = (HBReq)_that;

    if (!TBaseHelper.equalsNobinary(this.isSetRole(), that.isSetRole(), this.role, that.role)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetHost(), that.isSetHost(), this.host, that.host)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.cluster_id, that.cluster_id)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetLeader_partIds(), that.isSetLeader_partIds(), this.leader_partIds, that.leader_partIds)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetGit_info_sha(), that.isSetGit_info_sha(), this.git_info_sha, that.git_info_sha)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetDisk_parts(), that.isSetDisk_parts(), this.disk_parts, that.disk_parts)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetDir(), that.isSetDir(), this.dir, that.dir)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetVersion(), that.isSetVersion(), this.version, that.version)) { return false; }

    if (!TBaseHelper.equalsNobinary(this.isSetCpu_cores(), that.isSetCpu_cores(), this.cpu_cores, that.cpu_cores)) { return false; }

    if (!TBaseHelper.equalsSlow(this.isSetZone(), that.isSetZone(), this.zone, that.zone)) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[] {role, host, cluster_id, leader_partIds, git_info_sha, disk_parts, dir, version, cpu_cores, zone});
  }

  @Override
  public int compareTo(HBReq other) {
    if (other == null) {
      // See java.lang.Comparable docs
      throw new NullPointerException();
    }

    if (other == this) {
      return 0;
    }
    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetRole()).compareTo(other.isSetRole());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(role, other.role);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetHost()).compareTo(other.isSetHost());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(host, other.host);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCluster_id()).compareTo(other.isSetCluster_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(cluster_id, other.cluster_id);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetLeader_partIds()).compareTo(other.isSetLeader_partIds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(leader_partIds, other.leader_partIds);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetGit_info_sha()).compareTo(other.isSetGit_info_sha());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(git_info_sha, other.git_info_sha);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetDisk_parts()).compareTo(other.isSetDisk_parts());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(disk_parts, other.disk_parts);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetDir()).compareTo(other.isSetDir());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(dir, other.dir);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetVersion()).compareTo(other.isSetVersion());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(version, other.version);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetCpu_cores()).compareTo(other.isSetCpu_cores());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(cpu_cores, other.cpu_cores);
    if (lastComparison != 0) { 
      return lastComparison;
    }
    lastComparison = Boolean.valueOf(isSetZone()).compareTo(other.isSetZone());
    if (lastComparison != 0) {
      return lastComparison;
    }
    lastComparison = TBaseHelper.compareTo(zone, other.zone);
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
        case ROLE:
          if (__field.type == TType.I32) {
            this.role = HostRole.findByValue(iprot.readI32());
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case HOST:
          if (__field.type == TType.STRUCT) {
            this.host = new com.vesoft.nebula.HostAddr();
            this.host.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case CLUSTER_ID:
          if (__field.type == TType.I64) {
            this.cluster_id = iprot.readI64();
            setCluster_idIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case LEADER_PARTIDS:
          if (__field.type == TType.MAP) {
            {
              TMap _map144 = iprot.readMapBegin();
              this.leader_partIds = new HashMap<Integer,List<LeaderInfo>>(Math.max(0, 2*_map144.size));
              for (int _i145 = 0; 
                   (_map144.size < 0) ? iprot.peekMap() : (_i145 < _map144.size); 
                   ++_i145)
              {
                int _key146;
                List<LeaderInfo> _val147;
                _key146 = iprot.readI32();
                {
                  TList _list148 = iprot.readListBegin();
                  _val147 = new ArrayList<LeaderInfo>(Math.max(0, _list148.size));
                  for (int _i149 = 0; 
                       (_list148.size < 0) ? iprot.peekList() : (_i149 < _list148.size); 
                       ++_i149)
                  {
                    LeaderInfo _elem150;
                    _elem150 = new LeaderInfo();
                    _elem150.read(iprot);
                    _val147.add(_elem150);
                  }
                  iprot.readListEnd();
                }
                this.leader_partIds.put(_key146, _val147);
              }
              iprot.readMapEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case GIT_INFO_SHA:
          if (__field.type == TType.STRING) {
            this.git_info_sha = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case DISK_PARTS:
          if (__field.type == TType.MAP) {
            {
              TMap _map151 = iprot.readMapBegin();
              this.disk_parts = new HashMap<Integer,Map<byte[],PartitionList>>(Math.max(0, 2*_map151.size));
              for (int _i152 = 0; 
                   (_map151.size < 0) ? iprot.peekMap() : (_i152 < _map151.size); 
                   ++_i152)
              {
                int _key153;
                Map<byte[],PartitionList> _val154;
                _key153 = iprot.readI32();
                {
                  TMap _map155 = iprot.readMapBegin();
                  _val154 = new HashMap<byte[],PartitionList>(Math.max(0, 2*_map155.size));
                  for (int _i156 = 0; 
                       (_map155.size < 0) ? iprot.peekMap() : (_i156 < _map155.size); 
                       ++_i156)
                  {
                    byte[] _key157;
                    PartitionList _val158;
                    _key157 = iprot.readBinary();
                    _val158 = new PartitionList();
                    _val158.read(iprot);
                    _val154.put(_key157, _val158);
                  }
                  iprot.readMapEnd();
                }
                this.disk_parts.put(_key153, _val154);
              }
              iprot.readMapEnd();
            }
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case DIR:
          if (__field.type == TType.STRUCT) {
            this.dir = new com.vesoft.nebula.DirInfo();
            this.dir.read(iprot);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case VERSION:
          if (__field.type == TType.STRING) {
            this.version = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case CPU_CORES:
          if (__field.type == TType.I32) {
            this.cpu_cores = iprot.readI32();
            setCpu_coresIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, __field.type);
          }
          break;
        case ZONE:
          if (__field.type == TType.STRING) {
            this.zone = iprot.readBinary();
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
    if (this.role != null) {
      oprot.writeFieldBegin(ROLE_FIELD_DESC);
      oprot.writeI32(this.role == null ? 0 : this.role.getValue());
      oprot.writeFieldEnd();
    }
    if (this.host != null) {
      oprot.writeFieldBegin(HOST_FIELD_DESC);
      this.host.write(oprot);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(CLUSTER_ID_FIELD_DESC);
    oprot.writeI64(this.cluster_id);
    oprot.writeFieldEnd();
    if (this.leader_partIds != null) {
      if (isSetLeader_partIds()) {
        oprot.writeFieldBegin(LEADER_PART_IDS_FIELD_DESC);
        {
          oprot.writeMapBegin(new TMap(TType.I32, TType.LIST, this.leader_partIds.size()));
          for (Map.Entry<Integer, List<LeaderInfo>> _iter159 : this.leader_partIds.entrySet())          {
            oprot.writeI32(_iter159.getKey());
            {
              oprot.writeListBegin(new TList(TType.STRUCT, _iter159.getValue().size()));
              for (LeaderInfo _iter160 : _iter159.getValue())              {
                _iter160.write(oprot);
              }
              oprot.writeListEnd();
            }
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
    }
    if (this.git_info_sha != null) {
      oprot.writeFieldBegin(GIT_INFO_SHA_FIELD_DESC);
      oprot.writeBinary(this.git_info_sha);
      oprot.writeFieldEnd();
    }
    if (this.disk_parts != null) {
      if (isSetDisk_parts()) {
        oprot.writeFieldBegin(DISK_PARTS_FIELD_DESC);
        {
          oprot.writeMapBegin(new TMap(TType.I32, TType.MAP, this.disk_parts.size()));
          for (Map.Entry<Integer, Map<byte[],PartitionList>> _iter161 : this.disk_parts.entrySet())          {
            oprot.writeI32(_iter161.getKey());
            {
              oprot.writeMapBegin(new TMap(TType.STRING, TType.STRUCT, _iter161.getValue().size()));
              for (Map.Entry<byte[], PartitionList> _iter162 : _iter161.getValue().entrySet())              {
                oprot.writeBinary(_iter162.getKey());
                _iter162.getValue().write(oprot);
              }
              oprot.writeMapEnd();
            }
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
    }
    if (this.dir != null) {
      if (isSetDir()) {
        oprot.writeFieldBegin(DIR_FIELD_DESC);
        this.dir.write(oprot);
        oprot.writeFieldEnd();
      }
    }
    if (this.version != null) {
      if (isSetVersion()) {
        oprot.writeFieldBegin(VERSION_FIELD_DESC);
        oprot.writeBinary(this.version);
        oprot.writeFieldEnd();
      }
    }
    if (isSetCpu_cores()) {
      oprot.writeFieldBegin(CPU_CORES_FIELD_DESC);
      oprot.writeI32(this.cpu_cores);
      oprot.writeFieldEnd();
    }
    if (this.zone != null) {
      if (isSetZone()) {
        oprot.writeFieldBegin(ZONE_FIELD_DESC);
        oprot.writeBinary(this.zone);
        oprot.writeFieldEnd();
      }
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
    StringBuilder sb = new StringBuilder("HBReq");
    sb.append(space);
    sb.append("(");
    sb.append(newLine);
    boolean first = true;

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
    sb.append("host");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getHost() == null) {
      sb.append("null");
    } else {
      sb.append(TBaseHelper.toString(this.getHost(), indent + 1, prettyPrint));
    }
    first = false;
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("cluster_id");
    sb.append(space);
    sb.append(":").append(space);
    sb.append(TBaseHelper.toString(this.getCluster_id(), indent + 1, prettyPrint));
    first = false;
    if (isSetLeader_partIds())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("leader_partIds");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getLeader_partIds() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getLeader_partIds(), indent + 1, prettyPrint));
      }
      first = false;
    }
    if (!first) sb.append("," + newLine);
    sb.append(indentStr);
    sb.append("git_info_sha");
    sb.append(space);
    sb.append(":").append(space);
    if (this.getGit_info_sha() == null) {
      sb.append("null");
    } else {
        int __git_info_sha_size = Math.min(this.getGit_info_sha().length, 128);
        for (int i = 0; i < __git_info_sha_size; i++) {
          if (i != 0) sb.append(" ");
          sb.append(Integer.toHexString(this.getGit_info_sha()[i]).length() > 1 ? Integer.toHexString(this.getGit_info_sha()[i]).substring(Integer.toHexString(this.getGit_info_sha()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getGit_info_sha()[i]).toUpperCase());
        }
        if (this.getGit_info_sha().length > 128) sb.append(" ...");
    }
    first = false;
    if (isSetDisk_parts())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("disk_parts");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getDisk_parts() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getDisk_parts(), indent + 1, prettyPrint));
      }
      first = false;
    }
    if (isSetDir())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("dir");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getDir() == null) {
        sb.append("null");
      } else {
        sb.append(TBaseHelper.toString(this.getDir(), indent + 1, prettyPrint));
      }
      first = false;
    }
    if (isSetVersion())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("version");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getVersion() == null) {
        sb.append("null");
      } else {
          int __version_size = Math.min(this.getVersion().length, 128);
          for (int i = 0; i < __version_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getVersion()[i]).length() > 1 ? Integer.toHexString(this.getVersion()[i]).substring(Integer.toHexString(this.getVersion()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getVersion()[i]).toUpperCase());
          }
          if (this.getVersion().length > 128) sb.append(" ...");
      }
      first = false;
    }
    if (isSetCpu_cores())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("cpu_cores");
      sb.append(space);
      sb.append(":").append(space);
      sb.append(TBaseHelper.toString(this.getCpu_cores(), indent + 1, prettyPrint));
      first = false;
    }
    if (isSetZone())
    {
      if (!first) sb.append("," + newLine);
      sb.append(indentStr);
      sb.append("zone");
      sb.append(space);
      sb.append(":").append(space);
      if (this.getZone() == null) {
        sb.append("null");
      } else {
          int __zone_size = Math.min(this.getZone().length, 128);
          for (int i = 0; i < __zone_size; i++) {
            if (i != 0) sb.append(" ");
            sb.append(Integer.toHexString(this.getZone()[i]).length() > 1 ? Integer.toHexString(this.getZone()[i]).substring(Integer.toHexString(this.getZone()[i]).length() - 2).toUpperCase() : "0" + Integer.toHexString(this.getZone()[i]).toUpperCase());
          }
          if (this.getZone().length > 128) sb.append(" ...");
      }
      first = false;
    }
    sb.append(newLine + TBaseHelper.reduceIndent(indentStr));
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

