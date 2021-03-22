package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
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

        List<GeospatialScaleEntity> countryRegionGeospatialScaleEntityList3 = defaultGeospatialRegion.listProvinceEntities(GeospatialRegion.GeospatialProperty.ChineseName,"中国","江");
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

        coreRealm.closeGlobalSession();
    }
}
