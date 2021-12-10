package com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance

import com.viewfunction.docg.analysisProvider.exception.AnalysisProviderRuntimeException
import com.viewfunction.docg.analysisProvider.fundamental.dataMaintenance
import com.viewfunction.docg.coreRealm.realmServiceCore.term.{AttributeDataType, AttributeKind}
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JAttributeKindImpl
//import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleGrade.GeospatialScaleGrade
import com.viewfunction.docg.analysisProvider.fundamental.spatial.GeospatialScaleLevel.{CountryLevel, GeospatialScaleLevel, GlobalLevel, LocalLevel}
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion.GeospatialScaleGrade
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.{DataServiceInvoker, DataSlice, DataSlicePropertyType}
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil
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
        shpDataPropertyTypeMap.put(propertyName,DataSlicePropertyType.TIMESTAMP)
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

    // 获取特征资源
    val simpleFeatureSource: SimpleFeatureSource = dataStore.getFeatureSource
    val simpleFeatureType: SimpleFeatureType = dataStore.getSchema

    val _CRSName: String = simpleFeatureType.getCoordinateReferenceSystem.getName.getCode
    var entityCRSAID: String = null
    var geometryContentType: String =null

    if ("GCS_WGS_1984" == _CRSName || _CRSName.contains("WGS84")) {
      entityCRSAID = "EPSG:4326"
      geometryContentType = RealmConstant._GeospatialGLGeometryContent
    }else {
      if ("CGCS_2000" == _CRSName || _CRSName.contains("CGCS2000")) {
        entityCRSAID = "EPSG:4545"
        geometryContentType = RealmConstant._GeospatialCLGeometryContent
      }else {
        val _EpsgCodeValue: Integer = CRS.lookupEpsgCode(simpleFeatureType.getCoordinateReferenceSystem, true)
        if (_EpsgCodeValue != null) {
          entityCRSAID = "EPSG:" + _EpsgCodeValue.intValue
        }
        geometryContentType = RealmConstant._GeospatialLLGeometryContent
      }
    }

    shpDataPropertyTypeMap.put(RealmConstant._GeospatialGeometryType, DataSlicePropertyType.STRING)
    shpDataPropertyTypeMap.put(geometryContentType, DataSlicePropertyType.STRING)

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
        newEntityValueMap.put(RealmConstant._GeospatialGeometryType, geometryTypeValue)
        newEntityValueMap.put(geometryContentType, geometryContent)
      }
      shpDataValueList.add(newEntityValueMap)
    }

    dataMaintenance.SpatialDataInfo(shpDataPropertyTypeMap,shpDataValueList)
  }

  def duplicateSpatialDataInfoToDataSlice(dataServiceInvoker:DataServiceInvoker, spatialDataInfo: SpatialDataInfo,
                                          dataSliceName:String, dataSliceGroupName:String, removeExistingData:Boolean, dataSlicePrimaryKeys:Array[String]):DataSlice = {
    val spatialDataValue = spatialDataInfo.spatialDataValue
    val spatialDataPropertiesDefinition = spatialDataInfo.spatialDataPropertiesDefinition

    val primaryKeysList: util.ArrayList[String] = new util.ArrayList[String]()
    if(dataSlicePrimaryKeys != null && dataSlicePrimaryKeys.size >0){
      dataSlicePrimaryKeys.foreach(key=>{
        primaryKeysList.add(key)
      })
    }else{
      primaryKeysList.add("DOCG_AutoGeneratedPrimaryKey")
      spatialDataPropertiesDefinition.put("DOCG_AutoGeneratedPrimaryKey",DataSlicePropertyType.INT)
    }

    var targetDataSlice = dataServiceInvoker.getDataSlice(dataSliceName)

    if(targetDataSlice == null){
      targetDataSlice = dataServiceInvoker.createGridDataSlice(dataSliceName,dataSliceGroupName,spatialDataPropertiesDefinition,primaryKeysList)
    }
    if(removeExistingData){
      targetDataSlice.emptyDataSlice()
    }
    var countIndex = 0;
    spatialDataValue.forEach(dataItem =>{
      if(dataSlicePrimaryKeys == null){
        dataItem.put("DOCG_AutoGeneratedPrimaryKey",countIndex)
        countIndex = countIndex+1
      }
      targetDataSlice.addDataRecord(dataItem.asInstanceOf[util.HashMap[String,Object]])
    })
    targetDataSlice
  }

  @throws(classOf[AnalysisProviderRuntimeException])
  def syncGeospatialConceptionKindToDataSlice(dataServiceInvoker:DataServiceInvoker, conceptionKindName: String, dataSliceName: String, dataSliceGroup: String,
                                              conceptionEntityPropertyMap: util.HashMap[String, DataSlicePropertyType],geospatialScaleLevel:GeospatialScaleLevel):DataSlice={
    val targetDataSlice = dataServiceInvoker.getDataSlice(dataSliceName)
    if(targetDataSlice != null){
      throw new AnalysisProviderRuntimeException("DataSlice with name "+dataSliceName +" already exist.")
    }

    val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
    dataSlicePropertyMap.put(RealmConstant._GeospatialGeometryType,DataSlicePropertyType.STRING)

    if(conceptionEntityPropertyMap != null){
      dataSlicePropertyMap.putAll(conceptionEntityPropertyMap);
    }

    geospatialScaleLevel match {
      case GlobalLevel =>
        dataSlicePropertyMap.put(RealmConstant._GeospatialGLGeometryContent,DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put(RealmConstant._GeospatialGlobalCRSAID,DataSlicePropertyType.STRING);
      case CountryLevel =>
        dataSlicePropertyMap.put(RealmConstant._GeospatialCLGeometryContent,DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put(RealmConstant._GeospatialCountryCRSAID,DataSlicePropertyType.STRING);
      case LocalLevel =>
        dataSlicePropertyMap.put(RealmConstant._GeospatialLLGeometryContent,DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put(RealmConstant._GeospatialLocalCRSAID,DataSlicePropertyType.STRING);
    }

    val dataSliceOperationResult =
      CoreRealmOperationUtil.syncConceptionKindToDataSlice(conceptionKindName,dataSliceName,dataSliceGroup,dataSlicePropertyMap)
    dataServiceInvoker.getDataSlice(dataSliceName)
  }




  def syncGeospatialScaleEntityToDataSlice(dataServiceInvoker:DataServiceInvoker, geospatialScaleGrade:GeospatialScaleGrade,
                                           dataSliceName: String, dataSliceGroup: String):DataSlice={
    val targetDataSlice = dataServiceInvoker.getDataSlice(dataSliceName)
    if(targetDataSlice != null){
      throw new AnalysisProviderRuntimeException("DataSlice with name "+dataSliceName +" already exist.")
    }

   // val dataSlicePropertyMap: util.HashMap[String, DataSlicePropertyType] = new util.HashMap[String, DataSlicePropertyType]()
   // dataSlicePropertyMap.put(RealmConstant._GeospatialGeometryType,DataSlicePropertyType.STRING)

    var innerDataKindName:String = null
    val dataSlicePropertyList: util.List[AttributeKind] = new util.ArrayList[AttributeKind]()

    /*

      propertiesMap.put("ISO_Code",_ISOCode);
                    propertiesMap.put(RealmConstant.GeospatialChineseNameProperty,_ChnName);
                    propertiesMap.put(RealmConstant.GeospatialEnglishNameProperty,_EngName);
                    propertiesMap.put("ChineseFullName",_ChnFullName);
                    propertiesMap.put(RealmConstant.GeospatialCodeProperty,_EngName);
                    propertiesMap.put(RealmConstant.GeospatialRegionProperty,geospatialRegionName);
                    propertiesMap.put(RealmConstant.GeospatialScaleGradeProperty, ""+GeospatialRegion.GeospatialScaleGrade.CONTINENT);

    */



    geospatialScaleGrade match {
      case GeospatialScaleGrade.CONTINENT =>
        innerDataKindName = RealmConstant.GeospatialScaleContinentEntityClass
        val isoCodeAttributeKind:AttributeKind = new Neo4JAttributeKindImpl(null,"ISO_Code",null, AttributeDataType.STRING,null)
        dataSlicePropertyList.add(isoCodeAttributeKind)

        /*
        dataSlicePropertyList.add("ISO_Code")
        dataSlicePropertyList.add("ChineseFullName")
        dataSlicePropertyList.add(RealmConstant.GeospatialChineseNameProperty)
        dataSlicePropertyList.add(RealmConstant.GeospatialEnglishNameProperty)
        dataSlicePropertyList.add(RealmConstant.GeospatialCodeProperty)
        dataSlicePropertyList.add(RealmConstant.GeospatialRegionProperty)
        dataSlicePropertyList.add(RealmConstant.GeospatialScaleGradeProperty)
        */


      case GeospatialScaleGrade.COUNTRY_REGION =>
        innerDataKindName = RealmConstant.GeospatialScaleCountryRegionEntityClass
      case GeospatialScaleGrade.PROVINCE =>
        innerDataKindName = RealmConstant.GeospatialScaleProvinceEntityClass
      case GeospatialScaleGrade.PREFECTURE =>
        innerDataKindName = RealmConstant.GeospatialScalePrefectureEntityClass
      case GeospatialScaleGrade.COUNTY =>
        innerDataKindName = RealmConstant.GeospatialScaleCountyEntityClass
      case GeospatialScaleGrade.TOWNSHIP =>
        innerDataKindName = RealmConstant.GeospatialScaleTownshipEntityClass
      case GeospatialScaleGrade.VILLAGE =>
        innerDataKindName = RealmConstant.GeospatialScaleVillageEntityClass
    }

    CoreRealmOperationUtil.loadInnerDataKindEntitiesToDataSlice(dataServiceInvoker,innerDataKindName,dataSlicePropertyList ,null,dataSliceName,true,10)

  /*
    geospatialScaleLevel match {
      case GlobalLevel =>
        dataSlicePropertyMap.put(RealmConstant._GeospatialGLGeometryContent,DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put(RealmConstant._GeospatialGlobalCRSAID,DataSlicePropertyType.STRING);
      case CountryLevel =>
        dataSlicePropertyMap.put(RealmConstant._GeospatialCLGeometryContent,DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put(RealmConstant._GeospatialCountryCRSAID,DataSlicePropertyType.STRING);
      case LocalLevel =>
        dataSlicePropertyMap.put(RealmConstant._GeospatialLLGeometryContent,DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put(RealmConstant._GeospatialLocalCRSAID,DataSlicePropertyType.STRING);
    }

    val dataSliceOperationResult =
      CoreRealmOperationUtil.syncConceptionKindToDataSlice(conceptionKindName,dataSliceName,dataSliceGroup,dataSlicePropertyMap)
    dataServiceInvoker.getDataSlice(dataSliceName)


    */
    dataServiceInvoker.getDataSlice(dataSliceName)
  }


}
