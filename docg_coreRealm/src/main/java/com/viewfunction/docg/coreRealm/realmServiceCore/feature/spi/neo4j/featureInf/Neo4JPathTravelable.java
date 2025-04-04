package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.PathTravelable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesSpanningTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public interface Neo4JPathTravelable extends PathTravelable,Neo4JKeyResourcesRetrievable {

    Logger logger = LoggerFactory.getLogger(Neo4JPathTravelable.class);

    enum AdvancedExpandType {Path , Graph , SpanningTree , EndConceptionEntity}
    enum PathEntityType {ConceptionEntity , RelationEntity}

    default public List<EntitiesPath> expandPath(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                 List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int minJump, int maxJump,Integer resultPathUpperLimit){
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-paths/#path-expander-paths-procedure-overview
        MATCH (n) WHERE id(n)= 2
        CALL apoc.path.expand(n, "", null, 5, 10)
        YIELD path
        RETURN path, length(path) AS hops
        ORDER BY hops;
        */
        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        String conceptionMatchLogicFullString = CypherBuilder.generateConceptionKindMatchLogicsQuery(conceptionKindMatchLogics);
        int minHopNumber =  minJump > 0 ? minJump:1;
        int maxHopNumber = maxJump >= minHopNumber ? maxJump : minHopNumber;

        String cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.expand(n, \""+relationMatchLogicFullString+"\", \""+conceptionMatchLogicFullString+"\", "+minHopNumber+", "+maxHopNumber+")\n" +
                "YIELD path\n" +
                "RETURN path, length(path) AS hops\n" +
                "ORDER BY hops;";
        if(resultPathUpperLimit != null && resultPathUpperLimit > 0){
            cypherProcedureString = cypherProcedureString.replace(";"," LIMIT "+resultPathUpperLimit.intValue()+";");
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

    default public EntitiesGraph expandGraph(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                List<ConceptionKindMatchLogic> conceptionKindMatchLogics,boolean containsSelf,int maxJump,Integer resultConceptionEntitiesUpperLimit){
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

        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        String conceptionMatchLogicFullString = CypherBuilder.generateConceptionKindMatchLogicsQuery(conceptionKindMatchLogics);
        int minLevelNumber =  containsSelf ? 0:1;
        int maxLevelNumber = maxJump >= 1 ? maxJump : -1;

        String cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.subgraphAll(n, {\n" +
                "relationshipFilter: \""+relationMatchLogicFullString+"\",\n" +
                "labelFilter: \""+conceptionMatchLogicFullString+"\",\n" +
                "limit: -1"+",\n" +
                "minLevel: "+minLevelNumber+",\n" +
                "maxLevel: "+maxLevelNumber+"\n" +
                "})\n" +
                "YIELD nodes, relationships\n" +
                "RETURN nodes, relationships;";
        if(resultConceptionEntitiesUpperLimit != null && resultConceptionEntitiesUpperLimit > 0){
            cypherProcedureString = cypherProcedureString.replace("limit: -1","limit: "+resultConceptionEntitiesUpperLimit);
        }
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
                                                           List<ConceptionKindMatchLogic> conceptionKindMatchLogics,int maxJump,Integer resultConceptionEntitiesUpperLimit){
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
        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        String conceptionMatchLogicFullString = CypherBuilder.generateConceptionKindMatchLogicsQuery(conceptionKindMatchLogics);
        int minLevelNumber =  0;
        int maxLevelNumber = maxJump >= 1 ? maxJump : -1;

        String cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                "CALL apoc.path.spanningTree(n, {\n" +
                "relationshipFilter: \""+relationMatchLogicFullString+"\",\n" +
                "labelFilter: \""+conceptionMatchLogicFullString+"\",\n" +
                "limit: -1"+",\n" +
                "minLevel: "+minLevelNumber+",\n" +
                "maxLevel: "+maxLevelNumber+"\n" +
                "})\n" +
                "YIELD path\n" +
                "RETURN path;";
        if(resultConceptionEntitiesUpperLimit != null && resultConceptionEntitiesUpperLimit > 0){
            cypherProcedureString = cypherProcedureString.replace("limit: -1","limit: "+resultConceptionEntitiesUpperLimit);
        }
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

    default public List<EntitiesPath> advancedExpandPath(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-paths-config/
        MATCH (n) WHERE id(n)= 457049
        MATCH (whitelist) WHERE id(whitelist) IN [330535,330535]
        CALL apoc.path.expandConfig(n, {
           minLevel: 1,
           maxLevel: 5,
           relationshipFilter: "",
           labelFilter:"",
           sequence:null,
           beginSequenceAtStart:false,
           bfs:true,
           filterStartNode:false,
           limit:1,
           endNodes:null,
           terminatorNodes:null,
           whitelistNodes:[whitelist],
           blacklistNodes:null
           })
        YIELD path
        RETURN path, length(path) AS hops
        ORDER BY hops;
        */
        String cypherProcedureString = getAdvancedExpandQuery(AdvancedExpandType.Path,travelParameters);
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

    default public EntitiesGraph advancedExpandGraph(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.path/apoc.path.subgraphAll/
        */
        String cypherProcedureString = getAdvancedExpandQuery(AdvancedExpandType.Graph,travelParameters);
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

    default public EntitiesSpanningTree advancedExpandSpanningTree(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/graph-querying/expand-spanning-tree/
        */
        String cypherProcedureString = getAdvancedExpandQuery(AdvancedExpandType.SpanningTree,travelParameters);
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

    default public List<ConceptionEntity> getEndConceptionEntitiesByPathPattern(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException{
        String cypherProcedureString = getAdvancedExpandQuery(AdvancedExpandType.EndConceptionEntity,travelParameters);
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
                return queryResponse != null ? (List<ConceptionEntity>)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default List<EntitiesPath> getAllPathBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                       RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,
                                                       PathEntityFilterParameters relationPathEntityFilterParameters, PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.algo/apoc.algo.allSimplePaths/
        */
        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        int maxLevelNumber = maxJump >= 1 ? maxJump : 1;

        String relationPathEntityFilter = generatePathEntityFilterQuery(relationPathEntityFilterParameters,"path",PathEntityType.RelationEntity,"WHERE");
        String relationEntityFilterLogic = relationPathEntityFilter.equals("")?"":relationPathEntityFilter+"\n";
        String conjunctionKey = relationEntityFilterLogic.equals("") ? "WHERE" : "AND";
        String conceptionPathEntityFilter = generatePathEntityFilterQuery(conceptionPathEntityFilterParameters,"path",PathEntityType.ConceptionEntity,conjunctionKey);
        String conceptionEntityFilterLogic = conceptionPathEntityFilter.equals("")?"":conceptionPathEntityFilter+"\n";

        String cypherProcedureString = "MATCH (startNode) WHERE id(startNode)= "+this.getEntityUID()+"\n" +
                "MATCH (endNode) WHERE id(endNode)= "+targetEntityUID+"\n" +
                "CALL apoc.algo.allSimplePaths(startNode,endNode, \""+relationMatchLogicFullString+"\", "+maxLevelNumber+")\n" +
                "YIELD path\n" +
                relationEntityFilterLogic + conceptionEntityFilterLogic+
                "RETURN path, length(path) AS hops\n" +
                "ORDER BY hops;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null && targetEntityUID != null) {
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

    default public EntitiesPath getShortestPathBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                             PathEntityFilterParameters relationPathEntityFilterParameters, PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException{
        String cypherProcedureString = generateShortPathsQuery("shortestPath",targetEntityUID,pathAllowedRelationKinds,maxJump,relationPathEntityFilterParameters,conceptionPathEntityFilterParameters);

        if(this.getEntityUID() != null && targetEntityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetSingleEntitiesPathTransformer getSingleEntitiesPathTransformer = new GetSingleEntitiesPathTransformer(workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getSingleEntitiesPathTransformer,cypherProcedureString);
                return queryResponse != null? (EntitiesPath)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<EntitiesPath> getAllShortestPathsBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                             PathEntityFilterParameters relationPathEntityFilterParameters, PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException{
        String cypherProcedureString = generateShortPathsQuery("allshortestPaths",targetEntityUID,pathAllowedRelationKinds,maxJump,relationPathEntityFilterParameters,conceptionPathEntityFilterParameters);

        if(this.getEntityUID() != null && targetEntityUID != null) {
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

    default List<EntitiesPath> getLongestPathsBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                       RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,int maxPath,
                                                       PathEntityFilterParameters relationPathEntityFilterParameters, PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.algo/apoc.algo.allSimplePaths/
        */
        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        int maxLevelNumber = maxJump >= 1 ? maxJump : 1;
        int maxPathNumber = maxPath >= 1 ? maxPath : 1;

        String relationPathEntityFilter = generatePathEntityFilterQuery(relationPathEntityFilterParameters,"path",PathEntityType.RelationEntity,"WHERE");
        String relationEntityFilterLogic = relationPathEntityFilter.equals("")?"":relationPathEntityFilter+"\n";
        String conjunctionKey = relationEntityFilterLogic.equals("") ? "WHERE" : "AND";
        String conceptionPathEntityFilter = generatePathEntityFilterQuery(conceptionPathEntityFilterParameters,"path",PathEntityType.ConceptionEntity,conjunctionKey);
        String conceptionEntityFilterLogic = conceptionPathEntityFilter.equals("")?"":conceptionPathEntityFilter+"\n";

        String cypherProcedureString = "MATCH (startNode) WHERE id(startNode)= "+this.getEntityUID()+"\n" +
                "MATCH (endNode) WHERE id(endNode)= "+targetEntityUID+"\n" +
                "CALL apoc.algo.allSimplePaths(startNode,endNode, \""+relationMatchLogicFullString+"\", "+maxLevelNumber+")\n" +
                "YIELD path\n" +
                relationEntityFilterLogic + conceptionEntityFilterLogic+
                "RETURN path, length(path) AS hops\n" +
                "ORDER BY length(path) DESC LIMIT "+maxPathNumber;
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null && targetEntityUID != null) {
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

    default public EntitiesPath getShortestPathWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                               RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,
                                                               PathEntityFilterParameters relationPathEntityFilterParameters,
                                                               PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.algo/apoc.algo.dijkstra/
        */
        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        String relationPathEntityFilter = generatePathEntityFilterQuery(relationPathEntityFilterParameters,"path",PathEntityType.RelationEntity,"WHERE");
        String relationEntityFilterLogic = relationPathEntityFilter.equals("")?"":relationPathEntityFilter+"\n";
        String conjunctionKey = relationEntityFilterLogic.equals("") ? "WHERE" : "AND";
        String conceptionPathEntityFilter = generatePathEntityFilterQuery(conceptionPathEntityFilterParameters,"path",PathEntityType.ConceptionEntity,conjunctionKey);
        String conceptionEntityFilterLogic = conceptionPathEntityFilter.equals("")?"":conceptionPathEntityFilter+"\n";

        String cypherProcedureString = "MATCH (startNode) WHERE id(startNode)= "+this.getEntityUID()+"\n" +
                "MATCH (endNode) WHERE id(endNode)= "+targetEntityUID+"\n" +
                "CALL apoc.algo.dijkstra(startNode,endNode, \""+relationMatchLogicFullString+"\", \""+relationWeightProperty+"\")\n" +
                "YIELD path, weight\n" +
                relationEntityFilterLogic + conceptionEntityFilterLogic+
                "RETURN path, weight\n" ;
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null && targetEntityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetSingleEntitiesPathTransformer getSingleEntitiesPathTransformer = new GetSingleEntitiesPathTransformer(workingGraphOperationExecutor);
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getSingleEntitiesPathTransformer,cypherProcedureString);
                return queryResponse != null? (EntitiesPath)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<EntitiesPath> getShortestPathsWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                                      RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,float defaultWeight,int maxPathNumber,
                                                                      PathEntityFilterParameters relationPathEntityFilterParameters,
                                                                      PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.algo/apoc.algo.dijkstra/
        */
        String relationMatchLogicFullString = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogics,defaultDirectionForNoneRelationKindMatch);
        int maxReturnPathNumber = maxPathNumber >= 1 ? maxPathNumber : 1;
        String relationPathEntityFilter = generatePathEntityFilterQuery(relationPathEntityFilterParameters,"path",PathEntityType.RelationEntity,"WHERE");
        String relationEntityFilterLogic = relationPathEntityFilter.equals("")?"":relationPathEntityFilter+"\n";
        String conjunctionKey = relationEntityFilterLogic.equals("") ? "WHERE" : "AND";
        String conceptionPathEntityFilter = generatePathEntityFilterQuery(conceptionPathEntityFilterParameters,"path",PathEntityType.ConceptionEntity,conjunctionKey);
        String conceptionEntityFilterLogic = conceptionPathEntityFilter.equals("")?"":conceptionPathEntityFilter+"\n";

        String cypherProcedureString = "MATCH (startNode) WHERE id(startNode)= "+this.getEntityUID()+"\n" +
                "MATCH (endNode) WHERE id(endNode)= "+targetEntityUID+"\n" +
                "CALL apoc.algo.dijkstra(startNode,endNode, \""+relationMatchLogicFullString+"\", \""+relationWeightProperty+"\","+defaultWeight+","+maxReturnPathNumber+")\n" +
                "YIELD path, weight\n" +
                relationEntityFilterLogic + conceptionEntityFilterLogic+
                "RETURN path, weight\n" ;
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        if(this.getEntityUID() != null && targetEntityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            GetListEntitiesPathTransformer getListEntitiesPathTransformer = new GetListEntitiesPathTransformer(workingGraphOperationExecutor);
            getListEntitiesPathTransformer.enableRecordPathWeight();
            try {
                Object queryResponse = workingGraphOperationExecutor.executeRead(getListEntitiesPathTransformer,cypherProcedureString);
                return queryResponse != null? (List<EntitiesPath>)queryResponse : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private String generateShortPathsQuery(String pathFindingFunction,String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                           PathEntityFilterParameters relationPathEntityFilterParameters, PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException{
        /*
        Example:
        https://neo4j.com/docs/cypher-manual/current/clauses/match/
        https://neo4j.com/blog/query-cypher-data-relationships/
        https://graphaware.com/graphaware/2015/05/19/neo4j-cypher-variable-length-relationships-by-example.html
        https://stackoverflow.com/questions/38454046/cypher-shortestpath-query-with-filter
        https://stackoverflow.com/questions/47426924/neo4j-find-the-shortest-path-with-a-filter-on-every-node
        */
        /*
        MATCH (startNode),(endNode),
        p = shortestPath((startNode)-[*..10]-(endNode))   //OR p = shortestPath((startNode)-[*]-(endNode))  // OR p = allshortestPaths((startNode)-[:DOCG_GS_SpatialConnect1|DOCG_GS_SpatialConnect*..20]-(endNode))
        WHERE id(startNode) = 457380 AND id(endNode) = 457171
        RETURN p as path,length(p) AS hops
        */

        int maxLevelNumber = maxJump >= 2 ? maxJump : -1;
        String relationTypeFilter = "";
        if(pathAllowedRelationKinds == null || pathAllowedRelationKinds.size() == 0){
            if(maxLevelNumber == -1){
                relationTypeFilter = "*";
            }else{
                relationTypeFilter = "*.."+maxLevelNumber;
            }
        }else{
            String relationsFilterStr = ":";
            for(int i=0;i<pathAllowedRelationKinds.size();i++){
                String currentRelationKind = pathAllowedRelationKinds.get(i);
                relationsFilterStr = relationsFilterStr+currentRelationKind;
                if(i<pathAllowedRelationKinds.size()-1){
                    relationsFilterStr = relationsFilterStr+"|";
                }
            }
            if(maxLevelNumber == -1){
                relationTypeFilter = relationsFilterStr+"*";
            }else{
                relationTypeFilter = relationsFilterStr+"*.."+maxLevelNumber;
            }
        }

        String relationPathEntityFilter = generatePathEntityFilterQuery(relationPathEntityFilterParameters,"p",PathEntityType.RelationEntity,"AND");
        String relationEntityFilterLogic = relationPathEntityFilter.equals("")?"":relationPathEntityFilter+"\n";
        String conceptionPathEntityFilter = generatePathEntityFilterQuery(conceptionPathEntityFilterParameters,"p",PathEntityType.ConceptionEntity,"AND");
        String conceptionEntityFilterLogic = conceptionPathEntityFilter.equals("")?"":conceptionPathEntityFilter+"\n";

        String cypherProcedureString = "MATCH (startNode),(endNode),"+"\n" +
                "p = "+pathFindingFunction+"((startNode)-["+relationTypeFilter+"]-(endNode))" +"\n" +
                "WHERE id(startNode) = "+this.getEntityUID()+" AND id(endNode) = "+targetEntityUID +"\n" +
                conceptionEntityFilterLogic + relationEntityFilterLogic+
                "RETURN p as path,length(p) AS hops";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        return cypherProcedureString;
    }

    private String generatePathEntityFilterQuery(PathEntityFilterParameters pathEntityFilterParameters,String pathElementName,PathEntityType pathEntityType,String conjunctionKey) throws CoreRealmServiceEntityExploreException{
        if(pathEntityFilterParameters != null && pathEntityFilterParameters.getEntityAttributesFilterParameters() != null){
            String entityCollectFunction = "";
            String entityNodeName ="anyEntity";
            switch(pathEntityType){
                case RelationEntity:
                    entityCollectFunction = "relationships("+pathElementName+")";
                    entityNodeName = "relationEntity";
                    break;
                case ConceptionEntity:
                    entityCollectFunction = "nodes("+pathElementName+")";
                    entityNodeName = "conceptionEntity";
            }
            PathEntityFilterParameters.PathFilterScope entityPathFilterScope = pathEntityFilterParameters.getPathFilterScope() != null ?
                    pathEntityFilterParameters.getPathFilterScope() : PathEntityFilterParameters.PathFilterScope.AnyEntity;
            String pathEntityFilterScopeFunction = "ANY";
            switch (entityPathFilterScope){
                case AllEntity: pathEntityFilterScopeFunction = "ALL";
                    break;
                case AnyEntity: pathEntityFilterScopeFunction = "ANY";
                    break;
                //case NoneEntity: pathEntityFilterScopeFunction = "NONE";
            }

            AttributesParameters attributesParameters = pathEntityFilterParameters.getEntityAttributesFilterParameters();
            String wherePartQuery = CypherBuilder.generateAttributesParametersQueryLogic(attributesParameters,entityNodeName);
            if(!wherePartQuery.equals("")){
                String filterPartQueryString = conjunctionKey+" "+pathEntityFilterScopeFunction+"("+entityNodeName+" in "+entityCollectFunction+" "+wherePartQuery+") ";
                return filterPartQueryString;
            }
        }
        return "";
    }

    private String generateRelationKindFlowMatchLogicsQuery(LinkedList<List<RelationKindMatchLogic>> relationKindFlowMatchLogicsLink){
        if(relationKindFlowMatchLogicsLink != null && relationKindFlowMatchLogicsLink.size() > 0){
            String resultFullQueryString = "";
            boolean isFirstMatchLogic = true;
            for(List<RelationKindMatchLogic> currentMatchLogicList : relationKindFlowMatchLogicsLink){
                String currentQueryString = CypherBuilder.generateRelationKindMatchLogicsQuery(currentMatchLogicList,null);
                if(isFirstMatchLogic){
                    resultFullQueryString = currentQueryString;
                    if(!resultFullQueryString.equals("")){
                        isFirstMatchLogic = false;
                    }
                }else{
                    if(!currentQueryString.equals("")){
                        resultFullQueryString = resultFullQueryString + ","+currentQueryString;
                    }
                }
            }
            if(!resultFullQueryString.equals("")){
                return resultFullQueryString;
            }
        }
        return null;
    }

    private String generateConceptionKindFlowMatchLogicsQuery(LinkedList<List<ConceptionKindMatchLogic>> conceptionKindFlowMatchLogicList){
        if(conceptionKindFlowMatchLogicList != null && conceptionKindFlowMatchLogicList.size() > 0){
            String resultFullQueryString = "";
            boolean isFirstMatchLogic = true;
            for(List<ConceptionKindMatchLogic> currentMatchLogicList : conceptionKindFlowMatchLogicList){
                String currentQueryString = CypherBuilder.generateConceptionKindMatchLogicsQuery(currentMatchLogicList);
                if(isFirstMatchLogic){
                    resultFullQueryString = currentQueryString;
                    if(!resultFullQueryString.equals("")){
                        isFirstMatchLogic = false;
                    }
                }else{
                    if(!currentQueryString.equals("")){
                        resultFullQueryString = resultFullQueryString + ","+currentQueryString;
                    }
                }
            }
            if(!resultFullQueryString.equals("")){
                return resultFullQueryString;
            }
        }
        return null;
    }

    private String generateEntityPathFlowMatchLogicsQuery(LinkedList<List<? extends EntityKindMatchLogic>> entityPathFlowMatchLogics){
        if(entityPathFlowMatchLogics != null & entityPathFlowMatchLogics.size() < 2){
            return null;
        }
        if(entityPathFlowMatchLogics != null && entityPathFlowMatchLogics.size() > 0){
            String resultFullQueryString = "";
            boolean isFirstMatchLogic = true;
            for(List<? extends EntityKindMatchLogic> currentEntityKindMatchLogicsList : entityPathFlowMatchLogics){
                if(currentEntityKindMatchLogicsList.size()>0){
                    String currentQueryString = "";
                    if(currentEntityKindMatchLogicsList.get(0) instanceof ConceptionKindMatchLogic){
                        List<ConceptionKindMatchLogic> currentConceptionEntityKindMatchLogics = (List<ConceptionKindMatchLogic>)currentEntityKindMatchLogicsList;
                        currentQueryString = CypherBuilder.generateConceptionKindMatchLogicsQuery(currentConceptionEntityKindMatchLogics);
                    }
                    if(currentEntityKindMatchLogicsList.get(0) instanceof RelationKindMatchLogic){
                        List<RelationKindMatchLogic> currentRelationEntityKindMatchLogics = (List<RelationKindMatchLogic>)currentEntityKindMatchLogicsList;
                        currentQueryString = CypherBuilder.generateRelationKindMatchLogicsQuery(currentRelationEntityKindMatchLogics,null);
                    }
                    if(isFirstMatchLogic){
                        resultFullQueryString = currentQueryString;
                        if(!resultFullQueryString.equals("")){
                            isFirstMatchLogic = false;
                        }
                    }else{
                        if(!currentQueryString.equals("")){
                            resultFullQueryString = resultFullQueryString + ","+currentQueryString;
                        }
                    }
                }
            }
            if(!resultFullQueryString.equals("")){
                return resultFullQueryString;
            }
        }
        return null;
    }

    private String getAdvancedExpandQuery(AdvancedExpandType advancedExpandType,TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException{
        String apocProcedure = "";
        switch (advancedExpandType){
            case Path: apocProcedure = "apoc.path.expandConfig";
                break;
            case Graph: apocProcedure = "apoc.path.subgraphAll";
                break;
            case SpanningTree: apocProcedure = "apoc.path.spanningTree";
                break;
            case EndConceptionEntity: apocProcedure = "apoc.path.expandConfig";
        }

        String cypherProcedureString = null;
        if(travelParameters != null){
            int minJumpNumber = -1;
            switch (advancedExpandType){
                case Path: minJumpNumber = travelParameters.getMinJump() >= 0 ? travelParameters.getMinJump() : 0;
                    break;
                case Graph: minJumpNumber = travelParameters.getMinJump() == 0 ? 0 : 1;
                    break;
                case SpanningTree: minJumpNumber = travelParameters.getMinJump() == 0 ? 0 : 1;
                    break;
                case EndConceptionEntity: minJumpNumber = travelParameters.getMinJump() >= 0 ? travelParameters.getMinJump() : 0;
            }
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

            List<ConceptionKindMatchLogic> conceptionKindMatchLogicList = travelParameters.getConceptionKindMatchLogics();
            String labelFilterQueryString = CypherBuilder.generateConceptionKindMatchLogicsQuery(conceptionKindMatchLogicList);

            List<RelationKindMatchLogic> relationKindMatchLogicList = travelParameters.getRelationKindMatchLogics();
            RelationDirection relationDirection = travelParameters.getDefaultDirectionForNoneRelationKindMatch();
            String relationshipFilter = CypherBuilder.generateRelationKindMatchLogicsQuery(relationKindMatchLogicList,relationDirection);

            LinkedList<List<RelationKindMatchLogic>> relationKindFlowMatchLogicsLink = travelParameters.getRelationKindFlowMatchLogics();
            String relationKindFlowMatchLogicsQuery = generateRelationKindFlowMatchLogicsQuery(relationKindFlowMatchLogicsLink);
            if(relationKindFlowMatchLogicsQuery != null){
                relationshipFilter = relationKindFlowMatchLogicsQuery;
            }

            LinkedList<List<ConceptionKindMatchLogic>> conceptionKindFlowMatchLogicList = travelParameters.getConceptionKindFlowMatchLogics();
            String conceptionKindFlowMatchLogicsQuery = generateConceptionKindFlowMatchLogicsQuery(conceptionKindFlowMatchLogicList);
            if(conceptionKindFlowMatchLogicsQuery != null){
                labelFilterQueryString = conceptionKindFlowMatchLogicsQuery;
            }

            String sequenceQueryString = "null";
            LinkedList<List<? extends EntityKindMatchLogic>> entityPathFlowMatchLogics = travelParameters.getEntityPathFlowMatchLogics();
            String entityPathFlowMatchLogicsQuery = generateEntityPathFlowMatchLogicsQuery(entityPathFlowMatchLogics);
            if(entityPathFlowMatchLogicsQuery != null){
                sequenceQueryString =  "\"" + entityPathFlowMatchLogicsQuery + "\"";
            }

            String endNodesQueryString = "";
            String terminatorNodesQueryString = "";
            String whitelistNodesQueryString = "";
            String blacklistNodesQueryString = "";

            String endNodesString = "null";
            String terminatorNodesString = "null";
            String whitelistNodesString = "null";
            String blacklistNodesString = "null";

            List<String> endNodesUIDList = travelParameters.getEndWithConceptionEntityUIDs();
            List<String> terminatorNodesUIDList = travelParameters.getTerminateAtConceptionEntityUIDs();
            List<String> whitelistNodesUIDList = travelParameters.getMustHaveConceptionEntityUIDs();
            List<String> blacklistNodesUIDList = travelParameters.getNotAllowConceptionEntityUIDs();

            if(endNodesUIDList != null && endNodesUIDList.size()>0){
                if(endNodesUIDList.size() == 1){
                    endNodesQueryString = "MATCH (endlistNodes) WHERE id(endlistNodes) IN "+endNodesUIDList.toString()+"\n";
                    endNodesString = "[endlistNodes]";
                }else{
                    endNodesQueryString = "MATCH (endlist) WHERE id(endlist) IN "+endNodesUIDList.toString()+"\n"+
                            "WITH n, collect(endlist) AS endlistNodes"+"\n";
                    endNodesString = "endlistNodes";
                }
            }
            if(terminatorNodesUIDList != null && terminatorNodesUIDList.size()>0){
                if(terminatorNodesUIDList.size() == 1){
                    terminatorNodesQueryString = "MATCH (terminatorlistNodes) WHERE id(terminatorlistNodes) IN "+terminatorNodesUIDList.toString()+"\n";
                    terminatorNodesString = "[terminatorlistNodes]";
                }else{
                    terminatorNodesQueryString = "MATCH (terminatorlist) WHERE id(terminatorlist) IN "+terminatorNodesUIDList.toString()+"\n"+
                            "WITH n, collect(terminatorlist) AS terminatorlistNodes"+"\n";
                    terminatorNodesString = "terminatorlistNodes";
                }
            }
            if(whitelistNodesUIDList != null && whitelistNodesUIDList.size()>0){
                if(whitelistNodesUIDList.size() == 1){
                    whitelistNodesQueryString = "MATCH (whitelistNodes) WHERE id(whitelistNodes) IN "+whitelistNodesUIDList.toString()+"\n";
                    whitelistNodesString = "[whitelistNodes]";
                }else{
                    whitelistNodesQueryString = "MATCH (whitelist) WHERE id(whitelist) IN "+whitelistNodesUIDList.toString()+"\n"+
                            "WITH n, collect(whitelist) AS whitelistNodes"+"\n";
                    whitelistNodesString = "whitelistNodes";
                }
            }
            if(blacklistNodesUIDList != null && blacklistNodesUIDList.size()>0){
                if(blacklistNodesUIDList.size() ==1){
                    blacklistNodesQueryString = "MATCH (blacklistNodes) WHERE id(blacklistNodes) IN "+blacklistNodesUIDList.toString()+"\n";
                    blacklistNodesString = "[blacklistNodes]";
                }else{
                    blacklistNodesQueryString = "MATCH (blacklist) WHERE id(blacklist) IN "+blacklistNodesUIDList.toString()+"\n"+
                            "WITH n, collect(blacklist) AS blacklistNodes"+"\n";
                    blacklistNodesString = "blacklistNodes";
                }
            }
            String orderByLogicString = resultNumber > 0 ? "ORDER BY hops LIMIT "+resultNumber+";" :"ORDER BY hops;";

            String relationPathEntityFilter = generatePathEntityFilterQuery(travelParameters.getRelationPathEntityFilterParameters(),"path",PathEntityType.RelationEntity,"WHERE");
            String relationEntityFilterLogic = relationPathEntityFilter.equals("")?"":relationPathEntityFilter+"\n";
            String conjunctionKey = relationEntityFilterLogic.equals("") ? "WHERE" : "AND";
            String conceptionPathEntityFilter = generatePathEntityFilterQuery(travelParameters.getConceptionPathEntityFilterParameters(),"path",PathEntityType.ConceptionEntity,conjunctionKey);
            String conceptionEntityFilterLogic = conceptionPathEntityFilter.equals("")?"":conceptionPathEntityFilter+"\n";

            switch (advancedExpandType){
                case Path:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            endNodesQueryString +
                            terminatorNodesQueryString +
                            whitelistNodesQueryString +
                            blacklistNodesQueryString +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: "+minJumpNumber+",\n" +
                            "   maxLevel: "+maxJumpNumber+",\n" +
                            "   relationshipFilter: \""+relationshipFilter+"\",\n" +
                            "   labelFilter:\""+labelFilterQueryString+"\",\n" +
                            "   sequence:"+sequenceQueryString+",\n" +
                            "   beginSequenceAtStart: "+travelParameters.isMatchStartEntityForFlow()+",\n" +
                            "   bfs: "+usingBFS+",\n" +
                            "   filterStartNode: "+travelParameters.isMatchStartConceptionEntity()+",\n" +
                            "   limit: "+resultNumber+",\n" +
                            "   endNodes:"+endNodesString+",\n" +
                            "   terminatorNodes:"+terminatorNodesString+",\n" +
                            "   whitelistNodes:"+whitelistNodesString+",\n" +
                            "   blacklistNodes:"+blacklistNodesString+"\n" +
                            "   })\n" +
                            "YIELD path\n" +
                            "RETURN DISTINCT path, length(path) AS hops\n" +
                            orderByLogicString;
                    break;
                case Graph:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            endNodesQueryString +
                            terminatorNodesQueryString +
                            whitelistNodesQueryString +
                            blacklistNodesQueryString +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: "+minJumpNumber+",\n" +
                            "   maxLevel: "+maxJumpNumber+",\n" +
                            "   relationshipFilter: \""+relationshipFilter+"\",\n" +
                            "   labelFilter:\""+labelFilterQueryString+"\",\n" +
                            "   beginSequenceAtStart: "+travelParameters.isMatchStartEntityForFlow()+",\n" +
                            "   bfs: "+usingBFS+",\n" +
                            "   filterStartNode: "+travelParameters.isMatchStartConceptionEntity()+",\n" +
                            "   limit: "+resultNumber+",\n" +
                            "   endNodes:"+endNodesString+",\n" +
                            "   terminatorNodes:"+terminatorNodesString+",\n" +
                            "   whitelistNodes:"+whitelistNodesString+",\n" +
                            "   blacklistNodes:"+blacklistNodesString+"\n" +
                            "   })\n" +
                            "YIELD nodes, relationships\n" +
                            "RETURN nodes, relationships;";
                    break;
                case SpanningTree:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            endNodesQueryString +
                            terminatorNodesQueryString +
                            whitelistNodesQueryString +
                            blacklistNodesQueryString +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: "+minJumpNumber+",\n" +
                            "   maxLevel: "+maxJumpNumber+",\n" +
                            "   relationshipFilter: \""+relationshipFilter+"\",\n" +
                            "   labelFilter:\""+labelFilterQueryString+"\",\n" +
                            "   beginSequenceAtStart: "+travelParameters.isMatchStartEntityForFlow()+",\n" +
                            "   bfs: "+usingBFS+",\n" +
                            "   filterStartNode: "+travelParameters.isMatchStartConceptionEntity()+",\n" +
                            "   limit: "+resultNumber+",\n" +
                            "   endNodes:"+endNodesString+",\n" +
                            "   terminatorNodes:"+terminatorNodesString+",\n" +
                            "   whitelistNodes:"+whitelistNodesString+",\n" +
                            "   blacklistNodes:"+blacklistNodesString+"\n" +
                            "   })\n" +
                            "YIELD path\n" +
                            "RETURN path;";
                    break;
                case EndConceptionEntity:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            endNodesQueryString +
                            terminatorNodesQueryString +
                            whitelistNodesQueryString +
                            blacklistNodesQueryString +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: "+minJumpNumber+",\n" +
                            "   maxLevel: "+maxJumpNumber+",\n" +
                            "   relationshipFilter: \""+relationshipFilter+"\",\n" +
                            "   labelFilter:\""+labelFilterQueryString+"\",\n" +
                            "   sequence:"+sequenceQueryString+",\n" +
                            "   beginSequenceAtStart: "+travelParameters.isMatchStartEntityForFlow()+",\n" +
                            "   bfs: "+usingBFS+",\n" +
                            "   filterStartNode: "+travelParameters.isMatchStartConceptionEntity()+",\n" +
                            "   limit: "+resultNumber+",\n" +
                            "   endNodes:"+endNodesString+",\n" +
                            "   terminatorNodes:"+terminatorNodesString+",\n" +
                            "   whitelistNodes:"+whitelistNodesString+",\n" +
                            "   blacklistNodes:"+blacklistNodesString+"\n" +
                            "   })\n" +
                            "YIELD path\n" +
                            relationEntityFilterLogic + conceptionEntityFilterLogic+
                            "RETURN last(nodes(path)) as operationResult, length(path) AS hops"+"\n" +
                            "ORDER BY hops";
                    break;
            }
        }else{
            switch (advancedExpandType){
                case Path:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: 0,\n" +
                            "   maxLevel: 1,\n" +
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
                    break;
                case Graph:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: 0,\n" +
                            "   maxLevel: 1,\n" +
                            "   relationshipFilter: \"\",\n" +
                            "   labelFilter:\"\",\n" +
                            "   beginSequenceAtStart:true,\n" +
                            "   bfs:true,\n" +
                            "   filterStartNode:false,\n" +
                            "   limit:-1,\n" +
                            "   endNodes:null,\n" +
                            "   terminatorNodes:null,\n" +
                            "   whitelistNodes:null,\n" +
                            "   blacklistNodes:null\n" +
                            "   })\n" +
                            "YIELD nodes, relationships\n" +
                            "RETURN nodes, relationships;";
                    break;
                case SpanningTree:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: 0,\n" +
                            "   maxLevel: 1,\n" +
                            "   relationshipFilter: \"\",\n" +
                            "   labelFilter:\"\",\n" +
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
                            "RETURN path;";
                    break;
                case EndConceptionEntity:
                    cypherProcedureString = "MATCH (n) WHERE id(n)= "+this.getEntityUID()+"\n" +
                            "CALL "+apocProcedure+"(n, {\n" +
                            "   minLevel: 0,\n" +
                            "   maxLevel: 1,\n" +
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
                            "RETURN last(nodes(path)) as operationResult, length(path) AS hops"+"\n" +
                            "ORDER BY hops";
                    break;
            }
        }
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
        return cypherProcedureString;
    }
}
