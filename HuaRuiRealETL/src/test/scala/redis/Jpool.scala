package redis

import redis.clients.jedis.JedisPool

object Jpool {
  private lazy val jedisPool = new JedisPool("10.58.12.62:6379")
  def getJedis = {
    val jedis = jedisPool.getResource
    jedis
  }
}
