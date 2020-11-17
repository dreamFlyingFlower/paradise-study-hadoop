package com.wy.example

object S_Array {
  // 定长数组
  val nums = new Array[Int](10)

  val s = Array("Hello", "World")
  s(0) = "Goodbye"
  print(s)

  // 变长 数组缓冲
  import scala.collection.mutable.ArrayBuffer

  val b = ArrayBuffer[Int]()
  val b2 = new ArrayBuffer[Int] // If you use new, the () is optional

  b += 1
  b += (1, 2, 3, 5)
  b ++= Array(8, 13, 21)

  // 删除最后5个元素
  b.trimEnd(5)
  print(b)

  // 在第三个位置插入6
  b.insert(2, 6)
  print(b)

  // 在第三个位置插入 7、8、9
  b.insert(2, 7, 8, 9)
  print(b)

  // 删除第三个元素
  b.remove(2)
  print(b)
  // 删除从第三个位置开始的3个元素
  b.remove(2, 3)
  print(b)

  // 变长数组与不变数组之间的转换
  val a1 = b.toArray
  a1.toBuffer

  // 数组遍历
  val a = Array(1, 1, 2, 3, 5, 8, 13, 21, 34, 55)

  //  下标访问
  for (i <- 0 until a.length)
    println(i + ": " + a(i))

  // 产生一个Range
  0 until a.length

  // 产生一个Range，以2为间隔
  0 until (a.length, 2)

  // Range倒转
  (0 until a.length).reverse

  // a 遍历
  for (elem <- a)
    println(elem)

  // a 索引
  for (i <- a.indices)
    println(i + ": " + a(i))

  /** 多维数组 */
  // 多维数组 3行4列
  val matrix = Array.ofDim[Double](3, 4)

  val row = 0
  val column = 2

  matrix(0)(2) = 17.29

  matrix.length
  matrix(row) // An array
  matrix(row).length

  val triangle = new Array[Array[Int]](10)

  for (i <- triangle.indices)
    triangle(i) = new Array[Int](i + 1)

  triangle(0)(0) = 1

  for (i <- 1 until triangle.length) {
    triangle(i)(0) = 1
    triangle(i)(i) = 1
    for (j <- 1 until triangle(i).length - 1)
      triangle(i)(j) = triangle(i - 1)(j - 1) + triangle(i - 1)(j)
  }

  for (row <- triangle) {
    for (elem <- row) print(elem + " ")
    println()
  }

  /** Array Transform */
  val an = Array(2, 3, 5, 7, 11)
  // 产生新的数组
  val result = for (elem <- an) yield 2 * elem
  for (elem <- an if elem % 2 == 0) yield 2 * elem
  an.filter(_ % 2 == 0).map(2 * _)
  // 常用方法
  import scala.collection.mutable.ArrayBuffer
  // 求和
  Array(1, 7, 2, 9).sum
  //最大排序
  ArrayBuffer("Mary", "had", "a", "little", "lamb").max
  val bn = ArrayBuffer(1, 7, 2, 9)
  //排序
  val bSorted = bn.sorted
  val a3 = Array(1, 7, 2, 9)
  scala.util.Sorting.quickSort(a1)
  print(a3)
  // 形成String
  a3.mkString(" and ")
  a3.mkString("<", ",", ">")
  a3.toString
  bn.toString
  import scala.collection.mutable.ArrayBuffer
  // 更多方法
  val a2 = Array(1, -2, 3, -4, 5)
  val b3 = ArrayBuffer(1, 7, 2, 9)
  // 统计大于0的个数
  a2.count(_ > 0)
  b3.append(1, 7, 2, 8)
  print(b3)
  b3.appendAll(a2)
  print(b3)
  b3 += 4 -= 7
  print(b3)
  //将b2中的数据copy到a2中，a2的空间为5
  b3.copyToArray(a2)
  print(a2)
  val xs = Array(1, "Fred", 2, 9)
  b3.copyToArray(xs)
  print(xs)
  print(b3)
  // 加到20项，用-1填充
  b3.padTo(20, -1)
  (1 to 10).padTo(20, -1) // Note that the result is a Vector, not a Range

  /** 和Java互操作 */
  // 和Java的互操作

  import scala.collection.JavaConverters._
  import scala.collection.mutable
  import scala.collection.mutable.ArrayBuffer

  val command = ArrayBuffer("ls", "-al", "/")
  // ProessBuilder是java方法
  val pb = new ProcessBuilder(command.asJava) // Scala to Java

  val cmd: mutable.Buffer[String] = pb.command().asScala // Java to Scala

  cmd == command
}