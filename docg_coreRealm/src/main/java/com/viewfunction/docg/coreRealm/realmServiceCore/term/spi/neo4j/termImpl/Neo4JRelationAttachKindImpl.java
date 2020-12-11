package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JRelationAttachKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Neo4JRelationAttachKindImpl implements Neo4JRelationAttachKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JRelationAttachKindImpl.class);

    private String coreRealmName;
    private String relationAttachKindName;
    private String relationAttachKindDesc;
    private String relationAttachKindUID;
    private String sourceConceptionKindName;
    private String targetConceptionKindName;
    private String relationKindName;

    @Override
    public String getRelationAttachKindUID() {
        return this.relationAttachKindUID;
    }

    @Override
    public String getSourceConceptionKindName() {
        return this.sourceConceptionKindName;
    }

    @Override
    public String getTargetConceptionKindName() {
        return this.targetConceptionKindName;
    }

    @Override
    public String getRelationKindName() {
        return this.relationKindName;
    }

    @Override
    public String getRelationAttachKindDesc() {
        return this.relationAttachKindDesc;
    }

    @Override
    public boolean updateRelationAttachKindDesc(String newDesc) {
        return false;
    }

    @Override
    public List<RelationAttachLinkLogic> getRelationAttachLinkLogic() {
        return null;
    }

    @Override
    public RelationAttachLinkLogic createRelationAttachLinkLogic(RelationAttachLinkLogic relationAttachLinkLogic) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public boolean removeRelationAttachLinkLogic(String relationAttachLinkLogicUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public boolean newRelationEntity(String sourceConceptionEntityUID, String targetConceptionEntityUID, Map<String, Object> relationData) {
        return false;
    }

    @Override
    public EntitiesOperationResult newUniversalRelationEntities() {
        return null;
    }

    @Override
    public boolean allowRepeatableRelationKind() {
        return false;
    }

    @Override
    public List<String> getSourceKindRelationProperties() {
        return null;
    }

    @Override
    public List<String> getTargetKindRelationProperties() {
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return this.relationAttachKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
