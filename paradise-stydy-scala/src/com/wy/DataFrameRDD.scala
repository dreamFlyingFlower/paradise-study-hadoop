//package com.wy
//
//import org.apache.spark.sql.SparkSession
//
///**
// * DataFrame和RDD的互相操作:反射方式
// */
//object DataFrameRDD {
//
//  def main(args: Array[String]): Unit = {
//    val spark = SparkSession.builder().master("local[2]").appName("DataFrameRDD").getOrCreate();
//    val dataFrame = spark.read.json("file:///app/spark/test/person.json");
//    val rdd = spark.sparkContext.textFile("file:///app/spark/test/person.json");
//    // 需要导入隐式转换
//    import spark.implicits._
//    val infoDF = rdd.map(_.split(",")).map(line => Info(line(0).toInt, line(1), line(2).toInt)).toDF();
//    infoDF.show();
//  }
//
//  case class Info(id: Int, name: String, age: Int)
//}