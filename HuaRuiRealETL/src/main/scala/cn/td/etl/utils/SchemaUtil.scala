package cn.td.etl.utils

import cn.td.etl.utils.DataConstant.{DATE_TYPE, DOUBLE_TYPE, FLOAT_TYPE, INTEGER_TYPE, STRING_TYPE, TIMESTAMP_TYPE}
import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils

import scala.collection.mutable.ListBuffer

object SchemaUtil {


  def retrieveDataType(dataTypeMap: Map[String, String], k: String): String = {
    dataTypeMap.getOrElse(k, STRING_TYPE)
  }

  def buildVertexNGLStatement(labelMap: Map[String, ListBuffer[Map[String, Any]]]): ListBuffer[String] = {
    val statementList: ListBuffer[String] = ListBuffer()

    labelMap.foreach {
      case (k, v) =>
        val vertexLabel = k
        v.map(
          elem => {
            val vertexUid = elem("uid")
            val propValues = elem("propValues")
            val propNames = elem("propNames").asInstanceOf[String]
            val tmpNGQL = s" '${vertexUid}':(${propValues}) "
            val statement = s"INSERT VERTEX ${vertexLabel} ($propNames) VALUES " + tmpNGQL
            statementList += statement
          }
        )
    }
    statementList
  }

  def buildVertexUpsertNGLStatement(list: ListBuffer[Map[String, Any]]): ListBuffer[String] = {
    val statementList: ListBuffer[String] = ListBuffer()

    list.foreach {
      map: Map[String, Any] =>
        val vertexLabel = map("label").asInstanceOf[String]
        val vertexUid = map("uid").asInstanceOf[String]
        val propCondStr = map("propCondStr").asInstanceOf[String]
        val statement = s"UPSERT VERTEX ON $vertexLabel '$vertexUid' set  $propCondStr "
        statementList += statement
    }
    statementList
  }

  def buildEdgeUpsertNGLStatement(list: ListBuffer[Map[String, Any]]) = {
    val statementList: ListBuffer[String] = ListBuffer()

    list.foreach {
      map: Map[String, Any] =>
        val edgeLabel = map("label").asInstanceOf[String]
        val srcUid = map("srcUid").asInstanceOf[String]
        val dstUid = map("dstUid").asInstanceOf[String]
        val propCondStr = map("propCondStr").asInstanceOf[String]
        val statement = s"UPSERT EDGE ON $edgeLabel '$srcUid' -> '$dstUid' set  $propCondStr "
        statementList += statement
    }
    statementList
  }

  def buildEdgeNGLStatement(labelMap: Map[String, ListBuffer[Map[String, Any]]]): ListBuffer[String] = {
    val statementList: ListBuffer[String] = ListBuffer()

    labelMap.foreach {
      case (k, v) =>
        val edgeLabel = k
        v.map(
          elem => {
            val srcUid = elem("srcUid")
            val dstUid = elem("dstUid")
            val rank = elem("rank")

            val propValues = elem("propValues")
            val propNames = elem("propNames").asInstanceOf[String]


            val tmpNGQL = s" '${srcUid}'->'${dstUid}'${rank}:(${propValues})"

            val statement = s"INSERT EDGE ${edgeLabel} ($propNames) VALUES " + tmpNGQL
            statementList += statement
          }
        )
    }

    statementList
  }

