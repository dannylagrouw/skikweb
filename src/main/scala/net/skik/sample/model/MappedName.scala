package net.skik.sample.model

//TODO find out how to use a case class for this
class MappedName {

  var first: String = _
  var middle: String = _
  var last: String = _
  
  override def toString = first + " " + middle + " " + last
}
