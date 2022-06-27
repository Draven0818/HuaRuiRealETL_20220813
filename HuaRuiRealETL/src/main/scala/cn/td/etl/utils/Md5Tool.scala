package cn.td.etl.utils

import java.math.BigInteger
import java.security.MessageDigest

import cn.td.etl.utils.trans.BaseFunc

object Md5Tool {
  /**
   * md5生成32位的数据
   *
   * @param text
   * @return
   */
  def md5(text: String): String = {
    val digest = MessageDigest.getInstance("MD5")
    return digest.digest(text.getBytes).map("%02x".format(_)).mkString
  }

  def rowKeyPrefixGen(text: String): String = {
    val digest = MessageDigest.getInstance("MD5")
    val array = digest.digest(text.getBytes)
    val bigInt = new BigInteger(1, array)
    return bigInt.toString(10).substring(0, 3)
  }

  def main(args: Array[String]): Unit = {
    println(BaseFunc.md5("c92057befc9e11ea92e66c92bf677962"))
  }

}
