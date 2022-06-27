import cn.td.etl.utils.hbase.HbaseOperater
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.{Before, Test}

class HbaseOperaterTest {

  def getSparkSession: SparkSession = {
    val sparkSession: SparkSession = SparkSession.builder().appName("HbaseOperaterTest").master("local").getOrCreate()
    sparkSession
  }

  def createSparkDF()(implicit sparkSession: SparkSession): DataFrame = {
    sparkSession.createDataFrame(Seq(
      ("00x1", "张三", "70"),
      ("00x2", "里斯", "20"),
      ("00x3", "王武", "100")
    )).toDF("uid", "name", "count")
  }

  @Test
  def test01(): Unit = {
    implicit val sparkSession = getSparkSession
    val df1 = createSparkDF()

    val hbaseOperater = new HbaseOperater()

    val rddHbase = hbaseOperater.transDataFrameToRDD(df1)
//    hbaseOperater.sinkToHbase(rddHbase, "cpp:test")
  }


}
