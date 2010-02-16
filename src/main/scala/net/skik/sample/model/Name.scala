package net.skik.sample.model

//TODO find out how to use a case class for this
class Name {

  var first_name: String = _
  var middle_name: String = _
  var last_name: String = _
  
  override def toString = first_name + " " + middle_name + " " + last_name
}
