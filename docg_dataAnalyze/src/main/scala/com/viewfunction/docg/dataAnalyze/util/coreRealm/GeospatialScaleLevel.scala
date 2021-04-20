package com.viewfunction.docg.dataAnalyze.util.coreRealm

object GeospatialScaleLevel extends Enumeration{
  type GeospatialScaleLevel = Value
  val GlobalLevel = Value("GlobalLevel")
  val CountryLevel = Value("CountryLevel")
  val LocalLevel = Value("LocalLevel")

  def checkExists(day:String) = this.values.exists(_.toString==day)
  def showAll = this.values.foreach(println)
}
