package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeFlowImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

public class TimeFlowTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for TimeFlowTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testTimeFlowFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        TimeFlow defaultTimeFlow = coreRealm.getOrCreateTimeFlow();
        Assert.assertNotNull(defaultTimeFlow);
        Assert.assertNotNull(((Neo4JTimeFlowImpl)defaultTimeFlow).getTimeFlowUID());
        Assert.assertEquals(defaultTimeFlow.getTimeFlowName(), RealmConstant._defaultTimeFlowName);

        List<Integer> availableTimeSpanYears = defaultTimeFlow.getAvailableTimeSpanYears();
        System.out.println(availableTimeSpanYears);

        //boolean addTimeSpanEntities = defaultTimeFlow.createTimeSpanEntities(2004);
        //System.out.println(addTimeSpanEntities);

        //boolean addTimeSpanEntities2 = defaultTimeFlow.createTimeSpanEntities(2001,2003);
        //System.out.println(addTimeSpanEntities2);

        //availableTimeSpanYears = defaultTimeFlow.getAvailableTimeSpanYears();
        //System.out.println(availableTimeSpanYears);

        TimeScaleEntity _2000YearTimeScaleEntity = defaultTimeFlow.getYearEntity(2000);
        System.out.println(_2000YearTimeScaleEntity);
        System.out.println(_2000YearTimeScaleEntity.getTimeScaleGrade());
        System.out.println(_2000YearTimeScaleEntity.getEntityValue());

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList1 = defaultTimeFlow.getYearEntities(2001,2004);
        System.out.println(timeScaleEntityLinkedList1.getFirst().getEntityValue());
        System.out.println(timeScaleEntityLinkedList1.getLast().getEntityValue());


        TimeScaleEntity[] timeScaleEntityArray1 = defaultTimeFlow.getSpecificYearEntities(1998,2004,1997,4561);
        System.out.println(timeScaleEntityArray1.length);

    }

}
