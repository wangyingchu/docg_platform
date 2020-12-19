package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import static com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind.LinkLogicCondition.*;
import static com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind.LinkLogicType.*;

public class GetSingleRelationAttachLinkLogicTransformer implements DataTransformer<RelationAttachLinkLogic>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetSingleRelationAttachLinkLogicTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public RelationAttachLinkLogic transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                long nodeUID = resultNode.id();
                String attachLinkLogicType = resultNode.get(RealmConstant._attachLinkLogicType).asString();
                String attachLinkLogicCondition = resultNode.get(RealmConstant._attachLinkLogicCondition).asString();
                String attachLinkLogicSourceAttribute = resultNode.get(RealmConstant._attachLinkLogicSourceAttribute).asString();
                String attachLinkLogicTargetAttribute = resultNode.get(RealmConstant._attachLinkLogicTargetAttribute).asString();

                String relationAttachLinkLogicUID = ""+nodeUID;
                RelationAttachLinkLogic relationAttachLinkLogic =
                        new RelationAttachLinkLogic(getLinkLogicType(attachLinkLogicType),getLinkLogicCondition(attachLinkLogicCondition),
                                attachLinkLogicSourceAttribute,attachLinkLogicTargetAttribute,relationAttachLinkLogicUID);
                return relationAttachLinkLogic;
            }
        }
        return null;
    }

    private static RelationAttachKind.LinkLogicType getLinkLogicType(String linkLogicTypeStr){
        if(linkLogicTypeStr.equals("DEFAULT")){
            return DEFAULT;
        }
        if(linkLogicTypeStr.equals("AND")){
            return AND;
        }
        if(linkLogicTypeStr.equals("OR")){
            return OR;
        }
        return null;
    }

    private static RelationAttachKind.LinkLogicCondition getLinkLogicCondition(String linkLogicConditionStr){
        if(linkLogicConditionStr.equals("Equal")){
            return Equal;
        }
        if(linkLogicConditionStr.equals("GreaterThanEqual")){
            return GreaterThanEqual;
        }
        if(linkLogicConditionStr.equals("GreaterThan")){
            return GreaterThan;
        }
        if(linkLogicConditionStr.equals("LessThanEqual")){
            return LessThanEqual;
        }
        if(linkLogicConditionStr.equals("LessThan")){
            return LessThan;
        }
        if(linkLogicConditionStr.equals("NotEqual")){
            return NotEqual;
        }
        if(linkLogicConditionStr.equals("RegularMatch")){
            return RegularMatch;
        }
        if(linkLogicConditionStr.equals("BeginWithSimilar")){
            return BeginWithSimilar;
        }
        if(linkLogicConditionStr.equals("EndWithSimilar")){
            return EndWithSimilar;
        }
        if(linkLogicConditionStr.equals("ContainSimilar")){
            return ContainSimilar;
        }
        return null;
    }
}
