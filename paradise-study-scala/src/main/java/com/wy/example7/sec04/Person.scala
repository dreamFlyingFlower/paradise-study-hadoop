package com.sec04

package object people {
  val defaultName = "John Q. Public"
}

object Sec04Main extends App {
  val john = new com.sec04.people.Person
  println(john.description)
}

package people{
  private[sec04] class Person {
    var name = defaultName // A constant from the package
    private[sec04] def description = "A person with name " + name
    private[people] def description2 = "A person with name " + name
  }
}

