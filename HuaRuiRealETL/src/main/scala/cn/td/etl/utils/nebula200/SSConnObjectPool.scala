//package cn.tongdun.yuntu.shensuan.handler.util.dbtool.nebula
package cn.td.etl.utils.nebula200

import com.vesoft.nebula.client.graph.NebulaPoolConfig
import com.vesoft.nebula.client.graph.exception.IOErrorException
import com.vesoft.nebula.client.graph.net.{ConnObjectPool, LoadBalancer, SyncConnection}

/**
 * @description
 * @author liguangmin
 * @date 2021/7/19 下午1:43
 * @version 1.0
 */
class SSConnObjectPool(loadBalancer: LoadBalancer, config: NebulaPoolConfig) extends ConnObjectPool(loadBalancer, config){
  val retryTime = 3

  override def create(): SyncConnection = {
    val address = loadBalancer.getAddress
    if(address == null){
      throw new IOErrorException(IOErrorException.E_ALL_BROKEN, "All servers are broken")
    }
    var retry = retryTime
    val conn = new SSSyncConnection()
    while (retry > 0){
      retry = retry -1
      try {
        conn.open(address, config.getTimeout)
        return conn
      } catch {
        case e:IOErrorException =>{
          if(retry == 0){
            throw e
          }
          this.loadBalancer.updateServersStatus()
        }
      }
    }
    return null
  }
}
