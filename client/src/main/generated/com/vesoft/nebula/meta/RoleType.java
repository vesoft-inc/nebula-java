/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.meta;


import com.facebook.thrift.IntRangeSet;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({ "unused" })
public enum RoleType implements com.facebook.thrift.TEnum {
  GOD(1),
  ADMIN(2),
  DBA(3),
  USER(4),
  GUEST(5),
  BASIC(6);

  private final int value;

  private RoleType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static RoleType findByValue(int value) { 
    switch (value) {
      case 1:
        return GOD;
      case 2:
        return ADMIN;
      case 3:
        return DBA;
      case 4:
        return USER;
      case 5:
        return GUEST;
      case 6:
        return BASIC;
      default:
        return null;
    }
  }
}
