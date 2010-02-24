package net.skik.sample.model

case class ListOfFive(listAsString: String) {

  var list: List[String] = listAsString.split(",").toList
  
  def list_of_five = list.mkString(",")
  
  override def toString = list_of_five
}

