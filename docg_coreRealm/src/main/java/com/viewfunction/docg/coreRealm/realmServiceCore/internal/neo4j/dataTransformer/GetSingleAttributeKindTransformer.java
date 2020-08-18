package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j.Neo4jAttributeKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.List;

public class GetSingleAttributeKindTransformer  implements DataTransformer<AttributeKind>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetSingleAttributeKindTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public AttributeKind transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                boolean isMatchedKind = true;
                if(allLabelNames.size()>0){
                    isMatchedKind = allLabelNames.contains(RealmConstant.AttributeKindClass);
                }
                if(isMatchedKind){
                    long nodeUID = resultNode.id();
                    String coreRealmName = this.currentCoreRealmName;
                    String attributeKindName = resultNode.get(RealmConstant._NameProperty).asString();
                    String attributeKindNameDesc = null;
                    if(resultNode.get(RealmConstant._DescProperty) != null){
                        attributeKindNameDesc = resultNode.get(RealmConstant._DescProperty).asString();
                    }
                    String attributesViewKindDataForm = resultNode.get(RealmConstant._attributeDataType).asString();
                    AttributeDataType attributeDataType = null;
                    switch(attributesViewKindDataForm){
                        case "BOOLEAN":attributeDataType = AttributeDataType.BOOLEAN;
                            break;
                        case "INT":attributeDataType = AttributeDataType.INT;
                            break;
                        case "SHORT":attributeDataType = AttributeDataType.SHORT;
                            break;
                        case "LONG":attributeDataType = AttributeDataType.LONG;
                            break;
                        case "FLOAT":attributeDataType = AttributeDataType.FLOAT;
                            break;
                        case "DOUBLE":attributeDataType = AttributeDataType.DOUBLE;
                            break;
                        case "DATE":attributeDataType = AttributeDataType.DATE;
                            break;
                        case "STRING":attributeDataType = AttributeDataType.STRING;
                            break;
                        case "BINARY":attributeDataType = AttributeDataType.BINARY;
                            break;
                        case "BYTE":attributeDataType = AttributeDataType.BYTE;
                            break;
                        case "DECIMAL":attributeDataType = AttributeDataType.DECIMAL;
                            break;
                        case "BOOLEAN_ARRAY":attributeDataType = AttributeDataType.BOOLEAN_ARRAY;
                            break;
                        case "INT_ARRAY":attributeDataType = AttributeDataType.INT_ARRAY;
                            break;
                        case "SHORT_ARRAY":attributeDataType = AttributeDataType.SHORT_ARRAY;
                            break;
                        case "LONG_ARRAY":attributeDataType = AttributeDataType.LONG_ARRAY;
                            break;
                        case "FLOAT_ARRAY":attributeDataType = AttributeDataType.FLOAT_ARRAY;
                            break;
                        case "DOUBLE_ARRAY":attributeDataType = AttributeDataType.DOUBLE_ARRAY;
                            break;
                        case "DATE_ARRAY":attributeDataType = AttributeDataType.DATE_ARRAY;
                            break;
                        case "STRING_ARRAY":attributeDataType = AttributeDataType.STRING_ARRAY;
                            break;
                        case "BINARY_ARRAY":attributeDataType = AttributeDataType.BINARY_ARRAY;
                            break;
                        case "DECIMAL_ARRAY":attributeDataType = AttributeDataType.DECIMAL_ARRAY;
                    }
                    String attributeKindUID = ""+nodeUID;
                    Neo4jAttributeKindImpl Neo4jAttributeKindImpl =
                            new Neo4jAttributeKindImpl(coreRealmName,attributeKindName,attributeKindNameDesc,attributeDataType,attributeKindUID);
                    Neo4jAttributeKindImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                    return Neo4jAttributeKindImpl;
                }
            }
        }
        return null;
    }
}
