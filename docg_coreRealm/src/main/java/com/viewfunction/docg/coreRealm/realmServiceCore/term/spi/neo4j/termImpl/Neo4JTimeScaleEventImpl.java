package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JMultiConceptionKindsSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JTimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class Neo4JTimeScaleEventImpl extends Neo4JAttributesMeasurableImpl implements Neo4JClassificationAttachable, Neo4JTimeScaleEvent, Neo4JMultiConceptionKindsSupportable {

    private static Logger logger = LoggerFactory.getLogger(Neo4JTimeScaleEventImpl.class);

    private String timeFlowName;
    private String eventComment;
    private LocalDateTime referTime;
    private TimeFlow.TimeScaleGrade timeScaleGrade;
    private String timeScaleEventUID;

    public Neo4JTimeScaleEventImpl(String timeFlowName,String eventComment,LocalDateTime referTime,TimeFlow.TimeScaleGrade timeScaleGrade,
                                   String timeScaleEventUID) {
        super(timeScaleEventUID);
        this.timeFlowName = timeFlowName;
        this.eventComment = eventComment;
        this.referTime = referTime;
        this.timeScaleGrade = timeScaleGrade;
        this.timeScaleEventUID = timeScaleEventUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getTimeFlowName() {
        if(this.timeFlowName == null){
            this.timeFlowName = this.getAttribute(RealmConstant._TimeScaleEventTimeFlow).getAttributeValue().toString();
        }
        return this.timeFlowName;
    }

    @Override
    public LocalDateTime getReferTime() {
        return this.referTime;
    }

    @Override
    public TimeFlow.TimeScaleGrade getTimeScaleGrade() {
        return this.timeScaleGrade;
    }

    @Override
    public String getTimeScaleEventUID() {
        return this.timeScaleEventUID;
    }

    @Override
    public String getEventComment() {
        return this.eventComment;
    }

    @Override
    public TimeScaleEntity getReferTimeScaleEntity() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = "MATCH(currentEntity:"+ RealmConstant.TimeScaleEventClass+")<-[:"+RealmConstant.TimeScale_TimeReferToRelationClass+"]-(timeScaleEntity:"+RealmConstant.TimeScaleEntityClass+") WHERE id(currentEntity) = "+ this.timeScaleEventUID +" RETURN timeScaleEntity as operationResult";
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetSingleTimeScaleEntityTransformer getSingleTimeScaleEntityTransformer =
                    new GetSingleTimeScaleEntityTransformer(null,graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getSingleTimeScaleEntityTransformer,queryCql);
            if(queryRes != null){
                return (TimeScaleEntity)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public ConceptionEntity getAttachConceptionEntity() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = "MATCH(currentEntity:"+ RealmConstant.TimeScaleEventClass+")<-[:"+RealmConstant.TimeScale_AttachToRelationClass+"]-(conceptionEntity) WHERE id(currentEntity) = "+ this.timeScaleEventUID +" RETURN conceptionEntity as operationResult";
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                    new GetSingleConceptionEntityTransformer(null,graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                return (ConceptionEntity)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public List<String> getAliasConceptionKindNames() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) = " + this.timeScaleEventUID+"\n"+
                    "RETURN DISTINCT targetNodes as operationResult";
            DataTransformer<List<String>> dataTransfer = new DataTransformer<List<String>>() {
                @Override
                public List<String> transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                        List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                        allConceptionKindNames.remove(RealmConstant.TimeScaleEventClass);
                        return allConceptionKindNames;
                    }
                    return null;
                }
            };

            Object conceptionEntityNameList = workingGraphOperationExecutor.executeRead(dataTransfer,cypherProcedureString);
            if(conceptionEntityNameList != null){
                return (List<String>)conceptionEntityNameList;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        super.setGlobalGraphOperationExecutor(graphOperationExecutor);
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return this.timeScaleEventUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
