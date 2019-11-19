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

    public void switchSpace(int space);

    public boolean put(int part, String key, String value);

    public boolean put(int part, Map<String, String> values);

    public Optional<String> get(int part, String key);

    public Optional<Map<String, String>> get(int part, List<String> keys);

    public boolean remove(int part, String key);

    public boolean removeRange(int part, String start, String end);

    public long hash(String key);
}
