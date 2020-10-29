/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.config

case class NebulaConfig(hostPorts: String,
                        nameSpace: String,
                        partitionNumber: String,
                        labels: List[String],
                        hasWeight: Boolean,
                        weightCols: List[String])

object NebulaConfig {

  def getNebula(configs: Configs): NebulaConfig = {
    val nebulaConfigs   = configs.nebulaConfig
    val hostPorts       = nebulaConfigs.address
    val nameSpace       = nebulaConfigs.space
    val partitionNumber = nebulaConfigs.partitionNumber
    val labels          = nebulaConfigs.labels
    val hasWeight       = nebulaConfigs.hasWeight
    val weightCols      = nebulaConfigs.weightCols

    NebulaConfig(hostPorts, nameSpace, partitionNumber, labels, hasWeight, weightCols)
  }
}
