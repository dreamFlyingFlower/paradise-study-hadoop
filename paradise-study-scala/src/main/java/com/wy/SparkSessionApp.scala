package com.wy

import org.apache.spark.sql.SparkSession

/**
 * 使用SparkSession,需要Spark2.0以上
 */
object SparkSessionApp {

  def main(args: Array[String]): Unit = {
    val path = args(0);
    val spark = SparkSession.builder().master("local[2]").appName("SparkSessionApp").getOrCreate();
    val data = spark.read.json(path);
    // 关闭资源
    spark.stop();
  }
}