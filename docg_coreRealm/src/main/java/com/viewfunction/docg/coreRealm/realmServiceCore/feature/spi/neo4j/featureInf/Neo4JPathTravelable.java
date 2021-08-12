package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.ConceptionKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.EntityKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.RelationKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.TravelParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.PathTravelable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListEntitiesPathTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleEntitiesGraphTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleEntitiesSpanningTreeTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesSpanningTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
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

        String cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.expand(n, \""+relationMatchLogicFullString+"\", \""+conceptionMatchLogicFullString+"\", "+minHopNumber+", "+maxHopNumber+")\n" +
                "YIELD path\n" +
                "RETURN path, length(path) AS hops\n" +
                "ORDER BY hops;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetListEntitiesPathTransformer getListEntitiesPathTransformer = new GetListEntitiesPathTransformer(workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getListEntitiesPathTransformer,cypherProcedureString);
                return queryResponse != null? (List<EntitiesPath>)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public EntitiesGraph expandGraph(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                List<ConceptionKindMatchLogic> conceptionKindMatchLogics,boolean containsSelf,int maxJump){
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-subgraph/
        MATCH (n) WHERE id(n)= 2
        CALL apoc.path.subgraphAll(n, {
            relationshipFilter: "",
            labelFilter: ">Engineering",
            minLevel: 0,
            maxLevel: 5
        })
        YIELD nodes, relationships
        RETURN nodes, relationships;
         */

        String relationMatchLogicFullString = generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        String conceptionMatchLogicFullString = generateConceptionKindMatchLogicsQuery(conceptionKindMatchLogics);
        int minLevelNumber =  containsSelf ? 0:1;
        int maxLevelNumber = maxJump >= 1 ? maxJump : -1;

        String cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.subgraphAll(n, {\n" +
                "relationshipFilter: \""+relationMatchLogicFullString+"\",\n" +
                "labelFilter: \""+conceptionMatchLogicFullString+"\",\n" +
                "minLevel: "+minLevelNumber+",\n" +
                "maxLevel: "+maxLevelNumber+"\n" +
                "})\n" +
                "YIELD nodes, relationships\n" +
                "RETURN nodes, relationships;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetSingleEntitiesGraphTransformer getSingleEntitiesGraphTransformer = new GetSingleEntitiesGraphTransformer(workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getSingleEntitiesGraphTransformer,cypherProcedureString);
                return queryResponse != null ? (EntitiesGraph)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public EntitiesSpanningTree expandSpanningTree(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                           List<ConceptionKindMatchLogic> conceptionKindMatchLogics,int maxJump){
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-spanning-tree/
        MATCH (n) WHERE id(n)= 2
        CALL apoc.path.spanningTree(n, {
            relationshipFilter: "KNOWS",
            minLevel: 1,
            maxLevel: 2
        })
        YIELD path
        RETURN path;
        */
        String relationMatchLogicFullString = generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        String conceptionMatchLogicFullString = generateConceptionKindMatchLogicsQuery(conceptionKindMatchLogics);
        int minLevelNumber =  0;
        int maxLevelNumber = maxJump >= 1 ? maxJump : -1;

        String cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.spanningTree(n, {\n" +
                "relationshipFilter: \""+relationMatchLogicFullString+"\",\n" +
                "labelFilter: \""+conceptionMatchLogicFullString+"\",\n" +
                "minLevel: "+minLevelNumber+",\n" +
                "maxLevel: "+maxLevelNumber+"\n" +
                "})\n" +
                "YIELD path\n" +
                "RETURN path;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetSingleEntitiesSpanningTreeTransformer getSingleEntitiesSpanningTreeTransformer = new GetSingleEntitiesSpanningTreeTransformer(this.getEntityUID(),workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getSingleEntitiesSpanningTreeTransformer,cypherProcedureString);
                return queryResponse != null ? (EntitiesSpanningTree)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<EntitiesPath> advancedExpandPath(TravelParameters travelParameters){
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-paths-config/
        MATCH (n) WHERE id(n)= 2
        CALL apoc.path.expandConfig(n, {
           minLevel: 1,
           maxLevel: 3,
           relationshipFilter: "",
           labelFilter:"",
           sequence:"",
           beginSequenceAtStart:true,
           bfs:true,
           filterStartNode:true,
           limit:1,
           endNodes:null,
           terminatorNodes:null,
           whitelistNodes:null,
           blacklistNodes:null
           })
        YIELD path
        RETURN path, length(path) AS hops
        ORDER BY hops;





        MATCH (b) WHERE id(b) in [50003,330535]
        MATCH (n) WHERE id(n)= 457049

        MATCH (whitelist)
        WHERE id(whitelist) IN [330535]

        CALL apoc.path.expandConfig(n, {
           minLevel: 1,
           maxLevel: 5,
           relationshipFilter: "",
           labelFilter:"",
           sequence:"",
           beginSequenceAtStart:false,
           bfs:true,
           filterStartNode:false,
           limit:1,
           endNodes:null,
           terminatorNodes:null,
           whitelistNodes:null,
           blacklistNodes:null
           })
        YIELD path
        RETURN path, length(path) AS hops
        ORDER BY hops;





        */
        String cypherProcedureString = null;
        if(travelParameters != null){
            int minJumpNumber = travelParameters.getMinJump() >= 0 ? travelParameters.getMinJump() : 0;
            int maxJumpNumber;
            if(travelParameters.getMaxJump() <= -1){
                maxJumpNumber = -1;
            }else{
                maxJumpNumber = travelParameters.getMaxJump() >= minJumpNumber ? travelParameters.getMaxJump() : minJumpNumber;
            }
            int resultNumber;
            if(travelParameters.getResultNumber() <=-1){
                resultNumber = -1;
            }else{
                resultNumber = travelParameters.getResultNumber() > 0 ? travelParameters.getResultNumber() : 1;
            }

            String usingBFS = "true";
            TravelParameters.TraversalMethod traversalMethod = travelParameters.getTraversalMethod();
            switch(traversalMethod){
                case BFS: usingBFS = "true"; break;
                case DFS: usingBFS = "false";
            }



            LinkedList<List<EntityKindMatchLogic>>  entityPathFlowMatchLogics = travelParameters.getEntityPathFlowMatchLogics();




            cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                    "CALL apoc.path.expandConfig(n, {\n" +
                    "   minLevel: "+minJumpNumber+",\n" +
                    "   maxLevel: "+maxJumpNumber+",\n" +
                    "   relationshipFilter: \"\",\n" +
                    "   labelFilter:\"\",\n" +
                    "   sequence:\"\",\n" +
                    "   beginSequenceAtStart: "+travelParameters.isMatchStartEntityForPathFlow()+",\n" +
                    "   bfs: "+usingBFS+",\n" +
                    "   filterStartNode: "+travelParameters.isMatchStartConceptionEntity()+",\n" +
                    "   limit: "+resultNumber+",\n" +
                    "   endNodes:null,\n" +
                    "   terminatorNodes:null,\n" +
                    "   whitelistNodes:null,\n" +
                    "   blacklistNodes:null\n" +
                    "   })\n" +
                    "YIELD path\n" +
                    "RETURN path, length(path) AS hops\n" +
                    "ORDER BY hops;";
            if(entityPathFlowMatchLogics.size()>0){
                //use sequence, ignore labelFilter and relationshipFilter
            }else{





            }
        }else{
            cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                    "CALL apoc.path.expandConfig(n, {\n" +
                    "   minLevel: 0,\n" +
                    "   maxLevel: 3,\n" +
                    "   relationshipFilter: \"\",\n" +
                    "   labelFilter:\"\",\n" +
                    "   sequence:\"\",\n" +
                    "   beginSequenceAtStart:true,\n" +
                    "   bfs:true,\n" +
                    "   filterStartNode:false,\n" +
                    "   limit:-1,\n" +
                    "   endNodes:null,\n" +
                    "   terminatorNodes:null,\n" +
                    "   whitelistNodes:null,\n" +
                    "   blacklistNodes:null\n" +
                    "   })\n" +
                    "YIELD path\n" +
                    "RETURN path, length(path) AS hops\n" +
                    "ORDER BY hops;";
        }

        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetListEntitiesPathTransformer getListEntitiesPathTransformer = new GetListEntitiesPathTransformer(workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getListEntitiesPathTransformer,cypherProcedureString);
                return queryResponse != null? (List<EntitiesPath>)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public EntitiesGraph advancedExpandGraph(TravelParameters travelParameters){return null;}

    default public EntitiesSpanningTree advancedExpandSpanningTree(TravelParameters travelParameters){return null;}

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
                        case NOT_ALLOW: currentConceptionMatchLogicString = "-"+conceptionKindName;
                            break;
                        case END_WITH: currentConceptionMatchLogicString = ">"+conceptionKindName;
                            break;
                        case MUST_HAVE: currentConceptionMatchLogicString = "+"+conceptionKindName;
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
