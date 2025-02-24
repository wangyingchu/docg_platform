package com.viewfunction.docg.coreRealm.realmServiceCore.internal.memgraph.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.KindMetaInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetListKindMetaInfoTransformer implements DataTransformer<List<KindMetaInfo>> {

    private static final ZoneId systemDefaultZoneId = ZoneId.systemDefault();

    @Override
    public List<KindMetaInfo> transformResult(Result result) {
        List<KindMetaInfo> resultKindMetaInfoList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            String conceptionKindName = nodeRecord.get(CypherBuilder.operationResultName+"."+ RealmConstant._NameProperty).asString();
            String conceptionKindDesc = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._DescProperty).asString();

            ZonedDateTime createDate = null;
            Object _createDatePropertyObject = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asObject();
            if(_createDatePropertyObject instanceof LocalDateTime){
                createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asLocalDateTime().atZone(systemDefaultZoneId);
            }

            ZonedDateTime lastModifyDate = null;
            Object _lastModifyDatePropertyObject = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asObject();
            if(_lastModifyDatePropertyObject instanceof LocalDateTime){
                lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asLocalDateTime().atZone(systemDefaultZoneId);
            }

            String dataOrigin = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._dataOriginProperty).asString();
            long KindUID = nodeRecord.get("id("+CypherBuilder.operationResultName+")").asLong();
            String creatorId = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty) ?
                    nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty).asString():null;
            resultKindMetaInfoList.add(new KindMetaInfo(conceptionKindName,conceptionKindDesc,""+KindUID,createDate,lastModifyDate,creatorId,dataOrigin));
        }
        return resultKindMetaInfoList;
    }
}
