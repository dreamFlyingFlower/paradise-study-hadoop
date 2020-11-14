package com.wy

import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * SQLContext的使用
 */
object SQLContextApp {

  def main(args: Array[String]): Unit = {
    // 需要进行解析的数据文件,从运行时参数中传入
    val path = args(0);
    // 创建响应的Context
    val sparkConf = new SparkConf();
    // 在测试或生产环境中,AppName和Master由脚本指定
    //    sparkConf.setAppName("SQLContextApp").setMaster("local[2]");
    val sc = new SparkContext(sparkConf);
    // 该方法已废弃,需要换成其他方法,需要将该项目转成maven项目
    val sqlContext = new SQLContext(sc);
    // 对数据相关处理,假设处理的是一个json格式的文件,是什么格式写什么格式,相关资料见官网文档
    val data = sqlContext.read.format("json").load(path);
    // 打印json的key
    data.printSchema();
    // 打印整个json的值
    data.show();
    // 关闭资源
    sc.stop();
  }
}