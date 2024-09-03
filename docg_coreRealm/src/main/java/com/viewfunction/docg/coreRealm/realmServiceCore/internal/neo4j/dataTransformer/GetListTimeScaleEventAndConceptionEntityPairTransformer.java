package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEventAndConceptionEntityPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetListTimeScaleEventAndConceptionEntityPairTransformer implements DataTransformer<List<TimeScaleEventAndConceptionEntityPair>>{
    private GraphOperationExecutor workingGraphOperationExecutor;
    private String timeFlowName;
    private String targetConceptionKindName;

    public GetListTimeScaleEventAndConceptionEntityPairTransformer(String timeFlowName, GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.timeFlowName = timeFlowName;
    }

    @Override
    public List<TimeScaleEventAndConceptionEntityPair> transformResult(Result result) {
        List<TimeScaleEventAndConceptionEntityPair> timeScaleEventAndConceptionEntityPairList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            Node timeScaleEventNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
            TimeScaleEvent targetTimeScaleEvent = null;
            List<String> allConceptionKindNames = Lists.newArrayList(timeScaleEventNode.labels());
            boolean isMatchedConceptionKind = false;
            if(allConceptionKindNames.size()>0){
                isMatchedConceptionKind = allConceptionKindNames.contains(RealmConstant.TimeScaleEventClass);
            }
            if(isMatchedConceptionKind){
                targetTimeScaleEvent = getTimeScaleEvent(timeScaleEventNode);
            }

            Node conceptionEntityNode = nodeRecord.get("conceptionEntity").asNode();
            ConceptionEntity targetConceptionEntity = getConceptionEntity(conceptionEntityNode);
            if(targetTimeScaleEvent != null && targetConceptionEntity != null){
                TimeScaleEventAndConceptionEntityPair currentTimeScaleEventAndConceptionEntityPair = new TimeScaleEventAndConceptionEntityPair(targetTimeScaleEvent,targetConceptionEntity);
                timeScaleEventAndConceptionEntityPairList.add(currentTimeScaleEventAndConceptionEntityPair);
            }
        }
        return timeScaleEventAndConceptionEntityPairList;
    }

    private TimeScaleEvent getTimeScaleEvent(Node resultNode){
        long nodeUID = resultNode.id();
        String timeScaleEventUID = ""+nodeUID;
        String eventComment = resultNode.get(RealmConstant._TimeScaleEventComment).asString();
        String timeScaleGrade = resultNode.get(RealmConstant._TimeScaleEventScaleGrade).asString();
        LocalDateTime referTime = resultNode.get(RealmConstant._TimeScaleEventReferTime).asLocalDateTime();
        String currentTimeFlowName = timeFlowName != null ? timeFlowName : resultNode.get(RealmConstant._TimeScaleEventTimeFlow).asString();;
        Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(currentTimeFlowName,eventComment,referTime,getTimeScaleGrade(timeScaleGrade),timeScaleEventUID);
        neo4JTimeScaleEventImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
        return neo4JTimeScaleEventImpl;
    }

    private ConceptionEntity getConceptionEntity(Node resultNode){
        List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
        boolean isMatchedConceptionKind = true;
        if(allConceptionKindNames.size()>0){
            if(targetConceptionKindName != null){
                isMatchedConceptionKind = allConceptionKindNames.contains(targetConceptionKindName);
            }else{
                isMatchedConceptionKind = true;
            }
        }
        if(isMatchedConceptionKind){
            long nodeUID = resultNode.id();
            String conceptionEntityUID = ""+nodeUID;
            String resultConceptionKindName = targetConceptionKindName != null? targetConceptionKindName:allConceptionKindNames.get(0);
            Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                    new Neo4JConceptionEntityImpl(resultConceptionKindName,conceptionEntityUID);
            neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
            neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            return neo4jConceptionEntityImpl;
        }
        return null;
    }

    private TimeFlow.TimeScaleGrade getTimeScaleGrade(String timeScaleGradeValue){
        if(timeScaleGradeValue.equals("YEAR")){
            return TimeFlow.TimeScaleGrade.YEAR;
        }else if(timeScaleGradeValue.equals("MONTH")){
            return TimeFlow.TimeScaleGrade.MONTH;
        }else if(timeScaleGradeValue.equals("DAY")){
            return TimeFlow.TimeScaleGrade.DAY;
        }else if(timeScaleGradeValue.equals("HOUR")){
            return TimeFlow.TimeScaleGrade.HOUR;
        }else if(timeScaleGradeValue.equals("MINUTE")){
            return TimeFlow.TimeScaleGrade.MINUTE;
        }else if(timeScaleGradeValue.equals("SECOND")){
            return TimeFlow.TimeScaleGrade.SECOND;
        }
        return null;
    }

    public String getTargetConceptionKindName() {
        return targetConceptionKindName;
    }

    public void setTargetConceptionKindName(String targetConceptionKindName) {
        this.targetConceptionKindName = targetConceptionKindName;
    }
}
