package net.skik.model

abstract class Query(mode: QueryMode.Value) {

  var tableFields = Map.empty[String, Array[String]]

  var select = Select.empty

  var conditions = Conditions.empty
  
  var order = Order.empty
  
  var limit = Limit.empty
  
  var offset = Offset.empty
  
  var readonly = Readonly(false)
  
  var group = Group.empty
  
  def table(name: String) = {
    tableFields += name -> Array("*")
    this
  }
  
  def fields(names: String*) = {
    tableFields(tableFields.last._1) = tableFields.last._1 -> names.toArray
    this
  }

  def whereArgs: Array[Any] = if (conditions.hasNamedArgs)
    Array[Any]()
  else
    conditions.args.map(_.value)

  def toSql: String
  
}

trait QueryClause {
  def toSql: String
}

class Order(val clause: Option[String]) extends QueryClause {
  def toSql = clause match {
    case Some(o) => " order by " + o
    case _ => ""
  }
}

object Order {
  val empty = new Order(None)
  def apply(order: String) = new Order(Some(order))
}

class Group(val clause: Option[String]) extends QueryClause {
  def toSql = clause match {
    case Some(o) => " group by " + o
    case _ => ""
  }
}

object Group {
  val empty = new Group(None)
  def apply(group: String) = new Group(Some(group))
}

class Select(val clause: Option[String]) extends QueryClause {
  def toSql = clause match {
    case Some(s) => s
	case _ => "*"
  }
}

object Select {
  val empty = new Select(None)
  def apply(select: String) = new Select(Some(select))
}

class Readonly(val readonly: Boolean) extends QueryClause {
  def toSql = ""
}

object Readonly {
  def apply(readonly: Boolean) = new Readonly(readonly)
}

class Conditions(val clause: Option[String], val args: Array[WhereArg]) extends QueryClause {
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

class Limit(limit: Option[Int]) extends QueryClause {
  def toSql = limit match {
    case Some(l) => " limit " + l
    case _ => ""
  }
}

object Limit {
  def empty = new Limit(None)
  def apply(limit: Int) = new Limit(Some(limit))
}

class Offset(offset: Option[Long]) extends QueryClause {
  def toSql = offset match {
    case Some(o) => " offset " + o
    case _ => ""
  }
}

object Offset {
  def empty = new Offset(None)
  def apply(offset: Long) = new Offset(Some(offset))
}
