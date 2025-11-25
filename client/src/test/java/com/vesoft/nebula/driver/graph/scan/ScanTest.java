/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.scan;

import com.vesoft.nebula.driver.graph.ErrorCode;
import com.vesoft.nebula.driver.graph.ServerConstant;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import com.vesoft.nebula.driver.graph.util.MockGraph;
import io.grpc.Server;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ScanTest {

    String addresses    = ServerConstant.address;
    String user         = ServerConstant.user;
    String passwd       = ServerConstant.passwd;
    String graphName    = "nba";
    String sslGraphName = "nba_ssl";
    String nodeType     = "node_type_player";
    String edgeType     = "edge_type_follow";

    NebulaClient client;

    @Before
    public void setup() {
        try {
            client = NebulaClient.builder(addresses, user, passwd)
                .withRequestTimeoutMills(3000)
                .build();
            ResultSet resultSet = client.execute("return 1");
            Assert.assertEquals(resultSet.getErrorCode(), ErrorCode.SUCCESSFUL_COMPLETION);
        } catch (IOErrorException | AuthFailedException e) {
            Assert.fail(e.getMessage());
        }
        MockGraph.mockGraphData();
        MockGraph.mockSpecialGraphType();
        MockGraph.mockGraphDataWithSSL();
        MockGraph.mockMoreGraphData();
    }

    @After
    public void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void scanNodeForReturnCols() {
        // test specific return columns
        List<String>           returnCols = Arrays.asList("id", "name");
        ScanNodeResultIterator iterator   = client.scanNode(graphName, nodeType, returnCols, 1);
        List<TableRow>         rows       = new ArrayList<>();
        List<String>           columns    = new ArrayList<>();
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }

        }
        assert (rows.size() == 3);
        assert (columns.size() == 2);

        // test null return columns
        rows.clear();
        columns.clear();
        returnCols = null;
        iterator = client.scanNode(graphName, nodeType, returnCols);
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 3);
        assert (columns.size() == 5);

        // test empty return columns
        rows.clear();
        columns.clear();
        returnCols = new ArrayList<>();
        iterator = client.scanNode(graphName, nodeType, returnCols);
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 3);
        assert (columns.size() == 1);

        // test batch size
        rows.clear();
        columns.clear();
        returnCols = new ArrayList<>();
        iterator = client.scanNode(graphName, nodeType, returnCols, 2, 1);
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 1);
        assert (columns.size() == 1);
    }

    @Test
    public void scanNodeForEscapeProp() {
        String graphName = "图";
        String nodeType  = "user";
        // test specific return columns
        List<String>           returnCols = Arrays.asList("id", "type", "姓名", "age\t");
        ScanNodeResultIterator iterator   = client.scanNode(graphName, nodeType, returnCols, 1);
        List<TableRow>         rows       = new ArrayList<>();
        List<String>           columns    = new ArrayList<>();
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }

        }
        assert (rows.size() == 2);
        assert (columns.size() == 4);

        // test null return columns
        rows.clear();
        columns.clear();
        returnCols = null;
        iterator = client.scanNode(graphName, nodeType, returnCols);
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 4);

        // test empty return columns
        rows.clear();
        columns.clear();
        returnCols = new ArrayList<>();
        iterator = client.scanNode(graphName, nodeType, returnCols);
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 1);
    }

    @Test
    public void scanEdgeForReturnCols() {
        // test specific return columns
        List<String>           returnCols = Arrays.asList("likeness");
        ScanEdgeResultIterator iterator   = client.scanEdge(graphName, edgeType, returnCols, 1);
        List<TableRow>         rows       = new ArrayList<>();
        List<String>           columns    = new ArrayList<>();
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 3);

        // test null return columns
        rows.clear();
        columns.clear();
        returnCols = null;
        iterator = client.scanEdge(graphName, edgeType, returnCols);
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 4);

        // test empty return columns
        rows.clear();
        columns.clear();
        returnCols = new ArrayList<>();
        iterator = client.scanEdge(graphName, edgeType, returnCols);
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 2);

        // test batch size
        rows.clear();
        columns.clear();
        returnCols = new ArrayList<>();
        iterator = client.scanEdge(graphName, edgeType, returnCols, 2, 1);
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 1);
        assert (columns.size() == 2);
    }


    @Test
    public void scanEdgeForEscapeProp() {
        String graphName = "图";
        String edgeType  = "edge";
        // test specific return columns
        List<String>           returnCols = Arrays.asList("likeness", "type\r");
        ScanEdgeResultIterator iterator   = client.scanEdge(graphName, edgeType, returnCols, 10);
        List<TableRow>         rows       = new ArrayList<>();
        List<String>           columns    = new ArrayList<>();
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 4);

        // test null return columns
        rows.clear();
        columns.clear();
        returnCols = null;
        iterator = client.scanEdge(graphName, edgeType, returnCols);
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 5);

        // test empty return columns
        rows.clear();
        columns.clear();
        returnCols = new ArrayList<>();
        iterator = client.scanEdge(graphName, edgeType, returnCols);
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!result.isEmpty()) {
                rows.addAll(result.getTableRows());
                columns.addAll(result.getPropNames());
            }
        }
        assert (rows.size() == 2);
        assert (columns.size() == 2);
    }

    @Test
    public void testScanNodeWithSSL() {
        String       tlsCa         = "../docker-compose-ssl/certs/ca.crt";
        String       tlsCert       = "../docker-compose-ssl/certs/client.crt";
        String       tlsKey        = "../docker-compose-ssl/certs/client.key";
        NebulaClient clientWithSsl = null;
        try {
            clientWithSsl = new NebulaClient.Builder(ServerConstant.sslAddress,
                                                     user,
                                                     passwd)
                .withEnableTls(true)
                .withTlsCa(tlsCa)
                .withTlsCert(tlsCert, tlsKey)
                .build();
            List<String> returnCols = Arrays.asList("id", "name");
            ScanNodeResultIterator iterator = clientWithSsl.scanNode(sslGraphName,
                                                                     nodeType,
                                                                     returnCols,
                                                                     1);
            List<TableRow> rows    = new ArrayList<>();
            List<String>   columns = new ArrayList<>();
            while (iterator.hasNext()) {
                ScanNodeResult result = iterator.next();
                if (!result.isEmpty()) {
                    rows.addAll(result.getTableRows());
                    columns.addAll(result.getPropNames());
                }
            }
            assert (rows.size() == 3);
            assert (columns.size() == 2);
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            if (clientWithSsl != null) {
                clientWithSsl.close();
            }
        }
    }

    @Test
    public void testScanEdgeWithSSL() {
        String       tlsCa         = "../docker-compose-ssl/certs/ca.crt";
        String       tlsCert       = "../docker-compose-ssl/certs/client.crt";
        String       tlsKey        = "../docker-compose-ssl/certs/client.key";
        NebulaClient clientWithSsl = null;
        try {
            clientWithSsl = new NebulaClient.Builder(ServerConstant.sslAddress,
                                                     user,
                                                     passwd)
                .withEnableTls(true)
                .withTlsCa(tlsCa)
                .withTlsCert(tlsCert, tlsKey)
                .build();
            List<String> returnCols = Arrays.asList("likeness");
            ScanEdgeResultIterator iterator = clientWithSsl.scanEdge(sslGraphName,
                                                                     edgeType,
                                                                     returnCols,
                                                                     1);
            List<TableRow> rows    = new ArrayList<>();
            List<String>   columns = new ArrayList<>();
            while (iterator.hasNext()) {
                ScanEdgeResult result = iterator.next();
                if (!result.isEmpty()) {
                    rows.addAll(result.getTableRows());
                    columns.addAll(result.getPropNames());
                }
            }
            assert (rows.size() == 2);
            assert (columns.size() == 3);

        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            if (clientWithSsl != null) {
                clientWithSsl.close();
            }
        }
    }

    @Test
    public void testCursor() {
        // scan part 4 with batchSize 1
        ScanNodeResultIterator iter = client.scanNode("cursor", "player", null, 4, 1);
        if (iter.hasNext) {
            iter.next();
        }
        assert (iter.hasNext());
    }

    @Test
    public void testProcedureCursor() {
        // scan part 4 with batchSize 1
        try {
            ResultSet res = client.execute(
                "call cursor_node_scan('cursor','player',LIST[],4,\"\",1) return *");
            assert !res.getExtraInfo().getCursor().equals("");
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }
}
