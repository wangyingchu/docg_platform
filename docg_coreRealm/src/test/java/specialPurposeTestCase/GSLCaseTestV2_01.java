package specialPurposeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.NullValueFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.SimilarFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GSLCaseTestV2_01 {
    public static void main(String[] args) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        //createConceptionKind();
        //loadCSVEntities("/home/wangychu/.config/Neo4j Desktop/Application/relate-data/dbmss/dbms-0c5e0595-78cc-4566-9d8e-0a0495ee435e/import/");
        //linkFederationOfIndustryAndCommerce();
        //linkFederationOfIndustryAndCommerceAndChamberOfCommerce();
        //linkFederationOfIndustryAndMember();
        //linkChamberOfCommerceAndMember();
        //linkExecutiveRelations();
        //cleanExecutiveProperties();
        //linkSamePerson();
        //linkSameEnterprise();
        //linkSameExecutive();
        //need add  this logic MATCH p=(n1)-[r:IsSamePerson]->(n2) WHERE id(n1)=id(n2) delete r return count(r)
        //cleanSelfSamePersonLink();
        //linkChamberOfCommerceAndLeader();
        //linkFICAndLeader();
        //addLinkDisplayProperty();






        //linkFederationOfIndustryAndCommerceGeo();
        //linkKindGeo("ChamberOfCommerce","CHAMBER_COMMERCE_LEVEL", "chamberServiceForDistrict","ORG_PROVINCE","ORG_CITY","ORG_COUNTY");
        //linkKindGeo("EnterpriseMember","ADMINI_LEVEL","enterpriseLocatedAtDistrict","PROVINCE","CITY","COUNTY");
        //linkKindGeo("IndividualMember","ADMINI_LEVEL","personLocatedAtDistrict","PROVINCE","CITY","COUNTY");
        //linkKindGeo("GroupMember","ADMINI_LEVEL","organizationLocatedAtDistrict","PROVINCE","CITY","COUNTY");
        //linkKindGeo("Executive","ADMINI_LEVEL","personLocatedAtDistrict","PROVINCE","CITY","COUNTY");
        //linkKindGeo("ExecutiveEnterprise","ADMINI_LEVEL","enterpriseLocatedAtDistrict","PROVINCE","CITY","COUNTY");
        //linkEnterpriseMemberDate();
        //linkExecutiveEnterpriseDate();
        //linkIndividualMemberDate();
        //linkExecutiveDate();
        //linkChamberOfCommerceDate();
    }

    private static void createConceptionKind(){
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        /* Define ConceptionKind */
        coreRealm.createConceptionKind("FederationOfIndustryAndCommerce","工商联");
        coreRealm.createConceptionKind("FIC_HistoricalChangeTerm","工商联历史界次");
        coreRealm.createConceptionKind("ChamberOfCommerce","商会");
        coreRealm.createConceptionKind("EnterpriseMember","企业会员");
        coreRealm.createConceptionKind("IndividualMember","个人会员");
        coreRealm.createConceptionKind("GroupMember","团体会员");
        coreRealm.createConceptionKind("Executive","执常委");
        coreRealm.createConceptionKind("ExecutiveCommonDuty","执常委职务");
        coreRealm.createConceptionKind("ExecutiveEnterprise","执常委相关企业");
        coreRealm.createConceptionKind("ExecutiveFicElseDuty","执常委工商联职务");
        coreRealm.createConceptionKind("ExecutiveHonor","执常委荣誉");
        coreRealm.createConceptionKind("ChamberOfCommerceLeader","商会领导");
        coreRealm.createConceptionKind("ChamberOfCommerceLeaderDuty","商会领导职务");
        coreRealm.createConceptionKind("FICLeader","工商联领导");
        coreRealm.createConceptionKind("FICLeaderSocialDuty","工商联领导社会职务");
    }

    private static void loadCSVEntities(String headerFileLocation){
        Map<String,String>  attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_SH_INFO.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_SH_INFO.csv","ChamberOfCommerce",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_ENTERPRISE_MEMBER_INFO.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_ENTERPRISE_MEMBER_INFO.csv","EnterpriseMember",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_PERSONAL_MEMBER_INFO.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_PERSONAL_MEMBER_INFO.csv","IndividualMember",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_ORGANIZATION_MEMBER_INFO.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_ORGANIZATION_MEMBER_INFO.csv","GroupMember",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_EXECUTIVE_BASIC_INFO.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_EXECUTIVE_BASIC_INFO.csv","Executive",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_EXECUTIVE_COMMON_DUTY.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_EXECUTIVE_COMMON_DUTY.csv","ExecutiveCommonDuty",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_EXECUTIVE_ENTERPRISE.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_EXECUTIVE_ENTERPRISE.csv","ExecutiveEnterprise",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_EXECUTIVE_FIC_ELSE_DUTY.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_EXECUTIVE_FIC_ELSE_DUTY.csv","ExecutiveFicElseDuty",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_EXECUTIVE_HONOR.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_EXECUTIVE_HONOR.csv","ExecutiveHonor",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_DIM_ORG.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_DIM_ORG.csv","FederationOfIndustryAndCommerce",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_JG_GSL_INFO_DTL.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_JG_GSL_INFO_DTL.csv","FIC_HistoricalChangeTerm",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_SH_LEADER.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_SH_LEADER.csv","ChamberOfCommerceLeader",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_SH_LEADER_DUTY.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_SH_LEADER_DUTY.csv","ChamberOfCommerceLeaderDuty",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_ORG_LEADER_INFO.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_ORG_LEADER_INFO.csv","FICLeader",attributesMapping);

        attributesMapping = BatchDataOperationUtil.getAttributesMappingFromHeaderCSV(headerFileLocation+"DWS_ORG_LEADER_SOCIAL_DUTY.csv");
        BatchDataOperationUtil.importConceptionEntitiesFromCSV("file:///DWS_ORG_LEADER_SOCIAL_DUTY.csv","FICLeaderSocialDuty",attributesMapping);
    }

    private static void linkFederationOfIndustryAndCommerce(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        Map<String,Object> operationResult =null;

        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "FederationOfIndustryAndCommerce",queryParameters,"P_ORG_ID",
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "ParentFederationOfIndustryAndCommerceIs", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);

        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "FIC_HistoricalChangeTerm",queryParameters,"ORG_ID",
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "HistoricalChangeTermOfFIC", BatchDataOperationUtil.CPUUsageRate.High
        );

        System.out.println(operationResult);
    }

    private static void linkFederationOfIndustryAndCommerceAndChamberOfCommerce(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        Map<String,Object> operationResult =null;
        /*Link FederationOfIndustryAndCommerce and ChamberOfCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "ChamberOfCommerce",queryParameters,"ORG_ID",
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "BelongsToFederationOfIndustryAndCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
    }

    private static void linkFederationOfIndustryAndMember(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        QueryParameters queryParameters2 = new QueryParameters();
        queryParameters2.setResultNumber(10000000);
        queryParameters2.setDefaultFilteringItem(new EqualFilteringItem("MEMBER_ORG_FIRM","工商联直属会员"));
        Map<String,Object> operationResult =null;
        /*Link EnterpriseMember and FederationOfIndustryAndCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "EnterpriseMember",queryParameters2,"ORG_ID",
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "BelongsToFederationOfIndustryAndCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
        /*Link IndividualMember and FederationOfIndustryAndCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "IndividualMember",queryParameters2,"ORG_ID",
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "BelongsToFederationOfIndustryAndCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
        /*Link GroupMember and FederationOfIndustryAndCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "GroupMember",queryParameters2,"ORG_ID",
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "BelongsToFederationOfIndustryAndCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
    }

    private static void linkChamberOfCommerceAndMember(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        QueryParameters queryParameters2 = new QueryParameters();
        queryParameters2.setResultNumber(10000000);
        queryParameters2.setDefaultFilteringItem(new EqualFilteringItem("MEMBER_ORG_FIRM","所属商会会员"));
        Map<String,Object> operationResult =null;
        /*Link EnterpriseMember and ChamberOfCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "EnterpriseMember",queryParameters,"FIRM_ID",
                "ChamberOfCommerce",queryParameters,"SHID",
                "BelongsToChamberOfCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
        /*Link IndividualMember and ChamberOfCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "IndividualMember",queryParameters,"FIRM_ID",
                "ChamberOfCommerce",queryParameters,"SHID",
                "BelongsToChamberOfCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
        /*Link GroupMember and ChamberOfCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "GroupMember",queryParameters,"FIRM_ID",
                "ChamberOfCommerce",queryParameters,"SHID",
                "BelongsToChamberOfCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
    }

    private static void linkChamberOfCommerceAndLeader(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        QueryParameters queryParameters2 = new QueryParameters();
        queryParameters2.setResultNumber(10000000);

        Map<String,Object> operationResult =null;
        /*Link EnterpriseMember and ChamberOfCommerce*/
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "ChamberOfCommerce",queryParameters,"SHID",
                "ChamberOfCommerceLeader",queryParameters,"SHID",
                "LeaderOfChamberOfCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "ChamberOfCommerceLeader",queryParameters,"ID",
                "ChamberOfCommerceLeaderDuty",queryParameters,"LDID",
                "HasLeaderCommonDuty", BatchDataOperationUtil.CPUUsageRate.High
        );
    }

    private static void linkFICAndLeader(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        QueryParameters queryParameters2 = new QueryParameters();
        queryParameters2.setResultNumber(10000000);

        Map<String,Object> operationResult =null;
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "FICLeader",queryParameters,"ORG_ID",
                "LeaderOfFIC", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "FICLeader",queryParameters,"PERSON_ID",
                "FICLeaderSocialDuty",queryParameters,"PERSON_ID",
                "HasLeaderSocialDuty", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
    }

    private static void linkExecutiveRelations(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        Map<String,Object> operationResult =null;

        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "FederationOfIndustryAndCommerce",queryParameters,"ORG_ID",
                "Executive",queryParameters,"ORG_ID",
                "ExecutiveOfFederationOfIndustryAndCommerce", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);

        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "Executive",queryParameters,"PERSON_ID",
                "ExecutiveCommonDuty",queryParameters,"PERSON_ID",
                "HasExecutiveCommonDuty", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);

        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "Executive",queryParameters,"PERSON_ID",
                "ExecutiveEnterprise",queryParameters,"PERSON_ID",
                "RelatedToExecutiveEnterprise", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);

        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "Executive",queryParameters,"PERSON_ID",
                "ExecutiveHonor",queryParameters,"PERSON_ID",
                "HasHonor", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);

        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "Executive",queryParameters,"PERSON_ID",
                "ExecutiveFicElseDuty",queryParameters,"PERSON_ID",
                "HasFicElseDuty", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
    }

    private static void cleanExecutiveProperties() throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        ConceptionKind conceptionKind = coreRealm.getConceptionKind("Executive");
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDefaultFilteringItem(new SimilarFilteringItem("CERTIFICATE_NO","，", SimilarFilteringItem.MatchingType.EndWith));
        queryParameters.setResultNumber(10000000);

        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult = conceptionKind.getEntities(queryParameters);

        List<ConceptionEntity> conceptionEntityList = conceptionEntitiesRetrieveResult.getConceptionEntities();
        for(ConceptionEntity currentConceptionEntity:conceptionEntityList){
            AttributeValue idNOAttributeValue = currentConceptionEntity.getAttribute("CERTIFICATE_NO");
            if(idNOAttributeValue!=null){
                String currentIDValue = idNOAttributeValue.getAttributeValue().toString();
                if(currentIDValue.endsWith("，")){
                    currentConceptionEntity.updateAttribute("CERTIFICATE_NO",currentIDValue.replaceAll("，",""));
                }
            }
        }

        queryParameters.setDefaultFilteringItem(new SimilarFilteringItem("CERTIFICATE_NO"," ", SimilarFilteringItem.MatchingType.BeginWith));
        conceptionEntitiesRetrieveResult = conceptionKind.getEntities(queryParameters);

        conceptionEntityList = conceptionEntitiesRetrieveResult.getConceptionEntities();
        for(ConceptionEntity currentConceptionEntity:conceptionEntityList){
            AttributeValue idNOAttributeValue = currentConceptionEntity.getAttribute("CERTIFICATE_NO");
            if(idNOAttributeValue!=null){
                String currentIDValue = idNOAttributeValue.getAttributeValue().toString();
                if(currentIDValue.startsWith(" ")){
                    currentConceptionEntity.updateAttribute("CERTIFICATE_NO",currentIDValue.trim());
                }
            }
        }
        coreRealm.closeGlobalSession();
    }

    private static void linkSamePerson() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind conceptionKind = coreRealm.getConceptionKind("Executive");
        QueryParameters queryParameters2 = new QueryParameters();
        queryParameters2.setResultNumber(10000000);
        NullValueFilteringItem _NullValueFilteringItem = new NullValueFilteringItem("CERTIFICATE_NO");
        _NullValueFilteringItem.reverseCondition();
        queryParameters2.setDefaultFilteringItem(_NullValueFilteringItem);
        List<String> attributesNameList = new ArrayList<>();
        attributesNameList.add("CERTIFICATE_NO");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult =
                conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributesNameList,queryParameters2);
        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();
        Map<String,String> personIDAndEntityUIDMap = new HashMap<>();
        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
            Map<String,Object> propMap = currentConceptionEntityValue.getEntityAttributesValue();
            String entityUID = currentConceptionEntityValue.getConceptionEntityUID();
            if(propMap.get("CERTIFICATE_NO").toString().length() ==18){
                //只统计合法身份证
                personIDAndEntityUIDMap.put(propMap.get("CERTIFICATE_NO").toString(),entityUID);
            }
        }

        ConceptionKind conceptionKind2 = coreRealm.getConceptionKind("IndividualMember");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult2 =
                conceptionKind2.getSingleValueEntityAttributesByAttributeNames(attributesNameList,queryParameters2);
        List<ConceptionEntityValue> conceptionEntityValueList2 = conceptionEntitiesAttributesRetrieveResult2.getConceptionEntityValues();
        Map<String,String> individualIDAndEntityUIDMap = new HashMap<>();
        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList2){
            Map<String,Object> propMap = currentConceptionEntityValue.getEntityAttributesValue();
            String entityUID = currentConceptionEntityValue.getConceptionEntityUID();
            individualIDAndEntityUIDMap.put(propMap.get("CERTIFICATE_NO").toString(),entityUID);
        }

        List<RelationEntityValue> relationEntityValueList = new ArrayList<>();
        for(Map.Entry<String, String> entry:personIDAndEntityUIDMap.entrySet()){
            String personID = entry.getKey();
            String entityUID = entry.getValue();
            if(individualIDAndEntityUIDMap.containsKey(personID)){
                RelationEntityValue currentRelationEntityValue = new RelationEntityValue(null,individualIDAndEntityUIDMap.get(personID),entityUID,null);
                relationEntityValueList.add(currentRelationEntityValue);
            }
        }
        Map<String,Object> operationResult =null;
        operationResult = BatchDataOperationUtil.batchAttachNewRelations(relationEntityValueList,"IsSamePerson", BatchDataOperationUtil.CPUUsageRate.Middle);
        System.out.println(operationResult);
    }

    private static void linkSameEnterprise(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        NullValueFilteringItem _NullValueFilteringItem = new NullValueFilteringItem("ENTERPRISE_ID");
        _NullValueFilteringItem.reverseCondition();
        Map<String,Object> operationResult =null;
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "EnterpriseMember",queryParameters,"ENTERPRISE_ID",
                "ExecutiveEnterprise",queryParameters,"ENTERPRISE_ID",
                "IsSameEnterprise", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
    }

    private static void linkSameExecutive(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        NullValueFilteringItem _NullValueFilteringItem = new NullValueFilteringItem("PERSON_KEY");
        _NullValueFilteringItem.reverseCondition();
        Map<String,Object> operationResult =null;
        operationResult = BatchDataOperationUtil.batchAttachNewRelationsWithSinglePropertyValueMatch(
                "Executive",queryParameters,"PERSON_KEY",
                "Executive",queryParameters,"PERSON_KEY",
                "IsSamePerson", BatchDataOperationUtil.CPUUsageRate.High
        );
        System.out.println(operationResult);
    }

    private static void cleanSelfSamePersonLink(){}

    private static void addLinkDisplayProperty() throws CoreRealmServiceRuntimeException {
        Map<String,Object> attrMap = new HashMap<>();
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        /*
        RelationKind targetRelationKind = coreRealm.createRelationKind("BelongsToChamberOfCommerce","隶属于商会");
        attrMap.put("DISPLAY_PROP","隶属于商会");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("BelongsToFederationOfIndustryAndCommerce","隶属于工商联");
        attrMap.put("DISPLAY_PROP","隶属于工商联");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("HasExecutiveCommonDuty","拥有的职务");
        attrMap.put("DISPLAY_PROP","拥有的职务");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("HasFicElseDuty","拥有的职务");
        attrMap.put("DISPLAY_PROP","拥有的职务");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("ExecutiveOfFederationOfIndustryAndCommerce","工商联常务理事");
        attrMap.put("DISPLAY_PROP","工商联常务理事");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("HasHonor","获得的荣誉");
        attrMap.put("DISPLAY_PROP","获得的荣誉");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("IsSameEnterprise","同家企业");
        attrMap.put("DISPLAY_PROP","同家企业");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("IsSamePerson","同一个人");
        attrMap.put("DISPLAY_PROP","同一个人");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("ParentFederationOfIndustryAndCommerceIs","上级工商联");
        attrMap.put("DISPLAY_PROP","上级工商联");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("RelatedToExecutiveEnterprise","相关企业");
        attrMap.put("DISPLAY_PROP","相关企业");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("HistoricalChangeTermOfFIC","工商联历年界次");
        attrMap.put("DISPLAY_PROP","工商联历史换届");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("LeaderOfChamberOfCommerce","商会领导");
        attrMap.put("DISPLAY_PROP","商会领导");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("HasLeaderCommonDuty","拥有的职务");
        attrMap.put("DISPLAY_PROP","拥有的职务");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
*/




        RelationKind    targetRelationKind = coreRealm.createRelationKind("LeaderOfFIC","工商联领导");
        attrMap.put("DISPLAY_PROP","工商联领导");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
        targetRelationKind = coreRealm.createRelationKind("HasLeaderSocialDuty","拥有的职务");
        attrMap.put("DISPLAY_PROP","拥有的职务");
        System.out.println(targetRelationKind.setKindScopeAttributes(attrMap));
    }

    private static void linkFederationOfIndustryAndCommerceGeo() throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        /*
        coreRealm.createConceptionKind("FIC_TEMP","FIC_TEMPDesc");
        //get FederationOfIndustryAndCommerceGeo
        Map<String,String> attributesMapping = new HashMap<>();
        attributesMapping.put("ORG_ID","ZZID");
        attributesMapping.put("ORG_NAME","ZZMC");
        attributesMapping.put("SHENG","SHENG");
        attributesMapping.put("SHI","SHI");
        attributesMapping.put("XIAN","XIAN");

        attributesMapping.put("XZJB_NAME","XZJB_NAME");
        attributesMapping.put("SHENG_NAME","SHENG_NAME");
        attributesMapping.put("SHI_NAME","SHI_NAME");
        attributesMapping.put("XIAN_NAME","XIAN_NAME");

        boolean exeResult = BatchDataOperationUtil.importConceptionEntitiesFromCSV("TEMP_DWS_DIM_ORG.csv","FIC_TEMP",attributesMapping);
        System.out.println(exeResult);

        CrossKindDataOperator crossKindDataOperator = coreRealm.getCrossKindDataOperator();
        List<String> attributesForFusionList = new ArrayList<>();
        attributesForFusionList.add("SHENG");
        attributesForFusionList.add("SHI");
        attributesForFusionList.add("XIAN");
        crossKindDataOperator.fuseConceptionKindsAttributes("FIC_TEMP","ORG_ID",attributesForFusionList,"FederationOfIndustryAndCommerce","ORG_ID");
        coreRealm.removeConceptionKind("FIC_TEMP",true);
        */

        ConceptionKind conceptionKind = coreRealm.getConceptionKind("FederationOfIndustryAndCommerce");
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);

        List<String> attributesNameList = new ArrayList<>();
        attributesNameList.add("XIAN");
        attributesNameList.add("SHI");
        attributesNameList.add("SHENG");

        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult = conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributesNameList,queryParameters);
        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();

        Map<String,String> _XIANDataMap = new HashMap<>();
        Map<String,String> _SHIDataMap = new HashMap<>();
        Map<String,String> _SHENGDataMap = new HashMap<>();
        for(ConceptionEntityValue conceptionEntityValue:conceptionEntityValueList){
            Map<String,Object> attributesMap = conceptionEntityValue.getEntityAttributesValue();
            if(attributesMap.containsKey("XIAN")){
                _XIANDataMap.put(conceptionEntityValue.getConceptionEntityUID(),attributesMap.get("XIAN").toString());
            }else if(attributesMap.containsKey("SHI")){
                _SHIDataMap.put(conceptionEntityValue.getConceptionEntityUID(),attributesMap.get("SHI").toString());
            }else {
                if (attributesMap.containsKey("SHENG")){
                    _SHENGDataMap.put(conceptionEntityValue.getConceptionEntityUID(),attributesMap.get("SHENG").toString());
                }
            }
        }
        System.out.println(_XIANDataMap.size());
        System.out.println(_SHIDataMap.size());
        System.out.println(_SHENGDataMap.size());

        Map<String,Object> attachResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByGeospatialCode(_SHENGDataMap,"organizationAdministrativeAtDistrict",null,null, GeospatialRegion.GeospatialScaleGrade.PROVINCE,BatchDataOperationUtil.CPUUsageRate.Low);
        System.out.println(attachResult);
        attachResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByGeospatialCode(_SHIDataMap,"organizationAdministrativeAtDistrict",null,null, GeospatialRegion.GeospatialScaleGrade.PREFECTURE,BatchDataOperationUtil.CPUUsageRate.Low);
        System.out.println(attachResult);
        attachResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByGeospatialCode(_XIANDataMap,"organizationAdministrativeAtDistrict",null, null,GeospatialRegion.GeospatialScaleGrade.COUNTY,BatchDataOperationUtil.CPUUsageRate.Middle);
        System.out.println(attachResult);
    }

    private static void linkKindGeo(String kindName,String levelProperty,String eventComment,String provinceProperty,String cityProperty,String countyProperty) throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind conceptionKind = coreRealm.getConceptionKind(kindName);
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);

        List<String> attributesNameList = new ArrayList<>();
        attributesNameList.add(provinceProperty);
        attributesNameList.add(cityProperty);
        attributesNameList.add(countyProperty);
        attributesNameList.add(levelProperty);// 地市级 区县级 省级 全国

        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult = conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributesNameList,queryParameters);
        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();

        Map<String,String> _XIANDataMap = new HashMap<>();
        Map<String,String> _SHIDataMap = new HashMap<>();
        Map<String,String> _SHENGDataMap = new HashMap<>();
        for(ConceptionEntityValue conceptionEntityValue:conceptionEntityValueList){
            Map<String,Object> attributesMap = conceptionEntityValue.getEntityAttributesValue();
            String _ADMINI_LEVEL = attributesMap.get(levelProperty).toString();
            if(_ADMINI_LEVEL.trim().equals("区县级")){
                if(attributesMap.containsKey(countyProperty)){
                    String cityName = "市辖区";
                    if(attributesMap.containsKey(cityProperty)){
                        cityName = attributesMap.get(cityProperty).toString();
                    }

                    String geoName = attributesMap.get(provinceProperty).toString()+"-"+cityName+"-"+attributesMap.get(countyProperty).toString();
                    _XIANDataMap.put(conceptionEntityValue.getConceptionEntityUID(),geoName);}
            }
            if(_ADMINI_LEVEL.trim().equals("地市级")){
                String cityName = "市辖区";
                if(attributesMap.containsKey(cityProperty)){
                    cityName = attributesMap.get(cityProperty).toString();
                }
                String geoName = attributesMap.get(provinceProperty).toString()+"-"+cityName;
                _SHIDataMap.put(conceptionEntityValue.getConceptionEntityUID(),geoName);
            }

            if(_ADMINI_LEVEL.trim().equals("省级")){
                if(!attributesMap.get(provinceProperty).equals("全国"));{
                    _SHENGDataMap.put(conceptionEntityValue.getConceptionEntityUID(),attributesMap.get(provinceProperty).toString());
                }
            }
        }
        System.out.println(_XIANDataMap.size());
        System.out.println(_SHIDataMap.size());
        System.out.println(_SHENGDataMap.size());

        Map<String,Object> attachResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByChineseNames(_SHENGDataMap,eventComment,null, null,GeospatialRegion.GeospatialScaleGrade.PROVINCE,BatchDataOperationUtil.CPUUsageRate.High);
        System.out.println(attachResult);
        attachResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByChineseNames(_SHIDataMap,eventComment,null, null,GeospatialRegion.GeospatialScaleGrade.PREFECTURE,BatchDataOperationUtil.CPUUsageRate.High);
        System.out.println(attachResult);
        attachResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByChineseNames(_XIANDataMap,eventComment,null, null,GeospatialRegion.GeospatialScaleGrade.COUNTY,BatchDataOperationUtil.CPUUsageRate.High);
        System.out.println(attachResult);
    }

    private static void linkEnterpriseMemberDate() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind conceptionKind = coreRealm.getConceptionKind("EnterpriseMember");
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add("ESTABLISH_TIME");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
        BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(conceptionEntityValueList,"ESTABLISH_TIME",null,"firmStartedAt",dtf,null, TimeFlow.TimeScaleGrade.DAY, BatchDataOperationUtil.CPUUsageRate.High);
    }

    private static void linkIndividualMemberDate() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind conceptionKind = coreRealm.getConceptionKind("IndividualMember");
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add("BIRTHDATE");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
        BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(conceptionEntityValueList,"BIRTHDATE",null,"personBirthdayIs",dtf,null, TimeFlow.TimeScaleGrade.DAY, BatchDataOperationUtil.CPUUsageRate.High);
    }

    private static void linkExecutiveDate() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind conceptionKind = coreRealm.getConceptionKind("Executive");
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add("BIRTHDATE");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
        BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(conceptionEntityValueList,"BIRTHDATE",null,"personBirthdayIs",dtf,null, TimeFlow.TimeScaleGrade.DAY, BatchDataOperationUtil.CPUUsageRate.High);
    }

    private static void linkExecutiveEnterpriseDate() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind conceptionKind = coreRealm.getConceptionKind("ExecutiveEnterprise");
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add("ESTABLISH_TIME");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
        BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(conceptionEntityValueList,"ESTABLISH_TIME",null,"firmStartedAt",dtf,null, TimeFlow.TimeScaleGrade.DAY, BatchDataOperationUtil.CPUUsageRate.High);
    }

    private static void linkChamberOfCommerceDate() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind conceptionKind = coreRealm.getConceptionKind("ChamberOfCommerce");
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add("ESTABLISHED_TIME");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
        BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(conceptionEntityValueList,"ESTABLISHED_TIME",null,"chamberFoundedAt",dtf,null, TimeFlow.TimeScaleGrade.DAY, BatchDataOperationUtil.CPUUsageRate.High);
    }

    private static void createCPCOrganizationForChamberOfCommerce() throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        ConceptionKind _ConceptionKind =coreRealm.getConceptionKind("ChamberOfCommerce");
        QueryParameters queryParameters1 = new QueryParameters();
        queryParameters1.setResultNumber(100000000);
        queryParameters1.setDefaultFilteringItem(new EqualFilteringItem("IF_PARTY_ORGANIZATION","1"));

        List<String> resultAttributesNames = new ArrayList<>();
        resultAttributesNames.add("ORG_SHENG");
        resultAttributesNames.add("ORG_SHI");
        resultAttributesNames.add("ORG_XIAN");
        resultAttributesNames.add("ORG_XIANG");
        resultAttributesNames.add("ORG_CUN");
        resultAttributesNames.add("SHENG");
        resultAttributesNames.add("SHI");
        resultAttributesNames.add("XIAN");
        resultAttributesNames.add("SHID");







        /*
        PARTY_MEMBER_NUM	0
PARTY_ORG_FORM	联合党组织
PARTY_ORG_FORM_CODE	DZZXS-02
PARTY_SECRETARY
         */




        resultAttributesNames.add("PARTY_MEMBER_NUM");
        resultAttributesNames.add("PARTY_SECRETARY");
        resultAttributesNames.add("PARTY_ORG_FORM");
        resultAttributesNames.add("PARTY_ORG_FORM_CODE");
        resultAttributesNames.add("SHDZZ_JLSJ");

        ConceptionEntitiesAttributesRetrieveResult _ConceptionEntitiesAttributesRetrieveResult =_ConceptionKind.getSingleValueEntityAttributesByAttributeNames(resultAttributesNames,queryParameters1);

        ConceptionKind _ConceptionKind2 =coreRealm.getConceptionKind("CPC_Organization");
        for(ConceptionEntityValue _ConceptionEntityValue : _ConceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues()){
            Map<String,Object> _ConceptionEntityDataMap = new HashMap<>();
            Map<String, Object> sourceMap = _ConceptionEntityValue.getEntityAttributesValue();
            if(sourceMap != null){
                if(sourceMap.containsKey("PARTY_ORG_FORM_CODE")){
                    String _PARTY_ORGANIZATIO_FORM = sourceMap.get("PARTY_ORG_FORM_CODE").toString();
                    if(_PARTY_ORGANIZATIO_FORM.equals("DZZXS-01")){
                        sourceMap.put("CPC_Organization_Form","独立党组织");
                    }
                    if(_PARTY_ORGANIZATIO_FORM.equals("DZZXS-02")){
                        sourceMap.put("CPC_Organization_Form","联合党组织");
                    }
                }
                _ConceptionEntityDataMap.putAll(sourceMap);
                _ConceptionEntityDataMap.put("FOUNDED_BY","ChamberOfCommerce");
            }
            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue();
            conceptionEntityValue.setEntityAttributesValue(_ConceptionEntityDataMap);
            ConceptionEntity newEntity = _ConceptionKind2.newEntity(conceptionEntityValue,false);
            newEntity.attachFromRelation(_ConceptionEntityValue.getConceptionEntityUID(),"CPC_OrganizationFoundedBy",null,false);
        }

        coreRealm.closeGlobalSession();
    }

}
