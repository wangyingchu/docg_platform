package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

public class GeospatialRegionTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for GeospatialRegionTest");
        System.out.println("--------------------------------------------------");
        setupGeospatialRegionFunction();
    }

    private void setupGeospatialRegionFunction(){
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        //long resultCount = coreRealm.removeGeospatialRegionWithEntities();
        GeospatialRegion geospatialRegion = coreRealm.getOrCreateGeospatialRegion();
        List<GeospatialScaleEntity> entityList = geospatialRegion.listContinentEntities();
        if(entityList.size() == 0){
            geospatialRegion.createGeospatialScaleEntities();
        }
    }

    @Test
    public void testGeospatialRegionFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        List<GeospatialRegion> geospatialRegionList = coreRealm.getGeospatialRegions();
        Assert.assertTrue(geospatialRegionList.size()>=1);

        GeospatialRegion defaultGeospatialRegion = coreRealm.getOrCreateGeospatialRegion();
        Assert.assertEquals(defaultGeospatialRegion.getGeospatialRegionName(), RealmConstant._defaultGeospatialRegionName);

        GeospatialRegion geospatialRegion1 = coreRealm.getOrCreateGeospatialRegion("geospatialRegion1");
        Assert.assertEquals(geospatialRegion1.getGeospatialRegionName(),"geospatialRegion1");

        geospatialRegion1 = coreRealm.getOrCreateGeospatialRegion("geospatialRegion1");
        Assert.assertEquals(geospatialRegion1.getGeospatialRegionName(),"geospatialRegion1");

        GeospatialRegion geospatialRegion2 = coreRealm.getOrCreateGeospatialRegion("geospatialRegion2");
        Assert.assertEquals(geospatialRegion2.getGeospatialRegionName(),"geospatialRegion2");

        geospatialRegionList = coreRealm.getGeospatialRegions();
        Assert.assertTrue(geospatialRegionList.size()>=3);
        int orgCount = geospatialRegionList.size();
        for(GeospatialRegion currentGeospatialRegion: geospatialRegionList){
            boolean matchResult = currentGeospatialRegion.getGeospatialRegionName().equals(RealmConstant._defaultGeospatialRegionName)|
                                    currentGeospatialRegion.getGeospatialRegionName().equals("geospatialRegion1")|
                                    currentGeospatialRegion.getGeospatialRegionName().equals("geospatialRegion2");
            Assert.assertTrue(matchResult);
        }

        long removeRegionEntitiesCount = coreRealm.removeGeospatialRegionWithEntities("geospatialRegion2");
        Assert.assertEquals(removeRegionEntitiesCount,1l);
        geospatialRegionList = coreRealm.getGeospatialRegions();
        Assert.assertEquals(geospatialRegionList.size(),orgCount-1);

        List<GeospatialScaleEntity> continentGeospatialScaleEntityList = defaultGeospatialRegion.listContinentEntities();
        Assert.assertEquals(continentGeospatialScaleEntityList.size(),7);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:continentGeospatialScaleEntityList){
            Assert.assertNotNull(currentGeospatialScaleEntity.getGeospatialCode());
            Assert.assertNotNull(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNotNull(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity1 = defaultGeospatialRegion.getEntityByGeospatialCode("640522406498");
        Assert.assertEquals(targetGeospatialScaleEntity1.getGeospatialCode(),"640522406498");
        Assert.assertEquals(targetGeospatialScaleEntity1.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.VILLAGE);
        Assert.assertEquals(targetGeospatialScaleEntity1.getChineseName(),"海兴虚拟社区");
        Assert.assertNull(targetGeospatialScaleEntity1.getEnglishName());

        GeospatialScaleEntity targetGeospatialScaleEntity2 = defaultGeospatialRegion.getEntityByGeospatialCode("AD-07");
        Assert.assertEquals(targetGeospatialScaleEntity2.getGeospatialCode(),"AD-07");
        Assert.assertEquals(targetGeospatialScaleEntity2.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.PROVINCE);
        Assert.assertEquals(targetGeospatialScaleEntity2.getChineseName(),"");
        Assert.assertEquals(targetGeospatialScaleEntity2.getEnglishName(),"Andorra la Vella");

        GeospatialScaleEntity targetGeospatialScaleEntity3 = defaultGeospatialRegion.getContinentEntity(GeospatialRegion.GeospatialProperty.ChineseName,"北");
        Assert.assertEquals(targetGeospatialScaleEntity3.getGeospatialCode(),"North America");
        Assert.assertEquals(targetGeospatialScaleEntity3.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.CONTINENT);
        Assert.assertEquals(targetGeospatialScaleEntity3.getChineseName(),"北美洲");
        Assert.assertEquals(targetGeospatialScaleEntity3.getEnglishName(),"North America");

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList2  = defaultGeospatialRegion.listCountryRegionEntities(GeospatialRegion.GeospatialProperty.GeospatialCode,"U");
        Assert.assertEquals(countryRegionGeospatialScaleEntityList2.size(),6);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList2){
            Assert.assertTrue(currentGeospatialScaleEntity.getGeospatialCode().startsWith("U"));
            Assert.assertEquals(currentGeospatialScaleEntity.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.COUNTRY_REGION);
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNotNull(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity4 = defaultGeospatialRegion.getCountryRegionEntity(GeospatialRegion.GeospatialProperty.GeospatialCode,"CN");
        Assert.assertEquals(targetGeospatialScaleEntity4.getGeospatialCode(),"CN");
        Assert.assertEquals(targetGeospatialScaleEntity4.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.COUNTRY_REGION);
        Assert.assertEquals(targetGeospatialScaleEntity4.getChineseName(),"中国");
        Assert.assertEquals(targetGeospatialScaleEntity4.getEnglishName(),"China");

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList3 = defaultGeospatialRegion.listProvinceEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国",null);
        Assert.assertEquals(countryRegionGeospatialScaleEntityList3.size(),34);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList3){
            Assert.assertNotNull(currentGeospatialScaleEntity.getGeospatialCode());
            Assert.assertEquals(currentGeospatialScaleEntity.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.PROVINCE);
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNotNull(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity5 = defaultGeospatialRegion.getProvinceEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省");
        Assert.assertEquals(targetGeospatialScaleEntity5.getGeospatialCode(),"360000");
        Assert.assertEquals(targetGeospatialScaleEntity5.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.PROVINCE);
        Assert.assertEquals(targetGeospatialScaleEntity5.getChineseName(),"江西省");
        Assert.assertEquals(targetGeospatialScaleEntity5.getEnglishName(),"Jiangxi Sheng");

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList4 = defaultGeospatialRegion.listPrefectureEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省",null);
        Assert.assertEquals(countryRegionGeospatialScaleEntityList4.size(),11);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList4){
            Assert.assertTrue(currentGeospatialScaleEntity.getGeospatialCode().startsWith("36"));
            Assert.assertEquals(currentGeospatialScaleEntity.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.PREFECTURE);
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNull(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity6 = defaultGeospatialRegion.getPrefectureEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市");
        Assert.assertEquals(targetGeospatialScaleEntity6.getGeospatialCode(),"360900");
        Assert.assertEquals(targetGeospatialScaleEntity6.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.PREFECTURE);
        Assert.assertEquals(targetGeospatialScaleEntity6.getChineseName(),"宜春市");
        Assert.assertNull(targetGeospatialScaleEntity6.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList5 = defaultGeospatialRegion.listCountyEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市",null);
        Assert.assertEquals(countryRegionGeospatialScaleEntityList5.size(),10);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList5){
            Assert.assertTrue(currentGeospatialScaleEntity.getGeospatialCode().startsWith("3609"));
            Assert.assertEquals(currentGeospatialScaleEntity.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.COUNTY);
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNull(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity7 = defaultGeospatialRegion.getCountyEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区");
        Assert.assertEquals(targetGeospatialScaleEntity7.getGeospatialCode(),"360902");
        Assert.assertEquals(targetGeospatialScaleEntity7.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.COUNTY);
        Assert.assertEquals(targetGeospatialScaleEntity7.getChineseName(),"袁州区");
        Assert.assertNull(targetGeospatialScaleEntity7.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList6 = defaultGeospatialRegion.listTownshipEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区",null);
        Assert.assertEquals(countryRegionGeospatialScaleEntityList6.size(),38);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList6){
            Assert.assertTrue(currentGeospatialScaleEntity.getGeospatialCode().startsWith("360902"));
            Assert.assertEquals(currentGeospatialScaleEntity.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.TOWNSHIP);
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNull(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity8 = defaultGeospatialRegion.getTownshipEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区","飞剑潭乡");
        Assert.assertEquals(targetGeospatialScaleEntity8.getGeospatialCode(),"360902213");
        Assert.assertEquals(targetGeospatialScaleEntity8.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.TOWNSHIP);
        Assert.assertEquals(targetGeospatialScaleEntity8.getChineseName(),"飞剑潭乡");
        Assert.assertNull(targetGeospatialScaleEntity8.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList7 = defaultGeospatialRegion.listVillageEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区","飞剑潭乡",null);
        Assert.assertEquals(countryRegionGeospatialScaleEntityList7.size(),10);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList7){
            Assert.assertTrue(currentGeospatialScaleEntity.getGeospatialCode().startsWith("360902213"));
            Assert.assertEquals(currentGeospatialScaleEntity.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.VILLAGE);
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNull(currentGeospatialScaleEntity.getEnglishName());
        }

        GeospatialScaleEntity targetGeospatialScaleEntity9 = defaultGeospatialRegion.getVillageEntity(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江西省","宜春市","袁州区","飞剑潭乡","塘源村委会");
        Assert.assertEquals(targetGeospatialScaleEntity9.getGeospatialCode(),"360902213201");
        Assert.assertEquals(targetGeospatialScaleEntity9.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.VILLAGE);
        Assert.assertEquals(targetGeospatialScaleEntity9.getChineseName(),"塘源村委会");
        Assert.assertNull(targetGeospatialScaleEntity9.getEnglishName());

        GeospatialScaleEntity targetGeospatialScaleEntity10 = targetGeospatialScaleEntity9.getParentEntity();
        Assert.assertEquals(targetGeospatialScaleEntity10.getGeospatialCode(),"360902213");
        Assert.assertEquals(targetGeospatialScaleEntity10.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.TOWNSHIP);
        Assert.assertEquals(targetGeospatialScaleEntity10.getChineseName(),"飞剑潭乡");
        Assert.assertNull(targetGeospatialScaleEntity10.getEnglishName());

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList8 = targetGeospatialScaleEntity9.getFellowEntities();
        Assert.assertEquals(countryRegionGeospatialScaleEntityList8.size(),10);
        for(GeospatialScaleEntity currentGeospatialScaleEntity:countryRegionGeospatialScaleEntityList8){
            Assert.assertTrue(currentGeospatialScaleEntity.getGeospatialCode().startsWith("360902213"));
            Assert.assertEquals(currentGeospatialScaleEntity.getGeospatialScaleGrade(),GeospatialRegion.GeospatialScaleGrade.VILLAGE);
            Assert.assertNotNull(currentGeospatialScaleEntity.getChineseName());
            Assert.assertNull(currentGeospatialScaleEntity.getEnglishName());
        }

        InheritanceTree<GeospatialScaleEntity> geospatialScaleEntityTree = targetGeospatialScaleEntity6.getOffspringEntities();
        Assert.assertEquals(geospatialScaleEntityTree.getNode(geospatialScaleEntityTree.getRootID()).getGeospatialCode(),"360900");
        Assert.assertEquals(geospatialScaleEntityTree.size(),3012);





        coreRealm.closeGlobalSession();
    }



    // @Test
    public void testGeospatialRegionFunction0() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();

        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);







/*











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

        _ConceptionEntity01.attachGeospatialScaleEvent("360902213209","eventAttachComment",eventDataMap);
        GeospatialScaleEvent _GeospatialScaleEventForDelete = _ConceptionEntity01.attachGeospatialScaleEvent("360902213","eventAttachComment",eventDataMap);

        List<GeospatialScaleEvent> targetGeospatialScaleEventList = _ConceptionEntity01.getAttachedGeospatialScaleEvents();

        for(GeospatialScaleEvent currentGeospatialScaleEvent:targetGeospatialScaleEventList){
            System.out.println(currentGeospatialScaleEvent.getGeospatialScaleEventUID());
            System.out.println(currentGeospatialScaleEvent.getReferLocation());
            System.out.println(currentGeospatialScaleEvent.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEvent.getGeospatialRegionName());
            System.out.println(currentGeospatialScaleEvent.getEventComment());
        }
        System.out.println(targetGeospatialScaleEventList.size());

        List<GeospatialScaleEntity> targetGeospatialScaleEntityList = _ConceptionEntity01.getAttachedGeospatialScaleEntities();
        for(GeospatialScaleEntity currentGeospatialScaleEntity:targetGeospatialScaleEntityList){
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getParentEntity().getGeospatialCode());
        }
        System.out.println(targetGeospatialScaleEntityList.size());

        List<GeospatialScaleDataPair> targetGeospatialScaleDataPairList = _ConceptionEntity01.getAttachedGeospatialScaleDataPairs();
        for(GeospatialScaleDataPair currentGeospatialScaleDataPair:targetGeospatialScaleDataPairList){
            System.out.println("========================");
            GeospatialScaleEntity currentGeospatialScaleEntity = currentGeospatialScaleDataPair.getGeospatialScaleEntity();
            System.out.println(currentGeospatialScaleEntity.getEnglishName());
            System.out.println(currentGeospatialScaleEntity.getChineseName());
            System.out.println(currentGeospatialScaleEntity.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEntity.getGeospatialCode());
            System.out.println(currentGeospatialScaleEntity.getParentEntity().getGeospatialCode());

            GeospatialScaleEvent currentGeospatialScaleEvent = currentGeospatialScaleDataPair.getGeospatialScaleEvent();
            System.out.println(currentGeospatialScaleEvent.getGeospatialScaleEventUID());
            System.out.println(currentGeospatialScaleEvent.getReferLocation());
            System.out.println(currentGeospatialScaleEvent.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEvent.getGeospatialRegionName());
            System.out.println(currentGeospatialScaleEvent.getEventComment());
        }
        System.out.println(targetGeospatialScaleDataPairList.size());

        boolean detachGeospatialScaleEventResult = _ConceptionEntity01.detachGeospatialScaleEvent(_GeospatialScaleEventForDelete.getGeospatialScaleEventUID());
        System.out.println(detachGeospatialScaleEventResult);
        targetGeospatialScaleDataPairList = _ConceptionEntity01.getAttachedGeospatialScaleDataPairs();
        System.out.println(targetGeospatialScaleDataPairList.size());

        //_ConceptionEntity01.detachGeospatialScaleEvent("12345678900000000");


        //GeospatialScaleEntity targetGeospatialScaleEntity11 = defaultGeospatialRegion.getEntityByGeospatialCode("360000");
        //GeospatialScaleEntity targetGeospatialScaleEntity11 = defaultGeospatialRegion.getEntityByGeospatialCode("360902");
        //GeospatialScaleEntity targetGeospatialScaleEntity11 = defaultGeospatialRegion.getEntityByGeospatialCode("360902213");
        GeospatialScaleEntity targetGeospatialScaleEntity11 = defaultGeospatialRegion.getEntityByGeospatialCode("360902213200");

        System.out.println(targetGeospatialScaleEntity11.getGeospatialCode());
        System.out.println(targetGeospatialScaleEntity11.getGeospatialScaleGrade());
        System.out.println(targetGeospatialScaleEntity11.getChineseName());

        System.out.println("SELF "+targetGeospatialScaleEntity11.countAttachedConceptionEntities(GeospatialScaleEntity.GeospatialScaleLevel.SELF));
        System.out.println("CHILD "+targetGeospatialScaleEntity11.countAttachedConceptionEntities(GeospatialScaleEntity.GeospatialScaleLevel.CHILD));
        System.out.println("OFFSPRING "+targetGeospatialScaleEntity11.countAttachedConceptionEntities(GeospatialScaleEntity.GeospatialScaleLevel.OFFSPRING));

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        queryParameters.setDistinctMode(true);
        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult = targetGeospatialScaleEntity11.getAttachedConceptionEntities("GeospatialFeatureTestKind",queryParameters,GeospatialScaleEntity.GeospatialScaleLevel.SELF);
        System.out.println(conceptionEntitiesRetrieveResult.getConceptionEntities().size());
        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult2 = targetGeospatialScaleEntity11.getAttachedConceptionEntities(null,queryParameters,GeospatialScaleEntity.GeospatialScaleLevel.CHILD);
        System.out.println(conceptionEntitiesRetrieveResult2.getConceptionEntities().size());
        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult3 = targetGeospatialScaleEntity11.getAttachedConceptionEntities(null,queryParameters,GeospatialScaleEntity.GeospatialScaleLevel.OFFSPRING);
        System.out.println(conceptionEntitiesRetrieveResult3.getConceptionEntities().size());
        for(ConceptionEntity currentConceptionEntity:conceptionEntitiesRetrieveResult3.getConceptionEntities()){
            System.out.println(currentConceptionEntity.getConceptionEntityUID());
            System.out.println(currentConceptionEntity.getConceptionKindName());
        }

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println(targetGeospatialScaleEntity11.countAttachedConceptionEntities(GeospatialScaleEntity.GeospatialScaleLevel.SELF));
        System.out.println(targetGeospatialScaleEntity11.countAttachedConceptionEntities(GeospatialScaleEntity.GeospatialScaleLevel.CHILD));
        System.out.println(targetGeospatialScaleEntity11.countAttachedConceptionEntities(GeospatialScaleEntity.GeospatialScaleLevel.OFFSPRING));

        AttributesParameters attributesParameters = new AttributesParameters();
        attributesParameters.setDefaultFilteringItem(new EqualFilteringItem("prop1",10000l));
        System.out.println(targetGeospatialScaleEntity11.
                countAttachedConceptionEntities(null,attributesParameters,true,GeospatialScaleEntity.GeospatialScaleLevel.SELF));
        System.out.println(targetGeospatialScaleEntity11.
                countAttachedConceptionEntities(null,attributesParameters,true,GeospatialScaleEntity.GeospatialScaleLevel.CHILD));
        System.out.println(targetGeospatialScaleEntity11.
                countAttachedConceptionEntities(null,attributesParameters,true,GeospatialScaleEntity.GeospatialScaleLevel.OFFSPRING));

        GeospatialScaleEventsRetrieveResult geospatialScaleEventsRetrieveResult1 = targetGeospatialScaleEntity11.getAttachedGeospatialScaleEvents(queryParameters,GeospatialScaleEntity.GeospatialScaleLevel.SELF);
        System.out.println(geospatialScaleEventsRetrieveResult1.getGeospatialScaleEvents().size());
        GeospatialScaleEventsRetrieveResult geospatialScaleEventsRetrieveResult2 = targetGeospatialScaleEntity11.getAttachedGeospatialScaleEvents(queryParameters,GeospatialScaleEntity.GeospatialScaleLevel.CHILD);
        System.out.println(geospatialScaleEventsRetrieveResult2.getGeospatialScaleEvents().size());
        GeospatialScaleEventsRetrieveResult geospatialScaleEventsRetrieveResult3 = targetGeospatialScaleEntity11.getAttachedGeospatialScaleEvents(queryParameters,GeospatialScaleEntity.GeospatialScaleLevel.OFFSPRING);
        System.out.println(geospatialScaleEventsRetrieveResult3.getGeospatialScaleEvents().size());
        for(GeospatialScaleEvent currentGeospatialScaleEvent:geospatialScaleEventsRetrieveResult3.getGeospatialScaleEvents()){
            System.out.println(currentGeospatialScaleEvent.getEventComment());
            System.out.println(currentGeospatialScaleEvent.getGeospatialScaleEventUID());
            System.out.println(currentGeospatialScaleEvent.getGeospatialScaleGrade());
            System.out.println(currentGeospatialScaleEvent.getReferLocation());
        }

        AttributesParameters attributesParameters2 = new AttributesParameters();
        attributesParameters2.setDefaultFilteringItem(new EqualFilteringItem("DOCG_GeospatialScaleEventComment","eventAttachComment"));
        System.out.println(targetGeospatialScaleEntity11.
                countAttachedGeospatialScaleEvents(attributesParameters2,true,GeospatialScaleEntity.GeospatialScaleLevel.SELF));
        System.out.println(targetGeospatialScaleEntity11.
                countAttachedGeospatialScaleEvents(attributesParameters2,true,GeospatialScaleEntity.GeospatialScaleLevel.CHILD));
        System.out.println(targetGeospatialScaleEntity11.
                countAttachedGeospatialScaleEvents(attributesParameters2,true,GeospatialScaleEntity.GeospatialScaleLevel.OFFSPRING));
*/
        coreRealm.closeGlobalSession();
    }
}
