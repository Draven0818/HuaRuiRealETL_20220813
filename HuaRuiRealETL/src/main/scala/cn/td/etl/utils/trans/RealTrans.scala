package cn.td.etl.utils.trans

import cn.td.etl.utils.ConfigUtils.indicatorEntityTable
import cn.td.etl.utils.{CleanUtils, ConfigUtils, IndicatorResult, LogTool, NebulaMatchConstant}
import cn.td.etl.utils.hbase.HbaseOperater
import cn.td.etl.utils.init.KafkaSink
import cn.td.etl.utils.nebula200.{NebulaConfig, SSNebulaImportHelper}
import com.alibaba.fastjson.{JSON, JSONObject}
import com.vesoft.nebula.client.graph.data.ResultSet
import com.vesoft.nebula.client.graph.net.Session
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

import java.util.Properties
import scala.collection.mutable.ListBuffer

object RealTrans {
  //实时指标计算
  def realTimeQuotaETL(reDS: DStream[JSONObject], nebulaConfigBD: Broadcast[NebulaConfig])(implicit ssc: StreamingContext): Unit = {

    // 广播KafkaSink
    val kafkaProducer: Broadcast[KafkaSink[String, String]] = {
      val kafkaProducerConfig = {
        val p = new Properties()
        p.setProperty("bootstrap.servers", ConfigUtils.`source.kafka.bootstrap.servers`)
        p.setProperty("key.serializer", classOf[StringSerializer].getName)
        p.setProperty("value.serializer", classOf[StringSerializer].getName)
        p
      }
      LogTool.warn("kafka producer init done!")
      LogTool.warn("kafka properties is {}!",kafkaProducerConfig)
      ssc.sparkContext.broadcast(KafkaSink[String, String](kafkaProducerConfig))
    }

    reDS.foreachRDD((rdd: RDD[JSONObject]) =>
      if (!rdd.isEmpty()) {

        val rdd1: RDD[ListBuffer[IndicatorResult]] = rdd.mapPartitions((partition: Iterator[JSONObject]) => {
          //每个 task 创建 nebula session
          val cleanUtils = new CleanUtils()
          val session = new SSNebulaImportHelper().openNebulaGraph(nebulaConfigBD.value)
          // jsonObject需要查询指标的字段
          val idFields = List("prcid", "certId")
          val telFields = List("phonenumber", "mobilePhone")

          //指标计算结果listBuffer (指标标识,uid,value)
          val quotaRes: ListBuffer[ListBuffer[IndicatorResult]] = ListBuffer()

          partition.foreach(record => {
            //遍历id字段，查询nebula
            LogTool.info("开始计算实时指标")

            val ql: ListBuffer[IndicatorResult] = ListBuffer()

            for (id <- idFields) {
              //校验 jsonObject当前id字段是否有值，有值执行
              if (cleanUtils.checkJson(record, id)) {
                //获取 各个id指标的执行语句 (标签名称,query)
                for (query: (String, String) <- NebulaMatchConstant(record.getString(id)).idNumMatchList) {
                  LogTool.info("身份证号指标计算语句:{}", query._2)
                  //执行结果 (uid,value)
                  val rs = session.execute(query._2)
                  if (!rs.isEmpty) {
                    ql ++= cleanUtils.createKafkaRs(record.getString(id), record.getString("S_DC_VS_TOKEN"), query._1, rs)
                  }
                }
              }
            }

            for (tel <- telFields) {
              //校验 jsonObject当前id字段是否有值，有值执行
              if (cleanUtils.checkJson(record, tel)) {
                //获取 各个id指标的执行语句 (标签名称,query)
                for (query: (String, String) <- NebulaMatchConstant(record.getString(tel)).telMatchList) {
                  LogTool.info("手机号指标计算语句:{}", query._2)
                  //执行结果 (uid,value)
                  val rs = session.execute(query._2)
                  if (!rs.isEmpty) {
                    ql ++= cleanUtils.createKafkaRs(record.getString(tel), record.getString("S_DC_VS_TOKEN"), query._1, rs)
                  }
                }
              }
            }

            quotaRes += ql

          })
          quotaRes.iterator
        })

        rdd1.cache()

        // 指标结果写入hbase
        val hbaseOperater = new HbaseOperater()
        hbaseOperater.sinkToHbase(rdd1, indicatorEntityTable)

        // 指标结果写入kafka
        rdd1.foreach((r: ListBuffer[IndicatorResult]) => {
          val resJson = JSON.toJSONString(r.toArray, false)
          val kafka = kafkaProducer.value
          LogTool.info("指标开始写入kafka,{}",ConfigUtils.`sink.kafka.topic`)
          kafka.send(ConfigUtils.`sink.kafka.topic`, resJson)
          LogTool.info("指标写入kafka完成，共 {} 个",r.size)
        })

      })
  }
}
