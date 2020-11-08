package com.wy

import org.apache.spark.sql.SparkSession

object DataFrameApp {
  def main(args: Array[String]): Unit = {
    // 创建spark对象
    val spark = SparkSession.builder().master("local[2]").appName("DataFrameApi").getOrCreate();
    // 创建dataframe:参数为数据格式,文件地址
    val dataFrame = spark.read.json("file:///app/spark/test/person.json");
    // 输出dataframe对应的schema信息
    dataFrame.printSchema();
    // 输出数据集的记录
    dataFrame.show();
    // 查询某列所有的数据:select username from user;
    dataFrame.select("name").show();
    // 查询某几列所有的数据,并对列进行计算
    dataFrame.select(dataFrame.col("username"), (dataFrame.col("age") + 10)).show();
    // 根据某一列值进行过滤
    dataFrame.filter(dataFrame.col("age") > 18).show();
    // 根据某一列进行分组,然后再进行聚合操作
    dataFrame.groupBy("age").count();
    spark.stop();
  }
}