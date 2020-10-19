/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.sync;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.net.SyncConnection;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import org.junit.Test;

public class TestSyncConnection {
    @Test(timeout = 3000)
    public void testAll() {
        try {
            // Test open
            SyncConnection connection = new SyncConnection();
            connection.open(new HostAddress("127.0.0.1", 3699), 1000);

            // Test authenticate
            long sessionId = connection.authenticate("root", "nebula");
            assert (sessionId != 0);

            // Test execute
            ExecutionResponse resp = connection.execute(sessionId, "SHOW SPACES;");
            assert (resp.error_code == ErrorCode.SUCCEEDED);

            // Test signout
            connection.signout(sessionId);

            try {
                connection.execute(sessionId, "SHOW SPACES;");
            } catch (Exception e) {
                assert (true);
            }

            connection.close();

            try {
                connection.authenticate("root", "nebula");
            } catch (Exception e) {
                assert (true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }
}
