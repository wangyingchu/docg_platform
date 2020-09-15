package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JClassificationImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListClassificationTransformer implements DataTransformer<List<Classification>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetListClassificationTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<Classification> transformResult(Result result) {
        List<Classification> classificationList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                boolean isMatchedKind = true;
                if(allLabelNames.size()>0){
                    isMatchedKind = allLabelNames.contains(RealmConstant.ClassificationClass);
                }
                if(isMatchedKind){
                    long nodeUID = resultNode.id();
                    String coreRealmName = this.currentCoreRealmName;
                    String classificationName = resultNode.get(RealmConstant._NameProperty).asString();
                    String classificationDesc = null;
                    if(resultNode.get(RealmConstant._DescProperty) != null){
                        classificationDesc = resultNode.get(RealmConstant._DescProperty).asString();
                    }
                    String classificationUID = ""+nodeUID;
                    Neo4JClassificationImpl neo4JClassificationImpl =
                            new Neo4JClassificationImpl(coreRealmName,classificationName,classificationDesc,classificationUID);
                    neo4JClassificationImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                    classificationList.add(neo4JClassificationImpl);
                }
            }
        }
        return classificationList;
    }
}
