package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeospatialRegionTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for GeospatialRegionTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testGeospatialRegionFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();

        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        List<GeospatialRegion> geospatialRegionList = coreRealm.getGeospatialRegions();
        System.out.println(geospatialRegionList);

        GeospatialRegion defaultGeospatialRegion = coreRealm.getOrCreateGeospatialRegion();
        System.out.println(defaultGeospatialRegion.getGeospatialRegionName());

        /*
        GeospatialRegion geospatialRegion1 = coreRealm.getOrCreateGeospatialRegion("geospatialRegion1");
        System.out.println(geospatialRegion1.getGeospatialRegionName());

        geospatialRegion1 = coreRealm.getOrCreateGeospatialRegion("geospatialRegion1");
        System.out.println(geospatialRegion1.getGeospatialRegionName());

        GeospatialRegion geospatialRegion2 = coreRealm.getOrCreateGeospatialRegion("geospatialRegion2");
        System.out.println(geospatialRegion2.getGeospatialRegionName());

        geospatialRegionList = coreRealm.getGeospatialRegions();
        for(GeospatialRegion currentGeospatialRegion: geospatialRegionList){
            System.out.println(currentGeospatialRegion.getGeospatialRegionName());
        }
        */

        //defaultGeospatialRegion.createGeospatialScaleEntities();


        List<GeospatialScaleEntity> continentGeospatialScaleEntityList = defaultGeospatialRegion.listContinentEntities();

        for(GeospatialScaleEntity currentGeospatialScaleEntity:continentGeospatialScaleEntityList){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity1 = defaultGeospatialRegion.getEntityByGeospatialCode("640522406498");
        System.out.println(targetGeospatialScaleEntity1.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity1.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity1.getChineseName());
        System.out.println(targetGeospatialScaleEntity1.getEnglishName());

        GeospatialScaleEntity targetGeospatialScaleEntity2 = defaultGeospatialRegion.getEntityByGeospatialCode("AD-07");
        System.out.println(targetGeospatialScaleEntity2.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity2.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity2.getChineseName());
        System.out.println(targetGeospatialScaleEntity2.getEnglishName());

        GeospatialScaleEntity targetGeospatialScaleEntity3 = defaultGeospatialRegion.getContinentEntity(GeospatialRegion.GeospatialProperty.ChineseName,"北");
        System.out.println(targetGeospatialScaleEntity3.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity3.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity3.getChineseName());
        System.out.println(targetGeospatialScaleEntity3.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList2  = defaultGeospatialRegion.listCountryRegionEntities(GeospatialRegion.GeospatialProperty.GeospatialCode,"U");
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList2){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }
        System.out.println(countryRegionGeospatialScaleEntityList2.size());

        GeospatialScaleEntity targetGeospatialScaleEntity4 = defaultGeospatialRegion.getCountryRegionEntity(GeospatialRegion.GeospatialProperty.GeospatialCode,"CN");
        System.out.println(targetGeospatialScaleEntity4.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity4.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity4.getChineseName());
        System.out.println(targetGeospatialScaleEntity4.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList3 = defaultGeospatialRegion.listProvinceEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国",null);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList3){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }
        System.out.println(countryRegionGeospatialScaleEntityList3.size());

        GeospatialScaleEntity targetGeospatialScaleEntity5 = defaultGeospatialRegion.getProvinceEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省");
        System.out.println(targetGeospatialScaleEntity5.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity5.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity5.getChineseName());
        System.out.println(targetGeospatialScaleEntity5.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList4 = defaultGeospatialRegion.listPrefectureEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省",null);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList4){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }
        System.out.println(countryRegionGeospatialScaleEntityList4.size());

        GeospatialScaleEntity targetGeospatialScaleEntity6 = defaultGeospatialRegion.getPrefectureEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市");
        System.out.println(targetGeospatialScaleEntity6.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity6.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity6.getChineseName());
        System.out.println(targetGeospatialScaleEntity6.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList5 = defaultGeospatialRegion.listCountyEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市",null);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList5){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }
        System.out.println(countryRegionGeospatialScaleEntityList5.size());

        GeospatialScaleEntity targetGeospatialScaleEntity7 = defaultGeospatialRegion.getCountyEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区");
        System.out.println(targetGeospatialScaleEntity7.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity7.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity7.getChineseName());
        System.out.println(targetGeospatialScaleEntity7.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList6 = defaultGeospatialRegion.listTownshipEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区",null);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList6){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }
        System.out.println(countryRegionGeospatialScaleEntityList6.size());

        GeospatialScaleEntity targetGeospatialScaleEntity8 = defaultGeospatialRegion.getTownshipEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区","飞剑潭乡");
        System.out.println(targetGeospatialScaleEntity8.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity8.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity8.getChineseName());
        System.out.println(targetGeospatialScaleEntity8.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList7 = defaultGeospatialRegion.listVillageEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区","飞剑潭乡",null);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList7){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }
        System.out.println(countryRegionGeospatialScaleEntityList7.size());

        GeospatialScaleEntity targetGeospatialScaleEntity9 = defaultGeospatialRegion.getVillageEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区","飞剑潭乡","塘源村委会");
        System.out.println(targetGeospatialScaleEntity9.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity9.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity9.getChineseName());
        System.out.println(targetGeospatialScaleEntity9.getEnglishName());

        GeospatialScaleEntity targetGeospatialScaleEntity10 = targetGeospatialScaleEntity9.getParentEntity();
        System.out.println(targetGeospatialScaleEntity10.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity10.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity10.getChineseName());
        System.out.println(targetGeospatialScaleEntity10.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList8 = targetGeospatialScaleEntity9.getFellowEntities();
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList8){
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
        }
        System.out.println(countryRegionGeospatialScaleEntityList8.size());

        InheritanceTree<GeospatialScaleEntity> geospatialScaleEntityTree = targetGeospatialScaleEntity6.getOffspringEntities();
        System.out.println(geospatialScaleEntityTree.getRootID());
        System.out.println(geospatialScaleEntityTree.size());

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("GeospatialFeatureTestKind");
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind("GeospatialFeatureTestKind",true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind("GeospatialFeatureTestKind");
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind("GeospatialFeatureTestKind","-");
        }

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);

        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity _ConceptionEntity01 = _ConceptionKind01.newEntity(conceptionEntityValue,false);

        Map<String,Object> eventDataMap= new HashMap<>();
        eventDataMap.put("data1","this is s data");
        eventDataMap.put("data2",new Date());

        GeospatialScaleEvent _GeospatialScaleEvent1 = _ConceptionEntity01.attachGeospatialScaleEvent("360902213200","eventAttachComment",eventDataMap);
        System.out.println(_GeospatialScaleEvent1.getGeospatialScaleEventUID());
        System.out.println(_GeospatialScaleEvent1.getReferLocation());
        System.out.println(_GeospatialScaleEvent1.getGeospatialScaleGrade());
        System.out.println(_GeospatialScaleEvent1.getGeospatialRegionName());
        System.out.println(_GeospatialScaleEvent1.getEventComment());

        System.out.println("----------------------------------------------------");
        GeospatialScaleEntity targetGeospatialScaleEntity = _GeospatialScaleEvent1.getReferGeospatialScaleEntity();
        System.out.println(targetGeospatialScaleEntity.getEnglishName());
        System.out.println(targetGeospatialScaleEntity.getChineseName());
        System.out.println(targetGeospatialScaleEntity.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity.getParentEntity().getGeospatialCode());

        ConceptionEntity targetConceptionEntity = _GeospatialScaleEvent1.getAttachConceptionEntity();
        System.out.println(targetConceptionEntity.getConceptionKindName());
        System.out.println(targetConceptionEntity.getConceptionEntityUID());
        System.out.println("----------------------------------------------------");
        coreRealm.closeGlobalSession();
    }
}
