import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.SparkSession
import org.junit.Test

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class Test05 {


  @Test
  def checkArrayByte(): Unit = {
    val a = Array[Byte](1, 9, 0, 0, 49, 48, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -68, 32, 0, 0)
    println(new String(a))


    val b = Array[Byte](8, 0, 0, 0, -108, 0, 0, 0, 4, 0, 0, 0, -104, 0, 0, 0, 0, 0, 0, 0, -104, 0, 0, 0, 2, 0, 0, 0, -102, 0, 0, 0, 0, 0, 0, 0, -102, 0, 0, 0, 18, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, -84, 0, 0, 0, 0, 0, 0, 0, 0)
    println(new String(b))


  }

  @Test
  def checkJoin(): Unit = {
    val list: List[String] = List("a", "b", "c")

    val l2: ListBuffer[String] = new ListBuffer()
    l2.append("aa")


    val str = l2.toArray.mkString(",")

    println(str)

  }

  @Test
  def testDate(): Unit ={
    val hashMapValue = new mutable.HashMap[String, String]()
    hashMapValue.put("value", "value")
    hashMapValue.put("gmtCreate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date))

    val map: Map[String, String] = Map("value" -> "value", "gmtCreate" -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date))

    val redisValue = JSON.toJSONString(map, SerializerFeature.MapSortField)
    print(redisValue)
  }
}
