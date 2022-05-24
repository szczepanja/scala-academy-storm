package com.adform.scalaacademy.storm.bolt

import com.adform.scalaacademy.storm.TopologyTuples
import com.typesafe.scalalogging.LazyLogging
import org.apache.storm.task.{OutputCollector, TopologyContext}
import org.apache.storm.topology.OutputFieldsDeclarer
import org.apache.storm.topology.base.BaseRichBolt
import org.apache.storm.tuple.{Tuple, Values}

import java.util.{Map => JMap}

class PrinterBolt extends BaseRichBolt with LazyLogging {

  var collector: OutputCollector = _

  override def prepare(
      _topoConf: JMap[String, AnyRef],
      _context: TopologyContext,
      _collector: OutputCollector
  ): Unit = {
    collector = _collector
  }

  override def execute(input: Tuple): Unit = {
    if (input.contains(TopologyTuples.Message)) {
      input.getValueByField(TopologyTuples.Message) match {
        case msg =>
          logger.info(s"""
             |Received message:
             |$msg
             |""".stripMargin)
          collector.emit(new Values(msg))
          collector.ack(input)
      }
    } else {
      logger.error(s"Received $input that do not contains '${TopologyTuples.Message}' field. Skipping message")
      collector.ack(input)
    }
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer): Unit = {
    declarer.declare(TopologyTuples.defaultFields)
  }
}
