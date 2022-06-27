import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, TaskContext}
import org.junit.Test

class TestStreamingLog {
  @Test
  def check01(): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local[2]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val ssc = new StreamingContext(conf, Seconds(2))
    ssc.sparkContext.setLogLevel("WARN")

    val value = ssc.socketTextStream("localhost", 9999)

    val re = value.transform((rdd: RDD[String]) => {

      rdd.mapPartitions(partitions => {
        val t2 = System.currentTimeMillis()

        partitions.foreach(record => {
          record.foreach(c => println(c))

        })

        System.out.println("###### realTimeQuotaPOC step interval (" + TaskContext.getPartitionId() + "): " + (System.currentTimeMillis() - t2) + "ms");
        partitions
      })
    })

    re.print(1)

    ssc.start()
    ssc.awaitTermination()
  }
}
