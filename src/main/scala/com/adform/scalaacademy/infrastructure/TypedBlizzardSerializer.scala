package com.adform.scalaacademy.infrastructure

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.ScalaKryoInstantiator
import com.twitter.chill.config.ConfigurationException
import org.apache.storm.serialization.IKryoFactory

import java.util

class TypedBlizzardSerializer extends IKryoFactory {
  override def getKryo(conf: util.Map[String, AnyRef]): Kryo = {
    try {
      val kryoInst = new ScalaKryoInstantiator()

      kryoInst.newKryo()
    } catch {
      case cx: ConfigurationException => throw new RuntimeException(cx)
    }
  }

  override def preRegister(k: Kryo, conf: util.Map[String, AnyRef]): Unit = {}

  override def postRegister(k: Kryo, conf: util.Map[String, AnyRef]): Unit = {}

  override def postDecorate(k: Kryo, conf: util.Map[String, AnyRef]): Unit = {}
}
