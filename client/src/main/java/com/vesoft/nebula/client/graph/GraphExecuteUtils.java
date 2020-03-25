package com.vesoft.nebula.client.graph;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TTransport;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description GraphExecuteUtils is used for
 * @Date 2020/3/24 - 14:34
 */
public class GraphExecuteUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphExecuteUtils.class);


    /**
     * Execute the query sentence.
     *
     * @param statement The query sentence.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public static int execute(TTransport transport,
                              GraphService.Client client, long sessionId,
                              String statement, int executionRetry) {
        if (!checkTransportOpened(transport)) {
            return ErrorCode.E_DISCONNECTED;
        }

        int retry = executionRetry;
        while (retry-- > 0) {
            try {
                ExecutionResponse executionResponse = client.execute(sessionId, statement);
                if (executionResponse.getError_code() != ErrorCode.SUCCEEDED) {
                    LOGGER.error("execute error: " + executionResponse.getError_msg());
                }
                return executionResponse.getError_code();
            } catch (TException e) {
                LOGGER.error("Thrift rpc call failed: " + e.getMessage());
                return ErrorCode.E_RPC_FAILURE;
            }
        }
        return ErrorCode.E_RPC_FAILURE;
    }

    /**
     * Execute the query sentence which will return a ResultSet.
     *
     * @param statement The query sentence.
     * @return The result set of the query sentence.
     */
    public static ResultSet executeQuery(TTransport transport,
                                         GraphService.Client client, long sessionId,
                                         String statement) throws ConnectionException,
            NGQLException, TException {
        if (!checkTransportOpened(transport)) {
            LOGGER.error("Thrift rpc call failed");
            throw new ConnectionException();
        }

        ExecutionResponse executionResponse = client.execute(sessionId, statement);
        int code = executionResponse.getError_code();
        if (code == ErrorCode.SUCCEEDED) {
            return new ResultSet(executionResponse.getColumn_names(),
                    executionResponse.getRows());
        } else {
            LOGGER.error("Execute error: " + executionResponse.getError_msg());
            throw new NGQLException(code);
        }
    }


    private static boolean checkTransportOpened(TTransport transport) {
        return transport != null && transport.isOpen();
    }


}
