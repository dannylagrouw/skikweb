package net.skik.model

abstract class Query(mode: QueryMode.Value) {

  var tableFields = Map.empty[String, Array[String]]
  
  var conditions = Conditions.empty
  
  var orderClause: Option[String] = None
  
  def table(name: String) = {
    tableFields += name -> Array("*")
    this
  }
  
  def fields(names: String*) = {
    tableFields(tableFields.last._1) = tableFields.last._1 -> names.toArray
    this
  }

  def where(conditions: Conditions) = {
    this.conditions = conditions
    this
  }

  def whereClause: String = conditions.toSql
  
  def whereArgs: Array[Any] = if (conditions.hasNamedArgs)
    Array[Any]()
  else
    conditions.args.map(_.value)

  def order(orderClause: Option[String]) = {
    orderClause match {
      case Some(o) => this.orderClause = Some(" order by " + o)
    }
    this
  }
    
  def toSql: String
  
}

object Order {
  def apply(order: String) = Some(order)
}

case class Conditions(val clause: Option[String], val args: Array[WhereArg]) {
  def isEmpty = clause.isEmpty && args.isEmpty
  def hasNamedArgs = args.find(_.isNamed).isDefined
  def toSql = clause match {
    case Some(c) => " where " + (if (hasNamedArgs) replaceArgs else c)
    case None if (hasNamedArgs) => " where " + clauseFromArgs
    case None => ""
  }
  def replaceArgs =
    (clause.get /: args) ((c, a) => c.replaceAll(":" + a.nameStr, a.valueAsSql))
  
  def clauseFromArgs =
    args.map(a => a.nameStr + " = " + a.valueAsSql).mkString(" and ")
}

object Conditions {
  def empty = new Conditions(None, Array[WhereArg]())
  def apply(args: Map[Symbol, Any]) = new Conditions(None, args.toArray.map(a => WhereArg(a._1, a._2)))
  def apply(clause: String, args: Map[Symbol, Any]) = new Conditions(Some(clause), args.toArray.map(a => WhereArg(a._1, a._2)))
  def apply(clause: String, args: Any*) = new Conditions(Some(clause), args.toArray.map(WhereArg(_)))
}

case class WhereArg(val name: Option[Symbol] = None, val value: Any) {
  def isNamed = name.isDefined
  def nameStr = name match {
    case Some(symbol) => symbol.name
    case None => ""
  }
  
  /**
   * Returns the value suitable for use in SQL. E.g. Strings are
   * surrounded with single quotes.
   * 
   * TODO Date etc
   */
  def valueAsSql: String = value match {
    case v: String => "'" + v + "'"
    case _ => value.toString
  }
}

object WhereArg {
  def apply(value: Any) = new WhereArg(None, value)
  def apply(name: Symbol, value: Any) = new WhereArg(Some(name), value)
}
