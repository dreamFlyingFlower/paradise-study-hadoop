# Azkaban



# 概述

* 工作流调度系统,和oozie类似,更轻量级
* 一个完整的数据分析系统通常都是由大量任务单元组成:shell,Java程序,MR程序,Hive等
* 各任务单元之间存在时间先后及前后依赖关系
* 为了很好地组织起这样的复杂执行计划,需要一个工作流调度系统来调度执行



# 特性

* 可视化界面
* 简单的工作流上传
* 方便设置任务之间的关系
* 调度工作流
* 模块化和可插拔的插件机制
* 认证/授权
* 能够杀死并重新启动工作流
* 有关失败和成功的电子邮件提醒
* 工作流和任何的日志记录和审计



# 各种调度框架

| 特性          | Hamake               | Oozie              | Azkaban           | Cascading |
| ------------- | -------------------- | ------------------ | ----------------- | --------- |
| 描述语言      | XML                  | XML                | key/value  file   | Java API  |
| 依赖机制      | data-driven          | explicit           | explicit          | explicit  |
| 是否要web容器 | No                   | Yes                | Yes               | No        |
| 进度跟踪      | log messages         | web page           | web page          | Java API  |
| HadoopJob支持 | no                   | yes                | yes               | yes       |
| 运行模式      | command line utility | daemon             | daemon            | API       |
| Pig支持       | yes                  | yes                | yes               | yes       |
| 事件通知      | no                   | no                 | no                | yes       |
| 需要安装      | no                   | yes                | yes               | no        |
| 支持的hadoop  | 0.18+                | 0.20+              | currently unknown | 0.18+     |
| 重试支持      | no                   | workflownode  evel | yes               | yes       |
| 运行任意命令  | yes                  | yes                | yes               | yes       |
| AmazonEMR支持 | yes                  | no                 | currently unknown | yes       |



## Azkaban和Oozie

* Oozie相比Azkaban更重量级,功能全面,但配置复杂
* 功能
  * 两者均可以调度mapreduce,pig,java,shell脚本工作流任务
  * 两者均可以定时执行工作流任务
* 工作流定义
  * Azkaban使用Properties文件定义工作流
  * Oozie使用XML文件定义工作流
* 工作流传参
  * Azkaban支持直接传参,例如${input}
  * Oozie支持参数和EL表达式,例如${fs:dirSize(myInputDir)}
* 定时执行
  * Azkaban的定时执行任务是基于时间的
  * Oozie的定时执行任务基于时间和输入数据
* 资源管理
  * Azkaban有较严格的权限控制,如用户对工作流进行读/写/执行等操作
  * Oozie暂无严格的权限控制
* 工作流执行
  * Azkaban有两种运行模式,分别是:
    * solo server mode:executor server和web server部署在同一台节点
    * multi server mode:executor server和web server可以部署在不同节点
  * Oozie作为工作流服务器运行,支持多用户和多工作流
* 工作流管理
  * Azkaban支持浏览器以及ajax方式操作工作流
  * Oozie支持命令行,HTTP REST,Java API,浏览器操作工作流







# 安装

* 将Azkaban Web服务器,Azkaban执行服务器和MySQL拷贝到hadoop102虚拟机/opt/software目录下
* 解压上述文件到/opt/module/azkaban中并安装MySQL
* 在/opt/module/目录下创建azkaban目录
* 将azkaban-web改名为server,azkaban-executor改名为executor
* azkaban脚本导入:进入mysql,创建azkaban数据库,并将解压的脚本导入到azkaban数据库
* 创建SSL配置,并进行进群间时间同步配置
* 进入azkaban web服务器安装目录conf目录,打开azkaban.properties文件
* 按照如下配置修改azkaban.properties文件

