package example

import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel
import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.{SpatialPredicateType, SpatialQueryOperator, SpatialQueryParam}
import com.viewfunction.docg.dataAnalyze.util.spark.util.DataOutputUtil
import org.apache.spark.sql.{Row, SaveMode}
import org.apache.spark.sql.functions.{avg, stddev, sum,count}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

import scala.collection.mutable

object IslandGeoDataAnalyzeTest {

  def main(args:Array[String]):Unit = {
    DataSliceOperationUtil.turnOffSparkLog()
    DataSliceOperationUtil.turnOffDataSliceLog()
    val dataSliceSparkAccessor = new DataSliceSparkAccessor("SpatialQueryOperatorExample","local","20")
    try{
      //analyzeTreesCrownAreaInSection(dataSliceSparkAccessor)
      analyzeTreesMaintainNozzlesEffect(dataSliceSparkAccessor)
    }finally dataSliceSparkAccessor.close()
  }

  def analyzeTreesCrownAreaInSection(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit = {
    println("Start analyzeTreesCrownAreaInSection")
    val individualTreeDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_IndividualTree",GeospatialScaleLevel.GlobalLevel,"individualTreeDF","geoLocation")
    //val frutexDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_Frutex",GeospatialScaleLevel.GlobalLevel,"frutexDF","geoLocation")
    val sectionBlockDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_SectionBlock",GeospatialScaleLevel.GlobalLevel,"sectionBlockDF","geoArea")

    val spatialQueryOperator = new SpatialQueryOperator
    val sectionBlock_spatialQueryParam = SpatialQueryParam("sectionBlockDF","geoArea",mutable.Buffer[String]("BH","XBMC","REALMGLOBALUID"))
    val individualTree_spatialQueryParam = SpatialQueryParam("individualTreeDF","geoLocation",mutable.Buffer[String]("SZ","BH1","SGMJ"))
    val frutex_spatialQueryParam = SpatialQueryParam("frutexDF","geoLocation",mutable.Buffer[String]("TREEID","CROWNVOLUM"))

    val sectionIndividualTreeJoinDF = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,sectionBlock_spatialQueryParam,SpatialPredicateType.Contains,individualTree_spatialQueryParam,"sectionIndividualTreeJoinDF")
    //val sectionFrutexJoinDF = spatialQueryOperator.spatialJoinQuery(dataSliceSparkAccessor,sectionBlock_spatialQueryParam,SpatialPredicateType.Contains,frutex_spatialQueryParam,"sectionIndividualTreeJoinDF")

    val sectionStaticResultDF = sectionIndividualTreeJoinDF.groupBy("REALMGLOBALUID").agg(sum("SGMJ"),avg("SGMJ"),stddev("SGMJ"))
    val mergedSectionStaticResultDF = sectionStaticResultDF.join(sectionBlockDF,"REALMGLOBALUID")

    val caculRes = mergedSectionStaticResultDF.select("REALMGLOBALUID","sum(SGMJ)","BKMC","GNQHID","DOCG_GS_GLGEOMETRYCONTENT","BH","SHAPE_AREA","avg(SGMJ)")
    val mappedResult = caculRes.rdd.map(row =>{
      val divValue = row.get(1).asInstanceOf[Double]/row.get(6).asInstanceOf[Double]
      Row(row.get(0).asInstanceOf[String],
        row.get(1).asInstanceOf[Double],
        row.get(2).asInstanceOf[String],
        row.get(3).asInstanceOf[String],
        row.get(4).asInstanceOf[String],
        row.get(5).asInstanceOf[String],
        row.get(6).asInstanceOf[Double],
        divValue,
        row.get(7).asInstanceOf[Double]
      )
    })

    val schema = StructType(
      Seq(
        StructField("ID",StringType,true),
        StructField("SUM",DoubleType,true),
        StructField("BKMC",StringType,true),
        StructField("GNQHID",StringType,true),
        StructField("GLGEOMETRYCONTENT",StringType,true),
        StructField("BH",StringType,true),
        StructField("Area",DoubleType,true),
        StructField("Ratio",DoubleType,true),
        StructField("AVG",DoubleType,true)
      )
    )

    val finalResultDF = dataSliceSparkAccessor.getSparkSession().createDataFrame(mappedResult,schema)
    //finalResultDF.printSchema()
    //finalResultDF.show(20)

    DataOutputUtil.writeToCSV(finalResultDF,"/home/wangychu/Desktop/output/treeStatic/")
    println("Finish analyzeTreesCrownAreaInSection")
  }

  def analyzeTreesMaintainNozzlesEffect(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit = {
    println("Start analyzeTreesMaintainNozzlesEffect")
    val individualTreeDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("GYD_IndividualTree",GeospatialScaleLevel.GlobalLevel,"individualTreeDF","geoLocation")
    //individualTreeDF.show(10)
    val mockTreesMaintainNozzleDF = individualTreeDF.sample(0.01).select("REALMGLOBALUID","DMID","DOCG_GS_GLGEOMETRYCONTENT","OBJECTID_1","geoLocation")
    mockTreesMaintainNozzleDF.persist()
    mockTreesMaintainNozzleDF.show(10)
    println(mockTreesMaintainNozzleDF.count())
    mockTreesMaintainNozzleDF.createOrReplaceTempView("treesMaintainNozzleDF")

    val spatialQueryOperator = new SpatialQueryOperator

    val sectionBlock_spatialQueryParam = SpatialQueryParam("treesMaintainNozzleDF","geoLocation",mutable.Buffer[String]("DMID","OBJECTID_1","REALMGLOBALUID"))
    val individualTree_spatialQueryParam = SpatialQueryParam("individualTreeDF","geoLocation",mutable.Buffer[String]("SZ","BH1","SGMJ"))

    val degreeValue = 0.2 / (2 * Math.PI * 6371004) * 360
    val resultDF = spatialQueryOperator.spatialWithinDistanceJoinQuery(dataSliceSparkAccessor,sectionBlock_spatialQueryParam,individualTree_spatialQueryParam,degreeValue,"distanceValue",null)

     // .groupBy("REALMGLOBALUID").agg(count("SZ"),sum("SGMJ"))
    resultDF.show(10)
    resultDF.foreach(row=>{

      val meter = (row.get(6).asInstanceOf[Double])/360*2*(2 * Math.PI * 6371004)
      println(meter)
    })


    println(resultDF.count())
  }

}
