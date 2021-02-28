package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.orientdb.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

import java.util.List;
import java.util.Map;

public class OrientDBCoreRealmImpl implements CoreRealm {

    public OrientDBCoreRealmImpl(String coreRealmName){}

    public OrientDBCoreRealmImpl(){}

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.ORIENTDB;
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        return null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc) {
        return null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName, String conceptionKindDesc,String parentConceptionKindName) {
        return null;
    }

    @Override
    public boolean removeConceptionKind(String conceptionKindName, boolean deleteExistEntities) {
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
    public Classification createClassification(String classificationName, String classificationDesc, String parentClassificationName) throws CoreRealmServiceRuntimeException{
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
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames, ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation) {
        return null;
    }

    @Override
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames, ConceptionEntityValue conceptionEntityValue, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) {
        return null;
    }

    @Override
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames, List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation) {
        return null;
    }

    @Override
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames, List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) {
        return null;
    }

    @Override
    public List<Map<String,Map<String,Object>>> executeCustomQuery(String customQuerySentence) throws CoreRealmServiceRuntimeException {
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
    public boolean removeTimeFlow(String timeFlowName) throws CoreRealmServiceRuntimeException {
        return false;
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
    public boolean removeGeospatialRegion(String geospatialRegionName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<GeospatialRegion> getGeospatialRegions() {
        return null;
    }

    @Override
    public void openGlobalSession() {

    }

    @Override
    public void closeGlobalSession() {

    }
}
