package cn.td.etl.utils

import com.typesafe.config.{Config, ConfigFactory}

object ConfigUtils {
  private val config: Config = ConfigFactory.load()

  val `source.kafka.topic`: String = config.getString("source.kafka.topic.real")
  val `sink.kafka.topic`: String = config.getString("sink.kafka.topic.real")
  val `source.kafka.topic.black`: String = config.getString("source.kafka.topic.black")
  val `source.kafka.topic.entry`: String = config.getString("source.kafka.topic.entry")
  val `source.kafka.group.id`: String = config.getString("source.kafka.group.id")
  val `source.kafka.auto.offset.reset`: String = config.getString("source.kafka.auto.offset.reset")
  val `source.kafka.bootstrap.servers`: String = config.getString("source.kafka.bootstrap.servers")


  val `hbase.zookeeper.quorum`: String = config.getString("hbase.zookeeper.quorum")
  val `hbase.zookeeper.property.clientPort`: String = config.getString("hbase.zookeeper.property.clientPort")
  val `zookeeper.znode.parent`: String = config.getString("zookeeper.znode.parent")

  val indicatorEntityTable: String = config.getString("hbase.indicator.entity.table")
  val indicatorGroupTable: String = config.getString("hbase.indicator.group.table")

  val isLocal: String = config.getString("isLocal")

  val `workFlowCode.dxmsx`: String = config.getString("workFlowCode.dxmsx")
  val `workFlowCode.dxmyx`: String = config.getString("workFlowCode.dxmyx")
  val `workFlowCode.lxsx`: String = config.getString("workFlowCode.lxsx")
  val `workFlowCode.lsyx`: String = config.getString("workFlowCode.lsyx")
  val `workFlowCode.elmsx`: String = config.getString("workFlowCode.elmsx")
  val `workFlowCode.elmyx`: String = config.getString("workFlowCode.elmyx")

  val `streamContext.interval`: String = config.getString("streamContext.interval")


}
