package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListClassificationTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleClassificationTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.common.structureImpl.CommonInheritanceTreeImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JClassification;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4JClassificationImpl extends Neo4JAttributesMeasurableImpl implements Neo4JClassification {

    private static Logger logger = LoggerFactory.getLogger(Neo4JClassificationImpl.class);
    private String coreRealmName;
    private String classificationName;
    private String classificationDesc;
    private String classificationUID;

    public Neo4JClassificationImpl(String coreRealmName,String classificationName,String classificationDesc,String classificationUID){
        super(classificationUID);
        this.coreRealmName = coreRealmName;
        this.classificationName = classificationName;
        this.classificationDesc = classificationDesc;
        this.classificationUID = classificationUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public String getClassificationUID() {
        return this.classificationUID;
    }

    public String getCoreRealmName() {
        return this.coreRealmName;
    }

    @Override
    public String getClassificationName() {
        return this.classificationName;
    }

    @Override
    public String getClassificationDesc() {
        return this.classificationDesc;
    }

    @Override
    public boolean isRootClassification() {
        return getParentClassification() == null ? true : false;
    }

    @Override
    public Classification getParentClassification() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.TO, null);
            GetSingleClassificationTransformer getSingleClassificationTransformer =
                    new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object classificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer,queryCql);
            return classificationRes != null?(Classification)classificationRes:null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<Classification> getChildClassifications() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM, null);
            GetListClassificationTransformer getListClassificationTransformer =
                    new GetListClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object classificationListRes = workingGraphOperationExecutor.executeWrite(getListClassificationTransformer,queryCql);
            return classificationListRes != null ? (List<Classification>)classificationListRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public InheritanceTree<Classification> getOffspringClassifications() {
        Table<String,String,Classification> treeElementsTable = HashBasedTable.create();
        treeElementsTable.put(InheritanceTree.Virtual_ParentID_Of_Root_Node,this.classificationName,this);
        Map<String,String> classificationUID_NameMapping = new HashMap<>();
        classificationUID_NameMapping.put(this.classificationUID,this.classificationName);

        String currentCoreRealmName = this.coreRealmName;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            /*
            MATCH (sourceNode)<-[relation:`DOCG_ParentClassificationIs`*]-(operationResult:`DOCG_Classification`) WHERE id(sourceNode) = 2324 RETURN operationResult,relation
            */
            String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),
                    RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM,0,0);
            DataTransformer offspringClassificationsDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    List<Record> recordList = result.list();
                    if(recordList != null){
                        for(Record nodeRecord : recordList){
                            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            long nodeUID = resultNode.id();
                            String classificationName = resultNode.get(RealmConstant._NameProperty).asString();
                            classificationUID_NameMapping.put(""+nodeUID,classificationName);
                        }
                    }
                    if(recordList != null){
                        for(Record nodeRecord : recordList){
                            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            long nodeUID = resultNode.id();
                            String coreRealmName = currentCoreRealmName;
                            String classificationName = resultNode.get(RealmConstant._NameProperty).asString();
                            String classificationDesc = null;
                            if(resultNode.get(RealmConstant._DescProperty) != null){
                                classificationDesc = resultNode.get(RealmConstant._DescProperty).asString();
                            }
                            String classificationUID = ""+nodeUID;
                            Neo4JClassificationImpl neo4JClassificationImpl =
                                    new Neo4JClassificationImpl(coreRealmName,classificationName,classificationDesc,classificationUID);
                            neo4JClassificationImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

                            List<Object> relationships = nodeRecord.get(CypherBuilder.relationResultName).asList();
                            String parentClassificationUID = null;
                            for(Object currentRelationship : relationships){
                                Relationship currentTargetRelationship = (Relationship)currentRelationship;
                                String startNodeUID = "" + currentTargetRelationship.startNodeId();
                                String endNodeUID = "" + currentTargetRelationship.endNodeId();
                                if(startNodeUID.equals(classificationUID)){
                                    parentClassificationUID = endNodeUID;
                                    break;
                                }
                            }
                            treeElementsTable.put(classificationUID_NameMapping.get(parentClassificationUID),
                                    classificationUID_NameMapping.get(classificationUID),neo4JClassificationImpl);

                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(offspringClassificationsDataTransformer,queryCql);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        CommonInheritanceTreeImpl<Classification> resultInheritanceTree = new CommonInheritanceTreeImpl(this.classificationName,treeElementsTable);
        return resultInheritanceTree;
    }

    @Override
    public boolean attachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public boolean detachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public Classification createChildClassification(String classificationName, String classificationDesc) {
        return null;
    }

    @Override
    public boolean removeChildClassification(String classificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return classificationUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
