package com.vesoft.nebula.tools.importer.test.mock

import com.vesoft.nebula.graph.{AuthResponse, ErrorCode, ExecutionResponse, GraphService}

class MockQueryProcessor extends GraphService.Iface {
  var queryStatement = ""
  override def authenticate(username: java.lang.String,
                            password: java.lang.String): AuthResponse = {
    if (MockConfigs.userConfig.user == username && MockConfigs.userConfig.password == password)
      new AuthResponse(ErrorCode.SUCCEEDED, 1, "SUCCEEDED")
    else
      new AuthResponse(ErrorCode.E_BAD_USERNAME_PASSWORD, 1, "BAD USERNAME OR PASSWORD")

  }
  override def signout(sessionId: Long): Unit = {}
  override def execute(sessionId: Long, stmt: java.lang.String): ExecutionResponse = {

    if (stmt.trim().length() == 0)
      new ExecutionResponse(ErrorCode.E_SYNTAX_ERROR, 1);
    else {
      queryStatement = stmt
      new ExecutionResponse(ErrorCode.SUCCEEDED, 1);
    }
  }
}
