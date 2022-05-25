package com.adform.scalaacademy

import com.adform.scalaacademy.config.Config
import com.adform.scalaacademy.storm.Topology
import org.apache.storm.{LocalCluster, StormSubmitter}
import scopt.OParser

object Application {

  def main(args: Array[String]): Unit = {
    val appArgs = argParser(args)
    val config  = Config.load
    Config.log(config)
    val topology = new Topology(config)

    run(appArgs, topology)
  }

  def run(args: ApplicationArgs, topology: Topology): Unit = {
    if (args.isLocal) {
      val cluster = new LocalCluster()

      val name = s"${topology.config.topology.name}-local"
      cluster.submitTopology(name, topology.topologyConfig(), topology.create())
    } else {

      StormSubmitter
        .submitTopology(
          topology.config.topology.name,
          topology.topologyConfig(),
          topology.create()
        )
    }
  }

  def argParser(args: Array[String]): ApplicationArgs = {
    val builder = OParser.builder[ApplicationArgs]
    import builder._

    val parser = OParser.sequence(
      opt[Boolean]('l', "local")
        .action((v, c) => c.copy(isLocal = v))
        .text("Set true to run topology on local embedded cluster for testing purposes")
    )

    OParser.parse(parser, args, ApplicationArgs()).fold(ApplicationArgs())(identity)
  }
}

case class ApplicationArgs(isLocal: Boolean = true)
