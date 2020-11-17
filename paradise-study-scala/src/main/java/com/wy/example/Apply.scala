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

  /** unapply */
  val arr1 = Array(0, 1)

  val Array(x, y) = arr1

  val Array(z, _*) = arr1

  arr1 match {
    case Array(0, x) => x
  }

  Array.unapplySeq(arr)

  // 正则表达式对象
  val pattern = "([0-9]+) ([a-z]+)".r

  "99 bottles" match {
    case pattern(num, item) => (num.toInt, item)
  }
  // 直接unapplySeq
  pattern.unapplySeq("99 bottles")

  // unapply 提取值
  object Name {
    def unapply(input: String): Option[(String, String)] = {
      val pos = input.indexOf(" ")
      if (pos == -1) None
      else Some((input.substring(0, pos), input.substring(pos + 1)))
    }
  }

  val author = "Cay Horstmann"

  val Name(first, last) = author // calls Name.unapply(author)
  print(first,last);

  // 匹配上了
  Name.unapply(author)
  // 没有匹配上
  Name.unapply("Anonymous")

  // 单个提取
  object Number {
    def unapply(input: String): Option[Int] =
      try {
        Some(input.trim.toInt)
      } catch {
        case ex: NumberFormatException => None
      }
  }

  val Number(n) = "1729"

  // boolean测试,判断Horstmann
  object IsCompound {
    def unapply(input: String) = { println(input); !input.contains(" ") }
  }

  author match {
    case Name(first, IsCompound()) => println("compound")
    // Matches if the author is Peter van der Linden
    case Name(first, last) => println("simple")
  }

  // Use @ to bind an identifier to the match
  // 相当于一个守卫
  author match {
    case Name(first, last @ IsCompound()) =>
      println(last); last.split("\\s+").length
    // Matches if the author is Peter van der Linden
    case Name(first, last) => 1
  }

  // unapplySeq 提取序列
  object NameSeq {
    def unapplySeq(input: String): Option[Seq[String]] =
      if (input.trim == "") None else Some(input.trim.split("\\s+"))
  }

  val authorseq = "Peter van der Linden"

  // 将提取的序列与模式进行数量和字段上的比较。
  authorseq match {
    case NameSeq(first, last) => authorseq
    case NameSeq(first, middle, last) => first + " " + last
    case NameSeq(first, "van", "der", last) => "Hello Peter!"
  }
}