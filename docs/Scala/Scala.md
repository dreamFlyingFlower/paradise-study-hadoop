# Scala



# Eclipse使用Scala

* 从应用市场添加Scala-IDE插件
* 新建一个maven项目,完成之后右键项目->configure->add scala nature
* 需要安装Scala,和JDK一样
* Java语言的脚本化



# 语法



## 基础

* 代码在一行时分号可写可不写;若是多行语句在一行则必须写;
* 没有++操作符,但是有+=
* +的功能和Java中类似,但是有区别,Scala中+号其实是一个方法
* 导包:一样是import,但是导入某个包下所有类是import scala.math._,不是*
* 调用方法时,若是该方法无参,可以不用写(),如:"str".toString



## for

```scala
// 1 to 10从1到10的值,<-,将循环的值赋值给i,i不需要定义
// to代表闭区间[],即1和10都在循环范围内;until是[),前闭后开,不包含10
// 若是被循环的为字符串,表示将字符串中的每一个字符赋值个i
for(i <- 1 to 10){
    print(i); // 1,2,3,4,5,6,7,8,9,10
}
// 循环字符串
for(i <- "adgds"){
    print(i); // a,d,g,d,s
}
// 多个变量循环,且每个循环还可以带条件,循环从前往后嵌套,和Java的多层for循环一样
for(i <- 1 to 3;j <- 1 to 3 if i!=j){
    print(ij); // 12,13,21,23,31,32
}
```

* scala中没有break和continue,只能通过Breaks类的break方法跳出循环,需要导包
* scala中所有的表达式都有返回值,若是无返回值的,返回Unit,即()
* 若是在if中可能返回多种类型的值,则接收该返回值的变量的类型是Any,相当于java的object



## yield

* 在for循环中使用,可以将需要另外处理的结果值放入到一个集合中

  ```scala
  for (x <- 1 to 10 ) yield x % 2 ;//Vector(1,0,1,0,1,0,1,0,1,0)
  ```



## lazy

* 延迟加载,在类型检查上没用,主要是加载io流时,可以在调用的时候才读文件

  ```scala
  // 若文件存在,会立刻输出文件中的内容
  val l=scala.io.Source.fromFile("d:/text.txt").mkString();
  // 不会读文件内容,只会有一个表达式
  lazy val l = scala.io.Source.fromFile("d:/text.txt").mkString();
  // 使用时才输出文件内容
  l;
  ```

  



# 变量

* 定义变量时可不指定类型,编译器会自动推断.也可以指定类型
  * val ttt=1:定义一个不可变的变量,类型需要自定推断,和Java的final一样
  * val ttt:Int=11:定义一个不可变的变量,类型指定为int
  * val ttt:Any=100:定义一个不可变的变量,类型可以是任意类型,即使推断为int也可以后期更改
* val:修饰变量不可变,类似java的final,常量.定义变量时可不指定

* var:修饰变量可变



# 数据类型

* 和Java数据类型一样
* 不区分基本类型和class,都是class,因此可以直接访问方法
  * val ttt=1;print(1.toString());
* 



## Int

* 1 to 10:等价于1.to(10),表示从1到10的值,类似for循环的1到10



## String

* "str".distinct:字符串去重,无参方法可以不用写()
* "str"(1):等价于"str".apply(4),取得字符串中指定下标的值,下标从0开始
* 



## Array



###  定长

```scala
// 一个int类型数组,指定长度为10
var x=new Array[Int](10);
// int类型数组,长度为2.类型是推断出来的.提供初始化就不需要new
var x=Array(1,2);
// 访问,和Java中不一样
x(1);
```



### 可变

```scala
// 可变数组ArrayBuffer,类似Java的ArrayList
import scala.collection.mutable.ArrayBuffer
val buf = ArrayBuffer[Int]();
//+=在末尾追加
buf += 1
buf += (1,2,3,4)
//操纵集合,在末尾追加一个集合
buf ++= Array(3,4,5,6)
//trimEnd,从开始或末尾移除指定个数个元素
buf.trimStart(2)
buf.trimEnd(2)
//insert,在0元素位置插入后续数据.第一个参数为数组下标,后面所有的都是要加入的元素
buf.insert(0,1,2)
//remove按照索引移除
buf.remove(0)
//toArray
buf.toArray
```



### 其他操作

