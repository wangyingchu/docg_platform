package com.viewfunction.docg.analysisProvider.fundamental.spatial

object GeospatialScaleGrade extends Enumeration{
  type GeospatialScaleGrade = Value
  val CONTINENT = Value("CONTINENT")
  val COUNTRY_REGION = Value("COUNTRY_REGION")
  val PROVINCE = Value("PROVINCE")
  val PREFECTURE = Value("PREFECTURE")
  val COUNTY = Value("COUNTY")
  val TOWNSHIP = Value("TOWNSHIP")
  val VILLAGE = Value("VILLAGE")

  def checkExists(item: String) = this.values.exists(_.toString == item)

  def showAll = this.values.foreach(println)
}
