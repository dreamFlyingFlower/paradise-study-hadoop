package com.wy.example.oop

abstract class Item
case class Article(description: String, price: Double) extends Item
case class Bundle(description: String, discount: Double, items: Item*) extends Item

object S_Nest {
  val special = Bundle("Father's day special", 20.0,
    Article("Scala for the", 39.95),
    Bundle("Anchor Distillery Sampler", 10.0,
      Article("Old Potrero Straight Rye Whiskey", 79.95),
      Article("Junípero Gin", 32.95)))

  special match {
    case Bundle(_, _, Article(descr, _), _*) => descr
  }

  special match {
    case Bundle(_, _, art @ Article(_, _), rest @ _*) => (art, rest)
  }

  special match {
    case Bundle(_, _, art @ Article(_, _), rest) => (art, rest)
  }

  // 计算物品价格
  def price(it: Item): Double = it match {
    case Article(_, p) => p
    case Bundle(_, disc, its @ _*) => its.map(price _).sum - disc
  }

  price(special)
}