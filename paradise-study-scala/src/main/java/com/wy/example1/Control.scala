package com.wy.example1

object control {

  /**if-else */
  val x = 0
  // if else 语句结构
  if (x > 0) 1 else -1
  // 可以有返回值
  val s = if (x > 0) 1 else -1
  // 类型不一样时，返回公共超类型Any
  if (x > 0) "positive" else -1
  // 如果没有值，则返回Unit，类似于Java 的Void
  if (x > 0) 1
  if (x > 0) 1 else ()
  //  需不需要分号
  var n = 10
  var r = 1
  // 如果多行表达式卸载一行需要分号，单个表达式可以不用大括号
  if (n > 0) { r = r * n; n -= 1 }
  var s1, s0, v, v0, t, a, a0 = 0.0
  s1 = s0 + (v - v0) * t + // The + tells the parser that this is not the end
    0.5 * (a - a0) * t * t
  // 不需要分号,{}的值就是最后一个表达式的值
  if (n > 0) {
    r = r * n
    n -= 1
  }

  /** for */
  // 多个变量生成器
  for (i <- 1 to 3; j <- 1 to 3) print((10 * i + j) + " ")
  // 守卫模式
  for (i <- 1 to 3; j <- 1 to 3 if i != j) print((10 * i + j) + " ")
  // 多个守卫模式
  for (i <- 1 to 3; j <- 1 to 3 if i != j if j < 2) print((10 * i + j) + " ")
  // 引入变量
  for (i <- 1 to 3; from = 4 - i; j <- from to 3) print((10 * i + j) + " ")
  // 使用变量
  for (i <- 1 to 3; from = 4 - i) print(from + " ")
  // () 变成{}  都可以
  for {
    i <- 1 to 3
    from = 4 - i
    j <- from to 3
  } print((10 * i + j) + " ")
  // yield 将输出到集合，这种类型叫for推导式
  val result = for (i <- 1 to 10) yield i % 3
  print(result);
  // for推导式返回的类型和第一个生成器的类型是兼容的
  for (c <- "Hello"; i <- 0 to 1) yield (c + i).toChar
  for (i <- 0 to 1; c <- "Hello") yield (c + i).toChar

  /**while */
  var z = 1
  var y = 10
  // while 循环
  while (n > 0) {
    z = z * y
    y -= 1
    println(r)
  }

  z = 1
  y = 10

  // do while 循环
  do {
    z = z * y
    y -= 1
    println(z)
  } while (y > 0)

  import scala.util.control.Breaks._

  z = 1
  y = 10
  // break
  breakable {
    while (y > 0) {
      z = z * y
      y -= 1
      println(z)
      if (z == 720) break;
    }
  }

  /** switch-case */
  var sign = 0
  for (ch <- "+-!") {

    ch match {
      case '+' => sign = 1
      case '-' => sign = -1
      case _ => sign = 0
    }

    println(sign)
  }

  for (ch <- "+-!") {

    sign = ch match {
      case '+' => 1
      case '-' => -1
      case _ => 0
    }
    println(sign)
  }

  import java.awt._

  val color = SystemColor.textText
  color match {
    case Color.RED => "Text is red"
    case Color.BLACK => "Text is black"
    case _ => "Not red or black"
  }

  //如果匹配，则把字符转换成10进制。
  for (ch <- "+-3!") {
    var sign = 0
    var digit = 0

    ch match {
      case '+' => sign = 1
      case '-' => sign = -1
      case _ if Character.isDigit(ch) => digit = Character.digit(ch, 10)
      case _ => sign = 0
    }

    println(ch + " " + sign + " " + digit)
  }

  // 返回str的一组索引Range
  val str = "+-3!"
  for (i <- str.indices) {
    var sign = 0
    var digit = 0

    str(i) match {
      case '+' => sign = 1
      case '-' => sign = -1
      case ch if Character.isDigit(ch) => digit = Character.digit(ch, 10)
      case _ =>
    }

    println(str(i) + " " + sign + " " + digit)
  }

  import scala.math._
  val ran = random
  ran match {
    case Pi => "It's Pi"
    case _ => "It's not Pi"
  }
  // 变量必须以小写字母开头，常量用大写字母，如果常量用小写字母开头需要加反引号。
  import java.io.File._
  str match {
    case `pathSeparator` => "It's the path separator"
    case _ => "It's not the path separator"
  }
}