package com.wy

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType

/**
 * DataFrame和RDD的互相操作:反射方式,需要根据文件定义一个类
 *
 * 使用反射来推断包含了特定数据类型的RDD的元数据,使用DataFrame Api或者sql方式编程
 */
object DataFrameRDD {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[2]").appName("DataFrameRDD").getOrCreate();
    reflect(spark);
    program(spark);
    spark.stop();
  }

  /**
   * 通过编程的方式进行互操作
   */
  def program(spark: SparkSession): Unit = {
    // RDD=>DataFrame
    val rdd = spark.sparkContext.textFile("file:///app/spark/test/infos.txt");
    val infoRdd = rdd.map(_.split(",")).map(line => Row(line(0).toInt, line(1), line(2).toInt));
    val structType = StructType(Array(StructField("id", IntegerType, true), StructField("name", StringType, true),
      StructField("age", IntegerType, true)));
    val infoDF = spark.createDataFrame(infoRdd, structType);
    infoDF.printSchema()
    infoDF.show()
    infoDF.filter(infoDF.col("age") > 30).show();
    // 基于sql查询,等价于上面的filter
    infoDF.createOrReplaceTempView("infos");
    spark.sql("select * from infos where age > 30").show();
  }

  /**
   * 通过反射的方式进行互操作,优先考虑
   */
  def reflect(spark: SparkSession): Unit = {
    // RDD=>DataFrame
    val rdd = spark.sparkContext.textFile("file:///app/spark/test/infos.txt");
    // 需要导入隐式转换
    import spark.implicits._
    val infoDF = rdd.map(_.split(",")).map(line => Info(line(0).toInt, line(1), line(2).toInt)).toDF();
    infoDF.show();
    infoDF.filter(infoDF.col("age") > 30).show();
    // 基于sql查询,等价于上面的filter
    infoDF.createOrReplaceTempView("infos");
    spark.sql("select * from infos where age > 30").show();
  }

  case class Info(id: Int, name: String, age: Int)
}