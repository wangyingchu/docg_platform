package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.LinkedList;
import java.util.List;

public class GetLinkedListTimeScaleEntityTransformer implements DataTransformer<LinkedList<TimeScaleEntity>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetLinkedListTimeScaleEntityTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName = currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public LinkedList<TimeScaleEntity> transformResult(Result result) {
        LinkedList<TimeScaleEntity> resultEntityList = new LinkedList<>();
        while(result.hasNext()) {
            Record nodeRecord = result.next();
            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
            List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
            boolean isMatchedKind = true;
            if (allLabelNames.size() > 0) {
                isMatchedKind = allLabelNames.contains(RealmConstant.TimeScaleEntityClass);
            }
            if (isMatchedKind) {
                TimeFlow.TimeScaleGrade timeScaleGrade = null;
                long nodeUID = resultNode.id();
                String entityUID = "" + nodeUID;
                int value = resultNode.get("id").asInt();
                String timeFlowName = resultNode.get("timeFlow").asString();

                if (resultNode.get("year").asObject() != null) {
                    timeScaleGrade = TimeFlow.TimeScaleGrade.YEAR;
                } else if (resultNode.get("month").asObject() != null) {
                    timeScaleGrade = TimeFlow.TimeScaleGrade.MONTH;
                } else if (resultNode.get("day").asObject() != null) {
                    timeScaleGrade = TimeFlow.TimeScaleGrade.DAY;
                } else if (resultNode.get("hour").asObject() != null) {
                    timeScaleGrade = TimeFlow.TimeScaleGrade.HOUR;
                } else if (resultNode.get("minute").asObject() != null) {
                    timeScaleGrade = TimeFlow.TimeScaleGrade.MINUTE;
                }
                Neo4JTimeScaleEntityImpl neo4JTimeScaleEntityImpl = new Neo4JTimeScaleEntityImpl(
                        currentCoreRealmName, timeFlowName, entityUID, timeScaleGrade, value);
                neo4JTimeScaleEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                resultEntityList.add(neo4JTimeScaleEntityImpl);
            }
        }
        return resultEntityList;
    }
}
