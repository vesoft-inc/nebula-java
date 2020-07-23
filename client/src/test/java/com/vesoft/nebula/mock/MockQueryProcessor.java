/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.mock;

import com.facebook.thrift.TException;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;

public class MockQueryProcessor implements GraphService.Iface {

    private static final String USER_NAME = "user";
    private static final String PASSWORD = "password";

    @Override
    public AuthResponse authenticate(String username, String password) throws TException {
        if (username.equals(USER_NAME) && password.equals(PASSWORD)) {
            return new AuthResponse(ErrorCode.E_BAD_USERNAME_PASSWORD,
                    1, "BAD USERNAME OR PASSWORD");
        } else {
            return new AuthResponse(ErrorCode.SUCCEEDED, 1, "SUCCEEDED");
        }
    }

    @Override
    public void signout(long sessionId) throws TException {
        return;
    }

    @Override
    public ExecutionResponse execute(long sessionId, String stmt) throws TException {
        if (stmt.trim().length() == 0) {
            return new ExecutionResponse(ErrorCode.E_SYNTAX_ERROR, 1);
        } else {
            return new ExecutionResponse(ErrorCode.SUCCEEDED, 1);
        }
    }
}

