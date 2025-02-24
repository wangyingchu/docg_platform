package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.memgraph.dataTransformer.GetSingleConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLongFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf.MemGraphCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JClassificationImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MemGraphCoreRealmImpl extends Neo4JCoreRealmImpl implements MemGraphCoreRealm {

    private static Logger logger = LoggerFactory.getLogger(MemGraphCoreRealmImpl.class);

    public MemGraphCoreRealmImpl(String coreRealmName){
        super(coreRealmName);
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public MemGraphCoreRealmImpl(){
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.MEMGRAPH;
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        if(conceptionKindName == null){
            return null;
        }

        if(conceptionKindName.startsWith("DOCG_")){
            MemGraphConceptionKindImpl memGraphConceptionKindImpl =
                    new MemGraphConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,"0");
            memGraphConceptionKindImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            return memGraphConceptionKindImpl;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ConceptionKindClass,RealmConstant._NameProperty,conceptionKindName,1);
            GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                    new GetSingleConceptionKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,queryCql);
            return createConceptionKindRes != null?(ConceptionKind)createConceptionKindRes:null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeConceptionKind(String conceptionKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException{
        if(conceptionKindName == null){
            return false;
        }
        ConceptionKind targetConceptionKind =this.getConceptionKind(conceptionKindName);
        if(targetConceptionKind == null){
            logger.error("CoreRealm does not contains ConceptionKind with name {}.", conceptionKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm does not contains ConceptionKind with name " + conceptionKindName + ".");
            throw exception;
        }else{
            if(deleteExistEntities){
                targetConceptionKind.purgeAllEntities();
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String conceptionKindUID = ((MemGraphConceptionKindImpl)targetConceptionKind).getConceptionKindUID();
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(conceptionKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedConceptionKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedConceptionKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String conceptionKindId = deletedConceptionKindRes.toString();
                    MemGraphConceptionKindImpl resultMemGraphConceptionKindImplForCacheOperation = new MemGraphConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,conceptionKindId);
                    executeConceptionKindCacheOperation(resultMemGraphConceptionKindImplForCacheOperation,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        if(attributesViewKindUID == null){
            return false;
        }
        AttributesViewKind targetAttributesViewKind = this.getAttributesViewKind(attributesViewKindUID);
        if(targetAttributesViewKind != null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(attributesViewKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedAttributesViewKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeAttributesViewKindCacheOperation(targetAttributesViewKind,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }else{
            logger.error("AttributesViewKind does not contains entity with UID {}.", attributesViewKindUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("AttributesViewKind does not contains entity with UID " + attributesViewKindUID + ".");
            throw exception;
        }
    }

    @Override
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException {
        if(attributeKindUID == null){
            return false;
        }
        AttributeKind targetAttributeKind = this.getAttributeKind(attributeKindUID);
        if(targetAttributeKind != null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(attributeKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedAttributeKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedAttributeKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeAttributeKindCacheOperation(targetAttributeKind,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }else{
            logger.error("AttributeKind does not contains entity with UID {}.", attributeKindUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("AttributeKind does not contains entity with UID " + attributeKindUID + ".");
            throw exception;
        }
    }

    @Override
    public boolean removeRelationKind(String relationKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException {
        if(relationKindName == null){
            return false;
        }
        RelationKind targetRelationKind = this.getRelationKind(relationKindName);
        if(targetRelationKind == null){
            logger.error("RelationKind does not contains entity with UID {}.", relationKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("RelationKind does not contains entity with UID " + relationKindName + ".");
            throw exception;
        }else{
            if(deleteExistEntities){
                targetRelationKind.purgeAllRelationEntities();
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String relationKindUID = ((Neo4JRelationKindImpl)targetRelationKind).getRelationKindUID();
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedRelationKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedRelationKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String resultRelationKindUID = deletedRelationKindRes.toString();
                    Neo4JRelationKindImpl resultNeo4JRelationKindImplForCacheOperation = new Neo4JRelationKindImpl(getCoreRealmName(),relationKindName,null,resultRelationKindUID);
                    executeRelationKindCacheOperation(resultNeo4JRelationKindImplForCacheOperation,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public boolean removeClassification(String classificationName) throws CoreRealmServiceRuntimeException {
        return this.removeClassification(classificationName,false);
    }

    @Override
    public boolean removeClassificationWithOffspring(String classificationName) throws CoreRealmServiceRuntimeException {
        return this.removeClassification(classificationName,true);
    }

    private boolean removeClassification(String classificationName, boolean cascadingDeleteOffspring) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return false;
        }
        Classification targetClassification = this.getClassification(classificationName);
        if(targetClassification == null){
            logger.error("CoreRealm does not contains Classification with name {}.", classificationName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm does not contains Classification with name " + classificationName + ".");
            throw exception;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                if(!cascadingDeleteOffspring) {
                    String classificationUID = targetClassification.getClassificationUID();
                    String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.valueOf(classificationUID), CypherBuilder.CypherFunctionType.ID, null);
                    GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                    Object deletedClassificationRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                    if (deletedClassificationRes == null) {
                        throw new CoreRealmServiceRuntimeException();
                    } else {
                        String classificationId = deletedClassificationRes.toString();
                        Neo4JClassificationImpl resultNeo4JClassificationImplForCacheOperation = new Neo4JClassificationImpl(getCoreRealmName(), classificationName, null, classificationId);
                        executeClassificationCacheOperation(resultNeo4JClassificationImplForCacheOperation, CacheOperationType.DELETE);
                        return true;
                    }
                }else{
                    String classificationUID = ((Neo4JClassificationImpl) targetClassification).getClassificationUID();
                    List<Object> withOffspringClassificationUIDList = new ArrayList<>();
                    String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),
                            RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM,0,0, CypherBuilder.ReturnRelationableDataType.BOTH);
                    withOffspringClassificationUIDList.add(Long.parseLong(classificationUID));
                    DataTransformer offspringClassificationsDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            List<Record> recordList = result.list();
                            if(recordList != null){
                                for(Record nodeRecord : recordList){
                                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                    long nodeUID = resultNode.id();
                                    withOffspringClassificationUIDList.add(nodeUID);
                                }
                            }
                            return null;
                        }
                    };
                    workingGraphOperationExecutor.executeRead(offspringClassificationsDataTransformer,queryCql);

                    List<Object> deletedClassificationUIDList = new ArrayList<>();
                    String deleteCql = CypherBuilder.deleteNodesWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, withOffspringClassificationUIDList);
                    DataTransformer deleteOffspringClassificationsDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            List<Record> recordList = result.list();
                            if(recordList != null){
                                for(Record nodeRecord : recordList){
                                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                    long nodeUID = resultNode.id();
                                    deletedClassificationUIDList.add(nodeUID);
                                }
                            }
                            return null;
                        }
                    };
                    workingGraphOperationExecutor.executeWrite(deleteOffspringClassificationsDataTransformer,deleteCql);

                    if(deletedClassificationUIDList.size() == withOffspringClassificationUIDList.size()){
                        return true;
                    }else{
                        logger.error("Not all offspring classifications of Classification {} are successful removed.", classificationName);
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("Not all offspring classifications of Classification "+classificationName+" are successful removed.");
                        throw exception;
                    }
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
