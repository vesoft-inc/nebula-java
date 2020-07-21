package com.vesoft.nebula.tools.connector

trait AddressHandler {

  def toAddress(addresses: String): List[Address] = {
    addresses
      .split(",")
      .map(address => {
        val hostAndPort = address.split(":")
        (hostAndPort(0), hostAndPort(1).toInt)
      })
      .toList
  }
}
