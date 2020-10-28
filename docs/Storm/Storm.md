# Storm



# 概述



* Apache开源的分布式实时计算框架,擅长处理海量数据
* 可以轻松,可靠的处理无限数据流(流式计算),速度快,每秒可处理百万元组数据
* 对实时分析,机器学习,连续计算,分布式RPC,ETL等提供高效,可靠的支持
* 用于数据实时处理而非批量处理(Hadoop)



# 特性



* Storm拥有低延迟,高性能,分布式,可扩展,容错等特性,可确保消息不丢失且严格有序
* 简单的编程:类似于MR降低并行处理的复杂性,Storm降低了进行实时处理的复杂性
* 实时性:系统不停运转等待任务到达,接受任务后迅速处理
* 可伸缩性:随时添加新的节点扩充集群的计算能力
* 容错性:管理所有工作进程和节点的故障,并且循环修复故障
* 水平扩展:计算是在多个线程,进程和服务器之间并行进行的
* 多语言支持:提供多语言协议支持.默认支持Clojure,Java,Ruby和Python.要增加对其他语言的支持,只需实现一个简单的Storm通信协议即可



# 核心



## 数据流模型

![](StormData.jpg)

* Storm计算结构名为Topology(拓扑),由stream(数据流),spout(数据流的生成者),bolt(运算)组成
* Topology:Storm分布式计算结构称为Topology(拓扑),类似于网络拓扑的一种虚拟结构,地位上等
  同于Hadoop的MR任务,与MR不同的是MR运行一段时间就会完成,而Topology会一直运
  行知道手动kill掉
* Stream:数据流,分布式并行创建与处理的无界的连续元组,关键是定义tuple
* Tuple:元组,storm的核心数据结构,消息发送的最小单元,tuple是包含一个或多个键值对的列表
* Spout:spout为一个Storm Topology的主要数据入口,充当采集器的角色,连接到数据源,将数据
  转化为一个个tuple,并将tuple作为数据流进行发射
* Storm为spout提供了简易的API,开发一个spout的主要工作就是编写代码从数据源或者API消费数据
* 数据源的种类:
  * Web或者移动程序的点击数据
  * 应用程序的日志数据
  * 传感器的输出

* Bolt:业务逻辑运算节点,可以有多个(并行与串行均可,以实际业务决定),选择性地输出一个或
  者多个数据流.bolt可以订阅多个spout或者其他bolt发射的数据流,这样可以建立多个复杂
  的数据流转换网络.在定义Topology时,需要为每一个bolt指定接收什么样的流作为输入
  (通过Storm grouping)
* Bolt可以执行各式各样的处理功能,通常我们会将业务逻辑写在bolt,典型的功能:
  * 过滤tuple
  * 连接(join)和聚合操作(aggregation)
  * 计算
  * 数据库读写
* 数据流分组:Storm grouping(数据流分组)定义一个Topology中每个bolt接受什么样的流作为输入
* Storm定义了六种内置数据流分组的定义:
  * 随机分组(Shuffle grouping):这种方式下元组会被尽可能随机地分配到 bolt 的不同任务(tasks)中,使得每个任务所处理元组数量能够能够保持基本一致,以确保集群的负载均衡
  * 按字段分组(Fields grouping):这种方式下数据流根据定义的“字段”来进行分组.例如,如果某个数据流是基于一个名为“user-id”的字段进行分组的,那么所有包含相同的“user-id”的元组都会被分配到同一个task中,这样就可以确保消息处理的一致性
  * 完全分组(All grouping):将所有的tuple复制后分发给所有的bolt task.每个订阅的task都会接收到tuple的拷贝,所有在使用此分组时需小心使用
  * 全局分组(Global grouping):这种方式下所有的数据流都会被发送到 Bolt 的同一个任务中,也就是 id 最小的那个任务
  * 不分组(NoneGrouping):不关心数据流如何分组.目前这种方式的结果与随机分组完全等效,不过未来 Storm 社区可能会考虑通过非分组方式来让Bolt和它所订阅的Spout或Bolt在同一个线程中执行
  * 指向型分组(Direct grouping):数据源会调用emitDirect()方法来判断一个tuple应该由那个Storm组件来接收.只能在声明了是指向型的数据流上使用
