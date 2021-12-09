package com.viewfunction.docg.analysisProvider.fundamental.spatial

object SpatialDirectionType extends Enumeration {
  type SpatialDirectionType = Value
  val LeftOf = Value("LeftOf")
  val RightOf = Value("RightOf")
  val TopOf = Value("TopOf")
  val UnderOf = Value("UnderOf")

  def checkExists(item: String) = this.values.exists(_.toString == item)

  def showAll = this.values.foreach(println)
}
