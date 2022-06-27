package cn.td
import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import org.slf4j.LoggerFactory
import org.apache.spark.streaming.dstream
import scala.io.Source


object json {

  private val logger = LoggerFactory.getLogger(getClass.getSimpleName)
  private val str = "{\n  \"nodeName\": \"xxx\",\n  \"source\": \"join\",\n  \"left_child\": {\n  \"nodeName\": \"yyy\",\n  \"riskadd\": \"红旗村88号\",\n    \"join\": null,\n    \"source\": \"hive\",\n    \"parameter\": {\n      \"address\": \"\",\n      \"port\": 9083,\n      \"database\": \"\",\n      \"table\": \"\",\n      \"condition\": \"id>1\"\n    },\n    \"columns\":[\n      {\n        \"name\": \"id\",\n        \"type\": \"int\",\n        \"index\": 1\n      },\n      {\n        \"name\": \"\",\n        \"type\": \"\",\n        \"index\": 2\n      }\n    ],\n    \"transformer\": {\n      \"name\": \"xxx\",\n      \"parameter\":\n      {\n        \"function\": \"substring\",\n        \"columnIndex\": 1,\n        \"paras\": [0,3]\n      }\n    }\n  }\n}"


  def main(args: Array[String]): Unit = {
//    val s = "村"
//    val add = "红旗999组"
//
//    val add_mark = add.split("村")
//    if (add.split("村").length >= 2){
//      println(add_mark(0).concat("村"))
//    }else {
//      println("false")
//    }
////    println(add_mark(0).concat("村"))
//    println(add_mark.length)
    val js = JSON.parseObject(str)
    val namenode = js.getString("nodeName")
    val name: JSONObject = js.getJSONObject("left_child")
    val c = name.put("node","eee")

    println(c)



//    val ss = (name,namenode)


//    println(name)
//    println(ss)




    val s: String = getNode

    createstr(js)
    println(name.getString("riskadd"))
    logger.error("that is right")
    println(namenode)

//    println(s)
  }

  def getNode: String={

    val js = JSON.parseObject(str)
    val nameNode = js.getJSONObject("left_child").getString("nodeName")
    nameNode
  }


  def createstr(jObj: JSONObject): JSONObject={

//    val js = JSON.parseObject(str)
    val fjs = jObj.getJSONObject("left_child")
    val add = "riskadd"
    val risk = fjs.getString(add).split("村")

    if (fjs.containsKey(add)){
      if (risk.length < 2){
        logger.info("data is false")
      }else{
        val adds = risk(0).concat("村")
        println(adds)
      }
    }
    jObj

  }

}
