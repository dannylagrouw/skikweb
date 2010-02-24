package net.skik.sample.model

//TODO find out how to use a case class for this
case class Name(var first_name: String, var middle_name: String, var last_name: String) {
  
  override def toString = first_name + " " + middle_name + " " + last_name
}
