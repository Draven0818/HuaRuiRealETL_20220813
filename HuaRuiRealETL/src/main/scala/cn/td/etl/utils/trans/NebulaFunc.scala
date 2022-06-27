package cn.td.etl.utils.trans

import cn.td.etl.utils.nebula200.{NebulaConfig, SSNebulaImportHelper}
import cn.td.etl.utils.{CleanUtils, LogTool, SchemaUtil}
import com.alibaba.fastjson.JSONObject
import com.vesoft.nebula.client.graph.net.Session
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.ListBuffer

object NebulaFunc {

  def jsonUpsertNebula(schemaConfigBD: Broadcast[Map[String, Any]], nebulaConfigBD: Broadcast[NebulaConfig], sinkBean: DStream[JSONObject]): Unit = {
    LogTool.info("准备处理jsonUpsertNebula数据...")
    sinkBean.foreachRDD(
      rdd => if (!rdd.isEmpty()) {
        LogTool.info("准备处理sinkbean中每个分区中的多条数据")
        rdd.foreachPartition(partition => {

          var entityList: ListBuffer[Map[String, Any]] = ListBuffer()
          var relationList: ListBuffer[Map[String, Any]] = ListBuffer()

          partition.foreach((record: JSONObject) => {
            // 解析json数据的部分
            var entityL = SchemaUtil.parseUpsertEntity(schemaConfigBD.value, record)
            var relationL = SchemaUtil.parseUpsertRelation(schemaConfigBD.value, record)

            // 向 elemList 中增加另外一个ListBuffer
            entityList ++= entityL
            relationList ++= relationL
          })

          val statementVertexNGLList = SchemaUtil.buildVertexUpsertNGLStatement(entityList)
          val statementEdgeNGLList = SchemaUtil.buildEdgeUpsertNGLStatement(relationList)

          var session: Session = null

          try {
            // 与图库建立连接
            session = new SSNebulaImportHelper().openNebulaGraph(nebulaConfigBD.value)

            // 遍历每条点的 NGL 语句
            statementVertexNGLList.foreach(
              statement => {
                // 执行具体的入图库操作
                try {
                  LogTool.info("开始导入s图库...节点...")
                  LogTool.info(s"upsert vertex statement: ${statement}")
                  var resultSet = session.execute(statement)
                  if (!resultSet.isSucceeded) if (resultSet.getErrorCode == -8) {
                    resultSet = session.execute(statement)
                    if (!resultSet.isSucceeded)
                      LogTool.error(String.format("upsert vertex Retry Failed: %s %s", resultSet.getErrorCode + "", statement))
                  }
                  else {
                    LogTool.error(String.format("upsert vertex Failed: %s %s", resultSet.getErrorCode + "", statement))
                    LogTool.error("############ 写入图库操作失败, 可能是由于图谱schema设计问题导致 ############")
                  }
                } catch {
                  case e: Exception => {
                    LogTool.threadError("点导入异常，异常原因:{}", e)
                  }
                }
              }
            )

            // 遍历每条边的 NGL 语句
            statementEdgeNGLList.foreach(
              statement => {
                try {
                  LogTool.info("开始导入图库...边...")
                  LogTool.info(s"upsert edge statement: ${statement}")
                  var resultSet = session.execute(statement)
                  if (!resultSet.isSucceeded) if (resultSet.getErrorCode == -8) {
                    resultSet = session.execute(statement)
                    if (!resultSet.isSucceeded) LogTool.error(String.format("Insert Edges Retry Failed: %s %s", resultSet.getErrorCode + "", statement))
                  }
                  else {
                    LogTool.error(String.format("upsert Edges Failed: %s %s", resultSet.getErrorCode + "", statement))
                    LogTool.error("############ 写入图库操作失败, 可能是由于图谱schema设计问题导致 ############")
                  }
                } catch {
                  case e: Exception => {
                    LogTool.threadError("边导入异常，异常原因:{}", e)
                  }
                }
              }
            )
          } catch {
            case e: Exception => {
              LogTool.error(s"openNebulaGraph error cause=${e.getMessage}", e)
            }
          } finally {
            // 释放 nebula 连接
            if (session != null) {
              session.release()
            }
          }
        }
        )
      }
    )
  }

