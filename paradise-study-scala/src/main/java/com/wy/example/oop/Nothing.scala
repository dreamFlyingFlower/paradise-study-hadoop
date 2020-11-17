abstract class Amount

case class Dollar(value: Double) extends Amount
case class CurrencyScala(value: Double, unit: String) extends Amount

case object Nothing extends Amount {

  val dollar = Dollar(1000.0)
  print(dollar.value);
  dollar.toString
  for (amt <- Array(Dollar(1000.0), CurrencyScala(1000.0, "EUR"), Nothing)) {
    val result = amt match {
      case Dollar(v) => "$" + v
      case CurrencyScala(_, u) => "Oh noes, I got " + u
      case Nothing => ""
    }
    // Note that amt is printed nicely, thanks to the generated toString
    println(amt + ": " + result)
  }

  // copy
  val amt = CurrencyScala(29.95, "EUR")
  val price = amt.copy(value = 19.95)
  println(price)
  println(amt.copy(unit = "CHF"))
}