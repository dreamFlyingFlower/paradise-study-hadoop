package com.wy.example4

object Inter {

  def valueAtOneQuarter(f: (Double) => Double) = f(0.25)

  // 传入函数表达式
  valueAtOneQuarter((x: Double) => 3 * x)
  // 参数推断省去类型信息
  valueAtOneQuarter((x) => 3 * x)
  //  单个参数可以省去括号
  valueAtOneQuarter(x => 3 * x)
  // 如果变量旨在=>右边只出现一次，可以用_来代替
  valueAtOneQuarter(3 * _)

  val fun2 = 3 * (_: Double) // OK
  val fun3: (Double) => Double = 3 * _ // OK
  // Error: Can’t infer types
  //  val fun1 = 3 * _
}