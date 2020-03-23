/* Copyright (c) 2019 com.vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.session;

public class MetaSessionFactory implements SessionFactory<SimpleMetaSession> {
    @Override
    public SimpleMetaSession create() {
        return new SimpleMetaSession();
    }
}
