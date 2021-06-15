package com.viewfunction.docg.analysisProvider.feature.spark.spatial

object SpatialOrderType extends Enumeration{
  type SpatialOrderType = Value
  val DESC = Value("DESC")
  val ASC = Value("ASC")

  def checkExists(item:String) = this.values.exists(_.toString==item)
  def showAll = this.values.foreach(println)
}
