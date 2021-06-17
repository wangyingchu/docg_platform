package com.viewfunction.docg.analysisProvider.tools.dataMaintain

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.{DataSlice, DataSlicePropertyType}
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.data.simple.{SimpleFeatureCollection, SimpleFeatureIterator, SimpleFeatureSource}
import org.geotools.data.{FileDataStore, FileDataStoreFinder}
import org.geotools.referencing.CRS
import org.opengis.feature.GeometryAttribute
import org.opengis.feature.simple.{SimpleFeature, SimpleFeatureType}

import java.io.File
import java.nio.charset.Charset
import java.util

class SpatialDataMaintainUtil {

  def parseSHPData(shpFile: File, fileEncode: String):SpatialDataInfo = {
    val shpDataValueList = new util.ArrayList[util.HashMap[String, Any]]
    val charsetEncode: String = if (fileEncode != null) {
      fileEncode
    }else {
      "UTF-8"
    }
    // 读取到数据存储中
    val dataStore: FileDataStore = FileDataStoreFinder.getDataStore(shpFile)
    dataStore.asInstanceOf[ShapefileDataStore].setCharset(Charset.forName(charsetEncode))

    val shpDataPropertyTypeMap = new util.HashMap[String,DataSlicePropertyType]

    dataStore.getSchema.getTypes.forEach( propertyType =>{
      var propertyName = propertyType.getName.toString
      //handle invalid chars and reserved words
      propertyName = propertyName.replaceAll("△", "Delta_")
      propertyName = propertyName.replaceAll("OFFSET", "OFFSET_")
      val propertyValueType = propertyType.getBinding.getName
      if(propertyValueType.equals("java.util.Date")){
        shpDataPropertyTypeMap.put(propertyName,DataSlicePropertyType.DATE)
      }
      if(propertyValueType.equals("java.lang.String")){
        shpDataPropertyTypeMap.put(propertyName,DataSlicePropertyType.STRING)
      }
      if(propertyValueType.equals("java.lang.Integer")){
        shpDataPropertyTypeMap.put(propertyName,DataSlicePropertyType.INT)
      }
      if(propertyValueType.equals("java.lang.Long")){
        shpDataPropertyTypeMap.put(propertyName,DataSlicePropertyType.LONG)
      }
      if(propertyValueType.equals("java.lang.Double")){
        shpDataPropertyTypeMap.put(propertyName,DataSlicePropertyType.DOUBLE)
      }
      if(propertyValueType.equals("java.lang.Float")){
        shpDataPropertyTypeMap.put(propertyName,DataSlicePropertyType.FLOAT)
      }
    })
    shpDataPropertyTypeMap.put("DOCG_GeometryType", DataSlicePropertyType.STRING)
    shpDataPropertyTypeMap.put("DOCG_GLGeometryContent", DataSlicePropertyType.STRING)

    // 获取特征资源
    val simpleFeatureSource: SimpleFeatureSource = dataStore.getFeatureSource
    val simpleFeatureType: SimpleFeatureType = dataStore.getSchema

    val _CRSName: String = simpleFeatureType.getCoordinateReferenceSystem.getName.getCode
    var entityCRSAID: String = null
    if ("GCS_WGS_1984" == _CRSName || _CRSName.contains("WGS84")) {
      entityCRSAID = "EPSG:4326"
    }else {
      if ("CGCS_2000" == _CRSName || _CRSName.contains("CGCS2000")) {
        entityCRSAID = "EPSG:4545"
      }else {
        val _EpsgCodeValue: Integer = CRS.lookupEpsgCode(simpleFeatureType.getCoordinateReferenceSystem, true)
        if (_EpsgCodeValue != null) {
          entityCRSAID = "EPSG:" + _EpsgCodeValue.intValue
        }
      }
    }
    // 要素集合
    val simpleFeatureCollection: SimpleFeatureCollection = simpleFeatureSource.getFeatures
    // 获取要素迭代器
    val featureIterator: SimpleFeatureIterator = simpleFeatureCollection.features
    while ( {
      featureIterator.hasNext
    }) {
      val newEntityValueMap: util.HashMap[String, Any] = new util.HashMap[String, Any]
      // 要素对象
      val feature: SimpleFeature = featureIterator.next
      // 要素属性信息，名称，值，类型
      val propertyList= feature.getValue
      import scala.collection.JavaConversions._
      for (property <- propertyList) {
        var propertyName: String = property.getName.toString
        //handle invalid chars and reserved words
        propertyName = propertyName.replaceAll("△", "Delta_")
        propertyName = propertyName.replaceAll("OFFSET", "OFFSET_")
        val propertyValue: Any = property.getValue
        if (propertyValue != null && !propertyName.equals("the_geom")) {
          newEntityValueMap.put(propertyName, propertyValue)
        }
      }
      if (feature.getDefaultGeometry != null) {
        val geometryContent: String = feature.getDefaultGeometry.toString
        val geometryAttribute: GeometryAttribute = feature.getDefaultGeometryProperty
        val geometryType: String = geometryAttribute.getType.getName.toString
        var geometryTypeValue: String = "GEOMETRYCOLLECTION"
        if ("Point" == geometryType) {
          geometryTypeValue = "POINT"
        }
        if ("MultiPoint" == geometryType) {
          geometryTypeValue = "MULTIPOINT"
        }
        if ("LineString" == geometryType) {
          geometryTypeValue = "LINESTRING"
        }
        if ("MultiLineString" == geometryType) {
          geometryTypeValue = "MULTILINESTRING"
        }
        if ("Polygon" == geometryType) {
          geometryTypeValue = "POLYGON"
        }
        if ("MultiPolygon" == geometryType) {
          geometryTypeValue = "MULTIPOLYGON"
        }
        newEntityValueMap.put("DOCG_GeometryType", geometryTypeValue)
        newEntityValueMap.put("DOCG_GLGeometryContent", geometryContent)
      }
      shpDataValueList.add(newEntityValueMap)
    }

    SpatialDataInfo(shpDataPropertyTypeMap,shpDataValueList)
  }

