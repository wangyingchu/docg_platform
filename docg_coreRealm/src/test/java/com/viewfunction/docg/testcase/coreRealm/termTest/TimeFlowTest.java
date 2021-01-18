package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleMoment;
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

/*
        Assert.assertNotNull(defaultTimeFlow);
        Assert.assertNotNull(((Neo4JTimeFlowImpl)defaultTimeFlow).getTimeFlowUID());
        Assert.assertEquals(defaultTimeFlow.getTimeFlowName(), RealmConstant._defaultTimeFlowName);

        List<Integer> availableTimeSpanYears = defaultTimeFlow.getAvailableTimeSpanYears();
        System.out.println(availableTimeSpanYears);

        //boolean addTimeSpanEntities = defaultTimeFlow.createTimeSpanEntities(2004);
        //System.out.println(addTimeSpanEntities);

        //boolean addTimeSpanEntities2 = defaultTimeFlow.createTimeSpanEntities(2001,2003);
        //System.out.println(addTimeSpanEntities2);

        //boolean addTimeSpanEntities3 = defaultTimeFlow.createTimeSpanEntities(1996,2000);
        //System.out.println(addTimeSpanEntities3);

        availableTimeSpanYears = defaultTimeFlow.getAvailableTimeSpanYears();
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

        TimeScaleEntity singleTimeScaleEntity1 = defaultTimeFlow.getMonthEntity(1997,6);
        System.out.println(singleTimeScaleEntity1);
        System.out.println(singleTimeScaleEntity1.getTimeScaleGrade());
        System.out.println(singleTimeScaleEntity1.getEntityValue());

        TimeScaleEntity[] timeScaleEntityArray2 = defaultTimeFlow.getSpecificMonthEntities(new TimeScaleMoment(2004,6),new TimeScaleMoment(1999,8));
        System.out.println(timeScaleEntityArray2.length);
        System.out.println(timeScaleEntityArray2[0].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray2[0].getEntityValue());
        System.out.println(timeScaleEntityArray2[1].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray2[1].getEntityValue());

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList2 = defaultTimeFlow.getMonthEntities(new TimeScaleMoment(1997,4),new TimeScaleMoment(2004,9));
        System.out.println(timeScaleEntityLinkedList2.getFirst().getEntityValue());
        System.out.println(timeScaleEntityLinkedList2.getFirst().getTimeScaleGrade());
        System.out.println(timeScaleEntityLinkedList2.getLast().getEntityValue());
        System.out.println(timeScaleEntityLinkedList2.getLast().getTimeScaleGrade());
        System.out.println(timeScaleEntityLinkedList2.size());
*/
        /*
        TimeScaleEntity singleTimeScaleEntity2 = defaultTimeFlow.getDayEntity(2000,2,29);
        System.out.println(singleTimeScaleEntity2);
        System.out.println(singleTimeScaleEntity2.getTimeScaleGrade());
        System.out.println(singleTimeScaleEntity2.getEntityValue());

        TimeScaleEntity[] timeScaleEntityArray3 = defaultTimeFlow.getSpecificDayEntities(new TimeScaleMoment(2004,6,5),
                new TimeScaleMoment(1999,11,23),
                new TimeScaleMoment(2003,9,27));
        System.out.println(timeScaleEntityArray3.length);
        System.out.println(timeScaleEntityArray3[0].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray3[0].getEntityValue());
        System.out.println(timeScaleEntityArray3[1].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray3[1].getEntityValue());
        */

        /*
        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList3 = defaultTimeFlow.getDayEntities(new TimeScaleMoment(1997,12,2),new TimeScaleMoment(1998,1,2));
        System.out.println(timeScaleEntityLinkedList3.size());

        TimeScaleEntity singleTimeScaleEntity3 = defaultTimeFlow.getHourEntity(2000,2,29,18);
        System.out.println(singleTimeScaleEntity3);
        System.out.println(singleTimeScaleEntity3.getTimeScaleGrade());
        System.out.println(singleTimeScaleEntity3.getEntityValue());

        TimeScaleEntity[] timeScaleEntityArray4 = defaultTimeFlow.getSpecificHourEntities(new TimeScaleMoment(2004,6,5,13),
                new TimeScaleMoment(1999,11,23,22),
                new TimeScaleMoment(2003,9,27,2));
        System.out.println(timeScaleEntityArray4.length);
        System.out.println(timeScaleEntityArray4[0].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray4[0].getEntityValue());
        System.out.println(timeScaleEntityArray4[1].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray4[1].getEntityValue());

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,12,2,0),new TimeScaleMoment(1997,12,2,21));
        System.out.println(timeScaleEntityLinkedList4.size());
        System.out.println(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade());

        timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,12,2,0),new TimeScaleMoment(1997,12,4,21));
        System.out.println(timeScaleEntityLinkedList4.size());
        System.out.println(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade());

        timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,9,2,3),new TimeScaleMoment(1997,11,4,21));
        System.out.println(timeScaleEntityLinkedList4.size());
        System.out.println(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade());

        timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,12,31,3),new TimeScaleMoment(2000,1,1,22));
        System.out.println(timeScaleEntityLinkedList4.size());
        System.out.println(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade());
        */

        TimeScaleEntity singleTimeScaleEntity4 = defaultTimeFlow.getMinuteEntity(2000,5,29,18,43);
        System.out.println(singleTimeScaleEntity4);
        System.out.println(singleTimeScaleEntity4.getTimeScaleGrade());
        System.out.println(singleTimeScaleEntity4.getEntityValue());

        TimeScaleEntity[] timeScaleEntityArray5 = defaultTimeFlow.getSpecificMinuteEntities(new TimeScaleMoment(2004,6,5,13,24),
                new TimeScaleMoment(1999,11,23,22,5),
                new TimeScaleMoment(2003,9,27,2,1));
        System.out.println(timeScaleEntityArray5.length);
        System.out.println(timeScaleEntityArray5[0].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray5[0].getEntityValue());
        System.out.println(timeScaleEntityArray5[1].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray5[1].getEntityValue());
        System.out.println(timeScaleEntityArray5[2].getTimeScaleGrade());
        System.out.println(timeScaleEntityArray5[2].getEntityValue());

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(1997,12,31,3,5),new TimeScaleMoment(1997,12,31,3,18));
        System.out.println(timeScaleEntityLinkedList5.size());
        System.out.println(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade());

        timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(1997,12,31,3,5),new TimeScaleMoment(1997,12,31,6,18));
        System.out.println(timeScaleEntityLinkedList5.size());
        System.out.println(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade());

        timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(1997,12,15,3,5),new TimeScaleMoment(1997,12,28,5,18));
        System.out.println(timeScaleEntityLinkedList5.size());
        System.out.println(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade());


    }

}
