/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.test.mock

import com.vesoft.nebula.graph.{AuthResponse, ErrorCode, ExecutionResponse, GraphService}

class MockQueryProcessor extends GraphService.Iface {
  var queryStatement                    = ""
  var countDownLatchFailOfInsert: Int   = -1
  var countDownLatchFailOfSentence: Int = -1

  def resetLatch(): Unit = {
    countDownLatchFailOfSentence = -1
    countDownLatchFailOfInsert = -1
  }

  def resetLatchInsert(): Unit = countDownLatchFailOfInsert = -1
  def resetLatchSentence(): Unit = countDownLatchFailOfSentence = -1

  override def authenticate(username: java.lang.String,
                            password: java.lang.String): AuthResponse = {
    if (MockConfigs.userConfig.user == username && MockConfigs.userConfig.password == password)
      new AuthResponse(ErrorCode.SUCCEEDED, 1, "SUCCEEDED")
    else
      new AuthResponse(ErrorCode.E_BAD_USERNAME_PASSWORD, 1, "BAD USERNAME OR PASSWORD")

  }
  override def signout(sessionId: Long): Unit = {}
  override def execute(sessionId: Long, stmt: java.lang.String): ExecutionResponse = {
    println(stmt)
    queryStatement = stmt
    if (queryStatement.contains("INSERT")) {
      if (countDownLatchFailOfInsert == 0) {
        new ExecutionResponse(ErrorCode.E_SYNTAX_ERROR, 1)
      } else {
        if (countDownLatchFailOfInsert > 0)
          countDownLatchFailOfInsert -= 1
        new ExecutionResponse(ErrorCode.SUCCEEDED, 1);
      }
    } else {
      if (countDownLatchFailOfSentence == 0) {
        new ExecutionResponse(ErrorCode.E_SYNTAX_ERROR, 1)
      } else {
        if (countDownLatchFailOfSentence > 0)
          countDownLatchFailOfSentence -= 1
        new ExecutionResponse(ErrorCode.SUCCEEDED, 1);
      }
    }
  }
}
