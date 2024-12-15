package com.viewfunction.docg.analysisProvider.fundamental.coreRealm


object ConceptionEntitiesInsertMode extends Enumeration{

  type ConceptionEntitiesInsertMode = Value
  val CLEAN_INSERT = Value("CLEAN_INSERT")
  val APPEND = Value("APPEND")
  val OVERWRITE = Value("OVERWRITE")

  def checkExists(item: String) = this.values.exists(_.toString == item)

  def showAll = this.values.foreach(println)
}
