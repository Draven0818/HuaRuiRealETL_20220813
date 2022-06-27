package cn.td.etl.utils

import com.alibaba.fastjson.JSONObject
import com.google.common.net.HostAndPort
import com.vesoft.nebula.client.graph.data.ResultSet
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable.ListBuffer

class CleanUtils extends Serializable {

  def checkJson(j: JSONObject, s: String): Boolean = {
    j.containsKey(s) && "" != j.getString(s) && null != j.getString(s)
  }

  /**
   * 将resultSet 结果构建 结果对象
   */
  def createKafkaRs(key: String, token: String, indicatorName: String, set: ResultSet): ListBuffer[IndicatorResult] = {
    val kafkaRes: ListBuffer[IndicatorResult] = new ListBuffer()

    for (i <- 0 until set.rowsSize()) {
      val value = set.rowValues(i).values
      val uid = value.get(0).toString.replaceAll("\"", "")
      val indicatorValue = value.get(1).toString.replaceAll("\"", "")
      val rowKey = Md5Tool.rowKeyPrefixGen(uid) + uid
      kafkaRes += IndicatorResult(key, rowKey, uid, indicatorName, indicatorValue, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date), token)
    }
    kafkaRes
  }

  def parseRs(indicatorResult: IndicatorResult): (ImmutableBytesWritable, Put) = {


    LogTool.info(s"指标nebula查询结果为: ${indicatorResult.indicatorName} ,${indicatorResult.uid} ,${indicatorResult.indicatorValue}")


    val put = new Put(Bytes.toBytes(indicatorResult.rowkey))

    if (indicatorResult.indicatorValue != null) {
      put.addColumn(
        Bytes.toBytes("c"),
        Bytes.toBytes(indicatorResult.indicatorName),
        Bytes.toBytes(indicatorResult.indicatorValue)
      )
    }
    (new ImmutableBytesWritable, put)


  }

  def entityUidToHbase(uid: String): (ImmutableBytesWritable, Put) = {
    val rowKey = Md5Tool.rowKeyPrefixGen(uid) + uid
    val put = new Put(Bytes.toBytes(rowKey))
    put.addColumn(
      Bytes.toBytes("c"),
      Bytes.toBytes("uid"),
      Bytes.toBytes(uid)
    )
    (new ImmutableBytesWritable, put)
  }

  def getHostList(addr: String): List[HostAndPort] = {
    require(addr.nonEmpty, "nebula.address.graph cannot be empty")

    val hostAndPorts = new ListBuffer[HostAndPort]

    if (addr.contains(",")) {
      for (address <- addr.split(",")) {
        hostAndPorts.append(HostAndPort.fromString(address))
      }
    } else if (addr.contains(";")) {
      for (address <- addr.split(";")) {
        hostAndPorts.append(HostAndPort.fromString(address))
      }
    } else {
      hostAndPorts.append(HostAndPort.fromString(addr))
    }
    hostAndPorts.toList
  }

}