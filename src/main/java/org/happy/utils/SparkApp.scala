package org.happy.utils

import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark程序入口
  *
  * @author happy
  * @version 17/4/08 下午7:00
  */
object SparkApp {
    def main(args: Array[String]) {

        val batch = args(0).toInt

        val sparkConf = new SparkConf()
        //        sparkConf.setAppName("SparkApp").setMaster("local[2]");

        val sc = new SparkContext(sparkConf)
        val sqlc = new SQLContext(sc)
        val ssc = new StreamingContext(sc, Seconds(batch))

        var storageLevel = StorageLevel.MEMORY_AND_DISK_SER_2

        ssc.start
        ssc.awaitTermination()
    }
}
