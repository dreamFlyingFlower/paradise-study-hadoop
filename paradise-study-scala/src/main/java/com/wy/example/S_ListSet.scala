package com.wy.example

object S_ListSet {
  /** list */
  val digits = List(4, 2)
  // 右结合操作符
  9 :: List(4, 2)
  // Nil表示空列表
  9 :: 4 :: 2 :: Nil
  9 :: (4 :: (2 :: Nil))
  def sum(lst: List[Int]): Int = if (lst == Nil) 0 else lst.head + sum(lst.tail)
  //  def sum(lst: List[Int]): Int = lst match {
  //    case Nil => 0
  //    case h :: t => h + sum(t) // h is lst.head, t is lst.tail
  //  }
  sum(List(9, 4, 2))
  sum(List(9, 4, 2))
  List(9, 4, 2).sum

  /** set */
  var numberSet = Set(1, 2, 3)
  numberSet += 5 // Sets numberSet to the immutable set numberSet + 5
  print(numberSet)
  Set(1, 2, 3) - 2
  Set(1, 2, 3) + 1
  // 无序访问
  for (x <- Set(1, 2, 3, 4, 5, 6)) print(x)

  /** iterator */
  // 切片操作返回一个迭代器
  val iter1 = (1 to 10).sliding(3)

  while (iter1.hasNext)
    println(iter1.next())

  val iter2 = (1 to 10).sliding(3)

  for (elem <- iter2)
    println(elem)

  val iter3 = (1 to 10).sliding(3)

  // 执行length操作，会消费迭代器
  println(iter3.length)

  println(iter3.hasNext) // The iterator is now consumed

  val iter4 = (1 to 10).sliding(3)

  // 返回迭代器数组
  iter4.toArray
}