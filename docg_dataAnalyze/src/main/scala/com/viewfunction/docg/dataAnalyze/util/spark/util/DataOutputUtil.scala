package com.viewfunction.docg.dataAnalyze.util.spark.util

import org.apache.spark.sql.{DataFrame, SaveMode}

object DataOutputUtil {

  def writeToCSV(dataframe:DataFrame,csvFolderLocation:String): Unit ={
    dataframe.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .option("delimiter", ",")
      .option("header",true)
      .option("ignoreLeadingWhiteSpace", false)
      .option("ignoreTrailingWhiteSpace", false)
      .option("nullValue", null)
      .format("csv")
      .save(csvFolderLocation)
  }


}
