# Spark



# 概述

* 类似于MR,运行在yarn上,在逻辑回归的特定算法上比hadoop快100倍上
* 由Spark Core,Spark SQL,Spark Streaming,Spark MLLib,Spark GraphX组成其生态图
* Spark Core:是其他Spark框架的核心,而Spark的核心是RDD(弹性分布式数据集)
* Spark SQL:类似Hive使用SQL语句操作RDD DataFrame(表)
* Spark Streaming:流式计算
* Spark MLLib:Spark机器学习类库
* Spark GraphX:图计算



# 特性

* 完全兼容Hadoop,能够快速的切换hadoop,hbase等
* 只有数据计算,没有数据存储,是不能取代hadoop的



# 核心

* DataSets:
* DataFrames: