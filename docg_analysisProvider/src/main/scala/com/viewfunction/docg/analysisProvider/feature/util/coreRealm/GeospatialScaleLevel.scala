package com.viewfunction.docg.analysisProvider.feature.util.coreRealm

object GeospatialScaleLevel extends Enumeration{
  type GeospatialScaleLevel = Value
  val GlobalLevel = Value("GlobalLevel")
  val CountryLevel = Value("CountryLevel")
  val LocalLevel = Value("LocalLevel")

  def checkExists(item:String) = this.values.exists(_.toString == item)
  def showAll = this.values.foreach(println)
}
