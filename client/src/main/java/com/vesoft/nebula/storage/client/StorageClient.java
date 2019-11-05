/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.vesoft.nebula.Client;

import java.util.Optional;

public interface StorageClient extends Client {

    public boolean connect();

    public void switchSpace(int space);

    public boolean put(int part, String key, String value);

    public Optional<String> get(int part, String key);

    public boolean remove(int part, String key);

    public boolean removeRange(int part, String start, String end);
}
