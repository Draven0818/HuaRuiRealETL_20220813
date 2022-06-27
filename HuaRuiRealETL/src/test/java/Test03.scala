import cn.td.etl.utils.NebulaMatchConstant

import java.lang.reflect.Field
import scala.collection.mutable.ListBuffer

object Test03 {
  def main(args: Array[String]): Unit = {
    val l1: ListBuffer[(String, String, String)] = ListBuffer()
    val l2: ListBuffer[(String, String, String)] = ListBuffer()
    val l3: ListBuffer[(String, String, String)] = ListBuffer()

    l1 += (("11", "11", "11"))
    l1 += (("22", "22", "22"))

    println(l1)

    l2 ++= l1
    l2 += (("333", "333", "333"))

    println(l2)

    l3 ++= l1
    l3 ++= l2
    println(l3)
  }
}
