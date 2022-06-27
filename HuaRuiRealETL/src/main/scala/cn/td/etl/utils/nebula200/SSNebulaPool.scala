//package cn.tongdun.yuntu.shensuan.handler.util.dbtool.nebula
package cn.td.etl.utils.nebula200

import com.vesoft.nebula.client.graph.NebulaPoolConfig
import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.exception.{IOErrorException, InvalidConfigException, NotValidConnectionException}
import com.vesoft.nebula.client.graph.net._
import org.apache.commons.pool2.impl.{AbandonedConfig, GenericObjectPool, GenericObjectPoolConfig}
import org.slf4j.LoggerFactory

import java.net.InetAddress
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

/**
 * @description
 * @author liguangmin
 * @date 2021/7/19 上午11:06
 * @version 1.0
 */
class SSNebulaPool extends NebulaPool{
  var objectPool_L:GenericObjectPool[SyncConnection]  = null
  var  loadBalancer_L:LoadBalancer = null;
  var  log = LoggerFactory.getLogger(this.getClass)
  val waitTime = 60*1000

   def hostToIp(address:java.util.List[HostAddress]):List[HostAddress] = {
    var newAddrs = ListBuffer[HostAddress]()
    address.asScala.foreach(addr =>{
      val ip = InetAddress.getByName(addr.getHost()).getHostAddress
      newAddrs += new HostAddress(ip, addr.getPort)
    })
    newAddrs.toList
  }

   def checkConfig(config: NebulaPoolConfig): Unit = {
    if(config.getIdleTime < 0) {
      throw  new InvalidConfigException(s"config IdelTIme:${config.getIdleTime} is illegal")
    }
    if(config.getMaxConnSize <= 0){
      throw new InvalidConfigException(s"config max ConnSize: ${config.getMaxConnSize} is illegal")
    }
    if(config.getMinConnSize <0 || config.getMinConnSize > config.getMaxConnSize){
      throw new InvalidConfigException(s"config minConnSize: ${config.getMinConnSize} is illegal")
    }
    if(config.getTimeout < 0 ){
      throw new InvalidConfigException(s"config timeout: ${config.getTimeout} is illagel")
    }
  }

   override def init(addresses: java.util.List[HostAddress], config: NebulaPoolConfig): Boolean = {
    checkConfig(config)
    val newAddrs = hostToIp(addresses)
    this.loadBalancer_L = new RoundRobinLoadBalancer(newAddrs.asJava, config.getTimeout)
    val objectPool = new SSConnObjectPool(this.loadBalancer_L, config)
    this.objectPool_L = new GenericObjectPool[SyncConnection](objectPool)
    val objConfig = new GenericObjectPoolConfig()
    objConfig.setMinIdle(config.getMinConnSize)
    objConfig.setMaxTotal(config.getMaxConnSize)
    objConfig.setMinEvictableIdleTimeMillis(config.getIdleTime)
    this.objectPool_L.setConfig(objConfig)

    val abandonedConfig = new AbandonedConfig;
    abandonedConfig.setRemoveAbandonedOnBorrow(true)
    this.objectPool_L.setAbandonedConfig(abandonedConfig)
    objectPool.init()
  }

  override def close(): Unit = {
    this.loadBalancer_L.close()
    this.objectPool_L.close()
  }

  override def getSession(userName: String, password: String, reconnect: Boolean): Session ={
    try{
      var retry = if(getIdleConnNum == 0) 1 else getIdleConnNum
      var connection: SyncConnection = null
      breakable{
        while (retry > 0){
          connection = objectPool_L.borrowObject(waitTime)
          if(connection != null && connection.ping()){
            break()
          }
          retry = retry - 1
        }
      }
      if(connection == null){
        throw new NotValidConnectionException("Get connection object failed!")
      }
      log.info(String.format("Get connection to %s:%s", connection.getServerAddress.getHost, connection.getServerAddress.getPort.toString))
      val sessionID = connection.authenticate(userName, password)
      new Session(connection, sessionID, this.objectPool_L, reconnect)
    }catch {
      case e:NotValidConnectionException => throw e
      case e:IllegalStateException => throw new NotValidConnectionException(e.getMessage)
      case e:Exception => throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage)
    }
  }

  override def getActiveConnNum: Int = {
     objectPool_L.getNumActive
  }

  override def getIdleConnNum: Int = {
    objectPool_L.getNumIdle
  }

  override def getWaitersNum: Int = {
    objectPool_L.getNumWaiters
  }

  override def updateServerStatus(): Unit = {
    if(objectPool_L.getFactory.isInstanceOf[ConnObjectPool]){
      objectPool_L.getFactory.asInstanceOf[ConnObjectPool].updateServerStatus()
    }
  }
}
