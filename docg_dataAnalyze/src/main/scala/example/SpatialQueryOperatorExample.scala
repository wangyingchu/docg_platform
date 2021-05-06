package example

import com.viewfunction.docg.dataAnalyze.util.coreRealm.GeospatialScaleLevel
import com.viewfunction.docg.dataAnalyze.util.dataSlice.DataSliceOperationUtil
import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.SpatialDirectionType.SpatialDirectionType
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.{SpatialDirectionType, SpatialOrderType, SpatialPredicateType, SpatialQueryOperator, SpatialQueryParam}

import scala.collection.mutable

object SpatialQueryOperatorExample {

  def main(args:Array[String]):Unit = {
    DataSliceOperationUtil.turnOffSparkLog()
    DataSliceOperationUtil.turnOffDataSliceLog()

    val dataSliceSparkAccessor = new DataSliceSparkAccessor("SpatialQueryOperatorExample","local","20")
    try{
      val functionalZoneDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("FunctionalZone",GeospatialScaleLevel.LocalLevel,"functionalZonesDF",null)
      //functionalZoneDF.printSchema()
      var rangeWKTValue = ""
      functionalZoneDF.take(5).foreach(item=>{
        rangeWKTValue=item.getString(2)
      })
      //println(rangeWKTValue)

      val individualTreeDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("IndividualTree",GeospatialScaleLevel.LocalLevel,"individualTreesDF","treeLocation")
      //individualTreeDF.show(10)
      //individualTreeDF.printSchema()

      //println("Start At    " + new Date().toString)
      val spatialQueryOperator = new SpatialQueryOperator
      //val rangeQueryResultDF = spatialQueryOperator.spatialRangeQuery(dataSliceSparkAccessor,rangeWKTValue,SpatialPredicateType.Contains,"individualTreesDF","treeLocation",null,null)
      //println(rangeQueryResultDF.count())
      //println("Finish At   " + new Date().toString)
      //rangeQueryResultDF.show(10)

      val individualTreeKNNDF = spatialQueryOperator.spatialKNNQuery(dataSliceSparkAccessor,"POINT (374534.4689999996 3271806.7980000004)",10,SpatialOrderType.DESC,"individualTreesDF","treeLocation",null,"distanceValue",null)
      individualTreeKNNDF.show(5)

      val individualTreeWithInDistanceDF = spatialQueryOperator.spatialWithinDistanceQuery(dataSliceSparkAccessor,"POINT (374534.4689999996 3271806.7980000004)",100,500,"individualTreesDF","treeLocation",null,"distanceInMeter",null)
      individualTreeWithInDistanceDF.show(10)

      val individualTreeOutOfDistanceDF = spatialQueryOperator.spatialOutOfDistanceQuery(dataSliceSparkAccessor,"POINT (374534.4689999996 3271806.7980000004)",100,1200,"individualTreesDF","treeLocation",null,"distanceInMeter",null)
      individualTreeOutOfDistanceDF.show(10)

      val individualTreeBetweenDistanceDF =
        spatialQueryOperator.spatialBetweenDistanceQuery(dataSliceSparkAccessor,"POINT (374534.4689999996 3271806.7980000004)",50,1250,1255,SpatialOrderType.ASC,"individualTreesDF","treeLocation",null,"distanceInMeter",null)
      individualTreeBetweenDistanceDF.show(10)

      val spatialQueryParamA = SpatialQueryParam("individualTreesDF","treeLocation",null)
      val spatialQueryParamB = SpatialQueryParam("frutexTreesDF","frutexLocation",null)

      val frutexTreeDF = dataSliceSparkAccessor.getDataFrameWithSpatialSupportFromDataSlice("Frutex",GeospatialScaleLevel.LocalLevel,"frutexTreesDF","frutexLocation")
      val individualTree_FrutexDistanceJoinDF1 =
        spatialQueryOperator.spatialWithinDistanceJoinQuery(dataSliceSparkAccessor,spatialQueryParamA,spatialQueryParamB,50,"treeDistanceInMeter",null)
      individualTree_FrutexDistanceJoinDF1.show(20)
      //println(individualTree_FrutexDistanceJoinDF1.count())

      val individualTree_FrutexDistanceJoinDF2 =
        spatialQueryOperator.spatialOutOfDistanceJoinQuery(dataSliceSparkAccessor,spatialQueryParamA,spatialQueryParamB,2500,"treeDistanceInMeter",null)
      individualTree_FrutexDistanceJoinDF2.show(20)
      //println(individualTree_FrutexDistanceJoinDF2.count())

      val individualTree_FrutexDistanceJoinDF3 =
        spatialQueryOperator.spatialBetweenDistanceJoinQuery(dataSliceSparkAccessor,spatialQueryParamA,spatialQueryParamB,150,200,"treeDistanceInMeter",null)
      individualTree_FrutexDistanceJoinDF3.show(20)
      //println(individualTree_FrutexDistanceJoinDF3.count())

      val functionalZoneSpatialAttributesDF =
        spatialQueryOperator.spatialAttributesQuery(dataSliceSparkAccessor,"functionalZonesDF","LL_Geometry","REALMGLOBALUID",null)
      functionalZoneSpatialAttributesDF.show(20)
      functionalZoneSpatialAttributesDF.printSchema()

      val individualTree_FrutexDirectionQueryDF =
        spatialQueryOperator.spatialDirectionalQuery(dataSliceSparkAccessor,"POINT (374534.4689999996 3271806.7980000004)",SpatialDirectionType.RightOf,"individualTreesDF","treeLocation",null,null)
      individualTree_FrutexDirectionQueryDF.show(20)
      println(individualTree_FrutexDirectionQueryDF.count())

      val bufferQueryDF =
        spatialQueryOperator.spatialBufferQuery(dataSliceSparkAccessor,"POINT (374534.4689999996 3271806.7980000004)",100,"individualTreesDF","treeLocation",mutable.Buffer[String]("DMID","SZ"),null)
      bufferQueryDF.show(20)
      println(bufferQueryDF.count())

    }finally dataSliceSparkAccessor.close()
  }

}
