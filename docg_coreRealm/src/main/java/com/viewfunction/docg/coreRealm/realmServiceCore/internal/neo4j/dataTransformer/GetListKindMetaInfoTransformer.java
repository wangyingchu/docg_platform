package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.KindMetaInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetListKindMetaInfoTransformer implements DataTransformer<List<KindMetaInfo>>{

    @Override
    public List<KindMetaInfo> transformResult(Result result) {
        List<KindMetaInfo> resultKindMetaInfoList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            String conceptionKindName = nodeRecord.get(CypherBuilder.operationResultName+"."+ RealmConstant._NameProperty).asString();
            String conceptionKindDesc = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._DescProperty).asString();
            ZonedDateTime createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asZonedDateTime();
            ZonedDateTime lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asZonedDateTime();
            String dataOrigin = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._dataOriginProperty).asString();
            long KindUID = nodeRecord.get("id("+CypherBuilder.operationResultName+")").asLong();
            String creatorId = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty) ?
                    nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty).asString():null;
            resultKindMetaInfoList.add(new KindMetaInfo(conceptionKindName,conceptionKindDesc,""+KindUID,createDate,lastModifyDate,creatorId,dataOrigin));
        }
        return resultKindMetaInfoList;
    }
}
