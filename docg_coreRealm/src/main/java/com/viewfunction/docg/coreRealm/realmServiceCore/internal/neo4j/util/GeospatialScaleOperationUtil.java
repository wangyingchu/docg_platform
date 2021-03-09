package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeospatialScaleOperationUtil {

    private static Logger logger = LoggerFactory.getLogger(GeospatialScaleOperationUtil.class);
    private static final String GEOSPATIAL_DATA_FOLDER = "geospatialData";
    public static final String GeospatialCodeProperty = "DOCG_GeospatialCode";
    public static final String GeospatialRegionProperty = "DOCG_GeospatialRegion";
    public static final String GeospatialScaleGradeProperty = "DOCG_GeospatialScaleGrade";

    public static void generateGeospatialScaleEntities(GraphOperationExecutor workingGraphOperationExecutor, String geospatialRegionName){
        generateGeospatialScaleEntities_Continent(workingGraphOperationExecutor,geospatialRegionName);
        generateGeospatialScaleEntities_CountryRegion(workingGraphOperationExecutor,geospatialRegionName);
        generateGeospatialScaleEntities_ProvinceOfChina(workingGraphOperationExecutor,geospatialRegionName);
    }

    private static void generateGeospatialScaleEntities_Continent(GraphOperationExecutor workingGraphOperationExecutor, String geospatialRegionName){
        GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialRegionClass,workingGraphOperationExecutor);
        String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialRegionClass,RealmConstant._NameProperty,geospatialRegionName,1);
        Object getGeospatialRegionRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer,queryCql);
        String geospatialRegionUID = null;

        if(getGeospatialRegionRes != null){
            ConceptionEntity geospatialRegionEntity = (ConceptionEntity) getGeospatialRegionRes;
            geospatialRegionUID = geospatialRegionEntity.getConceptionEntityUID();
        }

        getSingleConceptionEntityTransformer =
                new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleContinentEntityClass,workingGraphOperationExecutor);

        String[] conceptionTypeNameArray = new String[2];
        conceptionTypeNameArray[0] = RealmConstant.GeospatialScaleEntityClass;
        conceptionTypeNameArray[1] = RealmConstant.GeospatialScaleContinentEntityClass;

        File file = new File(PropertiesHandler.SYSTEM_RESOURCE_ROOT+"/"+GEOSPATIAL_DATA_FOLDER+"/"+"ContinentsData.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String currentLine = !tempStr.startsWith("# ISO Code")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems = currentLine.split("\t");
                    String _ISOCode = dataItems[0];
                    String _EngName = dataItems[1];
                    String _ChnName = dataItems[2];
                    String _ChnFullName = dataItems[3];

                    Map<String,Object> propertiesMap = new HashMap<>();
                    propertiesMap.put("ISO_Code",_ISOCode);
                    propertiesMap.put("ChineseName",_ChnName);
                    propertiesMap.put("EnglishName",_EngName);
                    propertiesMap.put("ChineseFullName",_ChnFullName);
                    propertiesMap.put(GeospatialCodeProperty,_EngName);
                    propertiesMap.put(GeospatialRegionProperty,geospatialRegionName);
                    propertiesMap.put(GeospatialScaleGradeProperty, ""+GeospatialRegion.GeospatialScaleGrade.CONTINENT);

                    String createGeospatialScaleEntitiesCql = CypherBuilder.createLabeledNodeWithProperties(conceptionTypeNameArray,propertiesMap);
                    Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,createGeospatialScaleEntitiesCql);

                    if(newEntityRes != null && geospatialRegionUID != null){
                        ConceptionEntity geospatialScaleEntity = (ConceptionEntity) newEntityRes;
                        geospatialScaleEntity.attachToRelation(geospatialRegionUID, RealmConstant.GeospatialScale_SpatialContainsRelationClass, null, true);
                    }
                }
            }
            reader.close();
        } catch (
                IOException | CoreRealmServiceRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static void generateGeospatialScaleEntities_CountryRegion(GraphOperationExecutor workingGraphOperationExecutor, String geospatialRegionName){
        Map<String,String> ContinentCode_EntityUIDMap = new HashMap<>();
        String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleContinentEntityClass,GeospatialRegionProperty,geospatialRegionName,100000);

        GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(RealmConstant.GeospatialScaleContinentEntityClass,workingGraphOperationExecutor);
        Object resultEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
        if(resultEntityList != null){
            List<ConceptionEntity> resultContinentList =  (List<ConceptionEntity>)resultEntityList;
            for(ConceptionEntity currentConceptionEntity : resultContinentList){
                ContinentCode_EntityUIDMap.put(
                    currentConceptionEntity.getAttribute(GeospatialCodeProperty).getAttributeValue().toString(),
                        currentConceptionEntity.getConceptionEntityUID());
            }
        }

        String[] conceptionTypeNameArray = new String[2];
        conceptionTypeNameArray[0] = RealmConstant.GeospatialScaleEntityClass;
        conceptionTypeNameArray[1] = RealmConstant.GeospatialScaleCountryRegionEntityClass;

        File file = new File(PropertiesHandler.SYSTEM_RESOURCE_ROOT+"/"+GEOSPATIAL_DATA_FOLDER+"/"+"CountriesAndRegionsData(ISO_3166_1).txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if(currentLine != null){
                    String[] countriesAndRegionInfoValueArray = currentLine.split("\\|");

                    Map<String,Object> propertiesMap = new HashMap<>();
                    String continentEntityUID = null;
                    String _2bitCode = countriesAndRegionInfoValueArray[0].trim();
                    String _3bitCode = countriesAndRegionInfoValueArray[1].trim();
                    String _number = countriesAndRegionInfoValueArray[2].trim();
                    String _ISO3122_2Code = countriesAndRegionInfoValueArray[3].trim();
                    String _EnglishName = countriesAndRegionInfoValueArray[4].trim();
                    String _ChineseName = countriesAndRegionInfoValueArray[5].trim();

                    propertiesMap.put("Alpha_2Code",_2bitCode);
                    propertiesMap.put("Alpha_3Code",_3bitCode);
                    propertiesMap.put("NumericCode",_number);
                    propertiesMap.put("ISO3166_2Code",_ISO3122_2Code);
                    propertiesMap.put("EnglishName",_EnglishName);
                    propertiesMap.put("ChineseName",_ChineseName);
                    propertiesMap.put("Standard","ISO 3166-1:2013");
                    propertiesMap.put("StandardStatus","Officially assigned");

                    if(!"-".equals(countriesAndRegionInfoValueArray[6].trim())){
                        String _belongedContinent = countriesAndRegionInfoValueArray[6].trim();
                        propertiesMap.put("belongedContinent",_belongedContinent);
                        continentEntityUID = ContinentCode_EntityUIDMap.get(_belongedContinent);
                    }
                    if(!"-".equals(countriesAndRegionInfoValueArray[7].trim())){
                        String _capitalChineseName = countriesAndRegionInfoValueArray[7].trim();
                        propertiesMap.put("capitalChineseName",_capitalChineseName);
                    }
                    if(!"-".equals(countriesAndRegionInfoValueArray[8].trim())){
                        String _capitalEnglishName = countriesAndRegionInfoValueArray[8].trim();
                        propertiesMap.put("capitalEnglishName",_capitalEnglishName);
                    }

                    propertiesMap.put(GeospatialCodeProperty,_2bitCode);
                    propertiesMap.put(GeospatialRegionProperty,geospatialRegionName);
                    propertiesMap.put(GeospatialScaleGradeProperty, ""+GeospatialRegion.GeospatialScaleGrade.COUNTRY_REGION);

                    GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                            new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleCountryRegionEntityClass,workingGraphOperationExecutor);

                    String createGeospatialScaleEntitiesCql = CypherBuilder.createLabeledNodeWithProperties(conceptionTypeNameArray,propertiesMap);
                    Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,createGeospatialScaleEntitiesCql);

                    if(newEntityRes != null && continentEntityUID != null){
                        ConceptionEntity geospatialEntity = (ConceptionEntity) newEntityRes;
                        geospatialEntity.attachToRelation(continentEntityUID, RealmConstant.GeospatialScale_SpatialContainsRelationClass, null, true);
                    }
                }
            }
            reader.close();
        } catch (IOException | CoreRealmServiceRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static void generateGeospatialScaleEntities_ProvinceOfWorld(GraphOperationExecutor workingGraphOperationExecutor, String geospatialRegionName){

        Map<String,Map<String,Object>> _ProvincesISO_3166_2DataMap = generateStates_ProvincesISO_3166_2DataMap();


        String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleCountryRegionEntityClass,GeospatialRegionProperty,geospatialRegionName,100000);

        GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(RealmConstant.GeospatialScaleCountryRegionEntityClass,workingGraphOperationExecutor);
        Object resultEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
        if(resultEntityList != null){
            List<ConceptionEntity> resultContinentList =  (List<ConceptionEntity>)resultEntityList;
            for(ConceptionEntity currentConceptionEntity : resultContinentList){
                xxxx(currentConceptionEntity,_ProvincesISO_3166_2DataMap);
            }


            System.out.println(resultContinentList.size());


        }

    }

    private static void xxxx(ConceptionEntity _CountryRegionConceptionEntity,Map<String,Map<String,Object>> _ProvincesDataMap){
        String _CountryRegionAlpha_2Code = _CountryRegionConceptionEntity.getAttribute("Alpha_2Code").getAttributeValue().toString();
        String filePath =
                PropertiesHandler.SYSTEM_RESOURCE_ROOT+"/"+GEOSPATIAL_DATA_FOLDER+"/statesAndProvinces/states_provinces(ISO_3166_2)/"+_CountryRegionAlpha_2Code+".csv";
        File file = new File(filePath);
        if (file.exists()){
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempStr;
                while ((tempStr = reader.readLine()) != null) {
                    String[] iso_3166_2DataArray = tempStr.split("\\|");
                    String subdivisionCategory = iso_3166_2DataArray[0];
                    String _3166_2Code = iso_3166_2DataArray[1];
                    String subdivisionName = iso_3166_2DataArray[2];
                    String localVariant = iso_3166_2DataArray[3];
                    String languageCode = iso_3166_2DataArray[4];
                    //String romanizationSystem = iso_3166_2DataArray[5];
                    //String parentSubdivision = iso_3166_2DataArray[6];

                    //System.out.println(subdivisionCategory+"-"+_3166_2Code+"="+subdivisionName);

                    _3166_2Code=_3166_2Code.replace("*","");

                    Map<String,Object> _currentProvincesDataMap =  _ProvincesDataMap.get(_3166_2Code);
                    //System.out.println(_currentProvincesDataMap);

                    if(_currentProvincesDataMap == null){
                        System.out.println(_CountryRegionAlpha_2Code+" NOTFOUND  "+_3166_2Code);
                    }

                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private static void generateGeospatialScaleEntities_ProvinceOfChina(GraphOperationExecutor workingGraphOperationExecutor, String geospatialRegionName) {
        String _ChinaGeospatialEntityUID = null;
        try {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(1);
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem(GeospatialCodeProperty,"CN"));
            queryParameters.addFilteringItem(new EqualFilteringItem(GeospatialRegionProperty,geospatialRegionName), QueryParameters.FilteringLogic.AND);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleCountryRegionEntityClass,queryParameters,null);
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                    new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleCountryRegionEntityClass,workingGraphOperationExecutor);

            Object geospatialEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,queryCql);
            if(geospatialEntityRes != null ){
                ConceptionEntity geospatialEntity = (ConceptionEntity) geospatialEntityRes;
                _ChinaGeospatialEntityUID = geospatialEntity.getConceptionEntityUID();
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }

        String[] conceptionTypeNameArray = new String[2];
        conceptionTypeNameArray[0] = RealmConstant.GeospatialScaleEntityClass;
        conceptionTypeNameArray[1] = RealmConstant.GeospatialScaleProvinceEntityClass;

        File file = new File(PropertiesHandler.SYSTEM_RESOURCE_ROOT+"/"+GEOSPATIAL_DATA_FOLDER+"/"+"ChinaAdministrativeDivisionData(ISO_3166_2,GB_T_2260).txt");
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;

            while ((tempStr = reader.readLine()) != null) {
                String currentLine = !tempStr.startsWith("# DivisionCategory_EN")? tempStr : null;
                if(currentLine != null){
                    String[] administrativeDivisionInfoValueArray = currentLine.split("\\|");

                    Map<String,Object> propertiesMap = new HashMap<>();

                    String _DivisionCategory_EN = administrativeDivisionInfoValueArray[0].trim();
                    String _DivisionCategory_CH = administrativeDivisionInfoValueArray[1].trim();
                    String _ISO3166_2SubDivisionCode = administrativeDivisionInfoValueArray[2].trim();
                    String DivisionName_EN = administrativeDivisionInfoValueArray[3].trim();
                    String ChinaDivisionCode = administrativeDivisionInfoValueArray[4].trim();
                    String DivisionName_CH = administrativeDivisionInfoValueArray[5].trim();

                    propertiesMap.put("DivisionCategory_EN",_DivisionCategory_EN);
                    propertiesMap.put("DivisionCategory_CH",_DivisionCategory_CH);
                    propertiesMap.put("ISO3166_2SubDivisionCode",_ISO3166_2SubDivisionCode);
                    propertiesMap.put("ChinaDivisionCode",ChinaDivisionCode);
                    propertiesMap.put("EnglishName",DivisionName_EN);
                    propertiesMap.put("ChineseName",DivisionName_CH);
                    propertiesMap.put("Standard","GB/T 2260 | ISO 3166-2:2013");
                    propertiesMap.put("StandardStatus","Officially assigned");

                    propertiesMap.put(GeospatialCodeProperty,ChinaDivisionCode);
                    propertiesMap.put(GeospatialRegionProperty,geospatialRegionName);
                    propertiesMap.put(GeospatialScaleGradeProperty, ""+GeospatialRegion.GeospatialScaleGrade.PROVINCE);

                    GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                            new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleProvinceEntityClass,workingGraphOperationExecutor);

                    String createGeospatialScaleEntitiesCql = CypherBuilder.createLabeledNodeWithProperties(conceptionTypeNameArray,propertiesMap);
                    Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,createGeospatialScaleEntitiesCql);

                    if(newEntityRes != null && _ChinaGeospatialEntityUID != null){
                        ConceptionEntity geospatialEntity = (ConceptionEntity) newEntityRes;
                        geospatialEntity.attachToRelation(_ChinaGeospatialEntityUID, RealmConstant.GeospatialScale_SpatialContainsRelationClass, null, true);
                    }
                }
            }
            reader.close();
        } catch (IOException | CoreRealmServiceRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static Map<String,Map<String,Object>> generateStates_ProvincesISO_3166_2DataMap(){
        Map<String,Map<String,Object>> _ProvincesISO_3166_2DataMap = new HashMap<>();
        String filePath =
                PropertiesHandler.SYSTEM_RESOURCE_ROOT+"/"+GEOSPATIAL_DATA_FOLDER+"/statesAndProvinces/ne_10m_admin_1_states_provinces/"+"ne_10m_admin_1_states_provinces.shp";
        SimpleFeatureCollection colls = readShp(filePath,null);
        SimpleFeatureIterator iters = colls.features();

        while(iters.hasNext()){
            SimpleFeature sf = iters.next();
            Map<String,Object> _ISO_3166_2Data = new HashMap<>();
            String iso_3166_2Code = sf.getAttribute("iso_3166_2").toString();

            iso_3166_2Code = iso_3166_2Code.replace("~","");



            _ProvincesISO_3166_2DataMap.put(iso_3166_2Code,_ISO_3166_2Data);

/*
            System.out.println(sf.getID() + " , " + sf.getAttributes());
            System.out.println();System.out.println();
            System.out.println(sf.getDefaultGeometry());
            System.out.println(sf.getAttribute("iso_3166_2")+" ------- "+sf.getAttribute("the_geom"));
*/
            _ISO_3166_2Data.put("the_geom",sf.getAttribute("the_geom"));
            _ISO_3166_2Data.put("name_en",sf.getAttribute("iso_3166_2"));
            _ISO_3166_2Data.put("name_zh",sf.getAttribute("name_zh"));
            _ISO_3166_2Data.put("gns_name",sf.getAttribute("gns_name"));
            _ISO_3166_2Data.put("geonunit",sf.getAttribute("geonunit"));
            _ISO_3166_2Data.put("latitude",sf.getAttribute("latitude"));
            _ISO_3166_2Data.put("longitude",sf.getAttribute("longitude"));
            _ISO_3166_2Data.put("iso_a2",sf.getAttribute("iso_a2"));
            _ISO_3166_2Data.put("name_local",sf.getAttribute("name_local"));
            _ISO_3166_2Data.put("type",sf.getAttribute("type"));
            _ISO_3166_2Data.put("type_en",sf.getAttribute("type_en"));
            _ISO_3166_2Data.put("gn_name",sf.getAttribute("gn_name"));
            _ISO_3166_2Data.put("woe_label",sf.getAttribute("woe_label"));
            _ISO_3166_2Data.put("woe_name",sf.getAttribute("woe_name"));
            //_ISO_3166_2Data.put("name_en",sf.getAttribute("iso_3166_2"));
           // _ISO_3166_2Data.put("name_en",sf.getAttribute("iso_3166_2"));


            //"name_en","name_zh","gns_name","geonunit","latitude","longitude","iso_a2","name_local","type","type_en",""gn_name","woe_label","woe_name"
        }
        return _ProvincesISO_3166_2DataMap;
    }

    private static SimpleFeatureCollection  readShp(String path , Filter filter){
        SimpleFeatureSource featureSource = readStoreByShp(path);
        if(featureSource == null){
            return null;
        }
        try {
            return filter != null ? featureSource.getFeatures(filter) : featureSource.getFeatures() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }

    private static  SimpleFeatureSource readStoreByShp(String path ){
        File file = new File(path);
        FileDataStore store;
        SimpleFeatureSource featureSource = null;
        try {
            store = FileDataStoreFinder.getDataStore(file);
            ((ShapefileDataStore) store).setCharset(Charset.forName("UTF-8"));
            featureSource = store.getFeatureSource();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return featureSource;
    }

    public static void main(String[] args){
        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        //generateGeospatialScaleEntities_Continent(graphOperationExecutor,"DefaultGeospatialRegion");
        //generateGeospatialScaleEntities_CountryRegion(graphOperationExecutor,"DefaultGeospatialRegion");
        //generateGeospatialScaleEntities_ProvinceOfChina(graphOperationExecutor,"DefaultGeospatialRegion");

        generateGeospatialScaleEntities_ProvinceOfWorld(graphOperationExecutor,"DefaultGeospatialRegion");
        graphOperationExecutor.close();

        //generateStates_ProvincesISO_3166_2DataMap();
    }
}
