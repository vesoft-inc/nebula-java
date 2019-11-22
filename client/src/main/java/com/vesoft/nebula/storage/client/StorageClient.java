/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.google.common.base.Optional;
import com.vesoft.nebula.Client;

import java.util.List;
import java.util.Map;

public interface StorageClient extends Client {

    public boolean put(int space, String key, String value);

    public boolean put(int space, Map<String, String> kvs);

    public Optional<String> get(int space, String key);

    public Optional<Map<String, String>> get(int space, List<String> keys);

    public boolean remove(int space, String key);

    public boolean remove(int space, List<String> keys);

    // public boolean removeRange(int space, String start, String end);
}
