# Hadoop



# 概述

* 主要由HDFS,MR,YARN,COMMON组成
* HDFS:Hadoop Distributed File System,高可靠,高吞吐量的分布式文件系统
* MR:MapReduce,分布式离线并行计算框架
* YARN:作业调度与集群资源管理的框架
* COMMON:支持其他模块的工具模块,如RPC,序列化,日志等
* 高可靠性:维护多个工作副本,集群部署,在出现故障时可以对失败的节点重新分布处理
* 高扩展性:在集群间分配任务数据,可方便的扩展数以千计的节点
* 高效性:在MR的思想下,hadoop是并行工作的
* 高容错性:自动保存多份副本数据,并且能够自动将失败的任务重新分配



# 核心

* NameNode:存储文件的元数据,如文件名,文件目录结构,文件属性,以及每个文件的块列表和块所在的datanode等.默认情况下,只有1个namenode,3个datanode
* DataNode:数据节点,在本地文件系统存储文件块数据,以及块数据的校验
* Secondary NameNode:监控hdfs状态的辅助后台程序,每隔一段时间获得hdfs元数据的快照
* ResourceManager(rm):处理客户端请求,启动和监控ApplicationMaster,NodeManager,资源分配与调度
* NodeManager(nm):单个节点上的资源管理,处理来自ResourceManager和ApplicationMaster的命令
* ApplicationMaster:数据切分,为应用程序申请资源,并分配给内部任务,任务监控和容错
* Container:对任务运行环境的抽象,封装了CPU,内存等多维资源以及环境变量,启动命令等任务信息



# API

* hadoop checknative -a:检查hadoop本地库是否正常,false不正常
* bin/hadoop fs -put input/ /input:在hadoop目录下执行,上传当前目录input里的文件到/input,注意/input是hadoop隐藏的了,反正我是没看到在什么地方;若是/input存在,则删除bin/hadoop fs -rm -r /input,提示deleted input才表示删除成功
* hadoop fs -ls /input:查看上传的文件是否成功,成功会列出文件地址,否则报错文件不存在
* hadoop jar XXX.jar xx.xx.xxx.TestMain /input /output:运行jar包,需要指定main所在类,/input表示上传文件所在地址,/output表示文件输出地址
* hadoop fs -ls /output:查看运行生成的文件,若有success文件代表成功,我也不知道怎么查看,但是可以从50070的utilities的browse the file system下面查看,可将最后的结果下载下来查看



# 配置

* 设置集群block的备份数,hdfs-site.xml文件中dfs.replication的value值改成想要的值,但是要重启hadoop
* 设置集群block的备份数,命令bin/hadoop fs -setrep -R 3 /;不需要重启



# 伪分布式

1. 环境为linux,centos7.6

2. 下载jdk1.8.0.tar.gz,hadoop2.9.1.tar.gz,在linux根目录新建目录app,app下新建目录java,hadoop

4. 解压jdk和hadoop到各自文件夹中,tar -zxvf jdk1.8.0.tar.gz

5. 解压完成之后配置环境变量,编辑 vi /etc/profile,在文件最底下添加

   ```shell
   JAVA_HOME=/app/java/java1.8
   CLASSPATH=$JAVA_HOME/lib/
   PATH=$PATH:$JAVA_HOME/bin
   export PATH JAVA_HOME CLASSPATH
   export HADOOP_HOME=/app/hadoop/hadoop-2.9.1
   export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
   ```

6. 添加完之后命令source /etc/profile,输入java -version,出现版本表示安装成功,输入hadoop出现版本信息安装成功

7. 修改hadoop配置文件,所需配置文件都在/app/hadoop/hadoop-2.9.1/etc/hadoop文件夹下

7. 修改core-site.xml,在configuration标签中添加:

   ```xml
   <!-- 指定namenode地址,name为名称,可自定义,value为当前服务器地址或主机名,9000默认端口-->
   <property>
   	<name>fs.defaultFS</name>
   	<value>hdfs://192.168.1.146:9000/</value>
   </property>
   <!-- 指定hadoop运行时产生文件的存储目录 -->
   <property>
   	<name>hadoop.tmp.dir</name>
   	<value>/app/hadoop/data</value>
   </property>
   ```

