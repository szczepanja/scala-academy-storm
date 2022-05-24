package com.adform.scalaacademy.storm

import org.apache.storm.tuple.Fields

object TopologyTuples {
  val Message    = "Message"
  val MessageIdx = 0

  val defaultFields = new Fields(Message)
}
