import cn.td.etl.utils.IndicatorResult
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Test

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.JavaConverters.asJavaIterableConverter
import scala.collection.mutable.ListBuffer

class TestParam {
  @Test
  def t1(): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("test")

    val sparkContext = new SparkContext(conf)

    val interval = sparkContext.getConf.get("interval", "200")

    println(interval)

    sparkContext.stop()
  }

  @Test
  def test02(): Unit = {
    println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date))
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)
  }

  @Test
  def test03(): Unit = {
    val kafkaRes: ListBuffer[IndicatorResult] = new ListBuffer()

    kafkaRes += IndicatorResult("001", "r1", "u1", "i1", "1", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date),"")
    kafkaRes += IndicatorResult("002", "r2", "u2", "i2", "2", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date),"")

    println(kafkaRes)

    val str = JSON.toJSONString(kafkaRes.toArray, false)
    println(str)

    println(JSON.parseArray(str, classOf[IndicatorResult]))

  }
}
