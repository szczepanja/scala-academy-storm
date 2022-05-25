package com.adform.scalaacademy.test

import com.adform.scalaacademy.storm.TopologyTuples
import org.apache.storm.generated.StormTopology
import org.apache.storm.testing.{FixedTupleSpout, TupleCaptureBolt}
import org.apache.storm.topology.{IRichBolt, TopologyBuilder}
import org.apache.storm.testing.FixedTuple

import java.util.{List => JList, Map => JMap}
import scala.jdk.CollectionConverters.{ListHasAsScala, MapHasAsScala}

import scala.jdk.CollectionConverters.SeqHasAsJava

object TestBoltSupport {
  val SpoutId         = "test-spout-id"
  val BoltUnderTestId = "bolt-under-test-id"
  val EndBoltId       = "test-bolt-id"

  def createTestTopology(tuples: List[FixedTuple], bolt: IRichBolt): StormTopology = {

    val builder = new TopologyBuilder
    val spout   = new FixedTupleSpout(tuples.asJava, TopologyTuples.defaultFields)
    val endBolt = new TupleCaptureBolt

    builder.setSpout(SpoutId, spout, 1)
    builder
      .setBolt(BoltUnderTestId, bolt, 1)
      .shuffleGrouping(SpoutId)
    builder
      .setBolt(EndBoltId, endBolt, 1)
      .shuffleGrouping(BoltUnderTestId)

    builder.createTopology()
  }

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
