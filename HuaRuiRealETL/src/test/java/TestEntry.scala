import cn.td.etl.streaming.{EntryETL, HuaRuiETL}
import cn.td.etl.utils.init.SparkInit
import cn.td.etl.utils.trans.{NebulaFunc, RealTrans}
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

object TestEntry {
  def main(args: Array[String]): Unit = {

    implicit val ssc: StreamingContext = SparkInit.createSSC("test")
    val (schemaConfigBD, nebulaConfigBD) = SparkInit.initConfigBDValue("entry_stats.json", "nebulaConfig.properties")

    val source = ssc.socketTextStream("localhost", 9999)

    val rObject: DStream[JSONObject] = source.map(str => JSON.parseObject(str))

    val res: DStream[JSONObject] = EntryETL.dataClean(rObject)
    res.cache()

    NebulaFunc.jsonUpsertNebula(schemaConfigBD, nebulaConfigBD, res)
    RealTrans.realTimeQuotaETL(res, nebulaConfigBD)

    ssc.start()
    ssc.awaitTermination()
  }
}
