package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeospatialScaleCalculableTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for GeospatialScaleCalculableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testGeospatialScaleCalculableFunction() throws CoreRealmServiceRuntimeException{
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        //coreRealm.openGlobalSession();

        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind buildingConceptionKind = coreRealm.getConceptionKind("BuildingForGeospatialScaleCalculable");
        if(buildingConceptionKind == null){
            buildingConceptionKind = coreRealm.createConceptionKind("BuildingForGeospatialScaleCalculable","buildingForTest");
        }
        buildingConceptionKind.purgeAllEntities();

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1","prop1");
        newEntityValue.put("prop2","StringValue");
        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity _ConceptionEntity = buildingConceptionKind.newEntity(conceptionEntityValue,false);

        boolean exceptionShouldHappen = false;
        List<String> targetKind = new ArrayList<>();
        targetKind.add("TargetKind1");
        try {
            _ConceptionEntity.getSpatialPredicateMatchedConceptionEntities(targetKind, GeospatialScaleCalculable.SpatialPredicateType.Contains,
                    GeospatialScaleCalculable.SpatialScaleLevel.Country);
        }catch (CoreRealmServiceException e){
            exceptionShouldHappen = true;
        }
        Assert.assertTrue(exceptionShouldHappen);


        //String shpFileLocation = "/home/wangychu/Desktop/GYD_GYW_GIS_DATA/GYD-GIS_WGS84/gyd-building.shp";
        //String conceptionKindName = "GYD_Building";
        //File file = new File(shpFileLocation);
        //SHP_DataSourceImport.importSHPDataToConceptionKind(conceptionKindName,true,file,null);


    }
}
