# Spark



# 概述

* 类似于MR,运行在yarn上,在逻辑回归的特定算法上比hadoop快100倍上
* 由Spark Core,Spark SQL,Spark Streaming,Spark MLLib,Spark GraphX组成其生态图
* Spark Core:是其他Spark框架的核心,而Spark的核心是RDD(弹性分布式数据集)
* Spark SQL:类似Hive使用SQL语句操作RDD DataFrame(表)
* Spark Streaming:流式计算
* Spark MLLib:Spark机器学习类库
* Spark GraphX:图计算



# Spark和Hadoop

* 批处理:
  * Spark:Spark RDDS,java,Scala,Python
  * Hadoop:MapReduce,Pig,Hive,Java
* SQL查询:
  * Spark:Spark SQL
  * Hadoop:Hive
* 流式计算:
  * Spark:Spark Streaming
  * Hadoop:Storm,Kafka
* 机器学习:
  * Spark:Spark ML Lib
  * Hadoop:Mahout,该框架已经不更新
* 实时监控:
  * Spark:Spark能从NoSQL存储中查询数据
  * Hadoop:NoSQL,Hbase,Cassandra
* Hadoop:分布式计算+分布式数据存储;MR做计算;HDFS;不能迭代运行;批处理引擎;
* Spark:只有分布式计算,不存储数据;通用计算;即可以存磁盘也可以存内存;适合做迭代计算;即可以做批处理,也可以做流式处理,在磁盘上的计算比Hadoop快2-10倍,在内存中的计算快100倍;



# 特性

* 完全兼容Hadoop,能够快速的切换hadoop,hbase等
* 只有数据计算,没有数据存储,是不能取代hadoop的
* 框架多样化,可以和其他框架无缝衔接
  * 离线批处理,MapReduce,Hive,Pig
  * 实时流式处理,Storm,JStorm
  * 交互式计算:Impala



# 核心

* DataSets:
* DataFrames:



# 编译

* 因为Spark中所用的Hadoop,Yarn等其他框架的版本可能会生产环境中的版本不一致,故而需要重新编译

* 进入Spark官网后选择需要使用的版本的源码下载

* 将源码直接下载到服务器上,先安装maven,jdk,配置好环境变量

* 在官网的Documentation->More->Building Spark查看编译文档,注意Maven和JDK的版本是否符合Spark版本

* Spark有自带的Maven框架,但是仍然最好使用自己安装的Maven

* Maven的编译命令如下,[官网](http://spark.apache.org/docs/latest/building-spark.html#apache-maven)可查看:

  ```shell
  # 该处使用的是Spark自带的Maven,也可以使用自己的
  # 可以加上其他的如hive
  ./build/mvn -Pyarn -Dhadoop.version=2.8.5 -DskipTests clean package
  # 使用yarn,hive的相关命令,使用什么插件都可以在官网上查找
  ./build/mvn -Pyarn -Phive -Phive-thriftserver -DskipTests clean package
  ```

  * 因为Spark支持的版本不同,可以查看Spark源码中pom.xml中的插件版本
  * -p:pom.xml中的profiles中的profile配置,需要和pom.xml中一致
  * -D:表示指定hadoop的版本

* 编译可能出现的问题:

  * 缺少hadoop-cient.jar,这是因为仓库中没有该jar包,在pom.xml中添加如下仓库

    ```xml
    <repository>
    	<id>cloudera</id>
    	<url>https://repository.cloudera.com/artifactory/cloudera-repos</url>
    </repository>
    ```

  * 编译时的内不够,机器的内存最好是2-4G之间

    ```shell
    export MAVEN_OPTS="-Xmx2g -XX:ReservedCodeCacheSize=1g"
    ```

  * 在编译Scala之前,需要先从官网上查看最低版本的Scala需要的版本,并且先执行如下:

    ```shell
    # Scala根据Spark官网选择
    ./dev/change-scala-version.sh 2.13
    ./build/mvn -Pscala-2.13 compile
    ```

    

# 安装运行

详见官网[文档](http://spark.apache.org/docs/latest/#),参数[文档](http://spark.apache.org/docs/latest/submitting-applications.html)



## 本地模式

* 编译完成直接进入sbin中,不用修改任何配置,运行相关命令即可

  ```shell
  # master指定运行模式:local表示运行模式为本地,2表示多线程的线程数
  spark-shell --master local[2]
  ```



## StandAlone

* 官网[文档](http://spark.apache.org/docs/latest/spark-standalone.html)

* 当不配置任何works时,可以直接启动sbin/start-master.sh,web页面可以从启动日志中查看masterui

* 配置文件在conf/spark-env.sh.template,改名为spark-env.sh

  ```shell
  SPARK_MASTER_HOST=localhost # 主机名
  SPARK_WORKER_CORES=2 # works核心工作数量
  SPARK_WORKER_MEMORY=2g # 分配给work的总内存
  ```

* 当含有work时,可以使用哦sbin/start-all.sh启动spark



## 集群模式

* 该模式下需要在master节点的conf/slaves中配置所有的work节点的主机名,只配置work的,不配置master
* 启动:spark-shell --master spark://hadoop001:7077,该命令会启动master和所有的work
* 若多次启动了master,则后启动的master在默认情况下是分配不到内存的
* 若需要所有的master都可以运行,需要进行相关配置,详见官网文档





# Spark SQL

官网[文档](http://spark.apache.org/docs/latest/sql-programming-guide.html)