```properties
# Azkaban Personalization Settings
# 服务器UI名称,用于服务器上方显示的名字
azkaban.name=Test
# 描述
azkaban.label=My Local  Azkaban
# UI颜色
azkaban.color=#FF3601
azkaban.default.servlet.path=/index
# 默认根web目录
web.resource.dir=web/
# 默认时区,已改为亚洲/上海 默认为美国
default.timezone.id=Asia/Shanghai
# Azkaban UserManager class
# 用户权限管理默认类
user.manager.class=azkaban.user.XmlUserManager
# 用户配置,具体配置参加下文
user.manager.xml.file=conf/azkaban-users.xml
#Loader for projects
# global配置文件所在位置
executor.global.properties=conf/global.properties
azkaban.project.dir=projects
# 数据库类型及相关配置
database.type=mysql
mysql.port=3306
mysql.host=hadoop102
mysql.database=azkaban
mysql.user=root
mysql.password=000000
# 最大连接数
mysql.numconnections=100
# Velocity dev mode
velocity.dev.mode=false
# Jetty服务器属性
# 最大线程数
jetty.maxThreads=25
# Jetty SSL端口
jetty.ssl.port=8443
jetty.port=8081
# SSL文件名
jetty.keystore=keystore
# SSL文件密码
jetty.password=000000
# Jetty主密码与keystore文件相同
jetty.keypassword=000000
# SSL文件名
jetty.truststore=keystore
# SSL文件密码
jetty.trustpassword=000000
# 执行服务器端口属性
executor.port=12321
# 邮件设置
mail.sender=xxxxxxxx@163.com
mail.host=smtp.163.com
mail.user=xxxxxxxx
mail.password=**********
# 任务失败时发送邮件的地址
job.failure.email=xxxxxxxx@163.com
# 任务成功时发送邮件的地址
job.success.email=xxxxxxxx@163.com
lockdown.create.projects=false
# 缓存目录
cache.directory=cache
```

* web服务器用户配置,azkaban-users.xml,增加管理员用户

```xml
<azkaban-users>
    <user username="azkaban" password="azkaban"  roles="admin" groups="azkaban" />
    <user username="metrics" password="metrics"  roles="metrics"/>
    <user username="admin"  password="admin" roles="admin,metrics" />
    <role name="admin" permissions="ADMIN" />
    <role name="metrics" permissions="METRICS"/>
</azkaban-users>
```

* 配置executor目录下的azkaban.properties

```properties
# Azkaban时区
default.timezone.id=Asia/Shanghai
# Azkaban JobTypes 插件配置所在位置
azkaban.jobtype.plugin.dir=plugins/jobtypes
# Loader for projects
executor.global.properties=conf/global.properties
azkaban.project.dir=projects
# 数据库设置,数据库类型(目前只支持mysql)
database.type=mysql
mysql.port=3306
mysql.host=192.168.20.200
mysql.database=azkaban
mysql.user=root
mysql.password=000000
mysql.numconnections=100
# 执行服务器配置,最大线程数
executor.maxThreads=50
# 端口号(如修改,请与web服务中一致)
executor.port=12321
# 线程数
executor.flow.threads=30
```

* 启动web服务器,在azkaban/server下执行启动命令:bin/azkaban-web-start.sh
* 启动执行服务器,在azkaban/executor下执行命令:bin/azkaban-executor-start.sh
* 启动完成后,在浏览器中输入https://服务器IP:8443,即可访问azkaban服务



# 案例

> Azkaba内置的任务类型支持Command,Java



## Command

### 单job

* 创建job描述文件

```shell
vi command.job
#command.job
type=command
command=echo 'hello'  
```

* 将job资源文件打包成zip文件
* 通过azkaban的web管理平台创建project并上传job压缩包
* 启动执行该Job



### 多job

* 创建有依赖关系的多个job描述

```shell
# 第一个job:foo.job
# foo.job
type=command
command=echo foo
# 第二个job：bar.job依赖foo.job
# bar.job  
type=command
dependencies=foo
command=echo bar
```

* 将所有job资源文件打到一个zip包中
* 创建工程
* 在azkaban的web管理界面创建工程并上传zip包
* 启动工作流flow
* 查看结果



## HDFS

* 创建job描述文件

```shell
# fs.job
type=command
command=/opt/module/hadoop-2.7.2/bin/hadoop fs -mkdir /azkaban
```

* 将job资源文件打包成zip文件
* 通过azkaban的web管理平台创建project并上传job压缩包
* 启动执行该job
* 查看结果



## MapReduce

* MR任务依然可以使用command的job类型来执行
* 创建job描述文件及MR程序jar包,可以直接使用hadoop自带的example.jar

```shell
# mrwc.job
type=command
command=/opt/module/hadoop-2.7.2/bin/hadoop jar hadoop-mapreduce-examples-2.7.2.jar wordcount /wordcount/input /wordcount/output
```

* 将所有job资源文件打到一个zip包中
* 在azkaban的web管理界面创建工程并上传zip包
* 启动job



## HIVE

* 创建job描述文件和hive脚本

```shell
# Hive脚本:test.sql
use default;
drop table aztest;
create table aztest(id int,name string) row format delimited fields terminated by ',';
load data inpath '/aztest/hiveinput' into table aztest;
create table azres as select * from aztest;
insert overwrite directory '/aztest/hiveoutput' select count(1) from aztest;
```

```shell
# Job描述文件:hivef.job
type=command
command=/opt/module/hive/bin/hive -f 'test.sql'
```

* 将所有job资源文件打到一个zip包中
* 在azkaban的web管理界面创建工程并上传zip包
* 启动job