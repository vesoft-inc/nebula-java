/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.storage;


import com.facebook.thrift.IntRangeSet;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({ "unused" })
public enum ScanType implements com.facebook.thrift.TEnum {
  PREFIX(1),
  RANGE(2);

  private final int value;

  private ScanType(int value) {
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
  public static ScanType findByValue(int value) { 
    switch (value) {
      case 1:
        return PREFIX;
      case 2:
        return RANGE;
      default:
        return null;
    }
  }
}
