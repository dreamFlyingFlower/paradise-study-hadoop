package com.wy.example.oop

object S_Partial {
  val f: PartialFunction[Char, Int] = { case '+' => 1; case '-' => -1 }
  f('-')
  f.isDefinedAt('0')
  f('0')
}