```scala
// 数组操作
for (x <- buf if x % 2 ==0) yield x * 2
// 直接利用过滤方法,_表示集合中的每一个元素,filter是过滤,map是将再次处理
buf.filter(_ % 2 ==0).map(_ * 2)
var a = Array(1 to 10)
// 数组常用方法
a.sum
a.min
a.max
// 排序
val arr = Array(1,4,3,2);
// 数组升序排序,不会改变arr,会生成一个新的数组
arr.sorted(_ < _);
// 降序排序
arr.sorted(_>_);
// 快速排序
import scala.util.Sorting._
// 该方法只能对定长数组排序,且会改变数组本身
quickSort(arr);
// Array.mkString:第一个参数是前缀,中间的参数是分隔符,最后参数是后缀
// 当只有一个参数时,默认为分隔符
arr.mkString(","); // 1,2,3,4
arr.mkString("<<",",",">>"); // <<1,2,3,4>>;
// 二维数组,3行4列
val arr = Array.ofDim[Int](3,4);
// 下标访问数组元素
arr(0)(1);
arr.length;
// 和java对象交互，导入转换类型,使用的隐式转换
import scala.collection.JavaConversions.bufferAsJavaList
val buf = ArrayBuffer(1,2,3,4);
val list:java.util.List[Int] = buf;
```



## Map

```scala
// scala.collection.immutable.Map[Int,String] =不可变集合
val map = Map(100->"tom",200->"tomas",300->"tomasLee")
// 通过key访问value
map(100)
val newmap = map + (4->"ttt")
// 可变
val map = new scala.collection.mutable.HashMap[Int,Int]
val map = scala.collection.mutable.HashMap[Int,Int]()
map += (1->100,2->200); //追加
map -= 8; //移除元素
// 迭代map
for ((k,v)<- map) println(k + ":::" + v);
// 使用yield操作进行倒排序(kv对调),
for ((k,v)<- map) yield (v,k);
```



## Tuple

```scala
// 元组tuple,元数最多22-->Tuple22
val t = (1,"tom",12);
// 访问元组指定元
t._2
// 直接取出元组中的各分量
val (a,b,c) = t; //a=1,b="tom",c=12
// 数组的zip,
// 西门庆 -> 潘金莲  牛郎 -> 侄女
val hus = Array(1,2,3);
val wife = Array(4,5,6);
hus.zip(wife); //(1,4),(2,5),(3,6)
```



# 函数



## 基础

* 方法是在对象上,而函数不是,函数定义指定名称,参数和body,必须指定参数类型

* 定义函数的关键字为def,返回值若无特殊情况,可不指定

  ```scala
  def fun(n:Int,m:String)={ // 没有指定返回类型
  	var i=1;
  	for(j <- 1 to 10){
  		i = j*i;
  	}
      i; // 最后的表达式是函数的返回值
  }
  ```

* 函数必须先指定才能调用,调用直接写方法名加参数即可

* 递归函数必须指定返回类型

  ```scala
  def fun(n:Int,m:String):Int={ // 指定返回类型
      var i=1;
      for(j <- 1 to 10){
          i = j*i;
      }
      i; // 最后的表达式是函数的返回值
  }
  ```

* 指定参数和默认值,和Python一样.若不指定默认值,但是调用时参数个数不等,会报错

  ```scala
  def fun(n:Int,m:String="ttt"):Int={ // 指定返回类型
      var i=1;
      for(j <- 1 to 10){
          i = j*i;
      }
      i; // 最后的表达式是函数的返回值
  }
  fun(1); //n为1,m为默认值ttt
  fun(1,"ddd"); // n为1,m为ddd
  fun(m="fds",n=2); // n为2,m为dfs
  ```

* 变长参数

  ```scala
  def sum(n:Int*):Int={ // 指定返回类型
      var re=0;
      for(nt <- n){
          re+=nt;
          // head指第一个参数,tail是除head的其他参数,是一个seq,序列
          n.head + sum(n.tail:_*); 
      }
      re;
  }
  val s = sum(1,3,4);
  val s =sum(1 to 5); // 错误
val s = sum(1 to 5:_*); // 固定写法,若是想传递for循环
  ```
  
  

## ReadLine

* readLine("Str"):从控制台接收一个参数,Str为提示语言
* 还有readInt,readByte等各种基本类型值读取,但是不能带提示语参数



## Option

* 类似Java8中的Optional





# 过程

* procedure,同函数,但是没有返回值,没有等号.但本质上仍然是函数

  ```scala
  def fun(n:Int,m:String){ // 没有等号
  	var i=1;
  	for(j <- 1 to 10){
  		i = j*i;
  	}
      i; // 不同于函数,该值不返回
  }
  def fun(n:Int,m:String):Unit = { // 等同于上面的写法
  	var i=1;
  	for(j <- 1 to 10){
  		i = j*i;
  	}
      i; // 不同于函数,该值不返回
  }
  ```

  

