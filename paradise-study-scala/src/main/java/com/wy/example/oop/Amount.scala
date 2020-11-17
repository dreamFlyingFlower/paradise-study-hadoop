package com.wy.example.oop

sealed abstract class Amount

case class Dollar(value: Double) extends Amount
case class CurrencyScala(value: Double, unit: String) extends Amount

object Main extends App {
  def show(amt: Amount) =
    amt match {
      case CurrencyScala(v, u) => "I have " + v + " " + u
      case _ => print("no value")
    }
  println(show(CurrencyScala(100000, "JPY")))
}