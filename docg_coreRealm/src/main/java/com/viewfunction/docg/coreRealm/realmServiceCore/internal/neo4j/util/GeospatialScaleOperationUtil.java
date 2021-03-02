package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleGeospatialRegionTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeospatialScaleOperationUtil {

    private static Logger logger = LoggerFactory.getLogger(GeospatialScaleOperationUtil.class);
    private static final String GEOSPATIAL_DATA_FOLDER = "geospatialData";

    public static void generateGeospatialScaleEntities(GraphOperationExecutor workingGraphOperationExecutor, String geospatialRegionName){

    }

    private static void generateGeospatialScaleEntities_Continent(GraphOperationExecutor workingGraphOperationExecutor, String geospatialRegionName){
        File file = new File(PropertiesHandler.SYSTEM_RESOURCE_ROOT+"/"+GEOSPATIAL_DATA_FOLDER+"/"+"ContinentsData.txt");
        String[] conceptionTypeNameArray = new String[2];
        conceptionTypeNameArray[0] = RealmConstant.GeospatialScaleEntityClass;
        conceptionTypeNameArray[1] = RealmConstant.GeospatialScaleContinentEntityClass;

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
                    propertiesMap.put("code",_EngName);
                    propertiesMap.put("geospatialRegion",geospatialRegionName);
                    propertiesMap.put("geospatialScaleGrade", ""+GeospatialRegion.GeospatialScaleGrade.CONTINENT);

                    String createGeospatialScaleEntitiesCql = CypherBuilder.createLabeledNodeWithProperties(conceptionTypeNameArray,propertiesMap);
                    Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,createGeospatialScaleEntitiesCql);

                    if(newEntityRes != null && geospatialRegionUID != null){
                        ConceptionEntity timeScaleEventEntity = (ConceptionEntity) newEntityRes;
                        timeScaleEventEntity.attachToRelation(geospatialRegionUID, RealmConstant.GeospatialScale_SpatialContainsRelationClass, null, true);
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

    private static DataTransformer getSilentOperationDataTransformer(){
        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                return null;
            }
        };
        return dataTransformer;
    }


public static void main(String[] args){
    GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
    generateGeospatialScaleEntities_Continent(graphOperationExecutor,"DefaultGeospatialRegion");
    graphOperationExecutor.close();
}





}
