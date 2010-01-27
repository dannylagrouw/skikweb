package net.skik.model

object QueryMode extends Enumeration {

  val FindAll = Value
  val FindFirst = Value

  def apply(mode: Symbol) = mode match {
    case 'all => FindAll
    case 'first => FindFirst
    case _ => throw new IllegalArgumentException("Unknown QueryMode " + mode.name)
  }
  
}
