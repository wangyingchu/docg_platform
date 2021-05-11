package example

import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel
import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.{SpatialPredicateType, SpatialQueryOperator, SpatialQueryParam}
import org.apache.spark.sql.functions.{avg, stddev, sum}

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

  def analyzeTreesInSection(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit = {
    println("Start analyzeTreesInSection")
    val individualTreeDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_IndividualTree",GeospatialScaleLevel.GlobalLevel,"individualTreeDF","geoLocation")
    //individualTreeDF.show(5)
    val frutexDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_Frutex",GeospatialScaleLevel.GlobalLevel,"frutexDF","geoLocation")
    //frutexDF.show(5)
    val sectionBlockDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_SectionBlock",GeospatialScaleLevel.GlobalLevel,"sectionBlockDF","geoArea")
    //sectionBlockDF.show(5)

    val spatialQueryOperator = new SpatialQueryOperator

    val sectionBlockSpatialAttributesDF = spatialQueryOperator.spatialAttributesQuery(dataSliceSparkAccessor,"sectionBlockDF","geoArea","REALMGLOBALUID",null)
    sectionBlockSpatialAttributesDF.printSchema()
    //sectionBlockSpatialAttributesDF.show(10)

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

    val mergedSectionStaticResultDF = sectionStaticResultDF.join(sectionBlockDF,"REALMGLOBALUID").join(sectionBlockSpatialAttributesDF,"REALMGLOBALUID")
    //mergedSectionStaticResultDF.show(10)
    //println(mergedSectionStaticResultDF.count())

    val res = mergedSectionStaticResultDF.select("REALMGLOBALUID","sum(SGMJ)","BKMC","GNQHID","DOCG_GS_GLGEOMETRYCONTENT","BH","Area")
    res.write.csv("/home/wangychu/Desktop/output/sectionTeeSr")
  }

}
