package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j.Neo4jAttributesViewKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListAttributesViewKindTransformer  implements DataTransformer<List<AttributesViewKind>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetListAttributesViewKindTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<AttributesViewKind> transformResult(Result result) {
        List<AttributesViewKind> attributesViewKindList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                    List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                    boolean isMatchedKind = true;
                    if(allLabelNames.size()>0){
                        isMatchedKind = allLabelNames.contains(RealmConstant.AttributesViewKindClass);
                    }
                    if(isMatchedKind){
                        long nodeUID = resultNode.id();
                        String coreRealmName = this.currentCoreRealmName;
                        String attributesViewKindName = resultNode.get(RealmConstant._NameProperty).asString();
                        String attributesViewKindNameDesc = null;
                        if(resultNode.get(RealmConstant._DescProperty) != null){
                            attributesViewKindNameDesc = resultNode.get(RealmConstant._DescProperty).asString();
                        }
                        String attributesViewKindDataForm = resultNode.get(RealmConstant._viewKindDataForm).asString();

                        AttributesViewKind.AttributesViewKindDataForm currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE;
                        switch(attributesViewKindDataForm){
                            case "SINGLE_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE;
                                break;
                            case "LIST_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE;
                                break;
                            case "RELATED_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.RELATED_VALUE;
                                break;
                            case "EXTERNAL_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.EXTERNAL_VALUE;
                        }

                        String attributesViewKindUID = ""+nodeUID;
                        Neo4jAttributesViewKindImpl neo4jAttributesViewKindImpl =
                                new Neo4jAttributesViewKindImpl(coreRealmName,attributesViewKindName,attributesViewKindNameDesc,currentAttributesViewKindDataForm,attributesViewKindUID);
                        neo4jAttributesViewKindImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                        attributesViewKindList.add(neo4jAttributesViewKindImpl);
                    }
                }
            }
        }
        return attributesViewKindList;
    }
}
