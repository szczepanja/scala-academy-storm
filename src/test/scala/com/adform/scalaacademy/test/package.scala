package com.adform.scalaacademy

import com.adform.scalaacademy.config.Config
import com.softwaremill.quicklens._


package object test {
  val DefaultConfig: Config = Config.load
  val TestConfig: Config = DefaultConfig
    .modify(_.topology.numWorkers)
    .setTo(1)
    .modify(_.topology.numAckers)
    .setTo(1)
}
