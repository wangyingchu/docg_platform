package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAction;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationActionImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetSetRelationActionTransformer implements DataTransformer<Set<RelationAction>>{
    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetSetRelationActionTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public Set<RelationAction> transformResult(Result result) {
        Set<RelationAction> relationActionSet = new HashSet<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                    List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                    boolean isMatchedKind = true;
                    if(allLabelNames.size()>0){
                        isMatchedKind = allLabelNames.contains(RealmConstant.ActionClass);
                    }
                    if(isMatchedKind){
                        long nodeUID = resultNode.id();

                        String actionName = resultNode.get(RealmConstant._NameProperty).asString();
                        String actionDesc = null;
                        String actionImplementationClassProperty = null;
                        if(resultNode.get(RealmConstant._DescProperty) != null){
                            actionDesc = resultNode.get(RealmConstant._DescProperty).asString();
                        }
                        if(resultNode.get(RealmConstant._actionImplementationClassProperty) != null){
                            actionImplementationClassProperty = resultNode.get(RealmConstant._actionImplementationClassProperty).asString();
                        }
                        String attributesViewKindUID = ""+nodeUID;
                        Neo4JRelationActionImpl neo4JActionImpl =
                                new Neo4JRelationActionImpl(actionName,actionDesc,attributesViewKindUID,actionImplementationClassProperty);
                        neo4JActionImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                        relationActionSet.add(neo4JActionImpl);
                    }
                }
            }
        }
        return relationActionSet;
    }
}
