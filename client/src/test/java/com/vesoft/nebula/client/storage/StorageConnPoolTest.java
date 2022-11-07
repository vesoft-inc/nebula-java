/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;

public class StorageConnPoolTest extends TestCase {

    private StorageConnPool pool;

    public void setUp() throws Exception {
        super.setUp();
        testPoolInit();
    }

    public void tearDown() throws Exception {}

    public void testFreshCache() {}

    public void testPoolInit() {
        // invalidate host
        try {
            List<HostAddress> address = Arrays.asList(new HostAddress("hostname", 9559));
            StoragePoolConfig config = new StoragePoolConfig();
            config.setMaxTotal(20);
            config.setMaxTotalPerKey(8);
            StorageConnPool pool = new StorageConnPool(config);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        // normal
        try {
            List<HostAddress> address =
                    Arrays.asList(
                            new HostAddress("127.0.0.1", 9559),
                            new HostAddress("127.0.0.1", 9560),
                            new HostAddress("127.0.0.1", 9561));
            StoragePoolConfig config = new StoragePoolConfig();
            pool = new StorageConnPool(config);
            assertEquals(pool.getNumActive(new HostAddress("127.0.0.1", 9559)), 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetConnection() {
        try {
            List<HostAddress> address = Arrays.asList(new HostAddress("127.0.0.1", 9559));
            StoragePoolConfig config = new StoragePoolConfig();
            pool = new StorageConnPool(config);
            pool.getStorageConnection(address.get(0));

            assertEquals(pool.getNumActive(address.get(0)), 1);
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}
