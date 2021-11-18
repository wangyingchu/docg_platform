package specialPurposeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceAssessmentTestCase {

    private static List<String> elementIdList = new ArrayList<>();
    private static String BuildingElementType = "BuildingElementType";
    private static String ElementProperties = "ElementProperties";
    private static String HasProperty = "HasProperty";

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException, CoreRealmServiceEntityExploreException {
        setTypeDefinitions();
        List<ConceptionEntityValue> conceptionEntityValuesList1 = new ArrayList<>();
        List<ConceptionEntityValue> conceptionEntityValuesList2 = new ArrayList<>();
        prepareConceptionKindDataMap(conceptionEntityValuesList1,conceptionEntityValuesList2);
        loadData(conceptionEntityValuesList1,conceptionEntityValuesList2);
        linkData();
        deleteData();
    }

    private static void setTypeDefinitions() throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _BuildingElementType = coreRealm.getConceptionKind(BuildingElementType);
        if(_BuildingElementType != null){
            _BuildingElementType.purgeAllEntities();
        }else{
            _BuildingElementType = coreRealm.createConceptionKind(BuildingElementType,BuildingElementType+"DESC");
        }

        ConceptionKind _ElementProperties = coreRealm.getConceptionKind(ElementProperties);
        if(_ElementProperties != null){
            _ElementProperties.purgeAllEntities();
        }else{
            _BuildingElementType = coreRealm.createConceptionKind(ElementProperties,ElementProperties+"DESC");
        }

        RelationKind _HasProperty = coreRealm.getRelationKind(HasProperty);
        if(_ElementProperties == null){
            _HasProperty = coreRealm.createRelationKind(HasProperty,HasProperty+"DESC");
        }
    }

    private static void loadData(List<ConceptionEntityValue> conceptionEntityValuesList1,
                                List<ConceptionEntityValue> conceptionEntityValuesList2){
        BatchDataOperationUtil.batchAddNewEntities(BuildingElementType,conceptionEntityValuesList1,5);
        Map<String,Object> batchLoadResultMap = BatchDataOperationUtil.batchAddNewEntities(ElementProperties,conceptionEntityValuesList2,30);
        System.out.println(batchLoadResultMap);
    }

    private static void prepareConceptionKindDataMap(List<ConceptionEntityValue> conceptionEntityValuesList1,
                                             List<ConceptionEntityValue> conceptionEntityValuesList2){
        elementIdList.clear();
        for(int i =0 ;i< 5000; i++){
            ConceptionEntityValue currentConceptionEntityValue1 = new ConceptionEntityValue();
            Map<String,Object> attributeMap = new HashMap<>();
            attributeMap.put("atter01","atter01Value"+i);
            attributeMap.put("atter02","atter02Value"+i);
            attributeMap.put("atter03","atter03Value"+i);
            attributeMap.put("atter04","atter04Value"+i);
            attributeMap.put("atter05","atter05Value"+i);
            attributeMap.put("atter06","atter06Value"+i);
            attributeMap.put("elementId","elementId"+i);
            elementIdList.add("elementId"+i);
            currentConceptionEntityValue1.setEntityAttributesValue(attributeMap);
            conceptionEntityValuesList1.add(currentConceptionEntityValue1);
        }
        for(int i =0 ;i< 140000; i++){
            ConceptionEntityValue currentConceptionEntityValue2 = new ConceptionEntityValue();
            Map<String,Object> attributeMap = new HashMap<>();
            String parentElementId = "elementId"+(int)(Math.random()*5000);
            attributeMap.put("pAtter01","pAtter01Value"+i);
            attributeMap.put("pAtter02","pAtter02Value"+i);
            attributeMap.put("pAtter03","pAtter03Value"+i);
            attributeMap.put("pAtter04","pAtter04Value"+i);
            attributeMap.put("pAtter05","pAtter05Value"+i);
            attributeMap.put("pAtter06","pAtter06Value"+i);
            attributeMap.put("parentElementId",parentElementId);
            currentConceptionEntityValue2.setEntityAttributesValue(attributeMap);
            conceptionEntityValuesList2.add(currentConceptionEntityValue2);
        }
    }

    private static void linkData() throws CoreRealmServiceEntityExploreException {
        /*
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();

        Map<String,String> _BuildingElementId_UIDMapping = new HashMap<>();
        QueryParameters exploreParameters = new QueryParameters();
        exploreParameters.setResultNumber(1000000);

        ConceptionKind _BuildingElementType = coreRealm.getConceptionKind(BuildingElementType);
        List<String> _BuildingElementTypeAttributeList = new ArrayList<>();
        _BuildingElementTypeAttributeList.add("elementId");
        ConceptionEntitiesAttributesRetrieveResult _ConceptionEntitiesAttributesRetrieveResult1 =
                _BuildingElementType.getSingleValueEntityAttributesByAttributeNames(_BuildingElementTypeAttributeList,exploreParameters);
        List<ConceptionEntityValue> conceptionEntityValues1 = _ConceptionEntitiesAttributesRetrieveResult1.getConceptionEntityValues();

        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValues1){
            String conceptionEntityUID = currentConceptionEntityValue.getConceptionEntityUID();
            String elementId = currentConceptionEntityValue.getEntityAttributesValue().get("elementId").toString();
            _BuildingElementId_UIDMapping.put(elementId,conceptionEntityUID);
        }

        List<RelationEntityValue> relationEntityValueList = new ArrayList<>();

        ConceptionKind _ElementProperties = coreRealm.getConceptionKind(ElementProperties);
        List<String> _ElementPropertiesTypeAttributeList = new ArrayList<>();
        _ElementPropertiesTypeAttributeList.add("parentElementId");
        ConceptionEntitiesAttributesRetrieveResult _ConceptionEntitiesAttributesRetrieveResult2 =
                _ElementProperties.getSingleValueEntityAttributesByAttributeNames(_ElementPropertiesTypeAttributeList,exploreParameters);

        List<ConceptionEntityValue> conceptionEntityValues2 = _ConceptionEntitiesAttributesRetrieveResult2.getConceptionEntityValues();
        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValues2){
            String conceptionEntityUID_Param = currentConceptionEntityValue.getConceptionEntityUID();
            String parentElementId = currentConceptionEntityValue.getEntityAttributesValue().get("parentElementId").toString();
            String conceptionEntityUID_building = _BuildingElementId_UIDMapping.get(parentElementId);
            if(conceptionEntityUID_building != null){
                RelationEntityValue relationEntityValue = new RelationEntityValue(null,conceptionEntityUID_building,conceptionEntityUID_Param,null);
                relationEntityValueList.add(relationEntityValue);
            }

        }
        coreRealm.closeGlobalSession();
        Map<String,Object> batchLoadResultMap = BatchDataOperationUtil.batchAttachNewRelations(relationEntityValueList,HasProperty,30);
        System.out.println(batchLoadResultMap);

        above logic is equal to below method invoke
        */
        Map<String,Object> batchLoadResultMap = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                BuildingElementType,null,"elementId",ElementProperties,
                null,"parentElementId",HasProperty,30);
        System.out.println(batchLoadResultMap);
    }

    private static void deleteData() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        ConceptionKind _ElementProperties = coreRealm.getConceptionKind(ElementProperties);
        QueryParameters exploreParameters = new QueryParameters();
        exploreParameters.setResultNumber(1000000);
        ConceptionEntitiesRetrieveResult _ConceptionEntitiesRetrieveResult = _ElementProperties.getEntities(exploreParameters);
        List<String> entityUIDList = new ArrayList<>();
        List<ConceptionEntity> resultEntityList = _ConceptionEntitiesRetrieveResult.getConceptionEntities();
        for(ConceptionEntity currentConceptionEntity:resultEntityList){
            entityUIDList.add(currentConceptionEntity.getConceptionEntityUID());
        }
        coreRealm.closeGlobalSession();

        Map<String,Object> batchLoadResultMap = BatchDataOperationUtil.batchDeleteEntities(entityUIDList,30);
        System.out.println(batchLoadResultMap);
    }
}
