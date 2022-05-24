package com.adform.scalaacademy

import com.adform.scalaacademy.config.Config
import com.softwaremill.quicklens._
import org.apache.storm.testing.FixedTuple

import java.util.{List => JList, Map => JMap}
import scala.jdk.CollectionConverters.{ListHasAsScala, MapHasAsScala}

package object test {
  val DefaultConfig: Config = Config.load
  val TestConfig: Config = DefaultConfig
    .modify(_.topology.numWorkers)
    .setTo(1)
    .modify(_.topology.numAckers)
    .setTo(1)

  implicit class StormTestResultSupport(result: JMap[String, JList[FixedTuple]]) {
    def getEmittedTuplesBy(componentId: String): List[FixedTuple] = {
      result.asScala.get(componentId) match {
        case Some(tuples: JList[FixedTuple]) => tuples.asScala.toList
        case None                            => throw new AssertionError(s"The $componentId has not emit any tuples or does not exist in topology")
      }
    }

    def getEmittedTupleByTestedBolt(): List[FixedTuple] = {
      getEmittedTuplesBy(TestBoltSupport.BoltUnderTestId)
    }
  }
}