  def parseEntity(schemaConfig: Map[String, Any], jsonObj: JSONObject): ListBuffer[Map[String, Any]] = {
    val entityConfig = schemaConfig("entity").asInstanceOf[Map[String, Any]]
    val dataTypeConfig = schemaConfig("dataType").asInstanceOf[Map[String, String]]

    //    var labelMap: Map[String, ListBuffer[Map[String, Any]]] = Map()
    var elemList: ListBuffer[Map[String, Any]] = ListBuffer()

    entityConfig.foreach {
      case (k, v) => {
        // ?????????????????????key ????????????????????????????????????
        if (jsonObj.containsKey(k)) {
          LogTool.info("??????????????????")
          val vMap = v.asInstanceOf[Map[String, Any]]
          // ?????????????????????uid????????????
          val keyObjInputUidValue = jsonObj.get(k)
          // ???????????????????????????label
          val label = vMap("label").asInstanceOf[String]

          // ??????????????????
          val schemaPropsMap = vMap("props").asInstanceOf[Map[String, Any]]

          // ????????????????????????????????????
          var newPropJsonMap: Map[String, Any] = Map()
          // ??????schemaPropsMap ??????????????????key???????????????jsonObj?????????????????????
          schemaPropsMap.foreach {
            case (propKey, propValue) => {
              if (jsonObj.containsKey(propKey)) {
                // ??????jsonObj??????????????????????????????
                newPropJsonMap += (propValue.asInstanceOf[String] -> jsonObj.get(propKey))
              }
            }
          }

          // ########################## ??? RealtimeMultiNebula.scala ????????????????????? #########################
          // ??????????????? newPropJsonMap ??????keys??????????????????????????????
          var propNames: String = null
          if (propNames == null) {
            propNames = newPropJsonMap.keys.toList.fold("")((s1: String, s2: String) => {
              s1 + "," + s2
            })
            if (propNames.length > 0) {
              propNames = propNames.substring(1, propNames.length)
            }
          }


          // ########################## ??? RealtimeMultiNebula.scala ????????????????????? #########################
          // ??????????????????value????????????????????????
          val propValuesBuffer = new StringBuffer()
          newPropJsonMap.foreach((obj) => {
            val k = obj._1
            val v = obj._2
            //            val dataType = schemaMap.get(k).asInstanceOf[JSONObject].getOrDefault(SCHEMA_DATA_TYPE, STRING_TYPE)
            // ?????????????????????????????????????????????schema config map ???????????????????????????
            val dataType = retrieveDataType(dataTypeConfig, k)
            if (dataType.equals(STRING_TYPE)) {
              var filterV = v.toString.replaceAll("\"", "").replaceAll("'", "")
              propValuesBuffer.append("\"").append(filterV).append("\"").append(",")
            } else if (dataType.equals(DATE_TYPE)) {
              propValuesBuffer.append(v.toString.toLong / 1000).append(",")
            } else if (dataType.equals(TIMESTAMP_TYPE)) {
              propValuesBuffer.append(v.toString.toLong / 1000).append(",")
            } else if (dataType.equals(FLOAT_TYPE)) {
              propValuesBuffer.append(v.toString.toFloat).append(",")
            } else if (dataType.equals(DOUBLE_TYPE)) {
              propValuesBuffer.append(v.toString.toDouble).append(",")
            } else if (dataType.equals(INTEGER_TYPE)) {
              propValuesBuffer.append(v.toString.toInt).append(",")
            } else {
              propValuesBuffer.append(v).append(",")
            }
          })

          var propValues = propValuesBuffer.toString;
          if (propValuesBuffer.length() > 0) {
            propValues = propValuesBuffer.substring(0, propValuesBuffer.length() - 1);
          }


          val elem = Map("label" -> label, "uid" -> keyObjInputUidValue, "propNames" -> propNames, "propValues" -> propValues)
          elemList += elem
        }

      }
    }

    elemList
  }

  def parseUpsertEntity(schemaConfig: Map[String, Any], jsonObj: JSONObject): ListBuffer[Map[String, Any]] = {
    val entityConfig = schemaConfig("entity").asInstanceOf[Map[String, Any]]
    val dataTypeConfig = schemaConfig("dataType").asInstanceOf[Map[String, String]]

    //    var labelMap: Map[String, ListBuffer[Map[String, Any]]] = Map()
    var elemList: ListBuffer[Map[String, Any]] = ListBuffer()

    entityConfig.foreach {
      case (k, v) => {
        // ?????????????????????key ????????????????????????????????????
        if (jsonObj.containsKey(k)) {
          LogTool.info("??????????????????")
          val vMap = v.asInstanceOf[Map[String, Any]]
          // ?????????????????????uid????????????
          val keyObjInputUidValue = jsonObj.get(k)
          // ???????????????????????????label
          val label = vMap("label").asInstanceOf[String]

          // ??????????????????
          val schemaPropsMap = vMap("props").asInstanceOf[Map[String, Any]]

          val propCondList: ListBuffer[String] = new ListBuffer()

          schemaPropsMap.foreach {
            case (propKey, propValue) => {
              if (jsonObj.containsKey(propKey)) {
                // ??????jsonObj??????????????????????????????
                val resValue = checkUpsertDataType(dataTypeConfig, propKey, jsonObj)
                propCondList.append(propValue.asInstanceOf[String] + "=" + resValue)
              }
            }
          }

          val propCondStr = propCondList.toArray.mkString(",")

          val elem = Map("label" -> label, "uid" -> keyObjInputUidValue, "propCondStr" -> propCondStr)
          elemList += elem
        }

      }
    }

    elemList
  }

