package com.adform.scalaacademy.config

import com.adform.scalaacademy.storm.TopologyConfiguration
import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Config(topology: TopologyConfiguration)

object Config extends StrictLogging {
  def load: Config = ConfigSource.default.loadOrThrow[Config]
  def log(config: Config): Unit = {
    val info =
      s"""
         | Topology configuration:
         | -----------------------
         | topology: ${config.topology}
         | -----------------------
         |""".stripMargin

    logger.info(info)
  }
}