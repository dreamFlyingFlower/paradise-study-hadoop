package com.wy.example

sealed abstract class Amount

case class Dollar(value: Double) extends Amount
case class Currency(value: Double, unit: String) extends Amount

object Main extends App {
  def show(amt: Amount) =
    amt match {
      case Currency(v, u) => "I have " + v + " " + u
      case _ => print("no value")
    }
  println(show(Currency(100000, "JPY")))
}