package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.EntitiesExchangeOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeSystemInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Neo4JEntitiesExchangeOperatorImpl implements EntitiesExchangeOperator {

    private String coreRealmName;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;
    private static Logger logger = LoggerFactory.getLogger(DataScienceOperator.class);

    public Neo4JEntitiesExchangeOperatorImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public EntitiesOperationStatistics importConceptionEntitiesFromArrow(String conceptionKindName, String arrowFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.load/apoc.load.arrow/
        String cql = "CALL apoc.load.arrow(\""+arrowFileLocation+"\",{}) YIELD value\n" +
                "        UNWIND value.entityRow AS entity\n" +
                "        CREATE (operationResult:"+conceptionKindName+") SET operationResult = apoc.convert.fromJsonMap(entity).properties RETURN count(operationResult)";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("count(operationResult)")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("count(operationResult)").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("importConceptionEntitiesFromArrow operation execute finish. conceptionKindName is "
                +conceptionKindName+", arrowFileLocation is "+arrowFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToArrow(String conceptionKindName, String arrowFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.arrow.query/
        /*
           "CALL apoc.export.arrow.query(\"/home/wangychu/Desktop/tess/x4.arrow\",\"match (operationResult:DOCG_GS_Continent) return operationResult\",{})\n" +
           "YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
           "RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        */
        String cql = "CALL apoc.export.arrow.query(\""+arrowFileLocation+"\",\"match (entityRow:"+conceptionKindName+") return entityRow\",{})\n" +
                "YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("nodes")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("nodes").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportConceptionEntitiesToArrow operation execute finish. conceptionKindName is "
                +conceptionKindName+", arrowFileLocation is "+arrowFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics importConceptionEntitiesFromCSV(String conceptionKindName, String csvFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.import/apoc.import.csv/
        /*
        CALL apoc.import.csv(
          [{fileName: 'file:/export/movies-l.csv', labels: ['TestLoad2']}],
          [],
          {delimiter: ',', arrayDelimiter: ',', stringIds: false}
        )
        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data
        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data
        */
        String cql = "CALL apoc.import.csv(\n" +
                "          [{fileName: 'file:"+csvFileLocation+"', labels: ['"+conceptionKindName+"']}],\n" +
                "          [],\n" +
                "          {delimiter: ',', arrayDelimiter: ',', stringIds: false}\n" +
                "        )\n" +
                "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("nodes")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("nodes").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("importConceptionEntitiesFromCSV operation execute finish. conceptionKindName is "
                +conceptionKindName+", csvFileLocation is "+csvFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToCSV(String conceptionKindName, String csvFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.csv.data/
        /*
        MATCH (testLoad:DOCG_GS_County)
        WITH collect(testLoad) AS people
        CALL apoc.export.csv.data(people, [], "export/movies-l.csv", {})
        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data
        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data
         */
        String cql = "MATCH (kindEntity:"+conceptionKindName+")\n" +
                "        WITH collect(kindEntity) AS entityRow\n" +
                "        CALL apoc.export.csv.data(entityRow, [], \""+csvFileLocation+"\", {})\n" +
                "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("nodes")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("nodes").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportConceptionEntitiesToCSV operation execute finish. conceptionKindName is "
                +conceptionKindName+", csvFileLocation is "+csvFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics exportCoreRealmEntitiesToArrow(String arrowFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.arrow.all/
        String cql = "CALL apoc.export.arrow.all(\""+ arrowFileLocation +"\", {})\n"+
                "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("rows")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("rows").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportCoreRealmEntitiesToArrow operation execute finish. CoreRealm Name is "
                +this.coreRealmName+", arrowFileLocation is "+arrowFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics importRelationEntitiesFromArrow(String relationKindName, String arrowFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.load/apoc.load.arrow/
        String cql = "CALL apoc.load.arrow(\""+arrowFileLocation+"\",{}) YIELD value\n" +
                "        UNWIND value.entityRow AS entity\n" +
                "        WITH apoc.convert.fromJsonMap(entity) AS entityDataMap\n" +
                "        WITH entityDataMap.start.id AS startUID,entityDataMap.end.id AS endUID,entityDataMap.properties AS edgeProperties\n" +
                "        MATCH (fromNode) WHERE id(fromNode) = toIntegerOrNull(startUID)\n" +
                "        MATCH (toNode) WHERE id(toNode) = toIntegerOrNull(endUID)\n" +
                "        CALL apoc.create.relationship(fromNode, \""+relationKindName+"\", edgeProperties, toNode)\n" +
                "        YIELD rel\n" +
                "        RETURN count(rel) AS operationResult";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("operationResult")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("operationResult").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("importRelationEntitiesFromArrow operation execute finish. relationKindName is "
                +relationKindName+", arrowFileLocation is "+arrowFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics exportRelationEntitiesToArrow(String relationKindName, String arrowFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.arrow.query/
        String cql = "CALL apoc.export.arrow.query(\""+arrowFileLocation+"\",\"MATCH p=()-[entityRow:"+relationKindName+"]->() RETURN entityRow\",{})\n" +
                "YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("relationships")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("relationships").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportRelationEntitiesToArrow operation execute finish. relationKindName is "
                +relationKindName+", arrowFileLocation is "+arrowFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics importRelationEntitiesFromCSV(String relationKindName, String csvFileLocation) {
        TimeZone timeZone = TimeZone.getDefault();
        String timeZoneID = timeZone.getID();
        //https://neo4j.com/labs/apoc/5/import/load-csv/
        String cql = "CALL apoc.load.csv('file:"+csvFileLocation+"') YIELD map AS entityDataMap\n" +
                "WITH entityDataMap._start AS startUID, entityDataMap._end AS endUID, entityDataMap AS edgeProperties\n" +
                "MATCH (fromNode) WHERE id(fromNode) = toIntegerOrNull(startUID)\n" +
                "MATCH (toNode) WHERE id(toNode) = toIntegerOrNull(endUID)\n" +
                "CALL apoc.create.relationship(fromNode, \""+relationKindName+"\", edgeProperties, toNode)\n" +
                "YIELD rel\n" +
                "REMOVE rel._start,rel._end,rel._id,rel._labels,rel._type\n" +
                "SET rel.lastModifyDate = datetime({timezone: '"+timeZoneID+"'})\n" +
                "RETURN count(rel) AS operationResult;\n";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("operationResult")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("operationResult").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("importRelationEntitiesFromCSV operation execute finish. relationKindName is "
                +relationKindName+", csvFileLocation is "+csvFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics exportRelationEntitiesToCSV(String relationKindName, String csvFileLocation) {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.csv.data/
        String cql ="MATCH p=()-[entityRow:`"+relationKindName+"`]->()\n"+
                "WITH collect(entityRow) AS relationEntity\n"+
                "CALL apoc.export.csv.data([],relationEntity, \""+csvFileLocation+"\", {})\n" +
                "YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        logger.debug("Generated Cypher Statement: {}", cql);
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("relationships")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("relationships").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportRelationEntitiesToCSV operation execute finish. relationKindName is "
                +relationKindName+", csvFileLocation is "+csvFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics exportRelationEntitiesToArrow(String relationKindName, QueryParameters queryParameters, String arrowFileLocation) throws CoreRealmServiceEntityExploreException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.arrow.query/
        if (queryParameters != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            Neo4JSystemMaintenanceOperatorImpl systemMaintenanceOperator = new Neo4JSystemMaintenanceOperatorImpl(this.coreRealmName);
            systemMaintenanceOperator.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            List<AttributeSystemInfo> attributeSystemInfoList = systemMaintenanceOperator.getRelationKindAttributesSystemInfo(relationKindName);

            StringBuffer returnAttributesCqlPart = new StringBuffer();
            for(int i = 0 ; i < attributeSystemInfoList.size(); i++){
                AttributeSystemInfo currentAttributeSystemInfo = attributeSystemInfoList.get(i);
                String currentAttributeName = currentAttributeSystemInfo.getAttributeName();
                returnAttributesCqlPart.append(CypherBuilder.operationResultName + "." + currentAttributeName + " AS "+currentAttributeName);
                returnAttributesCqlPart.append(", ");
            }
            returnAttributesCqlPart.append("id(operationResult) AS _RelUID, id(sourceNode) AS _StartEntityUID, id(targetNode) AS _EndEntityUID");
            String returnAttributeCqlPart = "RETURN "+ returnAttributesCqlPart.toString();

            queryParameters.setEntityKind(relationKindName);
            String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,
                    null,null,false,queryParameters,null);

            queryCql = queryCql.replace("RETURN operationResult, sourceNode, targetNode",returnAttributeCqlPart);

            String exportCql = "CALL apoc.export.arrow.query(\""+ arrowFileLocation +"\",\""+queryCql+"\", {})\n"+
                    "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                    "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
            logger.debug("Generated Cypher Statement: {}", exportCql);

            EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
            entitiesOperationStatistics.setStartTime(new Date());

            try{
                DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record operationResultRecord = result.next();
                            if(operationResultRecord.containsKey("rows")){
                                long resultPropertiesRows = operationResultRecord.get("rows").asLong();
                                //data property number + _RelUID + _StartEntityUID + _EndEntityUID
                                long dataRows = resultPropertiesRows/(attributeSystemInfoList.size()+3);
                                entitiesOperationStatistics.setSuccessItemsCount(dataRows);
                            }
                        }
                        return true;
                    }
                };
                workingGraphOperationExecutor.executeWrite(dataTransformer, exportCql);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            entitiesOperationStatistics.setFinishTime(new Date());
            entitiesOperationStatistics.setOperationSummary("exportRelationEntitiesToArrow operation execute finish. relationKindName is "
                    +relationKindName+", arrowFileLocation is "+arrowFileLocation);
            return entitiesOperationStatistics;
        }
        return null;
    }

    @Override
    public EntitiesOperationStatistics exportRelationEntitiesToCSV(String relationKindName, QueryParameters queryParameters, String csvFileLocation) throws CoreRealmServiceEntityExploreException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.csv.query/
        if (queryParameters != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            Neo4JSystemMaintenanceOperatorImpl systemMaintenanceOperator = new Neo4JSystemMaintenanceOperatorImpl(this.coreRealmName);
            systemMaintenanceOperator.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            List<AttributeSystemInfo> attributeSystemInfoList = systemMaintenanceOperator.getRelationKindAttributesSystemInfo(relationKindName);

            StringBuffer returnAttributesCqlPart = new StringBuffer();
            for(int i = 0 ; i < attributeSystemInfoList.size(); i++){
                AttributeSystemInfo currentAttributeSystemInfo = attributeSystemInfoList.get(i);
                String currentAttributeName = currentAttributeSystemInfo.getAttributeName();
                returnAttributesCqlPart.append(CypherBuilder.operationResultName + "." + currentAttributeName + " AS "+currentAttributeName);
                returnAttributesCqlPart.append(", ");
            }
            returnAttributesCqlPart.append("id(operationResult) AS _RelUID, id(sourceNode) AS _StartEntityUID, id(targetNode) AS _EndEntityUID");
            String returnAttributeCqlPart = "RETURN "+ returnAttributesCqlPart.toString();

            queryParameters.setEntityKind(relationKindName);
            String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,
                    null,null,false,queryParameters,null);
            queryCql = queryCql.replace("RETURN operationResult, sourceNode, targetNode",returnAttributeCqlPart);
            String exportCql = "CALL apoc.export.csv.query(\""+ queryCql +"\",\""+csvFileLocation+"\", {})\n"+
                    "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                    "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
            logger.debug("Generated Cypher Statement: {}", exportCql);

            EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
            entitiesOperationStatistics.setStartTime(new Date());

            try{
                DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record operationResultRecord = result.next();
                            if(operationResultRecord.containsKey("rows")){
                                entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("rows").asLong());
                            }
                        }
                        return true;
                    }
                };
                workingGraphOperationExecutor.executeWrite(dataTransformer, exportCql);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            entitiesOperationStatistics.setFinishTime(new Date());
            entitiesOperationStatistics.setOperationSummary("exportRelationEntitiesToCSV operation execute finish. relationKindName is "
                    +relationKindName+", arrowFileLocation is "+csvFileLocation);
            return entitiesOperationStatistics;
        }
        return null;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToArrow(String conceptionKindName, QueryParameters queryParameters, String arrowFileLocation) throws CoreRealmServiceEntityExploreException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.arrow.query/
        if (queryParameters != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            Neo4JSystemMaintenanceOperatorImpl systemMaintenanceOperator = new Neo4JSystemMaintenanceOperatorImpl(this.coreRealmName);
            systemMaintenanceOperator.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            List<AttributeSystemInfo> attributeSystemInfoList = systemMaintenanceOperator.getConceptionKindAttributesSystemInfo(conceptionKindName);

            StringBuffer returnAttributesCqlPart = new StringBuffer();
            for(int i = 0 ; i < attributeSystemInfoList.size(); i++){
                AttributeSystemInfo currentAttributeSystemInfo = attributeSystemInfoList.get(i);
                String currentAttributeName = currentAttributeSystemInfo.getAttributeName();
                returnAttributesCqlPart.append(CypherBuilder.operationResultName + "." + currentAttributeName + " AS "+currentAttributeName);
                if(i != attributeSystemInfoList.size()-1){
                    returnAttributesCqlPart.append(", ");
                }
            }
            String returnAttributeCqlPart = "RETURN "+ returnAttributesCqlPart.toString();
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(conceptionKindName,queryParameters,null);
            queryCql = queryCql.replace("RETURN "+CypherBuilder.operationResultName,returnAttributeCqlPart);
            String exportCql = "CALL apoc.export.arrow.query(\""+ arrowFileLocation +"\",\""+queryCql+"\", {})\n"+
                    "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                    "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
            logger.debug("Generated Cypher Statement: {}", exportCql);

            EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
            entitiesOperationStatistics.setStartTime(new Date());

            try{
                DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record operationResultRecord = result.next();
                            if(operationResultRecord.containsKey("rows")){
                                entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("rows").asLong());
                            }
                        }
                        return true;
                    }
                };
                workingGraphOperationExecutor.executeWrite(dataTransformer, exportCql);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            entitiesOperationStatistics.setFinishTime(new Date());
            entitiesOperationStatistics.setOperationSummary("exportConceptionEntitiesToArrow operation execute finish. conceptionKindName is "
                    +conceptionKindName+", arrowFileLocation is "+arrowFileLocation);
            return entitiesOperationStatistics;
        }
        return null;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToCSV(String conceptionKindName, QueryParameters queryParameters, String csvFileLocation) throws CoreRealmServiceEntityExploreException {
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.csv.query/
        if (queryParameters != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            Neo4JSystemMaintenanceOperatorImpl systemMaintenanceOperator = new Neo4JSystemMaintenanceOperatorImpl(this.coreRealmName);
            systemMaintenanceOperator.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            List<AttributeSystemInfo> attributeSystemInfoList = systemMaintenanceOperator.getConceptionKindAttributesSystemInfo(conceptionKindName);

            StringBuffer returnAttributesCqlPart = new StringBuffer();
            for(int i = 0 ; i < attributeSystemInfoList.size(); i++){
                AttributeSystemInfo currentAttributeSystemInfo = attributeSystemInfoList.get(i);
                String currentAttributeName = currentAttributeSystemInfo.getAttributeName();
                returnAttributesCqlPart.append(CypherBuilder.operationResultName + "." + currentAttributeName + " AS "+currentAttributeName);
                if(i != attributeSystemInfoList.size()-1){
                    returnAttributesCqlPart.append(", ");
                }
            }
            String returnAttributeCqlPart = "RETURN "+ returnAttributesCqlPart.toString();
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(conceptionKindName,queryParameters,null);
            queryCql = queryCql.replace("RETURN "+CypherBuilder.operationResultName,returnAttributeCqlPart);
            String exportCql = "CALL apoc.export.csv.query(\""+ queryCql +"\",\""+csvFileLocation+"\", {})\n"+
                    "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                    "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
            logger.debug("Generated Cypher Statement: {}", exportCql);

            EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
            entitiesOperationStatistics.setStartTime(new Date());

            try{
                DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record operationResultRecord = result.next();
                            if(operationResultRecord.containsKey("nodes")){
                                entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("nodes").asLong());
                            }
                        }
                        return true;
                    }
                };
                workingGraphOperationExecutor.executeWrite(dataTransformer, exportCql);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            entitiesOperationStatistics.setFinishTime(new Date());
            entitiesOperationStatistics.setOperationSummary("exportConceptionEntitiesToCSV operation execute finish. conceptionKindName is "
                    +conceptionKindName+", csvFileLocation is "+csvFileLocation);
            return entitiesOperationStatistics;
        }
        return null;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToArrow(String conceptionKindName, List<String> resultAttributeNames, QueryParameters queryParameters, String arrowFileLocation) throws CoreRealmServiceEntityExploreException {
        String queryCql = CypherBuilder.matchAttributesWithQueryParameters(conceptionKindName,queryParameters,resultAttributeNames);

        for(String currentAttribute:resultAttributeNames){
            queryCql = queryCql.replace(CypherBuilder.operationResultName+"."+currentAttribute+"",""+CypherBuilder.operationResultName+"."+currentAttribute+" AS "+currentAttribute);
        }
        queryCql = queryCql.replace("id("+CypherBuilder.operationResultName+")","id("+CypherBuilder.operationResultName+") AS UID");

        String exportCql = "CALL apoc.export.arrow.query(\""+ arrowFileLocation +"\",\""+queryCql+"\", {})\n"+
                "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        logger.debug("Generated Cypher Statement: {}", exportCql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("rows")){
                            long arrowOperationResult = operationResultRecord.get("rows").asLong();
                            long resultCSVRows = arrowOperationResult / (resultAttributeNames.size()+1);
                            entitiesOperationStatistics.setSuccessItemsCount(resultCSVRows);
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, exportCql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportConceptionEntitiesToArrow operation execute finish. conceptionKindName is "
                +conceptionKindName+", arrowFileLocation is "+arrowFileLocation);
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics exportConceptionEntitiesToCSV(String conceptionKindName, List<String> resultAttributeNames, QueryParameters queryParameters, String csvFileLocation) throws CoreRealmServiceEntityExploreException {
        String queryCql = CypherBuilder.matchAttributesWithQueryParameters(conceptionKindName,queryParameters,resultAttributeNames);

        for(String currentAttribute:resultAttributeNames){
            queryCql = queryCql.replace(CypherBuilder.operationResultName+"."+currentAttribute+"",""+CypherBuilder.operationResultName+"."+currentAttribute+" AS "+currentAttribute);
        }
        queryCql = queryCql.replace("id("+CypherBuilder.operationResultName+")","id("+CypherBuilder.operationResultName+") AS UID");

        String exportCql = "CALL apoc.export.csv.query(\""+ queryCql +"\",\""+csvFileLocation+"\", {})\n"+
                "        YIELD file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data\n" +
                "        RETURN file, source, format, nodes, relationships, properties, time, rows, batchSize, batches, done, data";
        logger.debug("Generated Cypher Statement: {}", exportCql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("rows")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("rows").asLong());
                        }
                    }
                    return true;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, exportCql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportConceptionEntitiesToCSV operation execute finish. conceptionKindName is "
                +conceptionKindName+", csvFileLocation is "+csvFileLocation);
        return entitiesOperationStatistics;
    }
}
