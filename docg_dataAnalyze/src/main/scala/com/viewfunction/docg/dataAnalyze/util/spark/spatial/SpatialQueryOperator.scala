package com.viewfunction.docg.dataAnalyze.util.spark.spatial

import com.viewfunction.docg.dataAnalyze.util.spark.DataSliceSparkAccessor
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.SpatialOrderType.SpatialOrderType
import com.viewfunction.docg.dataAnalyze.util.spark.spatial.SpatialPredicateType.SpatialPredicateType
import org.apache.spark.sql.DataFrame

import scala.collection.mutable

class SpatialQueryOperator {

  def spatialAttributeQuery(dataSliceSparkAccessor:DataSliceSparkAccessor):Unit={}

  def spatialKNNQuery(dataSliceSparkAccessor:DataSliceSparkAccessor,queryPointWKT:String,kValue:Int,spatialOrderType:SpatialOrderType,
                      operationSourceDataFrame:String,spatialAttributeName:String,resultDFAttributes:mutable.Buffer[String],distanceAttributeName:String,resultDataFrameName:String):DataFrame={
    var resultAttributes = ""
    if(resultDFAttributes != null){
      resultDFAttributes.foreach(attributeName =>{
        resultAttributes = resultAttributes + operationSourceDataFrame+"."+attributeName
        resultAttributes = resultAttributes+" , "
      })
    }
    var resultAttributesStr = "*"
    if(!resultAttributes.equals("")) resultAttributesStr = resultAttributes
    if(resultAttributesStr.endsWith(", ")) resultAttributesStr = resultAttributesStr.reverse.replaceFirst(", ","").reverse

    var sortOrder = ""
    spatialOrderType match {
      case SpatialOrderType.ASC =>
        sortOrder = "ASC"
      case SpatialOrderType.DESC =>
        sortOrder = "DESC"
    }

    var distanceResultAttribute = "DISTANCE"
    if(distanceAttributeName != null){
      distanceResultAttribute = distanceAttributeName
    }
    val spatialFunctionComputeDfQueryString =
      "SELECT "+resultAttributesStr+", ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) AS "+distanceResultAttribute+" "+
      "FROM "+operationSourceDataFrame+" df "+
      "ORDER BY "+distanceResultAttribute+" "+sortOrder+" "+
      "LIMIT "+ kValue
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialWithinDistanceQuery(dataSliceSparkAccessor:DataSliceSparkAccessor,queryPointWKT:String,kValue:Int,distanceValue:Double,
                      operationSourceDataFrame:String,spatialAttributeName:String,resultDFAttributes:mutable.Buffer[String],distanceAttributeName:String,resultDataFrameName:String):DataFrame={
    var resultAttributes = ""
    if(resultDFAttributes != null){
      resultDFAttributes.foreach(attributeName =>{
        resultAttributes = resultAttributes + operationSourceDataFrame+"."+attributeName
        resultAttributes = resultAttributes+" , "
      })
    }
    var resultAttributesStr = "*"
    if(!resultAttributes.equals("")) resultAttributesStr = resultAttributes
    if(resultAttributesStr.endsWith(", ")) resultAttributesStr = resultAttributesStr.reverse.replaceFirst(", ","").reverse

    var distanceResultAttribute = "DISTANCE"
    if(distanceAttributeName != null){
      distanceResultAttribute = distanceAttributeName
    }
    val spatialFunctionComputeDfQueryString =
      "SELECT "+resultAttributesStr+", ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) AS "+distanceResultAttribute+" "+
        "FROM "+operationSourceDataFrame+" df "+
        "WHERE ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) <= "+ distanceValue + " "+
        "ORDER BY "+distanceResultAttribute+" ASC "+
        "LIMIT "+ kValue
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialOutOfDistanceQuery(dataSliceSparkAccessor:DataSliceSparkAccessor,queryPointWKT:String,kValue:Int,distanceValue:Double,
                                 operationSourceDataFrame:String,spatialAttributeName:String,resultDFAttributes:mutable.Buffer[String],distanceAttributeName:String,resultDataFrameName:String):DataFrame={
    var resultAttributes = ""
    if(resultDFAttributes != null){
      resultDFAttributes.foreach(attributeName =>{
        resultAttributes = resultAttributes + operationSourceDataFrame+"."+attributeName
        resultAttributes = resultAttributes+" , "
      })
    }
    var resultAttributesStr = "*"
    if(!resultAttributes.equals("")) resultAttributesStr = resultAttributes
    if(resultAttributesStr.endsWith(", ")) resultAttributesStr = resultAttributesStr.reverse.replaceFirst(", ","").reverse

    var distanceResultAttribute = "DISTANCE"
    if(distanceAttributeName != null){
      distanceResultAttribute = distanceAttributeName
    }
    val spatialFunctionComputeDfQueryString =
      "SELECT "+resultAttributesStr+", ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) AS "+distanceResultAttribute+" "+
        "FROM "+operationSourceDataFrame+" df "+
        "WHERE ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) >= "+ distanceValue + " "+
        "ORDER BY "+distanceResultAttribute+" ASC "+
        "LIMIT "+ kValue
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialBetweenDistanceQuery(dataSliceSparkAccessor:DataSliceSparkAccessor,queryPointWKT:String,kValue:Int,minDistanceValue:Double,maxDistanceValue:Double,spatialOrderType:SpatialOrderType,
                                operationSourceDataFrame:String,spatialAttributeName:String,resultDFAttributes:mutable.Buffer[String],distanceAttributeName:String,resultDataFrameName:String):DataFrame={
    var resultAttributes = ""
    if(resultDFAttributes != null){
      resultDFAttributes.foreach(attributeName =>{
        resultAttributes = resultAttributes + operationSourceDataFrame+"."+attributeName
        resultAttributes = resultAttributes+" , "
      })
    }
    var resultAttributesStr = "*"
    if(!resultAttributes.equals("")) resultAttributesStr = resultAttributes
    if(resultAttributesStr.endsWith(", ")) resultAttributesStr = resultAttributesStr.reverse.replaceFirst(", ","").reverse

    var sortOrder = ""
    spatialOrderType match {
      case SpatialOrderType.ASC =>
        sortOrder = "ASC"
      case SpatialOrderType.DESC =>
        sortOrder = "DESC"
    }

    var distanceResultAttribute = "DISTANCE"
    if(distanceAttributeName != null){
      distanceResultAttribute = distanceAttributeName
    }
    val spatialFunctionComputeDfQueryString =
      "SELECT "+resultAttributesStr+", ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) AS "+distanceResultAttribute+" "+
        "FROM "+operationSourceDataFrame+" df "+
        "WHERE ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) >= "+ minDistanceValue + " "+
        "AND ST_Distance(df."+spatialAttributeName+", ST_GeomFromWKT(\""+queryPointWKT+"\")) <= "+ maxDistanceValue + " "+
        "ORDER BY "+distanceResultAttribute+" "+sortOrder+" "+
        "LIMIT "+ kValue
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialRangeQuery(dataSliceSparkAccessor:DataSliceSparkAccessor,queryRangeWKT:String,spatialPredicateType:SpatialPredicateType,
                        operationSourceDataFrame:String,spatialAttributeName:String,resultDFAttributes:mutable.Buffer[String],resultDataFrameName:String):DataFrame={
    var resultAttributes = ""
    if(resultDFAttributes != null){
      resultDFAttributes.foreach(attributeName =>{
        resultAttributes = resultAttributes + operationSourceDataFrame+"."+attributeName
        resultAttributes = resultAttributes+" , "
      })
    }
    var resultAttributesStr = "*"
    if(!resultAttributes.equals("")) resultAttributesStr = resultAttributes
    if(resultAttributesStr.endsWith(", ")) resultAttributesStr = resultAttributesStr.reverse.replaceFirst(", ","").reverse

    val dataFrameSpatialAttributeName = operationSourceDataFrame+"."+spatialAttributeName
    var spatialFunctionComputeDfQueryString = ""
    spatialPredicateType match {
      case SpatialPredicateType.Contains =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Contains(ST_GeomFromWKT(\""+queryRangeWKT+"\"),"+dataFrameSpatialAttributeName+")"
      case SpatialPredicateType.Intersects =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Intersects(ST_GeomFromWKT(\""+queryRangeWKT+"\"),"+dataFrameSpatialAttributeName+")"
      case SpatialPredicateType.Within =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Within(ST_GeomFromWKT(\""+queryRangeWKT+"\"),"+dataFrameSpatialAttributeName+")"
      case SpatialPredicateType.Equals =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Equals(ST_GeomFromWKT(\""+queryRangeWKT+"\"),"+dataFrameSpatialAttributeName+")"
      case SpatialPredicateType.Crosses =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Crosses(ST_GeomFromWKT(\""+queryRangeWKT+"\"),"+dataFrameSpatialAttributeName+")"
      case SpatialPredicateType.Touches =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Touches(ST_GeomFromWKT(\""+queryRangeWKT+"\"),"+dataFrameSpatialAttributeName+")"
      case SpatialPredicateType.Overlaps =>
        spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Overlaps(ST_GeomFromWKT(\""+queryRangeWKT+"\"),"+dataFrameSpatialAttributeName+")"
    }
    //println(spatialFunctionComputeDfQueryString)
    val spatialFunctionComputeDf = dataSliceSparkAccessor.getDataFrameFromSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

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
