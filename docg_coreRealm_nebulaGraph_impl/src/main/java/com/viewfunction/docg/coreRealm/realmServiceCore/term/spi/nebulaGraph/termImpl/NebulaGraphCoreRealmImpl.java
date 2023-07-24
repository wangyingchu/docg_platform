package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.EntitiesExchangeOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.AttributesViewKindMetaInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.termInf.NebulaGraphCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class NebulaGraphCoreRealmImpl implements NebulaGraphCoreRealm {

    private static Logger logger = LoggerFactory.getLogger(NebulaGraphCoreRealmImpl.class);
    private String coreRealmName = null;

    public NebulaGraphCoreRealmImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.NEBULAGRAPH;
    }

    @Override
    public String getCoreRealmName() {
        return this.coreRealmName;
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        return null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName, String conceptionKindDesc) {






        return null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName, String conceptionKindDesc, String parentConceptionKindName) throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public boolean removeConceptionKind(String conceptionKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public AttributesViewKind getAttributesViewKind(String attributesViewKindUID) {
        return null;
    }

    @Override
    public AttributesViewKind createAttributesViewKind(String attributesViewKindName, String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm) {
        return null;
    }

    @Override
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<AttributesViewKind> getAttributesViewKinds(String attributesViewKindName, String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm) {
        return null;
    }

    @Override
    public AttributeKind getAttributeKind(String attributeKindUID) {
        return null;
    }

    @Override
    public AttributeKind createAttributeKind(String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType) {
        return null;
    }

    @Override
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<AttributeKind> getAttributeKinds(String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType) {
        return null;
    }

    @Override
    public RelationKind getRelationKind(String relationKindName) {
        return null;
    }

    @Override
    public RelationKind createRelationKind(String relationKindName, String relationKindDesc) {
        return null;
    }

    @Override
    public RelationKind createRelationKind(String relationKindName, String relationKindDesc, String parentRelationKindName) throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public boolean removeRelationKind(String relationKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<RelationAttachKind> getRelationAttachKinds(String relationAttachKindName, String relationAttachKindDesc, String sourceConceptionKindName, String targetConceptionKindName, String relationKindName, Boolean allowRepeatableRelationKind) {
        return null;
    }

    @Override
    public RelationAttachKind getRelationAttachKind(String relationAttachKindUID) {
        return null;
    }

    @Override
    public RelationAttachKind createRelationAttachKind(String relationAttachKindName, String relationAttachKindDesc, String sourceConceptionKindName, String targetConceptionKindName, String relationKindName, boolean allowRepeatableRelationKind) throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public boolean removeRelationAttachKind(String relationAttachKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public Classification getClassification(String classificationName) {
        return null;
    }

    @Override
    public Classification createClassification(String classificationName, String classificationDesc) {
        return null;
    }

    @Override
    public Classification createClassification(String classificationName, String classificationDesc, String parentClassificationName) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public boolean removeClassification(String classificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public boolean removeClassificationWithOffspring(String classificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames, ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames, ConceptionEntityValue conceptionEntityValue, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames, List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames, List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public List<Map<String, Map<String, Object>>> executeCustomQuery(String customQuerySentence) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public Map<String, Number> executeCustomStatistic(String customQuerySentence) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public TimeFlow getOrCreateTimeFlow() {
        return null;
    }

    @Override
    public TimeFlow getOrCreateTimeFlow(String timeFlowName) {
        return null;
    }

    @Override
    public long removeTimeFlowWithEntities() {
        return 0;
    }

    @Override
    public long removeTimeFlowWithEntities(String timeFlowName) {
        return 0;
    }

    @Override
    public List<TimeFlow> getTimeFlows() {
        return null;
    }

    @Override
    public GeospatialRegion getOrCreateGeospatialRegion() {
        return null;
    }

    @Override
    public GeospatialRegion getOrCreateGeospatialRegion(String geospatialRegionName) {
        return null;
    }

    @Override
    public long removeGeospatialRegionWithEntities() {
        return 0;
    }

    @Override
    public long removeGeospatialRegionWithEntities(String geospatialRegionName) {
        return 0;
    }

    @Override
    public List<GeospatialRegion> getGeospatialRegions() {
        return null;
    }

    @Override
    public List<EntityStatisticsInfo> getConceptionEntitiesStatistics() throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public List<EntityStatisticsInfo> getRelationEntitiesStatistics() {
        return null;
    }

    @Override
    public List<ConceptionKindCorrelationInfo> getConceptionKindsCorrelation() {
        return null;
    }

    @Override
    public CrossKindDataOperator getCrossKindDataOperator() {
        return null;
    }

    @Override
    public SystemMaintenanceOperator getSystemMaintenanceOperator() {
        return null;
    }

    @Override
    public DataScienceOperator getDataScienceOperator() {
        return null;
    }

    @Override
    public EntitiesExchangeOperator getEntitiesExchangeOperator() {
        return null;
    }

    @Override
    public List<KindMetaInfo> getConceptionKindsMetaInfo() throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public List<KindMetaInfo> getRelationKindsMetaInfo() throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public List<AttributeKindMetaInfo> getAttributeKindsMetaInfo() throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public List<AttributesViewKindMetaInfo> getAttributesViewKindsMetaInfo() throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public void openGlobalSession() {
        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        this.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public void closeGlobalSession() {
        if(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor() != null){
            try {
                this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor().close();
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
            this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(null);
        }
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor){
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
