/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.vesoft.nebula.Client;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StorageClient extends Client {

    public int put(int space, String key, int value);

    public int get(int space, String key);

    // public int cas(int space, String key, int expected , int value);
}
