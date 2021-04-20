import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkSession

object StandardUsageExample extends App{

  DataSliceOperationUtil.turnOffSparkLog()
  DataSliceOperationUtil.turnOffDataSliceLog()

  val dataSliceSparkSession = new DataSliceSparkSession("StandardUsageExample","local","4")
  try{
    val individualTreeDF = dataSliceSparkSession.getDataFrameFromDataSlice("IndividualTree")
    individualTreeDF.show(100)
    individualTreeDF.printSchema()

    val customDf = dataSliceSparkSession.getSparkSession().sql("SELECT * FROM IndividualTree WHERE SZ = '蓝花楹'")
    customDf.createOrReplaceTempView("IndividualTree_FilterA")
    customDf.printSchema()
    println("Result content:")
    customDf.show(10)

    // DBU = diameter ot breast height(胸径)
    val spatialIndividualTreeDf = dataSliceSparkSession.getSparkSession().sql(
      """
        |SELECT ST_GeomFromWKT(DOCG_GS_LLGeometryContent)  AS treeLocation ,SZ as treeType ,XJ as treeDBH
        |FROM IndividualTree_FilterA
    """.stripMargin)

    spatialIndividualTreeDf.printSchema()
    spatialIndividualTreeDf.show()

  }finally dataSliceSparkSession.close()
}
