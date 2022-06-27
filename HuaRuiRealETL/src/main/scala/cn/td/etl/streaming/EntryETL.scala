package cn.td.etl.streaming

import cn.td.etl.utils.Common.{UID_ENTRY_STATS_LIST, UID_FIELD_LIST1, UID_FIELD_LIST2}
import cn.td.etl.utils.init.{KafkaInit, SparkInit}
import cn.td.etl.utils.trans.BaseFunc.{defineRelationType, fieldUid, fieldsUid}
import cn.td.etl.utils.trans.NebulaFunc
import cn.td.etl.utils.{ConfigUtils, LogTool}
import com.alibaba.fastjson.JSONObject
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

object EntryETL {
  def main(args: Array[String]): Unit = {

    //  0.spark content初始化
    implicit val ssc: StreamingContext = SparkInit.createSSC("huarui_entry")

    // 1. 初始化配置
    val (schemaConfigBD, nebulaConfigBD) = SparkInit.initConfigBDValue("entry_stats.json", "nebulaConfig.properties")

    // 2.读取kafka中获取数据，解析成jsonObject
    val kafkaSource: DStream[JSONObject] = KafkaInit.parseKafka(ConfigUtils.`source.kafka.topic.entry`)

    // 3.数据加工处理
    val res: DStream[JSONObject] = dataClean(kafkaSource)
    res.cache()

    // 4. 写入nebula图库
    NebulaFunc.jsonUpsertNebula(schemaConfigBD, nebulaConfigBD, res)


    ssc.start()
    ssc.awaitTermination()
  }


  def dataClean(kafkaSource: DStream[JSONObject]): DStream[JSONObject] = {
    kafkaSource.transform(rdd => {
      //生成各实体uid
      rdd.map { jObj =>
        val oneUidObj = fieldUid(jObj, UID_ENTRY_STATS_LIST)
        LogTool.info(s"进件状态报文Uid生成后:${oneUidObj.toString()}")
        oneUidObj
      }
    })
  }
}
