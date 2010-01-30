package net.skik.model

trait BaseHelper[T] {

  def apply(values: Map[Symbol, Any])(implicit baseClass: Manifest[T]): T = {
    baseClass.erasure.newInstance.asInstanceOf[T]
  }

}