  def parseUpsertRelation(schemaConfig: Map[String, Any], jsonObj: JSONObject) = {
    val relationConfig = schemaConfig("relation").asInstanceOf[List[Map[String, Any]]]
    val dataTypeConfig = schemaConfig("dataType").asInstanceOf[Map[String, String]]

    val elemList: ListBuffer[Map[String, Any]] = ListBuffer()

    relationConfig.foreach { vMap =>
      val srcUidField = vMap("source").asInstanceOf[String]
      val dstUidField = vMap("target").asInstanceOf[String]
      if (jsonObj.containsKey(srcUidField) && jsonObj.containsKey(dstUidField)) {
        val srcUidValue = jsonObj.get(srcUidField)
        val dstUidValue = jsonObj.get(dstUidField)
        val label = vMap("label").asInstanceOf[String]
        val schemaPropsMap = vMap("props").asInstanceOf[Map[String, Any]]

        val propCondList: ListBuffer[String] = new ListBuffer()


        schemaPropsMap.foreach {
          case (propKey, propValue) => {
            if (jsonObj.containsKey(propKey)) {
              // ??????jsonObj??????????????????????????????
              val resValue = checkUpsertDataType(dataTypeConfig, propKey, jsonObj)
              propCondList.append(propValue.asInstanceOf[String] + "=" + resValue)
            }
          }
        }

        val propCondStr = propCondList.toArray.mkString(",")

        val elem = Map("label" -> label, "srcUid" -> srcUidValue, "dstUid" -> dstUidValue, "propCondStr" -> propCondStr)
        elemList += elem

      }
    }
    elemList
  }


  def checkUpsertDataType(dataTypeConfig: Map[String, String], propKey: String, jsonObj: JSONObject): String = {
    val keyType = dataTypeConfig.getOrElse(propKey, "String")

    keyType match {
      case STRING_TYPE => "'" + jsonObj.get(propKey).toString.replaceAll("\"", "").replaceAll("'", "") + "'"
      case DATE_TYPE => (jsonObj.get(propKey).toString.toLong / 1000).toString
      case TIMESTAMP_TYPE => (jsonObj.get(propKey).toString.toLong / 1000).toString
      case FLOAT_TYPE => jsonObj.get(propKey).toString.toFloat.toString
      case DOUBLE_TYPE => jsonObj.get(propKey).toString.toDouble.toString
      case INTEGER_TYPE => jsonObj.get(propKey).toString.toInt.toString
      case _ => "''"
    }

  }


