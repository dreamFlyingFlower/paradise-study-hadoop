package com.sec03

// 定义在父包
package object people {
  val defaultName = "John Q. Public"
}

package people{
  class Person {
    var name = defaultName // A constant from the package
    def description = "A person with name " + name
  }
}

object Sec03Main extends App {
  val john = new com.sec03.people.Person
  println(john.description)
}
