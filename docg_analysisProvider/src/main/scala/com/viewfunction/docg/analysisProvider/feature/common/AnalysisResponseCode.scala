package com.viewfunction.docg.analysisProvider.feature.common

object AnalysisResponseCode extends Enumeration{
  type AnalysisResponseCode = Value
  val ANALYSUS_SUCCESS = Value("ANALYSUS_SUCCESS")
  val ANALYSUS_FAIL = Value("ANALYSUS_FAIL")
  val ANALYSUS_INPUT_INVALID = Value("ANALYSUS_INPUT_INVALID")

  def checkExists(item: String) = this.values.exists(_.toString == item)

  def showAll = this.values.foreach(println)
}
