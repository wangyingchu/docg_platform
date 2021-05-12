package example

import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel
import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.{SpatialPredicateType, SpatialQueryOperator, SpatialQueryParam}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions.{avg, stddev, sum}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import scala.collection.mutable

object IslandGeoDataAnalyzeTest {

  def main(args:Array[String]):Unit = {
    DataSliceOperationUtil.turnOffSparkLog()
    DataSliceOperationUtil.turnOffDataSliceLog()
    val dataSliceSparkAccessor = new DataSliceSparkAccessor("SpatialQueryOperatorExample","local","20")
    try{
      analyzeTreesInSection(dataSliceSparkAccessor)
    }finally dataSliceSparkAccessor.close()
  }

  case class Person(_ID:String,_SUM:Double,_BKMC:String,_GNQHID:String,_GLGEOMETRYCONTENT:String,_BH:String,_Area:Double,_Ratio:Double,_AVG:Double)

  def analyzeTreesInSection(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit = {
    println("Start analyzeTreesInSection")
    val individualTreeDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_IndividualTree",GeospatialScaleLevel.GlobalLevel,"individualTreeDF","geoLocation")
    //individualTreeDF.show(5)
    val frutexDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_Frutex",GeospatialScaleLevel.GlobalLevel,"frutexDF","geoLocation")
    //frutexDF.show(5)
    val sectionBlockDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_SectionBlock",GeospatialScaleLevel.GlobalLevel,"sectionBlockDF","geoArea")
    //sectionBlockDF.show(5)

    val spatialQueryOperator = new SpatialQueryOperator
    val sectionBlock_spatialQueryParam = SpatialQueryParam("sectionBlockDF","geoArea",mutable.Buffer[String]("BH","XBMC","REALMGLOBALUID"))
    val individualTree_spatialQueryParam = SpatialQueryParam("individualTreeDF","geoLocation",mutable.Buffer[String]("SZ","BH1","SGMJ"))
    val frutex_spatialQueryParam = SpatialQueryParam("frutexDF","geoLocation",mutable.Buffer[String]("TREEID","CROWNVOLUM"))

    val sectionIndividualTreeJoinDF = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,sectionBlock_spatialQueryParam,SpatialPredicateType.Contains,individualTree_spatialQueryParam,"sectionIndividualTreeJoinDF")
    //sectionIndividualTreeJoinDF.show(10)
    val sectionFrutexJoinDF = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,sectionBlock_spatialQueryParam,SpatialPredicateType.Contains,frutex_spatialQueryParam,"sectionIndividualTreeJoinDF")
    //sectionFrutexJoinDF.show(10)

    val sectionStaticResultDF = sectionIndividualTreeJoinDF.groupBy("REALMGLOBALUID").agg(sum("SGMJ"),avg("SGMJ"),stddev("SGMJ"))
    //sectionStaticResultDF.show(10)
    //println(sectionStaticResultDF.count())
    sectionStaticResultDF.printSchema()

    val mergedSectionStaticResultDF = sectionStaticResultDF.join(sectionBlockDF,"REALMGLOBALUID")
    mergedSectionStaticResultDF.printSchema()

    val res = mergedSectionStaticResultDF.select("REALMGLOBALUID","sum(SGMJ)","BKMC","GNQHID","DOCG_GS_GLGEOMETRYCONTENT","BH","SHAPE_AREA","avg(SGMJ)")
    //res.show(10)

    val mappedResult = res.rdd.map(row =>{
      val divValue = row.get(1).asInstanceOf[Double]/row.get(6).asInstanceOf[Double]
      Person(row.get(0).asInstanceOf[String],
        row.get(1).asInstanceOf[Double],
        row.get(2).asInstanceOf[String],
        row.get(3).asInstanceOf[String],
        row.get(4).asInstanceOf[String],
        row.get(5).asInstanceOf[String],
        row.get(6).asInstanceOf[Double],
        divValue,
        row.get(6).asInstanceOf[Double]
      )
    })

    import dataSliceSparkAccessor.igniteSession.implicits._
    val resultDF = mappedResult.toDF()

    resultDF.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .option("delimiter", ",")
      // .option("quote", "")
      .option("header",true)
      .option("ignoreLeadingWhiteSpace", false)
      .option("ignoreTrailingWhiteSpace", false)
      .option("nullValue", null)
      .format("csv")
      .save("/home/wangychu/Desktop/output/testOutput/csv/")
  }

}
