package specialPurposeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchDataOperationUtilPerformanceTest {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException {
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
}