  def jsonToNebula(schemaConfigBD: Broadcast[Map[String, Any]], nebulaConfigBD: Broadcast[NebulaConfig], sinkBean: DStream[JSONObject]): Unit = {
    LogTool.info("准备处理jsonToNebula数据...")
    // 将json数据入图库的具体操作
    sinkBean.foreachRDD(
      rdd => if (!rdd.isEmpty()) {
        LogTool.info("准备处理sinkbean中每个分区中的多条数据")
        // 外层循环遍历先遍历每一个收到的DStream，可能是多个partition，每个partition中包含多个RDD
        rdd.foreachPartition(
          // 处理每个分区
          partition => {
            // 处理分区中每条记录
            var entityList: ListBuffer[Map[String, Any]] = ListBuffer()
            var relationList: ListBuffer[Map[String, Any]] = ListBuffer()
            val cleanUtils = new CleanUtils()

            partition.foreach(
              (record: JSONObject) => {
                // 解析json数据的部分
                var entityL = SchemaUtil.parseEntity(schemaConfigBD.value, record)
                var relationL = SchemaUtil.parseRelation(schemaConfigBD.value, record)

                // 向 elemList 中增加另外一个ListBuffer
                entityList ++= entityL
                relationList ++= relationL
              }
            )
            // 按要求组装label map
            var entityLabelMap = SchemaUtil.compoundLabelMap(entityList)
            var relationLabelMap = SchemaUtil.compoundLabelMap(relationList)

            // 处理完后入图库
            //TODO insert batch map to graph db
            // 分不同的lable组装成nebula 的语句
            // 然后操作图库入图
            val statementVertexNGLList = SchemaUtil.buildVertexNGLStatement(entityLabelMap)
            val statementEdgeNGLList = SchemaUtil.buildEdgeNGLStatement(relationLabelMap)

            var session: Session = null

            try {
              // 与图库建立连接
              session = new SSNebulaImportHelper().openNebulaGraph(nebulaConfigBD.value)

              // 遍历每条点的 NGL 语句
              statementVertexNGLList.foreach(
                statement => {
                  // 执行具体的入图库操作
                  try {
                    LogTool.info("开始导入图库...节点...")
                    LogTool.info(s"insert vertex statement: ${statement}")
                    var resultSet = session.execute(statement)
                    if (!resultSet.isSucceeded) if (resultSet.getErrorCode == -8) {
                      resultSet = session.execute(statement)
                      if (!resultSet.isSucceeded)
                        LogTool.error(String.format("Insert vertex Retry Failed: %s %s", resultSet.getErrorCode + "", statement))
                    }
                    else {
                      LogTool.error(String.format("Insert vertex Failed: %s %s", resultSet.getErrorCode + "", statement))
                      LogTool.error("############ 写入图库操作失败, 可能是由于图谱schema设计问题导致 ############")
                    }
                  } catch {
                    case e: Exception => {
                      LogTool.threadError("点导入异常，异常原因:{}", e)
                    }
                  }
                }
              )

              // 遍历每条边的 NGL 语句
              statementEdgeNGLList.foreach(
                statement => {
                  try {
                    LogTool.info("开始导入图库...边...")
                    LogTool.info(s"insert edge statement: ${statement}")
                    var resultSet = session.execute(statement)
                    if (!resultSet.isSucceeded) if (resultSet.getErrorCode == -8) {
                      resultSet = session.execute(statement)
                      if (!resultSet.isSucceeded) LogTool.error(String.format("Insert Edges Retry Failed: %s %s", resultSet.getErrorCode + "", statement))
                    }
                    else {
                      LogTool.error(String.format("Insert Edges Failed: %s %s", resultSet.getErrorCode + "", statement))
                      LogTool.error("############ 写入图库操作失败, 可能是由于图谱schema设计问题导致 ############")
                    }
                  } catch {
                    case e: Exception => {
                      LogTool.threadError("边导入异常，异常原因:{}", e)
                    }
                  }
                }
              )

            } catch {
              case e: Exception => {
                LogTool.error(s"openNebulaGraph error cause=${e.getMessage}", e)
              }
            } finally {
              // 释放 nebula 连接
              if (session != null) {
                session.release()
              }
            }

          }
        )
      }
    )

  }

}
