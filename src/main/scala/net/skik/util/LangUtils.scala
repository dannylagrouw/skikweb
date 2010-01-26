package net.skik.util

object LangUtils {
  
  def withDo[A <: Any, B](a: A)(f: A => B): B = {
    f(a)
  }

}
