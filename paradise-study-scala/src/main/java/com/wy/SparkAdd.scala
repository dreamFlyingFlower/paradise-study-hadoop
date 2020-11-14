package com.wy

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkFiles
import javafx.scene.shape.Line

object SparkAdd {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf();
    conf.setMaster("spark://hadoop001:7077");
    conf.setAppName("addfile test");
    val sc = new SparkContext(conf);
    // 将本地文件添加到spark集群中
    sc.addFile("/app/spark/test/test.txt");
    // 获得集群中的文件,只需要传文件名即可
    val rdd = sc.textFile(SparkFiles.get("test.txt"));
    rdd.foreach(line => {
      print(line);
    })
  }
}