package com.viewfunction.docg.realmExample.tools;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.referencing.CRS;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SHP_DataSourceImport {

    public static void main(String[] args) throws IOException, FactoryException, CoreRealmServiceRuntimeException {
        //String pathName = "/home/wangychu/Desktop/SEATTLE-GIS/Historic_Landslide_Locations_ECA/Historic_Landslide_Locations_ECA.shp";
        //String pathName = "/home/wangychu/Desktop/SEATTLE-GIS/Citywide_Green_Storm_Infrastructure/Citywide_Green_Storm_Infrastructure.shp";
        //String pathName = "/home/wangychu/Desktop/SEATTLE-GIS/Cadastral_Control_Lines/Cadastral_Control_Lines.shp";
        String pathName = "/home/wangychu/Desktop/SEATTLE-GIS/Seattle_Parks_and_Recreation_GIS_Map_Layer_Shapefile_Park_Boundary/geo_export_082187a2-56b7-4e45-a11c-58198c390817.shp";
        //String pathName = "/media/wangychu/Data/Nutstore/区划/县.shp";
        //String pathName = "/home/wangychu/Desktop/CQ-GS/GYD-GIS_WGS84/gyd-roadAndBuilding.shp";
        File file = new File(pathName);
        //importSHPDataToConceptionKind("HistoricLandslideLocations",true,file,null);
        //importSHPDataToConceptionKind("CitywideGreenStormInfrastructure",true,file,null);
        //importSHPDataToConceptionKind("CadastralControlLines",true,file,null);
        importSHPDataToConceptionKind("SeattleParksAndRecreation",true,file,null);
    }

    private static void importSHPDataToConceptionKind(String conceptionKindName,boolean removeExistData,File shpFile,String fileEncode) throws IOException, FactoryException, CoreRealmServiceRuntimeException {
        String charsetEncode = fileEncode != null ?  fileEncode : "UTF-8";
        // 读取到数据存储中
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(shpFile);
        ((ShapefileDataStore) dataStore).setCharset(Charset.forName(charsetEncode));
        // 获取特征资源
        SimpleFeatureSource simpleFeatureSource = dataStore.getFeatureSource();
        String conceptionKindNameValue = conceptionKindName != null ? conceptionKindName : simpleFeatureSource.getName().toString();
        SimpleFeatureType simpleFeatureType = dataStore.getSchema();
        String _CRSName = simpleFeatureType.getCoordinateReferenceSystem().getName().getCode();

        System.out.println(_CRSName);
        System.out.println(_CRSName);
        System.out.println(_CRSName);

        String entityCRSAID = null;
        String _CRS_Range = null;
        if("GCS_WGS_1984".equals(_CRSName)||_CRSName.contains("WGS84")){
            entityCRSAID= "EPSG:4326";
            _CRS_Range = "GlobalLevel";
        }else if("CGCS_2000".equals(_CRSName)||_CRSName.contains("CGCS2000")){
            entityCRSAID= "EPSG:4545";
            _CRS_Range = "CountryLevel";
        }else{
            _CRS_Range = "LocalLevel";
            Integer _EpsgCodeValue = CRS.lookupEpsgCode(simpleFeatureType.getCoordinateReferenceSystem(),true);
            if(_EpsgCodeValue != null){
                entityCRSAID= "EPSG:"+_EpsgCodeValue.intValue();
            }
        }
        // 要素集合
        SimpleFeatureCollection simpleFeatureCollection = simpleFeatureSource.getFeatures();
        List<ConceptionEntityValue> _targetConceptionEntityValueList = Lists.newArrayList();
        // 获取要素迭代器
        SimpleFeatureIterator featureIterator = simpleFeatureCollection.features();
        while(featureIterator.hasNext()){
            Map<String,Object> newEntityValueMap = new HashMap<>();
            // 要素对象
            SimpleFeature feature = featureIterator.next();
            // 要素属性信息，名称，值，类型
            List<Property> propertyList = (List<Property>) feature.getValue();
            for(Property property : propertyList){
                String propertyName = property.getName().toString();
                Object propertyValue = property.getValue();
                if(propertyValue != null){
                    newEntityValueMap.put(propertyName,propertyValue);
                }
            }

            String geometryContent = feature.getDefaultGeometry().toString();
            GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();
            String geometryType = geometryAttribute.getType().getName().toString();
            String geometryTypeValue = "GEOMETRYCOLLECTION";
            if("Point".equals(geometryType)){
                geometryTypeValue ="POINT";
            }
            if("MultiPoint".equals(geometryType)){
                geometryTypeValue ="MULTIPOINT";
            }
            if("LineString".equals(geometryType)){
                geometryTypeValue ="LINESTRING";
            }
            if("MultiLineString".equals(geometryType)){
                geometryTypeValue ="MULTILINESTRING";
            }
            if("Polygon".equals(geometryType)){
                geometryTypeValue ="POLYGON";
            }
            if("MultiPolygon".equals(geometryType)){
                geometryTypeValue ="MULTIPOLYGON";
            }

            newEntityValueMap.put("DOCG_GS_GeometryType",geometryTypeValue);
            if(_CRS_Range.equals("GlobalLevel")){
                newEntityValueMap.put("DOCG_GS_GLGeometryContent",geometryContent);
                newEntityValueMap.put("DOCG_GS_GlobalCRSAID",entityCRSAID);
            }
            if(_CRS_Range.equals("CountryLevel")){
                newEntityValueMap.put("DOCG_GS_CLGeometryContent",geometryContent);
                newEntityValueMap.put("DOCG_GS_CountryCRSAID",entityCRSAID);
            }
            if(_CRS_Range.equals("LocalLevel")){
                newEntityValueMap.put("DOCG_GS_LLGeometryContent",geometryContent);
                if(entityCRSAID != null){
                    newEntityValueMap.put("DOCG_GS_LocalCRSAID",entityCRSAID);
                }
            }
            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
            _targetConceptionEntityValueList.add(conceptionEntityValue);
        }
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind targetConceptionType = coreRealm.getConceptionKind(conceptionKindNameValue);
        if(targetConceptionType != null){
            if(removeExistData){
                targetConceptionType.purgeAllEntities();
            }
        }else{
            coreRealm.createConceptionKind(conceptionKindNameValue,"-");
        }
        BatchDataOperationUtil.batchAddNewEntities(conceptionKindNameValue,_targetConceptionEntityValueList,10);
        //featureIterator.close();
    }
}
