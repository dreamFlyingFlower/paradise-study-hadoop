package com {
  package sec02 {
    object Utils {
      def percentOf(value: Double, rate: Double) = value * rate / 100
    }
    package impatient {
      class Employee(initialSalary: Double) {
        private var salary = initialSalary
        def description = "An employee with salary " + salary
        def giveRaise(rate: scala.Double) {
          salary += Utils.percentOf(salary, rate)
          // Ok to access Utils from parent package
        }
      }
    }
  }
}

object Sec02Main extends App {
  val fred = new com.sec02.impatient.Employee(50000)
  fred.giveRaise(10)
  println(fred + ": " + fred.description)

  val wilma = new com.sec02.impatient.Manager
  wilma.subordinates += fred
  wilma.subordinates += new com.sec02.impatient.Employee(50000)
  println(wilma + ": " + wilma.description)
}