  def duplicateSpatialDataInfoToDataSlice(globalDataAccessor:GlobalDataAccessor, spatialDataInfo: SpatialDataInfo,
                                          dataSliceName:String, dataSliceGroupName:String, removeExistingData:Boolean, memoryTablePrimaryKeys:Array[String]):DataSlice = {
    val spatialDataValue = spatialDataInfo.spatialDataValue
    val spatialDataPropertiesDefinition = spatialDataInfo.spatialDataPropertiesDefinition

    val primaryKeysList: util.ArrayList[String] = new util.ArrayList[String]()
    if(memoryTablePrimaryKeys != null && memoryTablePrimaryKeys.size >0){
      memoryTablePrimaryKeys.foreach(key=>{
        primaryKeysList.add(key)
      })
    }else{
      primaryKeysList.add("DOCG_AutoGeneratedPrimaryKey")
      spatialDataPropertiesDefinition.put("DOCG_AutoGeneratedPrimaryKey",DataSlicePropertyType.INT)
    }

    val dataServiceInvoker = globalDataAccessor._getDataSliceServiceInvoker()
    var targetDataSlice = dataServiceInvoker.getDataSlice(dataSliceName)

    if(targetDataSlice == null){
      targetDataSlice = dataServiceInvoker.createGridDataSlice(dataSliceName,dataSliceGroupName,spatialDataPropertiesDefinition,primaryKeysList)
    }
    if(removeExistingData){
      targetDataSlice.emptyDataSlice()
    }
    var countIndex = 0;
    spatialDataValue.forEach(dataItem =>{
      if(memoryTablePrimaryKeys == null){
        dataItem.put("DOCG_AutoGeneratedPrimaryKey",countIndex)
        countIndex = countIndex+1
      }
      targetDataSlice.addDataRecord(dataItem.asInstanceOf[util.HashMap[String,Object]])
    })
    targetDataSlice
  }
}
