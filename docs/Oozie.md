# Oozie



# 概述

* 一个基于工作流引擎的开源框架,提供对Hadoop MapReduce,Pig Jobs的任务调度与协调
* Oozie需要部署到Java Servlet容器中运行
* Oozie在集群中扮演的角色:定时调度任务,多任务可以按照执行的逻辑顺序调度
* Oozie的功能模块
  		* Workflow:顺序执行流程节点,支持fork(分支多个节点),join(合并多个节点为一个)
  * Coordinator:定时触发workflow
  * Bundle Job:绑定多个Coordinator
* Oozie的常用节点
  		* 控制流节点(Control Flow Nodes):控制流节点一般都是定义在工作流开始或者结束的位置,比如start,end,kill等,以及提供工作流的执行路径机制,如decision,fork,join等
  * 动作节点(Action  Nodes):就是执行具体任务动作的节点,比如执行某个shell脚本



# 编译

* [官网](http://www.apache.org/dyn/closer.lua/oozie/)下载oozie源码

* 配置JDK,Maven,Pig

* 修改oozie源码中的pom.xml,根据服务器上hadoop等其他软件的版本进行修改

  ```xml
  <targetJavaVersion>1.8</targetJavaVersion>
  <hadoop.version>2.7.2</hadoop.version>
  <hadoop.majorversion>2</hadoop.majorversion>
  <pig.version>0.13.0</pig.version>
  <maven.javadoc.opts>-Xdoclint:none</maven.javadoc.opts>
  <spark.version>2.1.1</spark.version>
  <sqoop.version>1.99.7</sqoop.version>
  <hive.version>1.2.2</hive.version>
  <hbase.version>1.3.1</hbase.version>
  ```

* 编译

  ```shell
  bin/mkdistro.sh -Phadoop-2 -Dhadoop.auth.version=2.7.2 -Ddistcp.version=2.7.2 -Dhadoop.version=2.7.2 -Dsqoop.version=1.99.7 -DskipTests
  bin/mkdistro.sh -DskipTests -Phadoop-2 -Dhadoop.version=2.7.2 
  ```

  

# 安装

* 上传解压Oozie到指定目录:

  ```shell
  tar -zxf /opt/softwares/oozie-4.0.0-cdh5.3.6.tar.gz -C /opt/modules/cdh/
  ```

* Hadoop配置文件修改,core-site.xml,完成后scp到其他机器节点

  ```xml
  <!-- Oozie服务的Hostname -->
  <property>
  	<name>hadoop.proxyuser.admin.hosts</name>
  	<value>*</value>
  </property>
  <!-- 允许被Oozie代理的用户组 -->
  <property>
  	<name>hadoop.proxyuser.admin.groups</name>
  	<value>*</value>
  </property>
  ```

* 配置JobHistoryServer(必须),完成后scp到其他机器节点:

  ```xml
  <!-- mapred-site.xml -->
  <!-- 配置 MapReduce JobHistory地址,默认端口10020 -->
  <property>
  	<name>mapreduce.jobhistory.address</name>
  	<value>hadoop-senior01.itguigu.com:10020</value>
  </property>
  <!-- 配置 MapReduce JobHistory web ui 地址,默认端口19888 -->
  <property>
  	<name>mapreduce.jobhistory.webapp.address</name>
  	<value>hadoop-senior01.itguigu.com:19888</value>
  </property>
  <!-- yarn-site.xml -->
  <!-- 任务历史服务 -->
  <property>
  	<name>yarn.log.server.url</name>
  	<value>http://hadoop-senior01.itguigu.com:19888/jobhistory/logs/</value>
  </property>
  ```

* 开启Hadoop集群:sh ~/start-cluster.sh,需要配合开启JobHistoryServer

* 解压hadooplibs,完成后Oozie目录下会出现hadooplibs目录

  ```shell
  tar -zxf /opt/modules/cdh/oozie-4.0.0-cdh5.3.6/oozie-hadooplibs-4.0.0-cdh5.3.6.tar.gz -C /opt/modules/cdh/
  ```

* 在Oozie目录下创建libext目录:mkdir libext

* 拷贝一些依赖的Jar包和Js

  * 将hadooplibs里面的jar包,拷贝到libext目录下
  * 拷贝Mysql驱动包到libext目录下
  * 将ext-2.2.zip(一个js框架)拷贝到libext/目录下

* 修改Oozie配置文件:oozie-site.xml

  ```xml
  <!-- MySQL配置 -->
  <property> 
  	<name>oozie.service.JPAService.jdbc.driver</name>
  	<value>com.mysql.jdbc.Driver</value>
  </property>
  <property> 
  	<name>oozie.service.JPAService.jdbc.url</name>
  	<value>jdbc:mysql://192.168.122.20:3306/oozie</value>
  </property>
  <property> 
  	<name>oozie.service.JPAService.jdbc.username</name>
  	<value>root</value>
  </property>
  <property> 
  	<name>oozie.service.JPAService.jdbc.password</name>
  	<value>123456</value>
  </property>
  <!-- 让Oozie引用Hadoop的配置文件 -->
  <property> 
  	<name>oozie.service.HadoopAccessorService.hadoop.configurations</name>
  	<value>/opt/modules/cdh/hadoop-2.5.0-cdh5.3.6/etc/hadoop</value>
  </property>
  ```

* 在MySQL中创建Oozie的数据库

* 上传Oozie目录下的yarn.tar.gz文件到HDFS,yarn.tar.gz会自动解压

  ```shell
  bin/oozie-setup.sh sharelib create -fs hdfs://hadoop-senior01.itguigu.com:8020 -locallib oozie-sharelib-4.0.0-cdh5.3.6-yarn.tar.gz
  ```

* 执行oozie.sql文件

  ```shell
  bin/oozie-setup.sh db create -run -sqlfile oozie.sql
  ```

* 打包项目,生成war包

  ```shell
  bin/oozie-setup.sh prepare-war
  ```

* 启动Oozie,访问Web操作页面

  ```shell
  bin/oozied.sh start
  # http://hadoop-senior01.itguigu.com:11000/oozie
  ```



# 案例

## 调度Shell脚本

* 解压官方案例模板,创建工作目录,拷贝任务模板到oozie-apps目录

  ```shell
  tar -zxf oozie-examples.tar.gz
  mkdir oozie-apps/
  cp -r examples/apps/shell/ oozie-apps/
  ```

* 随意编写一个shell脚本test1.sh,并将该脚本复制到oozie-apps/shell目录下

* 修改job.properties

  ```properties
  # HDFS地址
  nameNode=hdfs://hadoop-senior01.itguigu.com:8020
  # ResourceManager地址
  jobTracker=hadoop-senior02.itguigu.com:8032
  # 队列名称
  queueName=default
  examplesRoot=oozie-apps
  # 指定需要执行的脚本目录
  oozie.wf.application.path=${nameNode}/user/${user.name}/${examplesRoot}/shell
  # 需要执行的脚本
  EXEC=p1.sh
  ```

* workflow.xml

  ```xml
  <workflow-app xmlns="uri:oozie:workflow:0.4" name="shell-wf">
      <!-- to:下一步的流程,由标签的name属性决定 -->
  	<start to="shell-node"/>
      <!-- name为标签的唯一标识,决定流程的走向 -->
  	<action name="shell-node">
          <shell xmlns="uri:oozie:shell-action:0.2">
              <job-tracker>${jobTracker}</job-tracker>
              <name-node>${nameNode}</name-node>
              <configuration>
                  <property>
                      <name>mapred.job.queue.name</name>
                      <value>${queueName}</value>
                  </property>
              </configuration>
              <exec>${EXEC}</exec>
              <!-- <argument>my_output=Hello Oozie</argument> -->
              <!-- 注意要执行脚本的前面有个# -->
              <file>/user/root/oozie-apps/shell/${EXEC}#${EXEC}</file>
          	<capture-output/>
      	</shell>
          <!-- 成功或失败的流程 -->
          <ok to="end"/>
          <error to="fail"/>
  	</action>
      <decision name="check-output">
          <switch>
              <case to="end">
              ${wf:actionData('shell-node')['my_output'] eq 'Hello Oozie'}
              </case>
              <default to="fail-output"/>
          </switch>
      </decision>
      <kill name="fail">
      	<message>
              Shell action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]
          </message>
      </kill>
      <kill name="fail-output">
  	    <message>
          Incorrect output, expected [Hello Oozie] but was [${wf:actionData('shell-node')['my_output']}]
          </message>
      </kill>
      <end name="end"/>
  </workflow-app>
  ```

* 上传任务配置

  ```shell
  /opt/modules/cdh/hadoop-2.5.0-cdh5.3.6/bin/hdfs dfs -put /opt/modules/cdh/oozie-4.0.0-cdh5.3.6/oozie-apps /user/admin
  ```

* 执行任务

  ```shell
  bin/oozie job -oozie http://hadoop-senior01.itguigu.com:11000/oozie -config oozie-apps/shell/job.properties -run
  ```

* kill某个任务

  ```shell
  bin/oozie job -oozie http://hadoop-senior01.itguigu.com:11000/oozie -kill 0000004-170425105153692-oozie-z-W
  ```



## 执行多个Job

* 解压官方案例模板

* 修改job.properties

  ```properties
  nameNode=hdfs://hadoop-senior01.itguigu.com:8020
  jobTracker=hadoop-senior02.itguigu.com:8032
  queueName=default
  examplesRoot=oozie-apps
  oozie.wf.application.path=${nameNode}/user/${user.name}/${examplesRoot}/shell
  EXEC1=p1.sh
  EXEC2=p2.sh
  ```

* workflow.xml

  ```xml
  <workflow-app xmlns="uri:oozie:workflow:0.4" name="shell-wf">
      <start to="p1-shell-node"/>
      <action name="p1-shell-node">
          <shell xmlns="uri:oozie:shell-action:0.2">
              <job-tracker>${jobTracker}</job-tracker>
              <name-node>${nameNode}</name-node>
              <configuration>
                  <property>
                      <name>mapred.job.queue.name</name>
                      <value>${queueName}</value>
                  </property>
              </configuration>
              <exec>${EXEC1}</exec>
              <file>/user/root/oozie-apps/shell/${EXEC1}#${EXEC1}</file>
              <!-- <argument>my_output=Hello Oozie</argument>-->
              <capture-output/>
          </shell>
          <ok to="p2-shell-node"/>
          <error to="fail"/>
      </action>
      <action name="p2-shell-node">
          <shell xmlns="uri:oozie:shell-action:0.2">
              <job-tracker>${jobTracker}</job-tracker>
              <name-node>${nameNode}</name-node>
              <configuration>
                  <property>
                      <name>mapred.job.queue.name</name>
                      <value>${queueName}</value>
                  </property>
              </configuration>
              <exec>${EXEC2}</exec>
              <file>/user/root/oozie-apps/shell/${EXEC2}#${EXEC2}</file>
              <!-- <argument>my_output=Hello Oozie</argument>-->
              <capture-output/>
          </shell>
          <ok to="end"/>
          <error to="fail"/>
      </action>
      <decision name="check-output">
          <switch>
              <case to="end">
                  ${wf:actionData('shell-node')['my_output'] eq 'Hello Oozie'}
              </case>
              <default to="fail-output"/>
          </switch>
      </decision>
      <kill name="fail">
          <message>
              Shell action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]
          </message>
      </kill>
      <kill name="fail-output">
          <message>
              Incorrect output, expected [Hello Oozie] but was [${wf:actionData('shell-node')['my_output']}]
          </message>
      </kill>
      <end name="end"/>
  </workflow-app>
  ```

* 上传任务配置

* 需要注意每个脚本是在哪台机器上执行的,脚本的执行时间以及脚本执行时间和本地时间不一致



## 调度MR任务

* 先编写一个可以运行的MR任务jar包

* 下载官方模板到oozie-apps目录下

* 配置job.properties

  ```properties
  nameNode=hdfs://hadoop-senior01.itguigu.com:8020
  jobTracker=hadoop-senior02.itguigu.com:8021
  queueName=default
  examplesRoot=oozie-apps
  # hdfs://hadoop-senior01.itguigu.com:8020/user/admin/oozie-apps/map-reduce/workflow.xml
  oozie.wf.application.path=${nameNode}/user/${user.name}/${examplesRoot}/map-reduce/workflow.xml
  outputDir=map-reduce
  ```

* 配置workflow.xml

  ```xml
  <workflow-app xmlns="uri:oozie:workflow:0.2" name="map-reduce-wf">
      <start to="mr-node"/>
      <action name="mr-node">
          <map-reduce>
              <job-tracker>${jobTracker}</job-tracker>
              <name-node>${nameNode}</name-node>
              <prepare>
                  <delete path="${nameNode}/output/"/>
              </prepare>
              <configuration>
                  <property>
                      <name>mapred.job.queue.name</name>
                      <value>${queueName}</value>
                  </property>
                  <!-- 配置调度MR任务时,使用新的API -->
                  <property>
                      <name>mapred.mapper.new-api</name>
                      <value>true</value>
                  </property>
                  <property>
                      <name>mapred.reducer.new-api</name>
                      <value>true</value>
                  </property>
                  <!-- 指定Job Key输出类型 -->
                  <property>
                      <name>mapreduce.job.output.key.class</name>
                      <value>org.apache.hadoop.io.Text</value>
                  </property>
                  <!-- 指定Job Value输出类型 -->
                  <property>
                      <name>mapreduce.job.output.value.class</name>
                      <value>org.apache.hadoop.io.IntWritable</value>
                  </property>
                  <!-- 指定输入路径 -->
                  <property>
                      <name>mapred.input.dir</name>
                      <value>/input/</value>
                  </property>
                  <!-- 指定输出路径 -->
                  <property>
                      <name>mapred.output.dir</name>
                      <value>/output/</value>
                  </property>
                  <!-- 指定Map类 -->
                  <property>
                      <name>mapreduce.job.map.class</name>
                      <value>
                          org.apache.hadoop.examples.WordCount$TokenizerMapper
                      </value>
                  </property>
                  <!-- 指定Reduce类 -->
                  <property>
                      <name>mapreduce.job.reduce.class</name>
                      <value>
                          org.apache.hadoop.examples.WordCount$IntSumReducer
                      </value>
                  </property>
                  <property>
                      <name>mapred.map.tasks</name>
                      <value>1</value>
                  </property>
              </configuration>
          </map-reduce>
          <ok to="end"/>
          <error to="fail"/>
      </action>
      <kill name="fail">
          <message>
              Map/Reduce failed, error message[${wf:errorMessage(wf:lastErrorNode())}]
          </message>
      </kill>
      <end name="end"/>
  </workflow-app>
  ```

* 拷贝待执行的jar包到map-reduce的lib目录下

  ```shell
  cp -a /opt/modules/cdh/oozie-4.0.0-cdh5.3.6/hadoop-mapreduce-examples-2.5.0-cdh5.3.6.jar ./
  ```

* 上传配置好的app文件夹到HDFS

  ```shell
  /opt/modules/cdh/hadoop-2.5.0-cdh5.3.6/bin/hdfs dfs -put map-reduce/ /user/admin/oozie-apps
  ```

* 执行任务

  ```shell
  bin/oozie job -oozie http://hadoop-senior01.itguigu.com:11000/oozie -config oozie-apps/map-reduce/job.properties -run
  ```



## Coordinator周期性调度

* 配置Linux时区,oozie允许的最小执行任务的频率是5分钟

* 修改oozie-default.xml文件,涉及属性

  ```xml
  <property>
      <name>oozie.processing.timezone</name>
      <value>GMT+0800</value>
  </property>
  ```

* 修改js框架中的关于时间设置的代码

  ```shell
  vi /opt/modules/cdh/oozie-4.0.0-cdh5.3.6/oozie-server/webapps/oozie/oozie-console.js
  ```

  ```javascript
  function getTimeZone() {
      Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
      return Ext.state.Manager.get("TimezoneId","GMT+0800");
  }
  ```

* 重启oozie服务:bin/oozied.sh restart,清除缓存并重启浏览器

* 拷贝官方模板配置定时任务

  ```shell
  cp -r /opt/modules/cdh/oozie-4.0.0-cdh5.3.6/examples/apps/cron/ /opt/modules/cdh/oozie-4.0.0-cdh5.3.6/oozie-apps/
  ```

* 修改job.properties

  ```properties
  nameNode=hdfs://hadoop-senior01.itguigu.com:8020
  jobTracker=hadoop-senior02.itguigu.com:8032
  queueName=default
  examplesRoot=oozie-apps
  oozie.coord.application.path=${nameNode}/user/${user.name}/${examplesRoot}/apps/cron
  #start：必须设置为未来时间,否则任务失败
  start=2017-07-29T17:00+0800
  end=2017-07-30T17:00+0800
  workflowAppUri=${nameNode}/user/${user.name}/${examplesRoot}/cron
  EXEC1=p1.sh
  EXEC2=p2.sh
  ```

* 修改coordinator.xml

  ```xml
  <coordinator-app name="cron-coord" frequency="${coord:minutes(10)}" start="${start}" end="${end}" timezone="GMT+0800" xmlns="uri:oozie:coordinator:0.2">
      <action>
          <workflow>
              <app-path>${workflowAppUri}</app-path>
              <configuration>
                  <property>
                      <name>jobTracker</name>
                      <value>${jobTracker}</value>
                  </property>
                  <property>
                      <name>nameNode</name>
                      <value>${nameNode}</value>
                  </property>
                  <property>
                      <name>queueName</name>
                      <value>${queueName}</value>
                  </property>
              </configuration>
          </workflow>
      </action>
  </coordinator-app>
  ```

* 修改workflow.xml

  ```xml
  <workflow-app xmlns="uri:oozie:workflow:0.5" name="one-op-wf">
      <start to="p1-shell-node"/>
      <action name="p1-shell-node">
          <shell xmlns="uri:oozie:shell-action:0.2">
              <job-tracker>${jobTracker}</job-tracker>
              <name-node>${nameNode}</name-node>
              <configuration>
                  <property>
                      <name>mapred.job.queue.name</name>
                      <value>${queueName}</value>
                  </property>
              </configuration>
              <exec>${EXEC1}</exec>
              <file>/user/admin/oozie-apps/shell/${EXEC1}#${EXEC1}</file>
              <!-- <argument>my_output=Hello Oozie</argument>-->
              <capture-output/>
          </shell>
          <ok to="p2-shell-node"/>
          <error to="fail"/>
      </action>
      <action name="p2-shell-node">
          <shell xmlns="uri:oozie:shell-action:0.2">
              <job-tracker>${jobTracker}</job-tracker>
              <name-node>${nameNode}</name-node>
              <configuration>
                  <property>
                      <name>mapred.job.queue.name</name>
                      <value>${queueName}</value>
                  </property>
              </configuration>
              <exec>${EXEC2}</exec>
              <file>/user/admin/oozie-apps/shell/${EXEC2}#${EXEC2}</file>
              <!-- <argument>my_output=Hello Oozie</argument>-->
              <capture-output/>
          </shell>
          <ok to="end"/>
          <error to="fail"/>
      </action>
      <decision name="check-output">
          <switch>
              <case to="end">
                  ${wf:actionData('shell-node')['my_output'] eq 'Hello Oozie'}
              </case>
              <default to="fail-output"/>
          </switch>
      </decision>
      <kill name="fail">
          <message>Shell action failed, error message[${wf:errorMessage(wf:lastErrorNode())}]</message>
      </kill>
      <kill name="fail-output">
          <message>Incorrect output, expected [Hello Oozie] but was [${wf:actionData('shell-node')['my_output']}]</message>
      </kill>
      <end name="end"/>
  </workflow-app>
  ```

* 上传配置,启动任务

  ```shell
  # 上传配置
  /opt/modules/cdh/hadoop-2.5.0-cdh5.3.6/bin/hdfs dfs -put oozie-apps/cron/ /user/admin/oozie-apps
  # 启动任务
  bin/oozie job -oozie http://hadoop-senior01.itguigu.com:11000/oozie -config oozie-apps/cron/job.properties -run
  ```

  

# 问题总结

* Mysql权限配置
* workflow.xml配置的时候不要忽略file属性
* jps查看进程时,注意有没有bootstrap
* 关闭oozie,如果无法关闭,则可以使用kill,oozie-server/temp/xxx.pid文件一定要删除
* Oozie重新打包时,一定要注意先关闭进程,删除对应文件夹下面的pid文件
* 配置文件一定要生效,配置文件的属性写错了,那么则执行默认的属性
* libext下边的jar存放于某个文件夹中,导致share/lib创建不成功
* -rmr share/lib这样是不行的,rm -rmr /user/admin这样删除是错误的
* 调度任务时,找不到指定的脚本,可能是oozie-site.xml里面的Hadoop配置文件没有关联上
* 修改Hadoop配置文件,需要重启集群,一定要记得scp到其他节点
* JobHistoryServer必须开启,集群要重启的
* Mysql配置如果没有生效的话,默认使用derby数据库
* 在本地修改完成的job配置,必须重新上传到HDFS
* 将HDFS上面的配置文件,下载下来查看是否有错误
* Linux用户名和Hadoop的用户名不一致