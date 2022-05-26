package com.adform.scalaacademy

import com.adform.scalaacademy.config.{Config, KafkaConsumerConfig}
import com.adform.scalaacademy.storm.TopologyTuples
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.storm.kafka.spout.{ByTopicRecordTranslator, KafkaSpout, KafkaSpoutConfig}
import org.apache.storm.tuple.Values

case class Measurements(id: Int, value: Double)

case class Parameters(id: String, rules: List[Measurements], createdAt: String)

object DeviceMessageSpout {

  val SpoutId = "kafka-uploader-device-spout"

  def createUnsafe(config: KafkaConsumerConfig): KafkaSpout[String, Array[String]] = {
    val translator = new ByTopicRecordTranslator[String, Array[String]](
      (record: ConsumerRecord[String, Array[String]]) => {
        new Values(record.topic(), record.partition(), record.value())
      },
      TopologyTuples.defaultFields
    )

    val configuration =
      new KafkaSpoutConfig.Builder[String, Array[String]](config.bootstrapServers.mkString(","), config.topic)
        .setRecordTranslator(translator)
        .setProp(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
        .setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringArrayDeserializer])
        .setProp(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 10)
        .setProp(ConsumerConfig.GROUP_ID_CONFIG, config.groupId)
        .build()

    new KafkaSpout(configuration)
  }

}
