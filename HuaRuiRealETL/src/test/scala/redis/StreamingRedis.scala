package redis

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.util.logging.{Level, Logger}

object StreamingRedis {

  Logger.getLogger("org.apache.spark").setLevel(Level.WARNING)

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    val conf = new SparkConf().setMaster("local[*]").setAppName("Word count and Redis")
    val ssc = new StreamingContext(conf, Seconds(2))

    val streams = ssc.socketTextStream("localhost", 8888)

    streams.foreachRDD(rdd => {
      val current_batch_result = rdd.flatMap(_.split(" ")).map(w => (w, 1)).reduceByKey(_ + _)
      current_batch_result.foreachPartition(part => {
        val jedis = Jpool.getJedis
        part.foreach(tp => {
          jedis.hincrBy("wc", tp._1, tp._2)
        })
        jedis.close()
      })
    })

    ssc.start()
    ssc.awaitTermination()

  }

}
