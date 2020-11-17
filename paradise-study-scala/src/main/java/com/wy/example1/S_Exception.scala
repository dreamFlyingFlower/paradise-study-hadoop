package com.wy.example1

object S_Exception {
  import java.io.IOException

  import scala.math._
  // 异常的为Nothing
  def root(x: Double) =
    if (x >= 0) {
      sqrt(x)
    } else throw new IllegalArgumentException("x should not be negative")

  try {
    println(root(4))
    println(root(-4))
  } catch {
    case e: Exception => println(e)
  } finally {
    println("finally....")
  }
}