package com.adform.scalaacademy.config

case class KafkaConsumerConfig(
                                bootstrapServers: String,
                                topic: String,
                                groupId: String,
                              )
