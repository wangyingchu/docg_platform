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
    private boolean allowRepeatableRelationKind;

    public Neo4JRelationAttachKindImpl(String coreRealmName, String relationAttachKindName, String relationAttachKindDesc, String relationAttachKindUID,
                                       String sourceConceptionKindName, String targetConceptionKindName,String relationKindName,boolean allowRepeatableRelationKind){
        this.coreRealmName = coreRealmName;
        this.relationAttachKindName = relationAttachKindName;
        this.relationAttachKindDesc = relationAttachKindDesc;
        this.relationAttachKindUID = relationAttachKindUID;
        this.sourceConceptionKindName = sourceConceptionKindName;
        this.targetConceptionKindName = targetConceptionKindName;
        this.relationKindName = relationKindName;
        this.allowRepeatableRelationKind = allowRepeatableRelationKind;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

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
    public String getRelationAttachKindName() {
        return this.relationAttachKindName;
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
    public boolean newRelationEntities(String conceptionEntityUID, EntityRelateRole entityRelateRole, Map<String,Object> relationData) {
        return false;
    }

    @Override
    public EntitiesOperationResult newUniversalRelationEntities(Map<String,Object> relationData) {
        return null;
    }

    @Override
    public boolean isRepeatableRelationKindAllow() {
        return this.allowRepeatableRelationKind;
    }

    @Override
    public boolean setAllowRepeatableRelationKind(boolean allowRepeatableRelationKind) {
        return false;
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
