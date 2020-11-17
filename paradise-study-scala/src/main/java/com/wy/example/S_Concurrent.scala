package com.wy.example

object S_Concurrent {

  (1 to 5).par.foreach { it => println(Thread.currentThread); print("^" + it) }

  (1 to 5).par.map(_ + 100).seq
}