* 可靠的数据处理:Storm可以通过拓扑来确保每个发送的元组都能得到正确处理.通过跟踪由 Spout 发出的每个元组构成的元组树可以确定元组是否已经完成处理.每个拓扑都有一个“消息延时”参数,
  如果 torm在延时时间内没有检测到元组是否处理完成,就会将该元组标记为处理失败,并会在稍后重新发送该元组
* 为了充分利用Storm的可靠性机制,你必须在元组树创建新结点的时候以及元组处理完成的时候通知Storm.这个过程可以在Bolt发送元组时通过OutputCollector实现:在emit方法中实现元组的锚定(Anchoring),同时使用 ack 方法表明你已经完成了元组的处理
* Storm的tuple锚定和应答确认机制中,当打开可靠传输的选项,传输到故障节点上的tuples将不会受到应答确认,spout会因为超时重新发射原始的tuple.这样的过程会一直重复直到Topology从故障中恢复开始正常处理数据
* 在Storm集群中真正运行Topology的主要有三个实体:工作进程,线程和任务
* Storm集群中的每台机器上都可以运行多个工作进程,每个工作进程又可创建多个线程,每个线程可以执行多个任务,任务是真正进行数据处理的实体,我们开发的spout,bolt就是作为一个或者多个任务的方式执行的.因此,计算任务在多个线程,进程和服务器之间并行进行,支持灵活的水平扩展



## 核心组件

![](StormRun.png)

* Storm集群由一个主节点(nimbus)和一个或者多个工作节点(supervisor)组成
* Nimbus:Storm主节点,类似于Hadoop中的jobtracker,管理,协调和监控在集群上运行的Topology.包括Topology的发布,事件处理失败时重新指派任务
* Supervisor:每个工作节点运行Supervisor守护进程
  * 负责监听工作节点上已经分配的主机作业,启动和停止Nimbus已经分配的工作进程
  * Supervisor会定时从ZK获取拓补信息topologies,任务分配信息assignments及各类心跳信息,以此为依据进行任务分配
  * supervisor同步时,会根据任务分配情况启动新worker或者关闭旧worker并进行负载均衡
  * 一个Storm集群可以包含一个或者多个supervisor
* Zookeeper:storm主要使用zookeeper来协调集群中的状态信息,比如任务的分配情况,worker的状态,supervisor之间的nimbus的拓扑质量.nimbus和supervisor节点的通信主要是结合zookeeper的状态变更通知和监控通知来处理的
* Worker:一个supervisor上相互独立运行的JVM进程
  * 具体处理Spout/Bolt逻辑的进程,根据提交的拓扑中conf.setNumWorkers()方法定义分配每个拓扑对应的worker数量,Storm会在每个Worker上均匀分配任务,一个Worker只能执行一个Topology的任务子集.worker进程会占用固定的可由配置进行修改的内存空间(默认768M)
  * 每个supervisor可以配置一个或多个worker,由配置文件storm.yaml中的supervisor.slots.ports控制
  * 一个supervisor配置了几个端口,其可运行的最大worker数就是几.一个topology会被分配在一个或多个worker上运行
* Executor:指一个worker在JVM中运行时开启的Java线程.多个task可以指派给同一个executer来执行,除非明确指定,Storm默认给每个executor分配一个task
* Task:可以简单的理解为spout或bolt的实例,它们的nextTuple和execute方法会被executors执行



## 任务分配

* client:提交Topology
* nimbus:这个角色所做的操作相对较多,具体如下:
  * 将提交的jar包放到nimbus所在服务器的storm.local.dir/nimbus/inbox目录下
  * submitTopology方法会负责Topology的处理;包括检查集群是否有active节点,配置文件是否正确,是否有重复的Topology名称,各个bolt/spout名是否使用相同的id等
  * nimbus任务分配,根据Topology中的定义,给spout/bolt设置task数,并分配对应的task-id
  * nimbus在zookeeper上创建workerbeats目录,要求每个worker定时向nimbus汇报
  * 将分配好的任务写入到zookeeper的storm/assignments目录下,此时任务提交完毕
  * 将Topology信息写入到zookeeper的/storm/storms目录
