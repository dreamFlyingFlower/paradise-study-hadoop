# Scala



# Eclipse使用Scala

* 从应用市场添加Scala-IDE插件
* 新建一个maven项目,完成之后右键项目->configure->add scala nature



# 语法

* 没有++操作符,但是有+=
* +的功能和Java中类似,但是有区别,Scala中+号其实是一个方法



# 变量

* 定义变量时可不指定类型,编译器会自动推断.也可以指定类型
  * val ttt=1:定义个不可变的变量,类型需要自定推断
  * val ttt:Int=11:定义一个不可变的变量,类型指定为int
  * val ttt:Any=100:定义一个不可变的变量,类型可以是任意类型,即使推断为int也可以后期更改
* val:修饰变量不可变,类似java的final,常量.定义变量时可不指定

* var:修饰变量可变\



# 数据类型

* 和Java数据类型一样
* 不区分基本类型和class,都是class,因此可以直接访问方法
  * val ttt=1;print(1.toString());