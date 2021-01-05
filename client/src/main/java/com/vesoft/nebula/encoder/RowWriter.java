/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Time;

public interface RowWriter {

    void write(int index, boolean v);

    void write(int index, float v);

    void write(int index, double v);

    void write(int index, byte[] v);

    void write(int index, byte v);

    void write(int index, short v);

    void write(int index, int v);

    void write(int index, long v);

    void write(int index, Time v);

    void write(int index, Date v);

    void write(int index, DateTime v);

    byte[] encodeStr();
}
