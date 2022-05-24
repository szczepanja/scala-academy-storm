package com.adform.scalaacademy.test

import com.adform.scalaacademy.infrastructure.TypedBlizzardSerializer
import com.adform.scalaacademy.storm.{Topology, TopologyTuples}
import com.typesafe.scalalogging.LazyLogging
import org.apache.storm.{Config, LocalCluster}
import org.apache.storm.testing.{CompleteTopologyParam, FixedTuple}
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Using

trait TestBase extends AnyWordSpec with Matchers with OptionValues with EitherValues with LazyLogging {}

trait TestStormBase extends TestBase {
  def run(topology: Topology, cluster: LocalCluster): Unit = {
    cluster.submitTopology(topology.config.topology.name, topology.topologyConfig(), topology.create())
  }

  def withStormCluster[T](fn: LocalCluster => T): T = {
    Using.resource(new LocalCluster()) { c =>
      fn(c)
    }
  }

  def defaultTopologyConfig(topologyName: String): CompleteTopologyParam = {
    val stormConfig = new Config
    stormConfig.setKryoFactory(classOf[TypedBlizzardSerializer])

    val config = new CompleteTopologyParam
    config.setTimeoutMs(40.seconds.toMillis.toInt)
    config.setTopologyName(topologyName)
    config.setStormConf(stormConfig)

    config
  }

  def assertTuples[T](actual: List[FixedTuple], expected: List[FixedTuple]): Unit = {
    val as = expected
      .sortBy(ft => ft.values.get(TopologyTuples.MessageIdx).asInstanceOf[String])
      .map(_.values.get(TopologyTuples.MessageIdx).asInstanceOf[T])
      .map(Some(_))
    val bs = actual
      .sortBy(ft => ft.values.get(TopologyTuples.MessageIdx).asInstanceOf[String])
      .map(_.values.get(TopologyTuples.MessageIdx).asInstanceOf[T])
      .map(Some(_))

    as.zipAll(bs, None, None).foreach {
      case (Some(a), Some(b)) =>
        assert(
          compareObjects(a, b),
          s"""
             |The messages in tuple are not equals
             |Expected: $a
             |Actual:   $b
             |""".stripMargin
        )
      case (None, Some(b)) => assert(false, s"The expected $b value does not have corresponding processed tuple")
      case (Some(a), None) =>
        assert(
          false,
          s"""
             |The given $a value is unexpected
             |Expected: ${as.mkString(", ")}
             |Actual:   ${bs.mkString(", ")}
             |""".stripMargin
        )
      case (None, None) => assert(false, "Both are none")
    }
  }

  private def compareObjects[T](a: T, b: T): Boolean = {
    a match {
      case arr: Array[Byte] =>
        arr.sameElements(b.asInstanceOf[Array[Byte]])
      case _ => a == b
    }
  }
}
