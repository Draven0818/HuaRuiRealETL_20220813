package cn.td.etl.utils.init

import cn.td.etl.utils.ConfigUtils.isLocal
import cn.td.etl.utils.nebula200.NebulaConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.{Seconds, StreamingContext}
import java.util.Properties

import cn.td.etl.utils.ConfigUtils

import scala.io.Source

object SparkInit {
  /**
   * 创建 StreamingContext ，时间间隔5s
   *
   * @return
   */
  def createSSC(appName: String): StreamingContext = {
    val conf = new SparkConf().setAppName(appName)
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array(classOf[ConsumerRecord[String, String]]))

    if (isLocal.equals("1")) {
      conf.setMaster("local[2]")
    }

    val ssc = new StreamingContext(conf, Seconds(ConfigUtils.`streamContext.interval`.toInt))
    ssc.sparkContext.setLogLevel("WARN")
    //    ssc.checkpoint(".")
    ssc
  }

  /**
   * 初始化nebula相关配置
   *
   * @param ssc
   * @return
   */
  def initConfigBDValue(schemaPath: String, nebulaPath: String)(implicit ssc: StreamingContext): (Broadcast[Map[String, Any]], Broadcast[NebulaConfig]) = {
    val schemaMapJson: String = Source.fromInputStream(this.getClass.getClassLoader.getResourceAsStream(schemaPath)).mkString
    val jsonObject: Option[Any] = scala.util.parsing.json.JSON.parseFull(schemaMapJson)
    val schemaConfig = regJson(jsonObject)

    // ---- 读取配置文件的操作 -----
    val nebulaProp = new Properties()
    val inputStream = this.getClass.getClassLoader.getResourceAsStream(nebulaPath)
    nebulaProp.load(inputStream)

    // 获取配置文件中nebula配置信息
    val nebulaConfig = NebulaConfig(nebulaProp.getProperty("hosts"), nebulaProp.getProperty("spaceName"), nebulaProp.getProperty("user"), nebulaProp.getProperty("password"), nebulaProp.getProperty("countPerBath").toInt, nebulaProp.getProperty("hiveDbName"))

    val schemaConfigBD = ssc.sparkContext.broadcast(schemaConfig)
    val nebulaConfigBD = ssc.sparkContext.broadcast(nebulaConfig)
    return (schemaConfigBD, nebulaConfigBD)
  }

  def regJson(json: Option[Any]): Map[String, Any] = json match {
    case Some(map: Map[String, Any]) => map
  }
}
