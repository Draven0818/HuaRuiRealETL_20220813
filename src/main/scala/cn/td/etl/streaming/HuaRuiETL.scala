package cn.td.etl.streaming

import cn.td.etl.utils.Common.{UID_FIELD_LIST1, UID_FIELD_LIST2}
import cn.td.etl.utils.{ConfigUtils, LogTool}
import cn.td.etl.utils.init.{KafkaInit, SparkInit}
import cn.td.etl.utils.trans.BaseFunc.{defineRelationType, fieldUid, fieldsUid}
import cn.td.etl.utils.trans.{NebulaFunc, RealTrans}
import com.alibaba.fastjson.JSONObject
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

object HuaRuiETL {

  /**
   * 实时程序入口
   *
   * @param args
   */
  def main(args: Array[String]): Unit = {

    //  0.spark content初始化
    implicit val ssc: StreamingContext = SparkInit.createSSC("huarui_real")

    // 1. 初始化配置
    val (schemaConfigBD, nebulaConfigBD) = SparkInit.initConfigBDValue("schema_map.json", "nebulaConfig.properties")

    // 2.读取kafka中获取数据，解析成jsonObject
    val kafkaSource: DStream[JSONObject] = KafkaInit.parseKafka(ConfigUtils.`source.kafka.topic`)

    // 3.数据加工处理
    val res: DStream[JSONObject] = dataClean(kafkaSource)
    res.cache()

    // 4. 写入nebula图库
    //    NebulaFunc.jsonToNebula(schemaConfigBD, nebulaConfigBD, res)
    NebulaFunc.jsonUpsertNebula(schemaConfigBD, nebulaConfigBD, res)

    // 5.实时指标计算，写入hbase
    RealTrans.realTimeQuotaETL(res, nebulaConfigBD)


    ssc.start()
    ssc.awaitTermination()
  }


  def dataClean(kafkaSource: DStream[JSONObject]): DStream[JSONObject] = {
    kafkaSource.transform(rdd => {
      //生成各实体uid
      rdd.map { jObj =>
        val oneUidObj = fieldUid(jObj, UID_FIELD_LIST1)
        val twoUidObj = fieldsUid(oneUidObj, UID_FIELD_LIST2)
        LogTool.info(s"报文Uid生成后:${twoUidObj.toString()}")
        val defineObject = defineRelationType(twoUidObj)
        LogTool.info(s"报文关系类型生成后:${twoUidObj.toString()}")
        defineObject
      }
    })
  }



  def kafkaClean(res: DStream[JSONObject]): DStream[JSONObject] ={
    res.transform(rdd => {
      rdd.map { jObj =>
        val str = jObj.getString("workflowcode")
        //val jSONObject = jObj.getJSONObject("workflowcode").toString()
        if(str == "DXM_USE_DECISION_NO" || str == "LX_USE_DECISION_NO" || str == "SH_USE_DECISION_NO" || str == "ELM_USE_DECISION_NO"){
          val jSONObject = "用信"
          jObj.put("workflowcode",jSONObject)
        }else if(str == "DXM_CREDIT_DECISION_NO" || str == "LX_CREDIT_DECISION_NO" || str == "SH_CREDIT_DECISION_NO" || str == "ELM_CREDIT_DECISION_NO"){
          val jSONObject = "授信"
          jObj.put("workflowcode",jSONObject)
        }
        jObj
      }
    })
  }



}
