package com.adform.scalaacademy.test

import com.adform.scalaacademy.storm.TopologyTuples
import org.apache.storm.generated.StormTopology
import org.apache.storm.testing.{FixedTuple, FixedTupleSpout, TupleCaptureBolt}
import org.apache.storm.topology.{IRichBolt, TopologyBuilder}

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

}
