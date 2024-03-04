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
public enum CatchUpStatus implements com.facebook.thrift.TEnum {
  CAUGHT_UP(1),
  WAITING_FOR_SNAPSHOT(2),
  RUNNING(3),
  STARTING(4);

  private final int value;

  private CatchUpStatus(int value) {
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
  public static CatchUpStatus findByValue(int value) { 
    switch (value) {
      case 1:
        return CAUGHT_UP;
      case 2:
        return WAITING_FOR_SNAPSHOT;
      case 3:
        return RUNNING;
      case 4:
        return STARTING;
      default:
        return null;
    }
  }
}
