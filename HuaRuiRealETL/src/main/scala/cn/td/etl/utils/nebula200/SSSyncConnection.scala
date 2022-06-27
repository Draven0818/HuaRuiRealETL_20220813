//package cn.tongdun.yuntu.shensuan.handler.util.dbtool.nebula
package cn.td.etl.utils.nebula200

import com.facebook.thrift.TException
import com.facebook.thrift.protocol.TCompactProtocol
import com.facebook.thrift.transport.{TSocket, TTransportException}
import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.exception.{AuthFailedException, IOErrorException}
import com.vesoft.nebula.client.graph.net.SyncConnection
import com.vesoft.nebula.graph.{ErrorCode, ExecutionResponse, GraphService}

import java.nio.charset.Charset

/**
 * @description
 * @author liguangmin
 * @date 2021/7/19 下午1:55
 * @version 1.0
 */
class SSSyncConnection extends SyncConnection{
  //var transport:TTransport = null
  //var protocol:TProtocol = null
  var client:GraphService.Client = null
  var charset:Charset = Charset.forName("UTF8")

  override def open(address: HostAddress, timeout: Int): Unit = {
    this.serverAddr = address
    try{
      val newTimeout = if(timeout <0) Integer.MAX_VALUE else timeout
      this.transport = new TSocket(address.getHost, address.getPort, newTimeout, newTimeout)
      this.transport.open()
      this.protocol = new TCompactProtocol(transport)
      client = new GraphService.Client(protocol)
    }catch {
      case e:TException => throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage)
    }
  }

  override def authenticate(user: String, password: String): Long = {
    try{
      val resp = client.authenticate(user.getBytes(), password.getBytes())
      if(resp.error_code != ErrorCode.SUCCEEDED){
        throw new AuthFailedException(new String(resp.error_msg).intern())
      }
      resp.session_id
    }catch {
      case e:TException =>{
        if(e.isInstanceOf[TTransportException]){
          val te = e.asInstanceOf[TTransportException]
          if(te.getType == TTransportException.END_OF_FILE){
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, e.getMessage)
          }
        }
        throw new AuthFailedException(String.format("Authenticate failed:%s", e.getMessage))
      }
    }
  }

  override def execute(sessionID: Long, stmt: String): ExecutionResponse = {
    try {
      client.execute(sessionID, stmt.getBytes(charset))
    }catch {
      case e:TException =>{
        if(e.isInstanceOf[TTransportException]){
          val te = e.asInstanceOf[TTransportException]
          if(te.getType == TTransportException.END_OF_FILE){
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, e.getMessage)
          }
        }
        throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage)
      }
    }
  }

  override def signout(sessionId: Long): Unit = {
    try{
      client.signout(sessionId)
    }catch {
      case e:TException =>{
        this.close()
      }
    }
  }

  override def ping(): Boolean = {
    try{
      client.execute(0, "YIELD 1;".getBytes())
      true
    }catch {
      case e:TException =>{
        if(e.isInstanceOf[TTransportException]){
          val te = e.asInstanceOf[TTransportException]
          te.getType != TTransportException.END_OF_FILE && te.getType != TTransportException.NOT_OPEN
        }else{
          true
        }

      }
    }
  }

  override def close(): Unit = {
    if(transport != null){
      transport.close()
    }
  }

  def setCharset(charSet: Charset): Unit ={
    this.charset = charSet
  }

  def setCharset(charset: String): Unit ={
    this.charset = Charset.forName(charset)
  }
}
