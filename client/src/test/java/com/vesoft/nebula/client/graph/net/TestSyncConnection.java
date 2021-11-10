/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.graph.ExecutionResponse;
import org.junit.Assert;
import org.junit.Test;

public class TestSyncConnection {
    @Test(timeout = 3000)
    public void testAll() {
        try {
            // Test open
            SyncConnection connection = new SyncConnection();
            connection.open(new HostAddress("127.0.0.1", 9671), 1000);

            // Test authenticate
            AuthResult authResult = connection.authenticate("root", "nebula");
            Assert.assertNotEquals(0, authResult.getSessionId());

            // Test execute
            ExecutionResponse resp = connection.execute(authResult.getSessionId(), "SHOW SPACES;");
            Assert.assertEquals(ErrorCode.SUCCEEDED, resp.error_code);

            // Test signout
            connection.signout(authResult.getSessionId());

            try {
                connection.execute(authResult.getSessionId(), "SHOW SPACES;");
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
