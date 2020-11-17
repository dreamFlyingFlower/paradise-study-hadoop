package com.wy.example4

object Closure {
  def mulBy(factor: Double) = (x: Double) => factor * x

  val triple = mulBy(3)
  val half = mulBy(0.5)
  println(triple(14) + " " + half(14))
}