package specialPurposeTestCase;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchDataOperationUtilPerformanceTest {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //batchAddNewEntitiesTest();
        batchAttachNewRelations();
    }

    public static void batchAddNewEntitiesTest() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind _operationType = coreRealm.getConceptionKind("BatchInsertOpType");
        if(_operationType != null){
            _operationType.purgeAllEntities();
        }else{
            _operationType = coreRealm.createConceptionKind("BatchInsertOpType","BatchInsertOpType"+"DESC");
        }

        List<ConceptionEntityValue> conceptionEntityValuesList = new ArrayList<>();
        for(int i =0 ;i< 1000300; i++){
            ConceptionEntityValue currentConceptionEntityValue = new ConceptionEntityValue();
            Map<String,Object> attributeMap = new HashMap<>();
            String parentElementId = "elementId"+(int)(Math.random()*5000);
            attributeMap.put("pAtter01","pAtter01Value"+i);
            attributeMap.put("pAtter02","pAtter02Value"+i);
            attributeMap.put("pAtter03","pAtter03Value"+i);
            attributeMap.put("pAtter04","pAtter04Value"+i);
            attributeMap.put("pAtter05","pAtter05Value"+i);
            attributeMap.put("pAtter06","pAtter06Value"+i);
            attributeMap.put("parentElementId",parentElementId);
            currentConceptionEntityValue.setEntityAttributesValue(attributeMap);
            conceptionEntityValuesList.add(currentConceptionEntityValue);
        }


        Map<String,Object> batchAddResult = BatchDataOperationUtil.batchAddNewEntities("BatchInsertOpType",conceptionEntityValuesList,18);
        System.out.println(batchAddResult);
    }

    public static void batchAttachNewRelations() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind _operationType = coreRealm.getConceptionKind("BatchInsertOpType");

        QueryParameters _QueryParameters = new QueryParameters();
        _QueryParameters.setResultNumber(1000300);

        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult = _operationType.getEntities(_QueryParameters);
        System.out.println(conceptionEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount());
        List<ConceptionEntity> conceptionEntityList = conceptionEntitiesRetrieveResult.getConceptionEntities();
        List<List<ConceptionEntity>> rsList = Lists.partition(conceptionEntityList, conceptionEntityList.size()/2);
        List<ConceptionEntity> fromList = rsList.get(0);
        List<ConceptionEntity> toList = rsList.get(1);
        List<RelationEntityValue> relationEntityValueList = new ArrayList<>();

        for(int i=0;i<fromList.size();i++){
            Map<String,Object> relationPropertiesMap = new HashMap<>();
            relationPropertiesMap.put("text01","text01"+i);
            relationPropertiesMap.put("int01",1000+i);
            RelationEntityValue relationEntityValue = new RelationEntityValue();
            relationEntityValue.setFromConceptionEntityUID(fromList.get(i).getConceptionEntityUID());
            relationEntityValue.setToConceptionEntityUID(toList.get(i).getConceptionEntityUID());
            relationEntityValue.setEntityAttributesValue(relationPropertiesMap);
            relationEntityValueList.add(relationEntityValue);
        }
        Map<String,Object> batchOperationResult = BatchDataOperationUtil.batchAttachNewRelations(relationEntityValueList,"batchAttachRelType01",18);
        System.out.println(batchOperationResult);
    }
}
