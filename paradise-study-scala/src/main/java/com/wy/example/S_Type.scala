package com.wy.example

object S_Type {
  for (obj <- Array(42, "42", BigInt(42), BigInt, 42.0)) {

    val result = obj match {
      case x: Int => x
      case s: String => s.toInt
      case _: BigInt => Int.MaxValue
      case BigInt => -1
      case _ => 0
    }
    println(result)
  }

  // Map(42 -> "Fred")也映射到Map[String, Int],显然不对,运行期已经没有类型信息
  for (obj <- Array(Map("Fred" -> 42), Map(42 -> "Fred"), Array(42), Array("Fred"))) {
    val result = obj match {
      // 会有报警,但不是错误
      //      case m: Map[String, Int] => "It's a Map[String, Int]"
      // Warning: Won't work because of type erasure
      //      case m: Map[_, _] => "It's a map"
      case a: Array[Int] => "It's an Array[Int]"
      case a: Array[_] => "It's an array of something other than Int"
    }
    println(result)
  }

  // 匹配数组
  for (arr <- Array(Array(0), Array(1, 0), Array(0, 1, 0), Array(1, 1, 0))) {

    val result = arr match {
      case Array(0) => "0"
      case Array(x, y) => x + " " + y
      case Array(0, _*) => "0 ..."
      case _ => "something else"
    }

    println(result)
  }

  // 匹配列表
  for (lst <- Array(List(0), List(1, 0), List(0, 0, 0), List(1, 0, 0))) {

    val result = lst match {
      case 0 :: Nil => "0"
      case x :: y :: Nil => x + " " + y
      case 0 :: tail => "0 ..."
      case _ => "something else"
    }
    println(result)
  }

  // 匹配元组
  for (pair <- Array((0, 1), (1, 0), (1, 1))) {
    val result = pair match {
      case (0, _) => "0 ..."
      case (y, 0) => y + " 0"
      case _ => "neither is 0"
    }

    println(result)
  }
}