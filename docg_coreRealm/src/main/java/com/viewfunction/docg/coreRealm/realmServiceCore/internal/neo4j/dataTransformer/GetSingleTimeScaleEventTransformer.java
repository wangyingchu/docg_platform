package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.time.LocalDateTime;
import java.util.List;

public class GetSingleTimeScaleEventTransformer implements DataTransformer<TimeScaleEvent>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String timeFlowName;

    public GetSingleTimeScaleEventTransformer(String timeFlowName, GraphOperationExecutor workingGraphOperationExecutor) {
        this.timeFlowName = timeFlowName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public TimeScaleEvent transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                boolean isMatchedConceptionKind = false;
                if(allConceptionKindNames.size()>0){
                    isMatchedConceptionKind = allConceptionKindNames.contains(RealmConstant.TimeScaleEventClass);
                }
                if(isMatchedConceptionKind){
                    long nodeUID = resultNode.id();
                    String timeScaleEventUID = ""+nodeUID;
                    String eventComment = resultNode.get(RealmConstant._TimeScaleEventComment).asString();
                    String timeScaleGrade = resultNode.get(RealmConstant._TimeScaleEventScaleGrade).asString();
                    LocalDateTime referTime = resultNode.get(RealmConstant._TimeScaleEventReferTime).asLocalDateTime();
                    String currentTimeFlowName = timeFlowName != null ? timeFlowName : resultNode.get(RealmConstant._TimeScaleEventTimeFlow).asString();
                    Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(currentTimeFlowName,eventComment,referTime,getTimeScaleGrade(timeScaleGrade),timeScaleEventUID);
                    neo4JTimeScaleEventImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    return neo4JTimeScaleEventImpl;
                }
            }
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
}