  def parseRelation(schemaConfig: Map[String, Any], jsonObj: JSONObject): ListBuffer[Map[String, Any]] = {
    val relationConfig = schemaConfig("relation").asInstanceOf[List[Map[String, Any]]]
    val dataTypeConfig = schemaConfig("dataType").asInstanceOf[Map[String, String]]

    //    var labelMap: Map[String, ListBuffer[Map[String, Any]]] = Map()
    var edgeList: ListBuffer[Map[String, Any]] = ListBuffer()

    relationConfig.foreach(
      relationElem => {
        val vMap = relationElem.asInstanceOf[Map[String, Any]]
        val srcFromSchema = vMap("source").asInstanceOf[String]
        val dstFromSchema = vMap("target").asInstanceOf[String]

        // ??????source ??? target ?????????????????????????????????????????????schema??????????????????
        if (jsonObj.containsKey(srcFromSchema) && jsonObj.containsKey(dstFromSchema)) {
          // ????????????
          val srcUidValue = jsonObj.get(srcFromSchema)
          val dstUidValue = jsonObj.get(dstFromSchema)
          val label = vMap("label").asInstanceOf[String]


          // ????????????????????????
          val schemaPropsMap = vMap("props").asInstanceOf[Map[String, Any]]
          // ????????????????????????????????????
          var newPropJsonMap: Map[String, Any] = Map()
          // ??????schemaPropsMap ??????????????????key???????????????jsonObj?????????????????????
          schemaPropsMap.foreach {
            case (propKey, propValue) => {
              if (jsonObj.containsKey(propKey)) {
                // ??????jsonObj??????????????????????????????
                newPropJsonMap += (propValue.asInstanceOf[String] -> jsonObj.get(propKey))
              }
            }
          }

          // ??????????????? newPropJsonMap ??????keys??????????????????????????????
          var propNames: String = null
          if (propNames == null) {
            propNames = newPropJsonMap.keys.toList.fold("")((s1: String, s2: String) => {
              s1 + "," + s2
            })
            if (propNames.length > 0) {
              propNames = propNames.substring(1, propNames.length)
            }
          }


          val propValuesBuffer = new StringBuffer()
          newPropJsonMap.foreach((obj) => {
            val k = obj._1
            val v = obj._2

            // ?????????????????????????????????????????????schema config map ???????????????????????????
            val dataType = retrieveDataType(dataTypeConfig, k)

            if (dataType.equals(STRING_TYPE)) {
              var filterV = v.toString.replaceAll("\"", "").replaceAll("'", "")
              propValuesBuffer.append("\"").append(filterV).append("\"").append(",")
            } else if (dataType.equals(DATE_TYPE)) {
              propValuesBuffer.append(v.toString.toLong / 1000).append(",")
            } else if (dataType.equals(TIMESTAMP_TYPE)) {
              propValuesBuffer.append(v.toString.toLong / 1000).append(",")
            } else if (dataType.equals(FLOAT_TYPE)) {
              propValuesBuffer.append(v.toString.toFloat).append(",")
            } else if (dataType.equals(DOUBLE_TYPE)) {
              propValuesBuffer.append(v.toString.toDouble).append(",")
            } else {
              propValuesBuffer.append(v).append(",")
            }
          })
          var propValues = propValuesBuffer.toString;
          if (propValuesBuffer.length() > 0) {
            propValues = propValuesBuffer.substring(0, propValuesBuffer.length() - 1);
          }


          val edgeType = schemaConfig.getOrElse("edgeType", GraphConstant.EDGE_TYPE_SIMPLE);
          val startTime = jsonObj.getOrDefault("start_time", "0");
          var rank = ""
          if (edgeType.equals(GraphConstant.EDGE_TYPE_MULTI)) {
            /**
             * INSERT EDGE e1 () VALUES 10->11@1:();
             * ranking ????????? ranking????????????????????????????????????????????????????????????????????????????????? 0???
             */
            rank = "@" + startTime
          }

          val edge = Map("label" -> label, "srcUid" -> srcUidValue, "dstUid" -> dstUidValue, "rank" -> rank, "propNames" -> propNames, "propValues" -> propValues)
          edgeList += edge


        }

      }
    )

    edgeList
  }

  def compoundLabelMap(inputList: ListBuffer[Map[String, Any]]): Map[String, ListBuffer[Map[String, Any]]] = {
    // ???????????????lable map
    var labelMap: Map[String, ListBuffer[Map[String, Any]]] = Map()
    inputList.map(elem => {
      var label = elem("label").asInstanceOf[String]
      if (labelMap.contains(label)) {
        var listBuffer = labelMap(label)
        listBuffer += elem
      } else {
        var listBuffer: ListBuffer[Map[String, Any]] = ListBuffer()
        listBuffer += elem
        labelMap += (label -> listBuffer)
      }
    })

    labelMap
  }


}
