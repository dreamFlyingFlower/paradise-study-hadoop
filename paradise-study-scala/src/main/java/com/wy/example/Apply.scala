package com.wy.example

/**
 * 每种类型自带的apply和update
 */
object Apply {
  //获取字符
  "Hello"(4)

  //apply获取字符
  "Hello".apply(4)

  //声明数组
  val arr = Array(1, 2, 3)

  //apply声明数组
  Array.apply(1, 2, 3)
  BigInt("1234567890")
  BigInt.apply("1234567890")
  BigInt("1234567890") * BigInt("112358111321")

  //更新值
  arr(1) = 3
  print(arr)
  //update方法更新值
  arr.update(1, 4)
  print(arr)
}