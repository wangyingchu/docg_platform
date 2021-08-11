package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.ConceptionKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.RelationKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.PathTravelable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntitiesPathTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public interface Neo4JPathTravelable extends PathTravelable,Neo4JKeyResourcesRetrievable {

    Logger logger = LoggerFactory.getLogger(Neo4JPathTravelable.class);

    default public List<EntitiesPath> expandPath(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                 List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int minJump, int maxJump){
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-paths/#path-expander-paths-procedure-overview
        MATCH (n) WHERE id(n)= 2
        CALL apoc.path.expand(n, "", null, 5, 10)
        YIELD path
        RETURN path, length(path) AS hops
        ORDER BY hops;
        */
        String relationMatchLogicFullString = generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        String conceptionMatchLogicFullString = generateConceptionKindMatchLogicsQuery(conceptionKindMatchLogics);
        int minHopNumber =  minJump > 0 ? minJump:1;
        int maxHopNumber = maxJump >= minHopNumber ? maxJump : minHopNumber;

        String cypherProcedureString="MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.expand(n, \""+relationMatchLogicFullString+"\", \""+conceptionMatchLogicFullString+"\", "+minHopNumber+", "+maxHopNumber+")\n" +
                "YIELD path\n" +
                "RETURN path, length(path) AS hops\n" +
                "ORDER BY hops;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetListConceptionEntitiesPathTransformer getListConceptionEntitiesPathTransformer = new GetListConceptionEntitiesPathTransformer(workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getListConceptionEntitiesPathTransformer,cypherProcedureString);
                return queryResponse != null? (List<EntitiesPath>)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public EntitiesGraph expandSubGraph(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                List<ConceptionKindMatchLogic> conceptionKindMatchLogics){
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-subgraph/
        MATCH (p:Person {name: "Praveena"})
        CALL apoc.path.subgraphAll(p, {
            relationshipFilter: "",
            minLevel: 0,
            maxLevel: 5
        })
        YIELD nodes, relationships
        RETURN nodes, relationships;
         */

        return null;
    }

    private String generateRelationKindMatchLogicsQuery(List<RelationKindMatchLogic> relationKindMatchLogics,RelationDirection defaultDirectionForNoneRelationKindMatch){
        String relationMatchLogicFullString = null;
        if(relationKindMatchLogics != null && relationKindMatchLogics.size()>0){
            boolean isFirstMatchLogic = true;
            for(RelationKindMatchLogic currentRelationKindMatchLogic:relationKindMatchLogics){
                String currentRelationKindName = currentRelationKindMatchLogic.getRelationKindName();
                if(currentRelationKindName != null){
                    String currentRelationMatchLogicString = null;
                    switch(currentRelationKindMatchLogic.getRelationDirection()){
                        case FROM: currentRelationMatchLogicString = currentRelationKindName+">";
                            break;
                        case TO: currentRelationMatchLogicString = "<"+currentRelationKindName;
                            break;
                        case TWO_WAY:currentRelationMatchLogicString = currentRelationKindName;
                    }
                    if(isFirstMatchLogic){
                        relationMatchLogicFullString = currentRelationMatchLogicString;
                        isFirstMatchLogic = false;
                    }else{
                        relationMatchLogicFullString = relationMatchLogicFullString + "|"+currentRelationMatchLogicString;
                    }
                }
            }
            if(relationMatchLogicFullString == null){
                relationMatchLogicFullString = "";
            }
        }else{
            if(defaultDirectionForNoneRelationKindMatch != null){
                switch(defaultDirectionForNoneRelationKindMatch) {
                    case FROM: relationMatchLogicFullString = ">";
                        break;
                    case TO: relationMatchLogicFullString = "<";
                        break;
                    case TWO_WAY:relationMatchLogicFullString = "";
                }
            }else{
                relationMatchLogicFullString = "";
            }
        }
        return relationMatchLogicFullString;
    }

    private String generateConceptionKindMatchLogicsQuery(List<ConceptionKindMatchLogic> conceptionKindMatchLogics){
        String conceptionMatchLogicFullString = null;
        if(conceptionKindMatchLogics != null && conceptionKindMatchLogics.size()>0){
            boolean isFirstMatchLogic = true;
            for(ConceptionKindMatchLogic currentConceptionKindMatchLogic:conceptionKindMatchLogics){
                String conceptionKindName = currentConceptionKindMatchLogic.getConceptionKindName();
                if(conceptionKindName != null){
                    String currentConceptionMatchLogicString = null;
                    switch(currentConceptionKindMatchLogic.getConceptionKindExistenceRule()){
                        case NOT_HAVE: currentConceptionMatchLogicString = "-"+conceptionKindName;
                            break;
                        case END_WITH: currentConceptionMatchLogicString = ">"+conceptionKindName;
                            break;
                        case ALL_HAVE: currentConceptionMatchLogicString = "+"+conceptionKindName;
                            break;
                        case TERMINATE_AT: currentConceptionMatchLogicString = "/"+conceptionKindName;
                            break;
                    }
                    if(isFirstMatchLogic){
                        conceptionMatchLogicFullString = currentConceptionMatchLogicString;
                        isFirstMatchLogic = false;
                    }else{
                        conceptionMatchLogicFullString = conceptionMatchLogicFullString + "|"+currentConceptionMatchLogicString;
                    }
                }
            }
            if(conceptionMatchLogicFullString == null){
                conceptionMatchLogicFullString = "";
            }
        }else{
            conceptionMatchLogicFullString = "";
        }
        return conceptionMatchLogicFullString;
    }
}
