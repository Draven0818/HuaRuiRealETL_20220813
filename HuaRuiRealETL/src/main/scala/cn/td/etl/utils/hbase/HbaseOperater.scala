package cn.td.etl.utils.hbase

import cn.td.etl.utils.ConfigUtils._
import cn.td.etl.utils.{CleanUtils, IndicatorResult, LogTool, Md5Tool}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Put, Result}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job

import scala.collection.mutable.ListBuffer


class HbaseOperater {
  def transDataFrameToRDD(df: DataFrame, rowKeyField: String = "uid", prefixForRowKey: Boolean = true, rowKeyAsValue: Boolean = true, cf: String = "c"): RDD[(ImmutableBytesWritable, Put)] = {

    val dataCols: Array[String] = df.columns
    val hResult: RDD[(ImmutableBytesWritable, Put)] = df.rdd.filter(_ != null).map(row => {

      var rowKey = String.valueOf(row.getAs[AnyRef](rowKeyField))
      if (prefixForRowKey) {
        rowKey = Md5Tool.rowKeyPrefixGen(rowKey) + rowKey
      }
      val put = new Put(Bytes.toBytes(rowKey))

      dataCols.map(colName => {
        if (row.getAs(colName) != null && (!colName.equalsIgnoreCase(rowKeyField) || rowKeyAsValue)) {
          put.addColumn(
            Bytes.toBytes(cf),
            Bytes.toBytes(colName),
            Bytes.toBytes(String.valueOf(row.getAs[AnyRef](colName)))
          )
        }
      })
      (new ImmutableBytesWritable, put)
    })

    hResult
  }

  def sinkToHbase(hRes: RDD[ListBuffer[IndicatorResult]], tableNameWithNs: String): Unit = {
    val hBaseConf = HBaseConfiguration.create()
    hBaseConf.set("hbase.zookeeper.quorum", `hbase.zookeeper.quorum`)
    hBaseConf.set("hbase.zookeeper.property.clientPort", `hbase.zookeeper.property.clientPort`)
    hBaseConf.set("zookeeper.znode.parent", `zookeeper.znode.parent`)
    val jobConf = new JobConf(hBaseConf)
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, tableNameWithNs)

    val job = Job.getInstance(jobConf)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Result])
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    val utils = new CleanUtils
    val res: RDD[(ImmutableBytesWritable, Put)] = hRes.flatMap(l => l).map(re => utils.parseRs(re))

    res.saveAsNewAPIHadoopDataset(job.getConfiguration)

    LogTool.info("hbase write end.cnt={},tableName={}", res.count(), tableNameWithNs)
  }


}
