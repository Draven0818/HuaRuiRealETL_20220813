package cn.td.etl.streaming

import cn.td.etl.utils.Common.{UID_BLACK_LIST1, UID_BLACK_LIST2, UID_FIELD_LIST1, UID_FIELD_LIST2}
import cn.td.etl.utils.{ConfigUtils, LogTool}
import cn.td.etl.utils.init.{KafkaInit, SparkInit}
import cn.td.etl.utils.trans.BaseFunc.{defineRelationType, fieldBlackUid, fieldUid, fieldsUid}
import cn.td.etl.utils.trans.{NebulaFunc, RealTrans}
import com.alibaba.fastjson.JSONObject
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

object BlackETL {
  def main(args: Array[String]): Unit = {

    //  0.spark content初始化
    implicit val ssc: StreamingContext = SparkInit.createSSC("huarui_black")

    // 1. 初始化配置
    val (schemaConfigBD, nebulaConfigBD) = SparkInit.initConfigBDValue("black.json", "nebulaConfig.properties")

    // 2.读取kafka中获取数据，解析成jsonObject
    val kafkaSource: DStream[JSONObject] = KafkaInit.parseKafka(ConfigUtils.`source.kafka.topic.black`)

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
      rdd.map { jObj: JSONObject =>
        //type 1 手机
        if ("1".equals(jObj.getString("type"))) {
          val oneUidObj = fieldBlackUid("1", jObj, UID_BLACK_LIST1).fluentPut("black_type", "是")
          LogTool.info(s"手机黑名单报文Uid生成后:${oneUidObj.toString()}")
          oneUidObj
        }
        //type 2 身份证
        else if ("2".equals(jObj.getString("type"))) {
          val oneUidObj = fieldBlackUid("2", jObj, UID_BLACK_LIST2).fluentPut("black_type", "是")
          LogTool.info(s"身份证黑名单报文Uid生成后:${oneUidObj.toString()}")
          oneUidObj
        }
        else {
          jObj
        }
      }
    })
  }
}
