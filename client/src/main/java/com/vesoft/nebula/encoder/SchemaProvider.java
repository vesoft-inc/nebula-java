/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

public interface SchemaProvider {
    public interface Field {
        public String name();

        public int type();

        public boolean nullable();

        public boolean hasDefault();

        public byte[] defaultValue();

        public int size();

        public int offset();

        public int nullFlagPos();
    }

    public long getVersion();

    public int getNumFields();

    public int getNumNullableFields();

    public int size();

    public int getFieldIndex(String name);

    public String getFiledName(int index);

    public int getFiledType(int index);

    public int getFiledType(String name);

    public Field field(int index);

    public Field field(String index);

    public int fieldSize(int type, int fixedStrLimit);
}
