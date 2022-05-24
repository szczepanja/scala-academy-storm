package com.adform.scalaacademy.storm

import scala.concurrent.duration.FiniteDuration

case class TopologyConfiguration(
    name: String,
    debug: Boolean,
    numAckers: Int,
    numWorkers: Int,
    maxTaskParallelism: Int,
    messageTimeout: FiniteDuration,
    maxSpoutPending: Int
)
