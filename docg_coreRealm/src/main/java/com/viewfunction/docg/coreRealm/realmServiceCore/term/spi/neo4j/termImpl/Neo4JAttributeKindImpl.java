package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListAttributesViewKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributeValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JAttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4JAttributeKindImpl implements Neo4JAttributeKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JAttributeKindImpl.class);
    private String coreRealmName;
    private String attributeKindName;
    private String attributeKindDesc;
    private String attributeKindUID;
    private AttributeDataType attributeDataType;

    public Neo4JAttributeKindImpl(String coreRealmName, String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType, String attributeKindUID){
        this.coreRealmName = coreRealmName;
        this.attributeKindName = attributeKindName;
        this.attributeKindDesc = attributeKindDesc;
        this.attributeDataType = attributeDataType;
        this.attributeKindUID = attributeKindUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getAttributeKindName() {
        return attributeKindName;
    }

    @Override
    public String getAttributeKindUID() {
        return attributeKindUID;
    }

    @Override
    public String getAttributeKindDesc() {
        return attributeKindDesc;
    }

    @Override
    public boolean updateAttributeKindDesc(String kindDesc) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._DescProperty, kindDesc);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.attributeKindUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._DescProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.attributeKindUID,false);
            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(kindDesc)){
                this.attributeKindDesc = kindDesc;
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public AttributeDataType getAttributeDataType() {
        return attributeDataType;
    }

    @Override
    public List<AttributesViewKind> getContainerAttributesViewKinds() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributeKindUID),
                    RealmConstant.AttributesViewKindClass,RealmConstant.AttributesViewKind_AttributeKindRelationClass, RelationDirection.FROM, null);
            GetListAttributesViewKindTransformer getListAttributesViewKindTransformer =
                    new GetListAttributesViewKindTransformer(RealmConstant.AttributesViewKind_AttributeKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object attributesViewKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributesViewKindTransformer,queryCql);
            return attributesViewKindsRes != null ? (List<AttributesViewKind>) attributesViewKindsRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Map<String, Long> getAttributeInConceptionKindDistributionStatistics() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            /*
            "MATCH (attributeKind:DOCG_AttributeKind)<-[DOCG_ViewContainsAttributeKindIs]- " +
            "(attributesViewKind:DOCG_AttributesViewKind)<-[DOCG_ConceptionContainsViewKindIs]-(conceptionKind:DOCG_ConceptionKind) " +
            "WHERE id(attributeKind) = 40710969 RETURN DISTINCT conceptionKind.name as operationResult LIMIT 1000000";
            */
            String conceptionKindListQuery = "MATCH (attributeKind:"+RealmConstant.AttributeKindClass+")<-["+RealmConstant.AttributesViewKind_AttributeKindRelationClass+"]- " +
                    "(attributesViewKind:"+RealmConstant.AttributesViewKindClass+")<-["+RealmConstant.ConceptionKind_AttributesViewKindRelationClass+"]-(conceptionKind:"+RealmConstant.ConceptionKindClass+") " +
                    "WHERE id(attributeKind) = "+this.attributeKindUID+" RETURN DISTINCT conceptionKind.name as "+CypherBuilder.operationResultName+" LIMIT 1000000";
            logger.debug("Generated Cypher Statement: {}", conceptionKindListQuery);

            ArrayList<String> fixSequenceConceptionKindNameList = new ArrayList<>();
            DataTransformer<Object> queryConceptionKindNameDataTransformer = new DataTransformer(){
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record record = result.next();
                        String resultDataProperty = CypherBuilder.operationResultName;
                        if(record.containsKey(CypherBuilder.operationResultName)){
                            System.out.println(record.get(resultDataProperty).asString());
                            fixSequenceConceptionKindNameList.add(record.get(resultDataProperty).asString());
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(queryConceptionKindNameDataTransformer,conceptionKindListQuery);

            if(fixSequenceConceptionKindNameList.size() > 0){
                /*
                MATCH (n:CPC_Member) WHERE (n.ADMINI_LEVEL) IS NOT NULL
                RETURN count(n)
                UNION ALL
                MATCH (n:Company) WHERE (n.ADMINI_LEVEL) IS NOT NULL
                RETURN count(n)
                */
                String queryAttributesCountQueryCql="";
                for(int i=0;i<fixSequenceConceptionKindNameList.size();i++){
                    String currentConceptionKindName = fixSequenceConceptionKindNameList.get(i);
                    queryAttributesCountQueryCql = queryAttributesCountQueryCql +
                            "MATCH (n:"+currentConceptionKindName+") WHERE (n."+this.attributeKindName+") IS NOT NULL " + "RETURN count(n) AS "+CypherBuilder.operationResultName+"\n";
                    if(i != fixSequenceConceptionKindNameList.size()-1){
                        queryAttributesCountQueryCql = queryAttributesCountQueryCql + " UNION ALL \n";
                    }
                }
                logger.debug("Generated Cypher Statement: {}", queryAttributesCountQueryCql);

                List<Long> fixSequenceAttributeInEntityCountList = new ArrayList<>();
                DataTransformer<Object> queryConceptionEntityCountDataTransformer = new DataTransformer(){
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record record = result.next();
                            String resultDataProperty = CypherBuilder.operationResultName;
                            if(record.containsKey(CypherBuilder.operationResultName)){
                                fixSequenceAttributeInEntityCountList.add(record.get(resultDataProperty).asLong());
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(queryConceptionEntityCountDataTransformer,queryAttributesCountQueryCql);

                Map<String, Long> attributeInConceptionKindDistributionMap = new HashMap<>();
                for(int i=0;i<fixSequenceConceptionKindNameList.size();i++){
                    attributeInConceptionKindDistributionMap.put(
                            fixSequenceConceptionKindNameList.get(i),
                            fixSequenceAttributeInEntityCountList.get(i)
                    );
                }
                return attributeInConceptionKindDistributionMap;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return this.attributeKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return this.graphOperationExecutorHelper;
    }
}
