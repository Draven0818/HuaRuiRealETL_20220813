package cn.td.etl.utils.nebula200

//import org.slf4j.{Logger, LoggerFactory}

import cn.td.etl.utils.LogTool
import com.vesoft.nebula.client.graph.NebulaPoolConfig
import com.vesoft.nebula.client.graph.data.{HostAddress, ResultSet}
import com.vesoft.nebula.client.graph.net.Session
import org.apache.spark.TaskContext

import java.util

/**
 * @description
 * @author liguangmin
 * @date 2021/7/19 下午2:15
 * @version 1.0
 */
case class NebulaConfig(hosts: String, spaceName: String, user: String, password: String, countPerBath: Int, hiveDbName: String) extends Serializable

class SSNebulaImportHelper() extends Serializable {

//  private val logger: Logger = LoggerFactory.getLogger(classOf[SSNebulaImportHelper])

  def openNebulaGraph(nebulaConfig: NebulaConfig): Session = {
    try {
      val list: util.List[HostAddress] = new util.ArrayList[HostAddress]
      val arr: Array[String] = nebulaConfig.hosts.split(";")
      if (arr.length == 0) throw new Exception("nebula graph hosts is empty")
      for (s <- arr) {
        val pair: Array[String] = s.split(":")
        if (pair.length != 2) throw new Exception("nebula graph hosts is wrong, please check")
        list.add(new HostAddress(pair(0), pair(1).toInt))
      }

      val nebulaPoolConfig = new NebulaPoolConfig()
      nebulaPoolConfig.setMaxConnSize(10)
      val pool = new SSNebulaPool
//            this.nebulaPool = pool

      //val client = new GraphClientImpl(list, 30000, 30000, 3, 3)
      //client.setUser(nebulaConfig.user)
      //client.setPassword(nebulaConfig.password)
      var resultSet: ResultSet = null
      var session: Session = null
      try {
        pool.init(list, nebulaPoolConfig)
        session = pool.getSession(nebulaConfig.user, nebulaConfig.password, true)
        //client.connect
        resultSet = session.execute("use " + nebulaConfig.spaceName)

        //code = client.switchSpace(nebulaConfig.spaceName)
      } catch {
        case e: Exception =>
          LogTool.error(s"connect to ${nebulaConfig.hosts} failed", e)
          throw new Exception(e.getMessage)
      }
      if (!resultSet.isSucceeded) {
        val errorInfo: String = String.format("Switch Space %s Failed", nebulaConfig.spaceName)
        LogTool.error(errorInfo)
        throw new Exception(errorInfo)
      }
      LogTool.info(s"thread id=[${Thread.currentThread().getId}],partition id=[${TaskContext.getPartitionId()}].openNebulaGraph sucess.")
      return session
    } catch {
      case e: Exception => {
        LogTool.error(s"openNebulaGraph error cause=${e.getMessage}", e)
        null
      }
    }
  }

  //TODO 没找到判断是否为空的方法
  def isGraphEmpty(session: Session): Boolean = {
    return false;
  }

}
