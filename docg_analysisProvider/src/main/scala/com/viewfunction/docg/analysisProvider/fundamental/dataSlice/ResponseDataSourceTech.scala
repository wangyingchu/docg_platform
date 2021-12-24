package com.viewfunction.docg.analysisProvider.fundamental.dataSlice

object ResponseDataSourceTech  extends Enumeration {
  type ResponseDataSourceTech = Value
  val SPARK = Value("SPARK")

  def checkExists(item: String) = this.values.exists(_.toString == item)

  def showAll = this.values.foreach(println)

}
