package com.viewfunction.docg.realmExample.internalTest;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MassConceptionIntegratedTest {

    private static final String ChinaFirmConceptionType = "ChinaFirm";
    private static final String Name = "name";
    private static final String CompanyType = "companyType";
    private static final String Address = "address";
    private static final String Status = "status";
    private static final String StartDate = "startDate";
    private static final String ApprovedTime = "approvedTime";
    private static final String Category = "category";
    private static final String City = "city";
    private static final String Province = "province";
    private static final String Lat_wgs = "lat_wgs";
    private static final String Lng_wgs = "lng_wgs";

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //generateData();
        //linkApprovedAtTimeData(701,800);
        linkStartedDateAtTimeData(1,100);

    }

    public static void linkApprovedAtTimeData(int fromPage,int toPage) throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind _ChinaFirmConceptionKind = coreRealm.getConceptionKind(ChinaFirmConceptionType);
        if(_ChinaFirmConceptionKind != null){
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.addSortingAttribute("lastModifyDate", QueryParameters.SortingLogic.ASC);
            queryParameters.setStartPage(fromPage);
            queryParameters.setEndPage(toPage);
            queryParameters.setPageSize(10000);
            List<String> attributeNamesList = new ArrayList<>();
            attributeNamesList.add(ApprovedTime);
            ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult = _ChinaFirmConceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,ApprovedTime,"approvedAt",null, TimeFlow.TimeScaleGrade.DAY,10);
        }
    }

    public static void linkStartedDateAtTimeData(int fromPage,int toPage) throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind _ChinaFirmConceptionKind = coreRealm.getConceptionKind(ChinaFirmConceptionType);
        if(_ChinaFirmConceptionKind != null){
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.addSortingAttribute("lastModifyDate", QueryParameters.SortingLogic.ASC);
            queryParameters.setStartPage(fromPage);
            queryParameters.setEndPage(toPage);
            queryParameters.setPageSize(10000);
            List<String> attributeNamesList = new ArrayList<>();
            attributeNamesList.add(StartDate);
            ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult = _ChinaFirmConceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,StartDate,"startedAt",null, TimeFlow.TimeScaleGrade.DAY,10);
        }
    }

    public static void generateData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        //Part 1
        ConceptionKind _ChinaFirmConceptionKind = coreRealm.getConceptionKind(ChinaFirmConceptionType);
        if(_ChinaFirmConceptionKind != null){
            coreRealm.removeConceptionKind(ChinaFirmConceptionType,true);
        }
        _ChinaFirmConceptionKind = coreRealm.getConceptionKind(ChinaFirmConceptionType);
        if(_ChinaFirmConceptionKind == null){
            _ChinaFirmConceptionKind = coreRealm.createConceptionKind(ChinaFirmConceptionType,"中国公司");
        }

        List<ConceptionEntityValue> _ChinaFirmConceptionKindEntityValueList = Lists.newArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        File file = new File("/media/wangychu/Data/Data/firm_2015.csv"); //2010,2015
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String currentLine = !tempStr.startsWith("name\tcompany_type")? tempStr : null;
                if(currentLine != null){
                    String[] dataItems =  currentLine.split("\t");
                    if(dataItems.length == 11){
                        String name = dataItems[0];
                        String companyType = dataItems[1];
                        String address = dataItems[2];
                        String status = dataItems[3];
                        String startDate = dataItems[4];
                        String approvedTime = dataItems[5];
                        String category = dataItems[6];
                        String city = dataItems[7];
                        String province = dataItems[8];
                        String lat_wgs = dataItems[9];
                        String lng_wgs = dataItems[10];
                        Map<String,Object> newEntityValueMap = new HashMap<>();
                        newEntityValueMap.put(Name,name);
                        newEntityValueMap.put(CompanyType,companyType);
                        newEntityValueMap.put(Address,address);
                        newEntityValueMap.put(Status,status);
                        if(!startDate.equals("")){
                            newEntityValueMap.put(StartDate,sdf.parse(startDate));
                        }
                        if(!approvedTime.equals("")){
                            newEntityValueMap.put(ApprovedTime,sdf.parse(approvedTime));
                        }
                        newEntityValueMap.put(Category,category);
                        newEntityValueMap.put(City,city);
                        newEntityValueMap.put(Province,province);
                        newEntityValueMap.put(Lat_wgs,Double.parseDouble(lat_wgs));
                        newEntityValueMap.put(Lng_wgs,Double.parseDouble(lng_wgs));

                        String wktContent = "POINT ("+lng_wgs+" "+lat_wgs+")";
                        newEntityValueMap.put("DOCG_GS_GeometryType","POINT");
                        newEntityValueMap.put("DOCG_GS_GlobalCRSAID","EPSG:4326");
                        newEntityValueMap.put("DOCG_GS_GLGeometryContent",wktContent);

                        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                        _ChinaFirmConceptionKindEntityValueList.add(conceptionEntityValue);
                    }
                    if(dataItems.length == 10){
                        String name = "-";
                        String companyType = "-";
                        String address = dataItems[1];
                        String status = dataItems[2];
                        String startDate = dataItems[3];
                        String approvedTime = dataItems[4];
                        String category = dataItems[5];
                        String city = dataItems[6];
                        String province = dataItems[7];
                        String lat_wgs = dataItems[8];
                        String lng_wgs = dataItems[9];
                        Map<String,Object> newEntityValueMap = new HashMap<>();
                        newEntityValueMap.put(Name,name);
                        newEntityValueMap.put(CompanyType,companyType);
                        newEntityValueMap.put(Address,address);
                        newEntityValueMap.put(Status,status);
                        if(!startDate.equals("")){
                            newEntityValueMap.put(StartDate,sdf.parse(startDate));
                        }
                        if(!approvedTime.equals("")){
                            newEntityValueMap.put(ApprovedTime,sdf.parse(approvedTime));
                        }
                        newEntityValueMap.put(Category,category);
                        newEntityValueMap.put(City,city);
                        newEntityValueMap.put(Province,province);
                        newEntityValueMap.put(Lat_wgs,Double.parseDouble(lat_wgs));
                        newEntityValueMap.put(Lng_wgs,Double.parseDouble(lng_wgs));

                        String wktContent = "POINT ("+lng_wgs+" "+lat_wgs+")";
                        newEntityValueMap.put("DOCG_GS_GeometryType","POINT");
                        newEntityValueMap.put("DOCG_GS_GlobalCRSAID","EPSG:4326");
                        newEntityValueMap.put("DOCG_GS_GLGeometryContent",wktContent);

                        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                        _ChinaFirmConceptionKindEntityValueList.add(conceptionEntityValue);
                    }
                }
            }
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        BatchDataOperationUtil.batchAddNewEntities(ChinaFirmConceptionType,_ChinaFirmConceptionKindEntityValueList,10);
    }
}
