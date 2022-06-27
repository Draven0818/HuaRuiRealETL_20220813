package redis

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{JedisPool, Protocol}

class RedisSink(makeJedisPool: () => JedisPool) extends Serializable {
  lazy val pool: JedisPool = makeJedisPool()
}

object RedisSink {
  def apply(redisHost: String, redisPort: Int, password: String, database: Int): RedisSink = {

    val createJedisPoolFunc = () => {
      val poolConfig = new GenericObjectPoolConfig()
      val pool = new JedisPool(poolConfig, redisHost, redisPort, Protocol.DEFAULT_TIMEOUT, password, database)
      val hook = new Thread {
        override def run(): Unit = {
          pool.destroy()
        }
      }
      sys.addShutdownHook(hook.run())
      pool
    }
    new RedisSink(createJedisPoolFunc)
  }
}
