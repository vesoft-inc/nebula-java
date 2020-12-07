/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vesoft.nebula.graph;


import java.lang.reflect.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import com.facebook.thrift.IntRangeSet;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({ "unused" })
public class ErrorCode {
  public static final int SUCCEEDED = 0;
  public static final int E_DISCONNECTED = -1;
  public static final int E_FAIL_TO_CONNECT = -2;
  public static final int E_RPC_FAILURE = -3;
  public static final int E_BAD_USERNAME_PASSWORD = -4;
  public static final int E_SESSION_INVALID = -5;
  public static final int E_SESSION_TIMEOUT = -6;
  public static final int E_SYNTAX_ERROR = -7;
  public static final int E_EXECUTION_ERROR = -8;
  public static final int E_STATEMENT_EMPTY = -9;
  public static final int E_USER_NOT_FOUND = -10;
  public static final int E_BAD_PERMISSION = -11;
  public static final int E_SEMANTIC_ERROR = -12;

  public static final IntRangeSet VALID_VALUES;
  public static final Map<Integer, String> VALUES_TO_NAMES = new HashMap<Integer, String>();

  static {
    try {
      Class<?> klass = ErrorCode.class;
      for (Field f : klass.getDeclaredFields()) {
        if (f.getType() == Integer.TYPE) {
          VALUES_TO_NAMES.put(f.getInt(null), f.getName());
        }
      }
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }

    int[] values = new int[VALUES_TO_NAMES.size()];
    int i = 0;
    for (Integer v : VALUES_TO_NAMES.keySet()) {
      values[i++] = v;
    }

    VALID_VALUES = new IntRangeSet(values);
  }
}
