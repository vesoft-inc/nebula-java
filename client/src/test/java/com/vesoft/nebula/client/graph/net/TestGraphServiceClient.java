/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.vesoft.nebula.graph.GraphService;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;

public class TestGraphServiceClient {
    @Test
    public void testClientSerialize() {

        TTransport transport = new TSocket("127.0.0.1", 9669, 10000, 10000);
        TProtocol protocol = new TCompactProtocol(transport);
        GraphService.Client client = new GraphService.Client(protocol);

        if (!(client instanceof Serializable)) {
            System.out.println("GraphService.Client is not serialized.");
            Assert.assertFalse(
                    "GraphService.Client is not serialized, "
                            + "please modify the thrift file manually",
                    false);
        }
    }
}
