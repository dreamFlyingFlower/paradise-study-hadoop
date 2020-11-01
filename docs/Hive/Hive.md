# Hive



# 概述

* 由Facebook开源用于解决海量结构化日志的数据统计
* 基于Hadoop的一个数据仓库,可以将结构化的数据文件映射为一张表,并提供类SQL查询
* 传统的数据库是行结构,而大数据中基本都是列结构,Hive是列结构数据
* 本质上是将HQL转化成MR(MapReduce)程序
* Hive处理的数据存储在HDFS中,底层实现仍然是MR
* 执行程序运行在Yarn上
* 见图解.pptx-01



# 特性

* 操作接口类似SQL语法,简单,容易上手
* 避免了去写MR,减少开发时间
* Hive执行延迟比较高,只能做离线计算
* 对于处理大数据比较有优势,小数据处理没有优势
* 用户可以根据自己的需求自定义函数
* 迭代算法无法表达,数据挖掘不擅长
* 效率比较低,调优比较困难
* Hive的数据底层仍然是存储在HDFS中的
* Hive是读多写少的,不支持数据的改写和添加,所有的数据都是在加载的时候确定
* Hive在加载数据的过程中不会对数据进行任何处理,甚至不会对数据进行扫描,因此也没有对数据中的某些Key建立索引
* Hive要访问数据中满足条件的特定值时,需要暴力扫描整个数据,因此访问延迟较高



# 核心

![](HiveStructure.png)



* Hive通过给用户提供的一系列交互接口,接收到用户的指令(SQL),使用自己的Driver,结合元数据(MetaStore),将这些指令翻译成MR,提交到Hadoop中执行,最后,将执行返回的结果输出到用户交互接口
* 见图解.pptx-02



## Client

* 用户接口,包括CLI(HiveShell),JDBC/ODBC(Java访问Hive),WebUI(浏览器访问)



## Metastore

* 元数据,包括:表名,表所属的数据库(默认是default),表的拥有者,列/分区字段,表类型(是否外部表),表的数据所在目录等
* 默认存储在自带的derby数据库中,推荐使用MySQL存储Metastore



## Hadoop

* 使用HDFS进行存储,使用MapReduce进行计算



## Driver

* 驱动器,包括如下
* SQL Parser:解析器,将SQL字符串转换成抽象语法树AST,这一步一般都用第三方工具库完成,比如antlr;对AST进行语法分析,比如表是否存在,字段是否存在,SQL语义是否有误
* PhysicalPlan:编译器,将AST编译生成逻辑执行计划
* QueryOptimizer:优化器,对逻辑执行计划进行优化
* Execution:执行器,把逻辑执行计划转换成可以运行的物理计划,对于Hive来说,就是MR/Spark



# 安装配置

* 下载安装包,解压到/app/hive

* 配置Hive环境变量

  ```shell
  export HIVE_HOME =/app/hive
  export PATH=$PATH:$HIVE_HOME/bin
  ```

* 修改hive/conf目录下的hive-env.sh.template为hive-env.sh,配置hadoop环境变量

  ```shell
  HADOOP_HOME=/app/hadoop # 根据自己的来
  ```

* 必须启动hdfs和yarn

  ```shell
  sbin/start-dfs.sh
  sbin/start-yarn.sh
  ```

* 安装一个mysql,略

* 将MySQL目录下的mysq-connector-java.jar复制到/app/hive/lib中

* 配置MySQL的地址,在hive-site.xml中

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

* 启动hive:

  ```shell
  bin/hive
  ```



# Hive Shell

* HiveSQL语法同MySQL相同
* CREATE TABLE tablename(column type) ROW FORMAT DELIMITED FIELDS TERMINATED BY "\t":创建表的时候指定字段之间的分隔符,好用来加载数据
* LOAD DATA LOCAL INPATH path INTO TABLE tablename:从本地文件中加载数据到表,本地文件中的值中间的分隔符需要和建表时一样,其中的列值要和建表中的顺序相同
* bin/hive -e "sql":在LinuxShell中直接执行SQL命令
* bin/hive -f path:执行Shell脚本中的SQL语句
* dfs -ls /:在Hive中查看HDFS
* ! ls /root:在Hive中查看Linux的文件系统
* 查看hive的历史命令:进入当前用户根目录下,cat .hivehistory



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