/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

object NebulaTemplate {

  private[connector] val BATCH_INSERT_TEMPLATE                           = "INSERT %s %s(%s) VALUES %s"
  private[connector] val VERTEX_VALUE_TEMPLATE                           = "%s: (%s)"
  private[connector] val VERTEX_VALUE_TEMPLATE_WITH_POLICY               = "%s(%s): (%s)"
  private[connector] val ENDPOINT_TEMPLATE                               = "%s(\"%s\")"
  private[connector] val EDGE_VALUE_WITHOUT_RANKING_TEMPLATE             = "%s->%s: (%s)"
  private[connector] val EDGE_VALUE_WITHOUT_RANKING_TEMPLATE_WITH_POLICY = "%s(%s)->%s(%s): (%s)"
  private[connector] val EDGE_VALUE_TEMPLATE                             = "%s->%s@%d: (%s)"
  private[connector] val EDGE_VALUE_TEMPLATE_WITH_POLICY                 = "%s(%s)->%s(%s)@%d: (%s)"
  private[connector] val USE_TEMPLATE                                    = "USE %s"

}
