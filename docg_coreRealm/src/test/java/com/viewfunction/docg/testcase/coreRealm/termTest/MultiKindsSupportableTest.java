package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class MultiKindsSupportableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindMultiKindsSupportableTest";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for MultiKindsSupportableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testMultiKindsSupportable() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"TestConceptionKindADesc+中文描述");
        }

        Map<String,Object> newEntityValue1= new HashMap<>();
        newEntityValue1.put("prop1","fromEntity");
        ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValue1);
        ConceptionEntity _ConceptionEntity1 = _ConceptionKind01.newEntity(conceptionEntityValue1,false);

        Map<String,Object> newEntityValue2= new HashMap<>();
        newEntityValue2.put("prop1","toEntity");
        ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue2);
        ConceptionEntity _ConceptionEntity2 = _ConceptionKind01.newEntity(conceptionEntityValue2,false);

        Assert.assertEquals(_ConceptionEntity1.getAllConceptionKindNames().size(),1);

        String[] newKindNamesArray = new String[]{"newKind001","newKind002"};
        boolean joinResult = _ConceptionEntity1.joinKinds(newKindNamesArray);
        Assert.assertTrue(joinResult);

        _ConceptionEntity1 = _ConceptionKind01.getEntityByUID(_ConceptionEntity1.getConceptionEntityUID());
        Assert.assertEquals(_ConceptionEntity1.getAllConceptionKindNames().size(),3);
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains("newKind001"));
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains("newKind002"));
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains(testConceptionKindName));
    }
}
