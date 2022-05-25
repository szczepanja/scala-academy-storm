package com.adform.scalaacademy.storm.bolt

import com.adform.scalaacademy.storm.bolt.PrinterBoltTest.{correctTuples, topologyName}
import com.adform.scalaacademy.test.{TestBoltSupport, TestStormBase}
import com.adform.scalaacademy.storm.bolt.PrinterBolt
import com.adform.scalaacademy.test.TestBoltSupport.StormTestResultSupport
import org.apache.storm.Testing
import org.apache.storm.testing.FixedTuple
import org.apache.storm.tuple.Values

class PrinterBoltTest extends TestStormBase {
  "PrinterBolt" when {
    "given correct messages" should {
      "ack all" in {
        withStormCluster { cluster =>
          val tuples   = correctTuples
          val bolt     = new PrinterBolt
          val topology = TestBoltSupport.createTestTopology(tuples, bolt)

          val emittedTuples = Testing.completeTopology(cluster, topology, defaultTopologyConfig(topologyName))
          val result        = emittedTuples.getEmittedTupleByTestedBolt()

          result.size shouldBe correctTuples.size
        }
      }
    }
  }
}

object PrinterBoltTest {
  val topologyName = "test-printer-bolt-topology"
  val correctTuples =
    List(new FixedTuple(new Values("msg 1")), new FixedTuple(new Values("msg 2")), new FixedTuple(new Values("msg 3")))
}