# 异常

* 在使用形式上同Java

  ```scala
  try{
  	"str".toInt;
  }catch{
  	case _:Exception =>print("error"); // _表示任意变量,这里是所有异常
      case ex:java.io.IOException =>print("io exception"); // 指定异常名
  }
  ```



# OOP

```scala
// 类不声明public,多个类可以在一个文件中,且都是public的
// 声明
class Person{
    //定义变量,私有类型,必须初始化
    //set/get也私有
    private var id = 0 ;
    //只有get方法,没有set方法
    val age = 100 ;
    //生成私有属性,和公有的get/set方法
    var name = "tom";
    //默认public
    def incre(a:Int) = {id += a ;}
    // 使用+
    def +() = {id + 1};
    //如果定义时,没有(),调用就不能加(),该方法直接返回id的值
    def current() = id 
}
// 调用
var p = new Person();
p.current()
p.current
p.incr(100)
p.name // 获取name的值,相当于java中的get
p.name_=("kkkk"); // 修改name的值,使用_=,相当于java中的set
p.name = "kkkk";
// private[this]作用,控制成员只能在自己的对象中访问
class Counter{
    private[this] var value =  0;
    def incre(n:Int){value += n}
    def isLess(other:Counter) = value < other.value;
}
// 定义BeanProperty注解,可以使该字段和Java规范一样生成getter/setter方法
class Person{
    @scala.reflect.BeanProperty
    var name:String  = _
}
// 构造函数:主构造器,辅助构造
class Person{
    var id = 1 ;
    var name = "tom" ;
    var age = 12;
    //辅助构造
    def this(name:String){
        this(); // 调用主构造
        this.name = name;
    }
    //辅助构造
    def this(name:String,age:Int){
        //调用辅助构造
        this(name);
        this.age = age;
    }
}
// 主构造:参数直接放在类后面.val:只读,var:get/set,没有指定var或val的为none:none
// none类型在没有调用的时候不会在编译文件中生成成员变量,但是一旦有调用
// 会在class文件中直接生成val类型的成员变量
class Person(val name:String,var age:Int,id:Int){
    // 若类中调用了其他方法,会在new的时候直接执行一次,所有的方法都会执行一次
    println("fdsf");
    // 会在class文件中自动添加一个val类型的id成员变量
    def hello() = println(id);
}

// scala没有静态的概念,如果需要定义静态成员,可以通过object实现,类似Java中的静态类
// 编译完成后,会生成2个class文件,一个是同名class文件,一个是带$后缀的文件
// 生成的clas类中方法都是静态方法,非静态成员对应到单例类中,单例类以Util$作为类名称
object Util{
    //单例类中.(Util$)
    private var brand = "benz" ;
    //静态方法.
    def hello() = println("hello world");
}
// 伴生对象(companions object):类名和object名称相同,而且必须在一个scala文件中定义
// 类和它的伴生对象可以相互访问私有特性
class Car{
    def stop() = println("stop....")
}
// object Car是class Car的伴生对象.
object Car{
    def run() = println("run...")
}
// trait:特质,等价于java中的接口
// 抽象类
abstract class Dog(var Str:String){
    // 定义一个抽象方法,无返回值
    def a():Unit
}
// 继承抽象类,若抽象类带有参构造,子类也要构造
class DogSon extends Dog("构造"){
    override def a(){};
}
// 对象也可以继承抽象类,重写的方法可以直接用静态方式调用
object DogSon1 extends Dog("构造"){
    override def a(){};
}
// object等价于java中的静态
object Jing8 extends Dog{
    //重写方法
    override def a():Unit= print("hello") ;
}
// object(arg1...argn):通常这样事故apply方法返回的是伴生类的对象
object Util{
    def apply(s:String) = println(s) ;
}
// 以下2个调用等价
Util("hello world");
Util.apply("hello world");

// scalac编译scala文件,产生class文件
scalac xxxx.scala
// 要想运行scala文件,必须有main方法,而且必须写在伴生类中.除非是在代码中直接调用main
scala Person
scala Person.scala
// trait
traint HelloService{
}
// 包对象,编译完之后生成以xxx为package,下面含有类package.class + package.class
package object xxxx{
}
// 约束可见性
private[package|this]
// 导入
import java.io.Exception
import java.io.{A,B,C};
import java.io.{A => A0}; // 别名
```



