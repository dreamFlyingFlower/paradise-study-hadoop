package com.atguigu.scala.ch12.sec03

import scala.language.implicitConversions
import scala.math._

class Fraction(n: Int, d: Int) {
  val num: Int = if (d == 0) 1 else n * sign(d) / gcd(n, d);
  val den: Int = if (d == 0) 0 else d * sign(d) / gcd(n, d);
  override def toString = num + "/" + den
  def sign(a: Int) = if (a > 0) 1 else if (a < 0) -1 else 0
  def gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)

  def *(other: Fraction) = new Fraction(num * other.num, den * other.den)
}

object Fraction {
  def apply(n: Int, d: Int) = new Fraction(n, d)
  implicit def int2Fraction(n: Int) = Fraction(n, 1)
}

// 伴生对象中的隐式转换函数
object FractionConversions {
    implicit def fraction2Double(f: Fraction) = f.num * 1.0 / f.den
  }


object Main extends App {
  import FractionConversions._

  println(3 * Fraction(4, 5)) // Calls fraction2Double(Fraction(4, 5))
  //println(3.den) // Calls int2Fraction(3)
  println(Fraction(4, 5) * 3) // Calls int2Fraction(3)
}



