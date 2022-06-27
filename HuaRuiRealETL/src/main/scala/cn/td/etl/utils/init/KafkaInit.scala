package cn.td.etl.utils.init

import cn.td.etl.utils.{ConfigUtils, LogTool}
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object KafkaInit {

  def parseKafka(topic: String)(implicit ssc: StreamingContext): DStream[JSONObject] = {
    //读取kafka配置
    val kafkaParams = Map[String, Object](
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> ConfigUtils.`source.kafka.bootstrap.servers`,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> ConfigUtils.`source.kafka.group.id`,
      "auto.offset.reset" -> ConfigUtils.`source.kafka.auto.offset.reset`,
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = topic.split(" ")

    //spark读取kafka报文
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    ).filter(record => record.value().nonEmpty)
      .map(record => JSON.parseObject(record.value()))
    stream.foreachRDD(rdd => rdd.foreach(j => LogTool.info(s"进件报文:${j.toString}")))
    stream.repartition(20)
  }

}