9. 修改hadoop-env.sh,修改java的路径

   ```shell
   #export JAVA_HOME=${JAVA_HOME}
   export JAVA_HOME=/app/java/jdk1.8
   ```

9. 修改hdfs-site.xml,该文件是namenode和datanode的存放地址,在configuration标签添加:

   ```xml
   <!-- 指定hdfs的副本数量,即备份 -->
   <property>
   	<name>dfs.replication</name>
   	<value>1</value>
   </property>
   <!-- 指定namenode的存储路径 -->
   <property>
   	<name>dfs.name.dir</name>
   	<value>/app/hadoop/hadoop-2.9.1/namenode</value>
   </property>
   <!-- 指定secondary namenode的地址 -->
   <property>
   	<name>dfs.namenode.secondary.http.address</name>
   	<value>http://192.168.1.146:50090</value>
   </property>
   <!-- 指定datanode的存储路径 -->
   <property>
   	<name>dfs.data.dir</name>
   	<value>/app/hadoop/hadoop-2.9.1/datanode</value>
   </property>
   <!-- 关闭权限 -->
   <property>
   	<name>dfs.permissions</name>
   	<value>false</value>
   </property>
   ```

11. 修改mapred-site.xml.template(mv mapred-site.xml.template mapred-site.xml),在configuration下添加:

    ```xml
    <!-- 指定mapreduce运行在yarn下 -->
    <property>
    	<name>mapreduce.framework.name</name>
    	<value>yarn</value>
    </property>
    ```

12. 修改yarn-site.xml,在configuration下添加:

    ```xml
    <!-- 指定yarn的老大(resourceManager)的地址 -->
    <property>
    	<name>yarn.resourcemanager.hostname</name>
    	<value>192.168.1.146</value>
    </property>
    <!-- reducer获取数据的方式 -->
    <property>
    	<name>yarn.nodemanager.aux-services</name>
    	<value>mapreduce_shuffle</value>
    </property>
    ```

13. 修改slaves文件,加入自己的ip地址,删除localhost

14. 查看自己linux的ip地址:ifconfig,不是127.0.0.1的那个就是

15. 要想让其他机器能访问hadoop启动后的页面需要先关闭防火墙

    ```shell
    systemctl stop firewalld.service #停止firewall
    systemctl disable firewalld.service #禁止firewall开机启动
    firewall-cmd --state #查看默认防火墙状态（关闭后显示notrunning，开启后显示running）
    ```

16. 免密钥登录,必须配置

    1. 进入到/home文件夹,输入ssh-keygen -t rsa,连着回车确认
    2. 完成后会生成会生成两个文件id_rsa(私钥),id_rsa.pub(公钥)
    3. 将公钥复制到要免登录的机器上scp id_rsa.pub 192.168.1.111:/home
    4. 将公钥复制到密钥列表cat ~/id_rsa.pub >> ./authorized_keys
    5. 若没有authorized_keys文件,则自己新建touch authorized_keys,并改权限为600
    6. 验证是否成功:ssh localhost,首页登录需要密码确认,yes即可

17. 首次启动hadoop

    1. hdfs namenode -format
    2. 若是有错误或没有启动成功,需要再次format的时候,需要先进入namenode和datanode文件夹,删除里面的current文件夹,否则会出现namespaceid不一致的问题

18. 启动start-dfs.sh,输入jps查看会显示DataNode,NameNode,SecondaryNameNode,少了就重新format.若出现有些程序已经启动,则先要kill -9 进程号,结束这些进程

19. 启动start-yarn.sh,输入jps查看,会比上一个多显示NodeManager和ResouceManager

20. 访问192.168.1.146:8088和192.168.1.146:50070,若能出现网站表示成功;若需要访问jobhistory,需要命令mapred historyserver,之后在页面访问192.168.1.146:19888