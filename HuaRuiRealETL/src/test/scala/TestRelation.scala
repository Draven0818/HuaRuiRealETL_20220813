import cn.td.etl.streaming.HuaRuiETL
import cn.td.etl.utils.trans.BaseFunc.defineRelationType
import com.alibaba.fastjson.JSON
import org.junit.Test

import scala.util.parsing.json.JSONObject
class TestRelation {
  @Test
  def main(args: Array[String]): Unit = {

    val str = "{\n  \"nodeName\": \"xxx\",\n  \"source\": \"join\",\n  \"left_child\": {\n  \"nodeName\": \"yyy\",\n  \"riskadd\": \"红旗村88号\",\n    \"join\": null,\n    \"source\": \"hive\",\n    \"parameter\": {\n      \"address\": \"\",\n      \"port\": 9083,\n      \"database\": \"\",\n      \"table\": \"\",\n      \"condition\": \"id>1\"\n    },\n    \"columns\":[\n      {\n        \"name\": \"id\",\n        \"type\": \"int\",\n        \"index\": 1\n      },\n      {\n        \"name\": \"\",\n        \"type\": \"\",\n        \"index\": 2\n      }\n    ],\n    \"transformer\": {\n      \"name\": \"xxx\",\n      \"parameter\":\n      {\n        \"function\": \"substring\",\n        \"columnIndex\": 1,\n        \"paras\": [0,3]\n      }\n    }\n  }\n}"

    HuaRuiETL.dataClean()

    defineRelationType(JSON.parseObject(str))
  }

}

