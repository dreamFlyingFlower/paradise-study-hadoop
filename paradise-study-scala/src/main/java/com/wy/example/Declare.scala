package com.wy.example

/**
 * 基本语法,每一句后面的分号可带可不带,除非特殊情况,如if中多种情况写一行时
 */
object Declare {
  // val不能改变,自动推断类型,相当于Java中的final
  val answer = 8 * 5 + 2
  0.5 * answer
  // 编译不会通过,包裹
  // answer = 0

  // 手动定义类型
  val greeting: String = null
  // var值可以改变
  var counter = 0
  counter = 1;

  // 多值声明
  var i, j = 0
  var greeting2, message: String = null

  // 多值匹配赋值
  val (z, y) = (1, 2) // z=1,y=2

  val (q, r) = BigInt(10) /% 3

  val arr = Array(1, 7, 2, 9)

  val Array(first, second, _*) = arr

  // 隐式转换成RichInt
  var s = 1.toString();
  print(s);
  1.to(10)

  // String 隐式转换成 StringOps
  "Hello".intersect("World")

  // 类型之间的转换需要通过方法进行,没有强制转化
  var num = 99.04.toInt
  print(num);
  val m = null
  // 错误,基本类型不可以定义为null
  //  val n:Int=null;
  //  val x :Boolean = null;

  // Scala风格
  "Hello" intersect "World" // Can omit . and () for binary method
  "Hello".intersect("World")

  // 没有++,--操作符
  var counter1 = 0
  counter1 += 1 // Increments counter—Scala has no ++

  // 提供BigInt和BigDecimal对象来处理大数据
  val x: BigInt = 1234567890
  x * x * x

  // 随时导包,_效果等同于Java中的*
  import scala.math._
  // 函数调用
  sqrt(2)
  pow(2, 4)
  min(3, Pi)

  // 方法调用,去重
  "Hello".distinct

  // 静态方法调用
  BigInt.probablePrime(100, scala.util.Random)
}