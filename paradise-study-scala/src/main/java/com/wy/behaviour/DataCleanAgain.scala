package com.wy.behaviour

import org.apache.spark.sql.SparkSession

/**
 * 二次清洗用户日志,输入访问时间,访问url,耗费的流量,访问ip等得到用户访问网站的url,访问网站的分类等信息
 */
object DataCleanAgain {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[2]").appName("DataClean").getOrCreate();
    val access = spark.sparkContext.textFile("file///app/nginx/logs/access.log");
    access.map(line => {
      val splits = line.split(" ");
      val ip = splits(0);
      // 源日志文件的第三个和第四个字段拼接起来就是完整的访问时间,如果需要特殊的处理,可以写工具类或其他方法
      val datetime = splits(3) + " " + splits(4);
      val traffic = splits(9);
      val url = splits(11).replaceAll("\"", "");
      (ip, datetime, traffic, url)
    })
      // 直接输出都控制台
      //    .take(10).foreach(println);
      .saveAsTextFile("file///app/logs/analysis/userlog");
    spark.stop();
  }
}