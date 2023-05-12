package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.RelationKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.termInf.NebulaGraphConceptionKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NebulaGraphConceptionKindImpl implements NebulaGraphConceptionKind {

    private static Logger logger = LoggerFactory.getLogger(NebulaGraphConceptionKindImpl.class);
    private String coreRealmName;
    private String conceptionKindName;
    private String conceptionKindDesc;
    private String conceptionKindUID;

    public NebulaGraphConceptionKindImpl(String coreRealmName,String conceptionKindName,String conceptionKindDesc,String conceptionKindUID){
        this.coreRealmName = coreRealmName;
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindDesc = conceptionKindDesc;
        this.conceptionKindUID = conceptionKindUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public RelationEntity attachClassification(RelationAttachInfo relationAttachInfo, String classificationName) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public boolean detachClassification(String classificationName, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<Classification> getAttachedClassifications(String relationKindName, RelationDirection relationDirection) {
        return null;
    }

    @Override
    public Date getCreateDateTime() {
        return null;
    }

    @Override
    public Date getLastModifyDateTime() {
        return null;
    }

    @Override
    public String getCreatorId() {
        return null;
    }

    @Override
    public String getDataOrigin() {
        return null;
    }

    @Override
    public boolean updateLastModifyDateTime() {
        return false;
    }

    @Override
    public boolean updateCreatorId(String creatorId) {
        return false;
    }

    @Override
    public boolean updateDataOrigin(String dataOrigin) {
        return false;
    }

    @Override
    public boolean addOrUpdateMetaConfigItem(String itemName, Object itemValue) {
        return false;
    }

    @Override
    public Map<String, Object> getMetaConfigItems() {
        return null;
    }

    @Override
    public Object getMetaConfigItem(String itemName) {
        return null;
    }

    @Override
    public boolean deleteMetaConfigItem(String itemName) {
        return false;
    }

    @Override
    public Map<String, Number> statisticNumericalAttributes(QueryParameters queryParameters, List<NumericalAttributeStatisticCondition> statisticCondition) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public List<GroupNumericalAttributesStatisticResult> statisticNumericalAttributesByGroup(String groupByAttribute, QueryParameters queryParameters, List<NumericalAttributeStatisticCondition> statisticConditions) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public Map<String, List<ConceptionEntity>> statisticRelatedClassifications(QueryParameters queryParameters, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public List<KindEntityAttributeRuntimeStatistics> statisticEntityAttributesDistribution(long sampleCount) {
        return null;
    }

    @Override
    public Map<String, Long> statisticEntityRelationDegree(AttributesParameters queryParameters, List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public AttributeValueDistributionInfo statisticAttributeValueDistribution(AttributesParameters queryParameters, String attributeName) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public String getConceptionKindName() {
        return null;
    }

    @Override
    public String getConceptionKindDesc() {
        return null;
    }

    @Override
    public boolean updateConceptionKindDesc(String kindDesc) {
        return false;
    }

    @Override
    public Long countConceptionEntities() throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public Long countConceptionEntitiesWithOffspring() throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public List<ConceptionKind> getChildConceptionKinds() throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public ConceptionKind getParentConceptionKind() throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public InheritanceTree<ConceptionKind> getOffspringConceptionKinds() throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation) {
        return null;
    }

    @Override
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) {
        return null;
    }

    @Override
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation) {
        return null;
    }

    @Override
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) {
        return null;
    }

    @Override
    public ConceptionEntity updateEntity(ConceptionEntityValue conceptionEntityValueForUpdate) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public EntitiesOperationResult updateEntities(List<ConceptionEntityValue> entityValues) {
        return null;
    }

    @Override
    public boolean deleteEntity(String conceptionEntityUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public EntitiesOperationResult deleteEntities(List<String> conceptionEntityUIDs) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public Long countEntities(AttributesParameters attributesParameters, boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntities(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public ConceptionEntity getEntityByUID(String conceptionEntityUID) {
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKinds(List<String> attributesViewKindNames, QueryParameters exploreParameters) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters exploreParameters) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public boolean attachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<AttributesViewKind> getContainsAttributesViewKinds() {
        return null;
    }

    @Override
    public List<AttributesViewKind> getContainsAttributesViewKinds(String attributesViewKindName) {
        return null;
    }

    @Override
    public boolean detachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<AttributeKind> getContainsSingleValueAttributeKinds() {
        return null;
    }

    @Override
    public List<AttributeKind> getContainsSingleValueAttributeKinds(String attributeKindName) {
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getKindDirectRelatedEntities(List<String> startEntityUIDS, String relationKind, RelationDirection relationDirection, String aimConceptionKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getAttributesOfKindDirectRelatedEntities(List<String> startEntityUIDS, List<String> attributeNames, String relationKind, RelationDirection relationDirection, String aimConceptionKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntitiesByDirectRelations(String relationKind, RelationDirection relationDirection, String aimConceptionKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public Set<KindAttributeDistributionInfo> getKindAttributesDistributionStatistics(double sampleRatio) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public Set<KindDataDistributionInfo> getKindDataDistributionStatistics(double sampleRatio) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public Set<ConceptionKindCorrelationInfo> getKindRelationDistributionStatistics() {
        return null;
    }

    @Override
    public Set<ConceptionEntity> getRandomEntities(int entitiesCount) throws CoreRealmServiceEntityExploreException {
        return null;
    }

    @Override
    public Set<ConceptionEntity> getRandomEntities(AttributesParameters attributesParameters, boolean isDistinctMode, int entitiesCount) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public EntitiesOperationStatistics setKindScopeAttributes(Map<String, Object> attributes) {
        return null;
    }

    @Override
    public EntitiesOperationStatistics removeEntityAttributes(Set<String> attributeNames) {
        return null;
    }

    @Override
    public void convertEntityAttributeToIntType(String attributeName) {

    }

    @Override
    public void convertEntityAttributeToFloatType(String attributeName) {

    }

    @Override
    public void convertEntityAttributeToBooleanType(String attributeName) {

    }

    @Override
    public void convertEntityAttributeToStringType(String attributeName) {

    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor){
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
