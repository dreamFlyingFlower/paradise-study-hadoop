# Hive



# 概述

* 基于Hadoop的一个数据仓库工具,可以将结构化的数据文件映射为一张表,并提供类SQL查询
* 传统的数据库是行结构,而大数据中基本都是列结构,Hive是列结构数据
* 本质上是将HQL转化成MR(MapReduce)程序
* Hive处理的数据存储在HDFS中,底层实现仍然是MR
* 执行程序圆形在Yarn上



# 特性

* 操作接口类似SQL语法,简单,容易上手
* 避免了去写MR,减少开发时间
* Hive执行延迟比较高,只能做离线计算
* 对于处理大数据比较有优势,小数据处理没有优势
* 用户可以根据自己的需求自定义函数
* 迭代算法无法表达,数据挖掘不擅长
* 效率比较低,调优比较困难
* Hive的数据底层仍然是存储在HDFS中的
* 由于Hive是读多写少的,所以不支持数据的改写和添加,所有的数据都是在加载的时候确定好的



# 数据类型

* tinyint:对应java的byte
* smallint:对应java的short
* int:对应javaint
* bigint:对应java的long
* boolean:对应java的boolean
* float:对应java的float
* double:对应java的double
* string:对应java的string
* timestamp:时间戳
* binary:字节数组



# 安装

* 下载安装包,解压到/app/hive

* 配置Hive环境变量

  ```shell
  export HIVE_HOME =/app/hive
  export PATH=$PATH:$HIVE_HOME/bin
  ```

* 修改hive/conf目录下的hive-env.sh.template为hive-env.sh

* 在hive-env.conf加入hadoop环境地址

  ```shell
  HADOOP_HOME=/app/hadoop # 根据自己的来
  ```

* 安装一个mysql,略

* 配置mysql的地址,在hive-site.xml中

  ```xml
  <configuration>
  <property>
  	<name>java.jdo.option.ConnnectionURL</name>
  	<value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExists=true</value>
  </property>
  <property>
  	<name>java.jdo.option.ConnnectionDriverName</name>
  	<value>com.mysql.jdbc.Driver</value>
  </property>
  <property>
  	<name>java.jdo.option.ConnnectionUserName</name>
  	<value>http://192.168.1.146:50090</value>
  </property>
  <property>
  	<name>java.jdo.option.ConnnectionPassword</name>
  	<value>123456</value>
  </property>
  </configuration>
  ```

  