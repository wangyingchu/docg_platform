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

public class MultiConceptionKindsSupportableTest {

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

        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind("newKind001");
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind("newKind001",true);
        }
        _ConceptionKind02 = coreRealm.getConceptionKind("newKind001");
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind("newKind001","TestConceptionKindADesc+中文描述");
        }

        Assert.assertEquals(_ConceptionKind02.countConceptionEntities(),new Long("0"));
        Assert.assertEquals(_ConceptionKind01.countConceptionEntities(),new Long("2"));

        String[] newKindNamesArray = new String[]{"newKind001","newKind002"};
        boolean joinResult = _ConceptionEntity1.joinConceptionKinds(newKindNamesArray);
        Assert.assertTrue(joinResult);
        Assert.assertEquals(_ConceptionKind02.countConceptionEntities(),new Long("1"));
        Assert.assertEquals(_ConceptionKind01.countConceptionEntities(),new Long("2"));

        _ConceptionEntity1 = _ConceptionKind01.getEntityByUID(_ConceptionEntity1.getConceptionEntityUID());
        Assert.assertEquals(_ConceptionEntity1.getAllConceptionKindNames().size(),3);
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains("newKind001"));
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains("newKind002"));
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains(testConceptionKindName));

        boolean retreatResult = _ConceptionEntity1.retreatFromConceptionKind("newKind001");
        Assert.assertTrue(retreatResult);
        _ConceptionEntity1 = _ConceptionKind01.getEntityByUID(_ConceptionEntity1.getConceptionEntityUID());
        Assert.assertEquals(_ConceptionEntity1.getAllConceptionKindNames().size(),2);
        Assert.assertFalse(_ConceptionEntity1.getAllConceptionKindNames().contains("newKind001"));
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains("newKind002"));
        Assert.assertTrue(_ConceptionEntity1.getAllConceptionKindNames().contains(testConceptionKindName));
        Assert.assertEquals(_ConceptionKind02.countConceptionEntities(),new Long("0"));
        Assert.assertEquals(_ConceptionKind01.countConceptionEntities(),new Long("2"));

        boolean exceptionShouldBeCaught = false;
        try{
            _ConceptionEntity2.retreatFromConceptionKind("newKind001");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _ConceptionEntity1.retreatFromConceptionKind(null);
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _ConceptionEntity1.joinConceptionKinds(null);
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _ConceptionEntity1.joinConceptionKinds(new String[]{});
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);
    }
}
