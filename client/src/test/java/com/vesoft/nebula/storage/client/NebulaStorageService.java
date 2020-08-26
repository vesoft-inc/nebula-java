/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Schema;
import com.vesoft.nebula.storage.AddEdgesRequest;
import com.vesoft.nebula.storage.AddLearnerReq;
import com.vesoft.nebula.storage.AddPartReq;
import com.vesoft.nebula.storage.AddVerticesRequest;
import com.vesoft.nebula.storage.AdminExecResp;
import com.vesoft.nebula.storage.BlockingSignRequest;
import com.vesoft.nebula.storage.CatchUpDataReq;
import com.vesoft.nebula.storage.CheckPeersReq;
import com.vesoft.nebula.storage.CreateCPRequest;
import com.vesoft.nebula.storage.DeleteEdgesRequest;
import com.vesoft.nebula.storage.DeleteVerticesRequest;
import com.vesoft.nebula.storage.DropCPRequest;
import com.vesoft.nebula.storage.EdgePropRequest;
import com.vesoft.nebula.storage.EdgePropResponse;
import com.vesoft.nebula.storage.ExecResponse;
import com.vesoft.nebula.storage.GeneralResponse;
import com.vesoft.nebula.storage.GetLeaderReq;
import com.vesoft.nebula.storage.GetLeaderResp;
import com.vesoft.nebula.storage.GetNeighborsRequest;
import com.vesoft.nebula.storage.GetRequest;
import com.vesoft.nebula.storage.GetUUIDReq;
import com.vesoft.nebula.storage.GetUUIDResp;
import com.vesoft.nebula.storage.LookUpIndexRequest;
import com.vesoft.nebula.storage.LookUpIndexResp;
import com.vesoft.nebula.storage.MemberChangeReq;
import com.vesoft.nebula.storage.PutRequest;
import com.vesoft.nebula.storage.QueryResponse;
import com.vesoft.nebula.storage.QueryStatsResponse;
import com.vesoft.nebula.storage.RebuildIndexRequest;
import com.vesoft.nebula.storage.RemovePartReq;
import com.vesoft.nebula.storage.RemoveRangeRequest;
import com.vesoft.nebula.storage.RemoveRequest;
import com.vesoft.nebula.storage.ResponseCommon;
import com.vesoft.nebula.storage.ResultCode;
import com.vesoft.nebula.storage.ScanEdge;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import com.vesoft.nebula.storage.ScanVertex;
import com.vesoft.nebula.storage.ScanVertexRequest;
import com.vesoft.nebula.storage.ScanVertexResponse;
import com.vesoft.nebula.storage.StorageService;
import com.vesoft.nebula.storage.TransLeaderReq;
import com.vesoft.nebula.storage.UpdateEdgeRequest;
import com.vesoft.nebula.storage.UpdateResponse;
import com.vesoft.nebula.storage.UpdateVertexRequest;
import com.vesoft.nebula.storage.VertexPropRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaStorageService implements StorageService.Iface {
    List<byte[]> nextCursors = new ArrayList<byte[]>();
    Iterator<byte[]> nextCursorIt;

    private static final Logger LOGGER = LoggerFactory.getLogger(NebulaStorageService.class);

    NebulaStorageService() {
        prepareData();
    }

    @Override
    public QueryResponse getBound(GetNeighborsRequest req) throws TException {
        return null;
    }

    @Override
    public QueryStatsResponse boundStats(GetNeighborsRequest req) throws TException {
        return null;
    }

    @Override
    public QueryResponse getProps(VertexPropRequest req) throws TException {
        return null;
    }

    @Override
    public EdgePropResponse getEdgeProps(EdgePropRequest req) throws TException {
        return null;
    }

    @Override
    public ExecResponse addVertices(AddVerticesRequest req) throws TException {
        return null;
    }

    @Override
    public ExecResponse addEdges(AddEdgesRequest req) throws TException {
        return null;
    }

    @Override
    public ExecResponse deleteEdges(DeleteEdgesRequest req) throws TException {
        return null;
    }

    @Override
    public ExecResponse deleteVertices(DeleteVerticesRequest req) throws TException {
        return null;
    }

    @Override
    public UpdateResponse updateVertex(UpdateVertexRequest req) throws TException {
        return null;
    }

    @Override
    public UpdateResponse updateEdge(UpdateEdgeRequest req) throws TException {
        return null;
    }

    @Override
    public ScanEdgeResponse scanEdge(ScanEdgeRequest req) throws TException {
        final List<ResultCode> resultCodes = new ArrayList<ResultCode>();
        final int latency = RandomUtils.nextInt();
        final ResponseCommon result = new ResponseCommon(resultCodes, latency);
        final ScanEdgeResponse response = new ScanEdgeResponse(result);
        response.edge_schema = new HashMap<Integer, Schema>();
        response.edge_data = new ArrayList<ScanEdge>();
        if (nextCursorIt.hasNext()) {
            // Still has data in this part
            response.has_next = true;
            response.next_cursor = nextCursorIt.next();
        } else {
            // Mock the data in this part is done
            response.has_next = false;
            // Reset the cursor
            nextCursorIt = nextCursors.iterator();
        }
        return response;
    }

    @Override
    public ScanVertexResponse scanVertex(ScanVertexRequest req) throws TException {
        final List<ResultCode> resultCodes = new ArrayList<ResultCode>();
        final int latency = RandomUtils.nextInt();
        final ResponseCommon result = new ResponseCommon(resultCodes, latency);
        final ScanVertexResponse response = new ScanVertexResponse(result);
        response.vertex_schema = new HashMap<Integer, Schema>();
        response.vertex_data = new ArrayList<ScanVertex>();
        if (nextCursorIt.hasNext()) {
            // Still has data in this part
            response.has_next = true;
            response.next_cursor = nextCursorIt.next();
        } else {
            // Mock the data in this part is done
            response.has_next = false;
            // Reset the cursor
            nextCursorIt = nextCursors.iterator();
        }
        return response;
    }

    @Override
    public AdminExecResp transLeader(TransLeaderReq req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp addPart(AddPartReq req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp addLearner(AddLearnerReq req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp waitingForCatchUpData(CatchUpDataReq req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp removePart(RemovePartReq req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp memberChange(MemberChangeReq req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp checkPeers(CheckPeersReq req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp createCheckpoint(CreateCPRequest req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp dropCheckpoint(DropCPRequest req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp blockingWrites(BlockingSignRequest req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp rebuildTagIndex(RebuildIndexRequest req) throws TException {
        return null;
    }

    @Override
    public AdminExecResp rebuildEdgeIndex(RebuildIndexRequest req) throws TException {
        return null;
    }

    @Override
    public GetLeaderResp getLeaderPart(GetLeaderReq req) throws TException {
        return null;
    }

    @Override
    public ExecResponse put(PutRequest req) throws TException {
        return null;
    }

    @Override
    public GeneralResponse get(GetRequest req) throws TException {
        return null;
    }

    @Override
    public ExecResponse remove(RemoveRequest req) throws TException {
        return null;
    }

    @Override
    public ExecResponse removeRange(RemoveRangeRequest req) throws TException {
        return null;
    }

    @Override
    public GetUUIDResp getUUID(GetUUIDReq req) throws TException {
        return null;
    }

    @Override
    public LookUpIndexResp lookUpIndex(LookUpIndexRequest req) throws TException {
        return null;
    }

    void prepareData() {
        for (byte i = 1; i <= 16; i++) {
            byte[] cursor = new byte[]{i};
            nextCursors.add(cursor);
        }
        nextCursorIt = nextCursors.iterator();
    }
}

