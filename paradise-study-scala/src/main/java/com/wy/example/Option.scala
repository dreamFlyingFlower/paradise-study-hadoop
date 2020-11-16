package com.wy.example

/**
 * 类似于Java8中的Optional类
 */
object Option {

  def main(args: Array[String]): Unit = {
    val scores = Map("Alice" -> 1729, "Fred" -> 42)

    // match-case等价于Java中的switch-case,没有break,默认值用_
    scores.get("Alice") match {
      case Some(score) => println(score)
      case None => println("No score")
      case _ => println("defualt value")
    }

    val alicesScore = scores.get("Alice")
    if (alicesScore.isEmpty) {
      println("No score")
    } else println(alicesScore.get)
    // 没有值的时候输出其他值
    println(alicesScore.getOrElse("No score"))
    // 等价于上面几行
    println(scores.getOrElse("Alice", "No score"))
    for (score <- scores.get("Alice")) println(score)
    scores.get("Alice").foreach(println _)
  }
}