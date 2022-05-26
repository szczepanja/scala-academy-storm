package com.adform.scalaacademy.storm

import com.adform.scalaacademy.DeviceMessageSpout
import com.adform.scalaacademy.config.Config
import com.adform.scalaacademy.infrastructure.TypedBlizzardSerializer
import com.adform.scalaacademy.storm.bolt.PrinterBolt
import org.apache.storm.{Config => StormTopologyConfig}
import org.apache.storm.generated.StormTopology
import org.apache.storm.topology.TopologyBuilder

class Topology(val config: Config) {
  def create(): StormTopology = {
    val builder = new TopologyBuilder()
    val spout = DeviceMessageSpout.createUnsafe(config.kafkaSpout)
    val sink = new PrinterBolt

    builder.setSpout("printer-spout", spout)
    builder
      .setBolt("sink-printer-bolt", sink)
      .shuffleGrouping("printer-spout")
      .setNumTasks(1)

    builder.createTopology()
  }

  def topologyConfig(): StormTopologyConfig = {
    val topologyConf = config.topology
    val stormConfig = new StormTopologyConfig()

    stormConfig.setDebug(topologyConf.debug)
    stormConfig.setNumAckers(topologyConf.numAckers)
    stormConfig.setNumWorkers(topologyConf.numWorkers)
    stormConfig.setMaxTaskParallelism(topologyConf.maxTaskParallelism)
    stormConfig.setMessageTimeoutSecs(topologyConf.messageTimeout.toSeconds.toInt)
    stormConfig.setMaxSpoutPending(topologyConf.maxSpoutPending)
    stormConfig.setKryoFactory(classOf[TypedBlizzardSerializer])

    stormConfig
  }
}
