package com.viewfunction.docg.dataAnalyze.util.spark.spatial

object SpatialPredicateType extends Enumeration{
  type SpatialPredicateType = Value
  val Contains = Value("Contains")
  val Intersects = Value("Intersects")
  val Within = Value("Within")
  val Equals = Value("Equals")
  val Crosses = Value("Crosses")
  val Touches = Value("Touches")
  val Overlaps = Value("Overlaps")

  def checkExists(item:String) = this.values.exists(_.toString==item)
  def showAll = this.values.foreach(println)
}









