/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph;

import org.junit.Test;

public class ErrorCodeTest {
    @Test
    public void testErrorCode() {
        assert ErrorCode.find("00000") == ErrorCode.SUCCESSFUL_COMPLETION;
        assert ErrorCode.find("42006").isSyntaxError();
        assert ErrorCode.find("NS004").isSemanticError();
        assert ErrorCode.find("NE000").isSessionError();
    }

    @Test
    public void testUnknowCode() {
        assert ErrorCode.find("asjfdas") == ErrorCode.UNKNOWN_FOR_CLIENT;
    }
}
