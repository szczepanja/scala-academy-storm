package com.adform.scalaacademy.storm.spout

import com.adform.scalaacademy.storm.TopologyTuples
import com.typesafe.scalalogging.LazyLogging
import org.apache.storm.spout.SpoutOutputCollector
import org.apache.storm.task.TopologyContext
import org.apache.storm.topology.OutputFieldsDeclarer
import org.apache.storm.topology.base.BaseRichSpout
import org.apache.storm.tuple.Values
import org.apache.storm.utils.Utils

import java.util.{Map => JMap}
import scala.util.Random

class DummyRandomStringSpout extends BaseRichSpout with LazyLogging {

  private var collector: SpoutOutputCollector = _
  private var rand: Random                    = _
  private var msgId                           = 0L

  override def open(_conf: JMap[String, AnyRef], _context: TopologyContext, _collector: SpoutOutputCollector): Unit = {
    collector = _collector
    rand = new Random
  }

  override def nextTuple(): Unit = {
    Utils.sleep(50)
    val strLength = rand.between(5, 50)
    val msg       = rand.nextString(strLength)
    msgId += 1

    collector.emit(new Values(msg), msgId)
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer): Unit = {
    declarer.declare(TopologyTuples.defaultFields)
  }

  override def ack(id: Any): Unit = {
    logger.debug(s"Acknowledged $id")
  }

  override def fail(id: Any): Unit = {
    logger.debug(s"Failed: $id")
  }

}
