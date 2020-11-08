package com.wy.behaviour

import org.apache.spark.sql.SparkSession

/**
 * 用户行为日志:数据清洗,抽取出所需要的指定列的数据;源数据来自nginx或tomcat等web服务器的access日志
 */
object DataClean {
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