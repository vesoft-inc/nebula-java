/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.config

object NebulaConfig {

  def getReadNebula(configs: Configs): NebulaReadConfigEntry = {
    val nebulaConfigs = configs.nebulaConfig
    nebulaConfigs.readConfigEntry
  }

  def getWriteNebula(configs: Configs): NebulaWriteConfigEntry = {
    val nebulaConfigs = configs.nebulaConfig
    nebulaConfigs.writeConfigEntry
  }
}
