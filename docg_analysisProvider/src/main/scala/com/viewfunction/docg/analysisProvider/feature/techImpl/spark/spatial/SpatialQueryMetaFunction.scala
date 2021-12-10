package com.viewfunction.docg.analysisProvider.feature.techImpl.spark.spatial

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialDirectionType.SpatialDirectionType
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialOrderType.SpatialOrderType
import com.viewfunction.docg.analysisProvider.fundamental.spatial.SpatialPredicateType.SpatialPredicateType
import com.viewfunction.docg.analysisProvider.fundamental.spatial.{SpatialOrderType, SpatialPredicateType}
import org.apache.sedona.core.enums.GridType
import org.apache.sedona.sql.utils.Adapter
import org.apache.spark.sql.DataFrame

import scala.collection.mutable

class SpatialQueryMetaFunction {

  def transferMeterValueToDegree(meterValue:Double):Double = {
    meterValue/(2 * Math.PI * 6371004) * 360
  }

  def transferDegreeValueToMeter(degreeValue:Double):Double = {
    degreeValue/360*2*(2 * Math.PI * 6371004)
  }

  def spatialAttributesQuery(globalDataAccessor:GlobalDataAccessor,operationSourceDataFrame:String,spatialAttributeName:String,uidAttributeName:String,resultDataFrameName:String):DataFrame={
    val spatialFunctionComputeDfQueryString =
      "SELECT ST_ConvexHull("+spatialAttributeName+") AS ConvexHull" +
        ", ST_Envelope("+spatialAttributeName+") AS Envelope" +
        ", ST_Length("+spatialAttributeName+") AS Length" +
        ", ST_Area("+spatialAttributeName+") AS Area" +
        // ", ST_Area("+ "ST_Transform("+    spatialAttributeName  +",'epsg:4326','epsg:4326',false)"        +") AS Area" +  需要转换到米制单位下才能获得正确的计算结果
        /*
        * To convert Coordinate Reference System of an SpatialRDD, use the following code:
        * val sourceCrsCode = "epsg:4326" // WGS84, the most common degree-based CRS
        * val targetCrsCode = "epsg:3857" // The most common meter-based CRS
        * objectRDD.CRSTransform(sourceCrsCode, targetCrsCode, false)
        *
        * */
        ", ST_Centroid("+spatialAttributeName+") AS Centroid" +
        ", ST_Boundary("+spatialAttributeName+") AS Boundary" +
        //", ST_MinimumBoundingRadius("+spatialAttributeName+") AS InteriorRingN" + //supported at sedona 1.0.1
        //", ST_MinimumBoundingCircle("+spatialAttributeName+") AS InteriorRingN" + //supported at sedona 1.0.1
        //"," + uidAttributeName +" AS UID" +
        "," + uidAttributeName +
        " FROM "+operationSourceDataFrame
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialKNNQuery(globalDataAccessor:GlobalDataAccessor,queryPointWKT:String,kValue:Int,spatialOrderType:SpatialOrderType,
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
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialWithinDistanceQuery(globalDataAccessor:GlobalDataAccessor,queryPointWKT:String,kValue:Int,distanceValue:Double,
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
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialOutOfDistanceQuery(globalDataAccessor:GlobalDataAccessor,queryPointWKT:String,kValue:Int,distanceValue:Double,
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
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialBetweenDistanceQuery(globalDataAccessor:GlobalDataAccessor,queryPointWKT:String,kValue:Int,minDistanceValue:Double,maxDistanceValue:Double,spatialOrderType:SpatialOrderType,
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
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialRangeQuery(globalDataAccessor:GlobalDataAccessor,queryRangeWKT:String,spatialPredicateType:SpatialPredicateType,
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
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialJoinQuery(globalDataAccessor:GlobalDataAccessor,operationSourceDataFrameA:SpatialQueryParam,
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
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialJoinQuery_RDD(globalDataAccessor:GlobalDataAccessor,operationSourceDataFrameA:SpatialQueryParam,
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

    val sparkSession = globalDataAccessor.getSparkSession()

    val sourceDataFrameA = sparkSession.table(operationSourceDataFrameAName)
    val spatialRDDA = Adapter.toSpatialRdd(sourceDataFrameA, operationSourceDataFrameASpatialAttributeName)

    val sourceDataFrameB = sparkSession.table(operationSourceDataFrameBName)
    val spatialRDDB = Adapter.toSpatialRdd(sourceDataFrameB, operationSourceDataFrameBSpatialAttributeName)

    val buildOnSpatialPartitionedRDD = false // Set to TRUE only if run join query
    //spatialRDDA.buildIndex(IndexType.QUADTREE, buildOnSpatialPartitionedRDD)
    //spatialRDDB.buildIndex(IndexType.QUADTREE, buildOnSpatialPartitionedRDD)

    val numPartitions = 40
    spatialRDDA.analyze()
    spatialRDDA.spatialPartitioning(GridType.KDBTREE,numPartitions) //NB!
    spatialRDDB.spatialPartitioning(spatialRDDA.getPartitioner)

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
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialWithinDistanceJoinQuery(globalDataAccessor:GlobalDataAccessor,operationSourceDataFrameA:SpatialQueryParam,operationSourceDataFrameB:SpatialQueryParam,
                                     distanceValue:Double,distanceAttributeName:String,resultDataFrameName:String):DataFrame={
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

    var distanceResultAttribute = "DISTANCE"
    if(distanceAttributeName != null){
      distanceResultAttribute = distanceAttributeName
    }
    val spatialFunctionComputeDfQueryString = "SELECT "+ resultAttributesStr + ", ST_Distance("+calculateAttributeA+", "+calculateAttributeB+") AS "+distanceAttributeName+
      " FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Distance("+calculateAttributeA+", "+calculateAttributeB+") <= "+distanceValue
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialOutOfDistanceJoinQuery(globalDataAccessor:GlobalDataAccessor,operationSourceDataFrameA:SpatialQueryParam,operationSourceDataFrameB:SpatialQueryParam,
                                    distanceValue:Double,distanceAttributeName:String,resultDataFrameName:String):DataFrame={
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

    var distanceResultAttribute = "DISTANCE"
    if(distanceAttributeName != null){
      distanceResultAttribute = distanceAttributeName
    }
    val spatialFunctionComputeDfQueryString = "SELECT "+ resultAttributesStr + ", ST_Distance("+calculateAttributeA+", "+calculateAttributeB+") AS "+distanceAttributeName+
      " FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Distance("+calculateAttributeA+", "+calculateAttributeB+") >= "+distanceValue
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialBetweenDistanceJoinQuery(globalDataAccessor:GlobalDataAccessor,operationSourceDataFrameA:SpatialQueryParam,operationSourceDataFrameB:SpatialQueryParam,
                                      minDistanceValue:Double,maxDistanceValue:Double,distanceAttributeName:String,resultDataFrameName:String):DataFrame={
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

    var distanceResultAttribute = "DISTANCE"
    if(distanceAttributeName != null){
      distanceResultAttribute = distanceAttributeName
    }
    val spatialFunctionComputeDfQueryString = "SELECT "+ resultAttributesStr + ", ST_Distance("+calculateAttributeA+", "+calculateAttributeB+") AS "+distanceAttributeName+
      " FROM "+operationSourceDataFrameAName+", "+operationSourceDataFrameBName+" WHERE ST_Distance("+calculateAttributeA+", "+calculateAttributeB+") >= "+minDistanceValue +
      " AND ST_Distance("+calculateAttributeA+", "+calculateAttributeB+") <= "+maxDistanceValue
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialDirectionalQuery(globalDataAccessor:GlobalDataAccessor,queryPointWKT:String,spatialDirectionType:SpatialDirectionType,
                              operationSourceDataFrame:String,spatialAttributeName:String,resultDFAttributes:mutable.Buffer[String],resultDataFrameName:String):DataFrame={
    // <, > 等运算符的含义？？
    val spatialFunctionComputeDfQueryString = "SELECT * FROM "+ operationSourceDataFrame + " WHERE " + spatialAttributeName + " <= " + "ST_GeomFromWKT(\""+queryPointWKT+"\")"
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

  def spatialTopologicalQuery(globalDataAccessor:GlobalDataAccessor,queryPointWKT:String):Unit={}

  def spatialBufferQuery(globalDataAccessor:GlobalDataAccessor,queryWKT:String,bufferDistanceValue:Double,
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

    val targetWKTBufferQueryString = "SELECT ST_Buffer(ST_GeomFromWKT(\""+queryWKT+"\"),"+bufferDistanceValue+")"
    val targetWKTBufferDF = globalDataAccessor._getDataFrameFromSparkSQL(null,targetWKTBufferQueryString.stripMargin)
    var targetWKT = ""
    val resultBufferedWKT = targetWKTBufferDF.take(1)
    resultBufferedWKT
      .foreach(item=>{
        targetWKT = item.get(0).toString
        println( item.get(0))
      })
    val spatialFunctionComputeDfQueryString = "SELECT "+resultAttributesStr+" FROM "+operationSourceDataFrame+" WHERE ST_Contains(ST_GeomFromWKT(\""+targetWKT+"\"),"+spatialAttributeName+")"
    val spatialFunctionComputeDf = globalDataAccessor._getDataFrameFromSparkSQL(resultDataFrameName,spatialFunctionComputeDfQueryString.stripMargin)
    spatialFunctionComputeDf
  }

}
