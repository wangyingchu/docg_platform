package com.viewfunction.docg.dataAnalyze.util.spark.spatial

import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.SpatialPredicateType.SpatialPredicateType
import org.apache.spark.sql.DataFrame

class SpatialQueryOperator {

  def spatialAttributeQuery(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit={}
  def spatialRangeQuery(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit={}
  def spatialKNNQuery(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit={}

  def spatialJoinQuery(dataSliceSparkAccessor:DataSliceSparkAccessor,operationSourceDataFrameA:SpatialQueryParam,
                       spatialPredicateType:SpatialPredicateType,operationSourceDataFrameB:SpatialQueryParam,resultDataFrameName:String):DataFrame={
    val operationSourceDataFrameAName = operationSourceDataFrameA.spatialDataFrameName
    val operationSourceDataFrameASpatialAttributeName = operationSourceDataFrameA.spatialAttributeName
    val operationSourceDataFrameBName = operationSourceDataFrameB.spatialDataFrameName;
    val operationSourceDataFrameBSpatialAttributeName = operationSourceDataFrameB.spatialAttributeName
    val calculateAttributeA = operationSourceDataFrameAName+"."+operationSourceDataFrameASpatialAttributeName
    val calculateAttributeB = operationSourceDataFrameBName+"."+operationSourceDataFrameBSpatialAttributeName
    val operationSourceDataFrameAResultAttributes = operationSourceDataFrameA.resultAttributes
    val operationSourceDataFrameBResultAttributes = operationSourceDataFrameB.resultAttributes
    var resultAttributes = ""

    if(operationSourceDataFrameAResultAttributes != null){
      operationSourceDataFrameAResultAttributes.foreach(attributeName =>{
        resultAttributes = resultAttributes + operationSourceDataFrameAName+"."+attributeName
        resultAttributes = resultAttributes+" , "
      })
    }

    if(operationSourceDataFrameBResultAttributes != null){
      operationSourceDataFrameBResultAttributes.foreach(attributeName =>{
        resultAttributes = resultAttributes + operationSourceDataFrameBName+"."+attributeName
        resultAttributes = resultAttributes+" , "
      })
    }

    var resultAttributesStr = "*"
    if(!resultAttributes.equals("")) resultAttributesStr = resultAttributes
    if(resultAttributesStr.endsWith(", ")) resultAttributesStr = resultAttributesStr.reverse.replaceFirst(", ","").reverse

    var spatialFunctionComputeDfQueryString = ""
    spatialPredicateType match {
      case SpatialPredicateType.Contains =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Contains("+calculateAttributeA+","+calculateAttributeB+")"
      case SpatialPredicateType.Intersects =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Intersects("+calculateAttributeA+","+calculateAttributeB+")"
      case SpatialPredicateType.Within =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Within("+calculateAttributeA+","+calculateAttributeB+")"
      case SpatialPredicateType.Equals =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Equals("+calculateAttributeA+","+calculateAttributeB+")"
      case SpatialPredicateType.Crosses =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Crosses("+calculateAttributeA+","+calculateAttributeB+")"
      case SpatialPredicateType.Touches =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Touches("+calculateAttributeA+","+calculateAttributeB+")"
      case SpatialPredicateType.Overlaps =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Overlaps("+calculateAttributeA+","+calculateAttributeB+")"
    }
    //println(spatialFunctionComputeDfQueryString)
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def distanceJoinQuery(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit={}

}