* supervisor
  * 建立Topology的本地目录,storm.local.dir/supervisor/stormdist/topology-uuid,该目录包括三个文件:
    * stormjar.jar:从nimbus/inbox目录拷贝
    * stormcode.ser:此Topology对象的序列化
    * stormconf.ser:此Topology的配置文件序列化
  * 定期扫描zookeeper上的storms目录,看看是否有新的任务,有就下载
  * 删除本地不需要的Topology
  * 根据nimbus指定的任务信息启动worker
* worker
  * 查看需要执行的任务,根据任务id分辨出spout/bolt任务
  * 计算出所代表的spout/bolt会给哪些task发送信息
  * worker根据分配的tasks信息,启动多个executor线程,同时实例化spout,bolt,acker等组件,此时,等待所有connections(worker和其它机器通讯的网络连接)启动完毕执行spout任务或者blot任务
  * 在slot充沛的情况下,能够保证所有topology的task被均匀的分配到整个机器的所有机器上
  * 在slot不足的情况下,它会把topology的所有的task分配到仅有的slot上去,这时候其实不是理想状态,所以在nimbus发现有多余slot的时候,它会重新分配topology的task分配到空余的slot上去以达到理想状态
  * 在没有slot的时候,它什么也不做  



# 安装

* 搭建3节点的集群:master->master,slave->slave1,slave2
* 节点之间的时间要保持一致
* 安装Zookeeper集群,安装JDK,配置Java环境变量,安装Python
* 下载解压Storm压缩包到/app/storm中
* 进入conf目录,修改storm.yaml

```yaml
# 运行zookeeper的设置,可以使用hostname和IP地址两种方式
storm.zookeeper.servers: 
- "master"
- "slave1"
- "slave2"
# 指定nimbus的节点机器,使用服务器的hostname
nimbus.seeds: ["master", "slave1", "slave2"]
# 指定storm web UI的启动端口
ui.port: 9999
# 保存storm数据的路径
storm.local.dir: "/app/storm"
#指定work(spout和bolt)的运行端口,也是一个supervisor能并行的work的最大数量(能够开启的最大进程数),每个work占用1个端口,我这里每台supervisor设置的是可以并行4个work,端口可以任意指定不重复的端口
supervisor.slots.ports:
- 6700
- 6701
- 6702
- 6703
```

* 配置Storm环境变量,vi /etc/profile,完成之后重启环境变量:source /etc/profile

```shell
export STORM_HOME=storm安装路径
export PATH=.:$STORM_HOME/bin:$PATH
```

* 其他服务器配置相同配置
* 在nimbus主节点启动storm

```shell
# 启动nimbus,jps出现nimbus
storm nimbus < /dev/null 2<&1 &
# 启动web ui守护进程,jps出现core
storm ui < /dev/null 2<&1 &
```

* 启动从节点

```shell
storm supervisor < /dev/null 2<&1 &
```

* 查看web页面:ip:port



# 本地模式

> 即Topology(相当于一个任务)运行在本地机器的单一JVM上,这个模式主要用来开发,调试



# 远程模式

> 把Topology提交到集群,该模式中,Storm的所有组件都是线程安全的



* 本地模式和远程模式只是在Topology的运行主类的代码上有所差别
  * 远程模式:集群提交方法,StormSubmitter.submitTopology
  * 本地模式:本地提交方法,LocalCluster.submitTopology
* 运行模式和spout与bolt的编码没有关系,实际上编写一个Storm程序值需要2步即可
  * 编写spout,bolt,定义一个Topology把spout,bolt关联起来,对Topology进行配置
  * 编写Topology的提交方法,确定Topology的运行模式是本地还是远程
* 将写好的Java程序打成jar包,丢到nimbus上,执行命令运行即可

```shell
# storm jar jar包名称 执行主类的包名.类名 当前Topology命名名称
storm jar wordcount.jar com.wy.wordcount.cluster.WordCountTopology wordcount
```

  