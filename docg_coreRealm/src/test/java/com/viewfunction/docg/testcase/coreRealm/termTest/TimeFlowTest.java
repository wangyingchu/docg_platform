package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeFlowImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.*;

public class TimeFlowTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData() throws CoreRealmServiceRuntimeException {
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for TimeFlowTest");
        System.out.println("--------------------------------------------------");
        setupTimeFlowFunction();
    }

    private void setupTimeFlowFunction() throws CoreRealmServiceRuntimeException{
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        long resultCount = coreRealm.removeTimeFlowWithEntities();

        TimeFlow defaultTimeFlow = coreRealm.getOrCreateTimeFlow();
        Assert.assertNotNull(defaultTimeFlow);
        Assert.assertNotNull(((Neo4JTimeFlowImpl)defaultTimeFlow).getTimeFlowUID());
        Assert.assertEquals(defaultTimeFlow.getTimeFlowName(), RealmConstant._defaultTimeFlowName);

        List<Integer> availableTimeSpanYears = defaultTimeFlow.getAvailableTimeSpanYears();
        Assert.assertEquals(availableTimeSpanYears.size(),0);

        boolean addTimeSpanEntities = defaultTimeFlow.createTimeSpanEntities(2004,true);
        Assert.assertTrue(addTimeSpanEntities);
        boolean addTimeSpanEntities2 = defaultTimeFlow.createTimeSpanEntities(2001,2003,false);
        Assert.assertTrue(addTimeSpanEntities2);
        boolean addTimeSpanEntities3 = defaultTimeFlow.createTimeSpanEntities(1996,2000,false);
        Assert.assertTrue(addTimeSpanEntities3);
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
        Assert.assertEquals(availableTimeSpanYears.size(),9);
        Assert.assertTrue(availableTimeSpanYears.contains(1996));
        Assert.assertTrue(availableTimeSpanYears.contains(1997));
        Assert.assertTrue(availableTimeSpanYears.contains(1998));
        Assert.assertTrue(availableTimeSpanYears.contains(1999));
        Assert.assertTrue(availableTimeSpanYears.contains(2000));
        Assert.assertTrue(availableTimeSpanYears.contains(2001));
        Assert.assertTrue(availableTimeSpanYears.contains(2002));
        Assert.assertTrue(availableTimeSpanYears.contains(2003));
        Assert.assertTrue(availableTimeSpanYears.contains(2004));

        TimeScaleEntity _2000YearTimeScaleEntity = defaultTimeFlow.getYearEntity(2000);
        Assert.assertNotNull(_2000YearTimeScaleEntity);
        Assert.assertEquals(_2000YearTimeScaleEntity.getTimeScaleGrade(), TimeFlow.TimeScaleGrade.YEAR);
        Assert.assertEquals(_2000YearTimeScaleEntity.getEntityValue(),2000);
        Assert.assertNotNull(_2000YearTimeScaleEntity.getTimeScaleEntityUID());
        TimeScaleEntity _2000YearTimeScaleEntity2 = defaultTimeFlow.getTimeScaleEntityByUID(_2000YearTimeScaleEntity.getTimeScaleEntityUID());
        Assert.assertNotNull(_2000YearTimeScaleEntity2);
        Assert.assertEquals(_2000YearTimeScaleEntity2.getTimeScaleGrade(), TimeFlow.TimeScaleGrade.YEAR);
        Assert.assertEquals(_2000YearTimeScaleEntity2.getEntityValue(),2000);
        Assert.assertNotNull(_2000YearTimeScaleEntity2.getTimeScaleEntityDesc());

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList1 = defaultTimeFlow.getYearEntities(2001,2004);
        Assert.assertEquals(timeScaleEntityLinkedList1.getFirst().getEntityValue(),2001);
        Assert.assertEquals(timeScaleEntityLinkedList1.getLast().getEntityValue(),2004);

        TimeScaleEntity[] timeScaleEntityArray1 = defaultTimeFlow.getSpecificYearEntities(1998,2004,1997,4561);
        Assert.assertEquals(timeScaleEntityArray1.length,3);

        TimeScaleEntity singleTimeScaleEntity1 = defaultTimeFlow.getMonthEntity(1997,6);
        Assert.assertNotNull(singleTimeScaleEntity1);
        Assert.assertEquals(singleTimeScaleEntity1.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(singleTimeScaleEntity1.getEntityValue(),6);
        Assert.assertNotNull(singleTimeScaleEntity1.getTimeScaleEntityUID());
        TimeScaleEntity singleTimeScaleEntity1_2 = defaultTimeFlow.getTimeScaleEntityByUID(singleTimeScaleEntity1.getTimeScaleEntityUID());
        Assert.assertNotNull(singleTimeScaleEntity1_2);
        Assert.assertEquals(singleTimeScaleEntity1_2.getTimeScaleGrade(), TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(singleTimeScaleEntity1_2.getEntityValue(),6);
        Assert.assertNotNull(singleTimeScaleEntity1_2.getTimeScaleEntityDesc());

        TimeScaleEntity[] timeScaleEntityArray2 = defaultTimeFlow.getSpecificMonthEntities(new TimeScaleMoment(2004,6),new TimeScaleMoment(1999,8));
        Assert.assertEquals(timeScaleEntityArray2.length,2);
        Assert.assertEquals(timeScaleEntityArray2[0].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(timeScaleEntityArray2[0].getEntityValue(),6);
        Assert.assertEquals(timeScaleEntityArray2[1].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(timeScaleEntityArray2[1].getEntityValue(),8);

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList2 = defaultTimeFlow.getMonthEntities(new TimeScaleMoment(1997,4),new TimeScaleMoment(2004,9));
        Assert.assertEquals(timeScaleEntityLinkedList2.getFirst().getEntityValue(),4);
        Assert.assertEquals(timeScaleEntityLinkedList2.getFirst().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(timeScaleEntityLinkedList2.getLast().getEntityValue(),9);
        Assert.assertEquals(timeScaleEntityLinkedList2.getLast().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(timeScaleEntityLinkedList2.size(),90);

        timeScaleEntityLinkedList2 = defaultTimeFlow.getMonthEntities(new TimeScaleMoment(1997,4),new TimeScaleMoment(1997,6));
        Assert.assertEquals(timeScaleEntityLinkedList2.getFirst().getEntityValue(),4);
        Assert.assertEquals(timeScaleEntityLinkedList2.getFirst().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(timeScaleEntityLinkedList2.getLast().getEntityValue(),6);
        Assert.assertEquals(timeScaleEntityLinkedList2.getLast().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
        Assert.assertEquals(timeScaleEntityLinkedList2.size(),3);

        TimeScaleEntity singleTimeScaleEntity2 = defaultTimeFlow.getDayEntity(2000,2,29);
        Assert.assertNotNull(singleTimeScaleEntity2);
        Assert.assertEquals(singleTimeScaleEntity2.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.DAY);
        Assert.assertEquals(singleTimeScaleEntity2.getEntityValue(),29);
        Assert.assertNotNull(singleTimeScaleEntity2.getTimeScaleEntityUID());
        TimeScaleEntity singleTimeScaleEntity2_2 = defaultTimeFlow.getTimeScaleEntityByUID(singleTimeScaleEntity2.getTimeScaleEntityUID());
        Assert.assertNotNull(singleTimeScaleEntity2_2);
        Assert.assertEquals(singleTimeScaleEntity2_2.getTimeScaleGrade(), TimeFlow.TimeScaleGrade.DAY);
        Assert.assertEquals(singleTimeScaleEntity2_2.getEntityValue(),29);
        Assert.assertNotNull(singleTimeScaleEntity2_2.getTimeScaleEntityDesc());

        TimeScaleEntity[] timeScaleEntityArray3 = defaultTimeFlow.getSpecificDayEntities(new TimeScaleMoment(2004,6,5),
                new TimeScaleMoment(1999,11,23),
                new TimeScaleMoment(2003,9,27));
        Assert.assertEquals(timeScaleEntityArray3.length,3);
        Assert.assertEquals(timeScaleEntityArray3[0].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.DAY);
        Assert.assertEquals(timeScaleEntityArray3[0].getEntityValue(),5);
        Assert.assertEquals(timeScaleEntityArray3[1].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.DAY);
        Assert.assertEquals(timeScaleEntityArray3[1].getEntityValue(),23);
        Assert.assertEquals(timeScaleEntityArray3[2].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.DAY);
        Assert.assertEquals(timeScaleEntityArray3[2].getEntityValue(),27);

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList3 = defaultTimeFlow.getDayEntities(new TimeScaleMoment(1997,12,2),new TimeScaleMoment(1998,1,2));
        Assert.assertEquals(timeScaleEntityLinkedList3.size(),32);

        TimeScaleEntity singleTimeScaleEntity3 = defaultTimeFlow.getHourEntity(2000,2,29,18);
        Assert.assertNotNull(singleTimeScaleEntity3);
        Assert.assertEquals(singleTimeScaleEntity3.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);
        Assert.assertEquals(singleTimeScaleEntity3.getEntityValue(),18);
        Assert.assertNotNull(singleTimeScaleEntity3.getTimeScaleEntityUID());
        TimeScaleEntity singleTimeScaleEntity3_2 = defaultTimeFlow.getTimeScaleEntityByUID(singleTimeScaleEntity3.getTimeScaleEntityUID());
        Assert.assertNotNull(singleTimeScaleEntity3_2);
        Assert.assertEquals(singleTimeScaleEntity3_2.getTimeScaleGrade(), TimeFlow.TimeScaleGrade.HOUR);
        Assert.assertEquals(singleTimeScaleEntity3_2.getEntityValue(),18);
        Assert.assertNotNull(singleTimeScaleEntity3_2.getTimeScaleEntityDesc());

        TimeScaleEntity[] timeScaleEntityArray4 = defaultTimeFlow.getSpecificHourEntities(new TimeScaleMoment(2004,6,5,13),
                new TimeScaleMoment(1999,11,23,22),
                new TimeScaleMoment(2003,9,27,2));
        Assert.assertEquals(timeScaleEntityArray4.length,3);
        Assert.assertEquals(timeScaleEntityArray4[0].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);
        Assert.assertEquals(timeScaleEntityArray4[0].getEntityValue(),13);
        Assert.assertEquals(timeScaleEntityArray4[1].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);
        Assert.assertEquals(timeScaleEntityArray4[1].getEntityValue(),22);

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,12,2,0),new TimeScaleMoment(1997,12,2,21));
        Assert.assertEquals(timeScaleEntityLinkedList4.size(),22);
        Assert.assertEquals(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);

        timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,12,2,0),new TimeScaleMoment(1997,12,4,21));
        Assert.assertEquals(timeScaleEntityLinkedList4.size(),70);
        Assert.assertEquals(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);

        timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,9,2,3),new TimeScaleMoment(1997,11,4,21));
        Assert.assertEquals(timeScaleEntityLinkedList4.size(),787);
        Assert.assertEquals(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);

        timeScaleEntityLinkedList4 = defaultTimeFlow.getHourEntities(new TimeScaleMoment(1997,12,31,3),new TimeScaleMoment(2000,1,1,22));
        Assert.assertEquals(timeScaleEntityLinkedList4.size(),17564);
        Assert.assertEquals(timeScaleEntityLinkedList4.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);

        TimeScaleEntity singleTimeScaleEntity4 = defaultTimeFlow.getMinuteEntity(2004,5,29,18,35);
        Assert.assertNotNull(singleTimeScaleEntity4);
        Assert.assertEquals(singleTimeScaleEntity4.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity4.getEntityValue(),35);
        Assert.assertNotNull(singleTimeScaleEntity4.getTimeScaleEntityUID());
        TimeScaleEntity singleTimeScaleEntity4_2 = defaultTimeFlow.getTimeScaleEntityByUID(singleTimeScaleEntity4.getTimeScaleEntityUID());
        Assert.assertNotNull(singleTimeScaleEntity4_2);
        Assert.assertEquals(singleTimeScaleEntity4_2.getTimeScaleGrade(), TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity4_2.getEntityValue(),35);
        Assert.assertNotNull(singleTimeScaleEntity4_2.getTimeScaleEntityDesc());

        TimeScaleEntity singleTimeScaleEntity4_nextEntity = singleTimeScaleEntity4.getNextSameScaleEntity();
        Assert.assertEquals(singleTimeScaleEntity4_nextEntity.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity4_nextEntity.getEntityValue(),36);

        TimeScaleEntity singleTimeScaleEntity4_previousEntity = singleTimeScaleEntity4.getPreviousSameScaleEntity();
        Assert.assertEquals(singleTimeScaleEntity4_previousEntity.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity4_previousEntity.getEntityValue(),34);

        TimeScaleEntity singleTimeScaleEntity5 = defaultTimeFlow.getHourEntity(2004,11,13,23);
        Assert.assertNotNull(singleTimeScaleEntity5);
        Assert.assertEquals(singleTimeScaleEntity5.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);
        Assert.assertEquals(singleTimeScaleEntity5.getEntityValue(),23);

        TimeScaleEntity singleTimeScaleEntity5_firstChildEntity = singleTimeScaleEntity5.getFirstChildEntity();
        Assert.assertNotNull(singleTimeScaleEntity5_firstChildEntity);
        Assert.assertEquals(singleTimeScaleEntity5_firstChildEntity.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity5_firstChildEntity.getEntityValue(),0);

        TimeScaleEntity singleTimeScaleEntity5_lastChildEntity = singleTimeScaleEntity5.getLastChildEntity();
        Assert.assertNotNull(singleTimeScaleEntity5_lastChildEntity);
        Assert.assertEquals(singleTimeScaleEntity5_lastChildEntity.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity5_lastChildEntity.getEntityValue(),59);

        LinkedList<TimeScaleEntity> singleTimeScaleEntity5_childList = singleTimeScaleEntity5.getChildEntities();
        Assert.assertNotNull(singleTimeScaleEntity5_childList);
        Assert.assertEquals(singleTimeScaleEntity5_childList.size(),60);
        Assert.assertEquals(singleTimeScaleEntity5_childList.getFirst().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity5_childList.getFirst().getEntityValue(),0);
        Assert.assertEquals(singleTimeScaleEntity5_childList.getLast().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(singleTimeScaleEntity5_childList.getLast().getEntityValue(),59);

        TimeScaleEntity singleTimeScaleEntity6 = defaultTimeFlow.getHourEntity(2000,5,29,15);
        LinkedList<TimeScaleEntity> singleTimeScaleEntity6_fellowList = singleTimeScaleEntity6.getFellowEntities();
        Assert.assertNotNull(singleTimeScaleEntity6_fellowList);
        Assert.assertEquals(singleTimeScaleEntity6_fellowList.size(),24);
        Assert.assertEquals(singleTimeScaleEntity6_fellowList.getFirst().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);
        Assert.assertEquals(singleTimeScaleEntity6_fellowList.getFirst().getEntityValue(),0);
        Assert.assertEquals(singleTimeScaleEntity6_fellowList.getLast().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.HOUR);
        Assert.assertEquals(singleTimeScaleEntity6_fellowList.getLast().getEntityValue(),23);

        TimeScaleEntity singleTimeScaleEntity6_parent = singleTimeScaleEntity6.getParentEntity();
        Assert.assertNotNull(singleTimeScaleEntity6_parent);
        Assert.assertEquals(singleTimeScaleEntity6_parent.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.DAY);
        Assert.assertEquals(singleTimeScaleEntity6_parent.getEntityValue(),29);

        TimeScaleEntity singleTimeScaleEntity7 = defaultTimeFlow.getYearEntity(1999);
        LinkedList<TimeScaleEntity> singleTimeScaleEntity7_fellowList = singleTimeScaleEntity7.getFellowEntities();
        Assert.assertNotNull(singleTimeScaleEntity7_fellowList);
        Assert.assertEquals(singleTimeScaleEntity7_fellowList.size(),9);
        Assert.assertEquals(singleTimeScaleEntity7_fellowList.getFirst().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.YEAR);
        Assert.assertEquals(singleTimeScaleEntity7_fellowList.getFirst().getEntityValue(),1996);
        Assert.assertEquals(singleTimeScaleEntity7_fellowList.getLast().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.YEAR);
        Assert.assertEquals(singleTimeScaleEntity7_fellowList.getLast().getEntityValue(),2004);

        TimeScaleEntity singleTimeScaleEntity8 = defaultTimeFlow.getDayEntity(2004,6,19);
        InheritanceTree<TimeScaleEntity> singleTimeScaleEntity8_InheritanceTree = singleTimeScaleEntity8.getOffspringEntities();
        Assert.assertEquals(singleTimeScaleEntity8_InheritanceTree.size(),1465);
        Assert.assertEquals(singleTimeScaleEntity8_InheritanceTree.getRoot().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.DAY);
        Assert.assertEquals(singleTimeScaleEntity8_InheritanceTree.getRoot().getEntityValue(),19);
        Assert.assertEquals(singleTimeScaleEntity8_InheritanceTree.numOfChildren(singleTimeScaleEntity8_InheritanceTree.getRootID()),24);
        Collection<String> rootChildrenIDCollection = singleTimeScaleEntity8_InheritanceTree.getChildrenID(singleTimeScaleEntity8_InheritanceTree.getRootID());
        for(String currentNodeId:rootChildrenIDCollection){
            Assert.assertEquals(singleTimeScaleEntity8_InheritanceTree.numOfChildren(currentNodeId),60);
            String firstChildId = singleTimeScaleEntity8_InheritanceTree.getChildrenID(currentNodeId).iterator().next();
            Assert.assertEquals(singleTimeScaleEntity8_InheritanceTree.getNode(firstChildId).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        }

        TimeScaleEntity[] timeScaleEntityArray5 = defaultTimeFlow.getSpecificMinuteEntities(new TimeScaleMoment(2004,6,5,13,24),
                new TimeScaleMoment(2004,11,23,22,5),
                new TimeScaleMoment(2004,9,27,2,1));
        Assert.assertEquals(timeScaleEntityArray5.length,3);
        Assert.assertEquals(timeScaleEntityArray5[0].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(timeScaleEntityArray5[0].getEntityValue(),24);
        Assert.assertEquals(timeScaleEntityArray5[1].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(timeScaleEntityArray5[1].getEntityValue(),5);
        Assert.assertEquals(timeScaleEntityArray5[2].getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(timeScaleEntityArray5[2].getEntityValue(),1);

        LinkedList<TimeScaleEntity> timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(2004,12,31,3,5),new TimeScaleMoment(2004,12,31,3,18));
        Assert.assertEquals(timeScaleEntityLinkedList5.size(),14);
        Assert.assertEquals(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);

        timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(2004,12,31,3,5),new TimeScaleMoment(2004,12,31,6,18));
        Assert.assertEquals(timeScaleEntityLinkedList5.size(),194);
        Assert.assertEquals(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);

        timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(2004,12,15,3,5),new TimeScaleMoment(2004,12,28,5,18));
        Assert.assertEquals(timeScaleEntityLinkedList5.size(),18854);
        Assert.assertEquals(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);

        timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(2004,6,15,3,5),new TimeScaleMoment(2004,11,28,22,18));
        Assert.assertEquals(timeScaleEntityLinkedList5.size(),238994);
        Assert.assertEquals(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);

        timeScaleEntityLinkedList5 = defaultTimeFlow.getMinuteEntities(new TimeScaleMoment(2004,7,15,3,5),new TimeScaleMoment(2004,11,28,22,18));
        Assert.assertEquals(timeScaleEntityLinkedList5.size(),195794);
        Assert.assertEquals(timeScaleEntityLinkedList5.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("TimeFlowEventTest");
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind("TimeFlowEventTest","TimeFlowEventTest+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
        }

        EntitiesOperationResult purgeEntitiesOperationResult = _ConceptionKind01.purgeAllEntities();

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",Long.parseLong("12345"));
        newEntityValue.put("prop2",Double.parseDouble("12345.789"));
        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);

        ConceptionEntity _ConceptionEntity = _ConceptionKind01.newEntity(conceptionEntityValue,false);

        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind("TimeFlowEventTest2");
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind("TimeFlowEventTest2","TimeFlowEventTest2+中文描述");
            Assert.assertNotNull(_ConceptionKind02);
        }

        EntitiesOperationResult purgeEntitiesOperationResult2 = _ConceptionKind02.purgeAllEntities();

        Map<String,Object> newEntityValue2= new HashMap<>();
        newEntityValue.put("prop1",Long.parseLong("12345"));
        newEntityValue.put("prop2",Double.parseDouble("12345.789"));
        ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue2);

        ConceptionEntity _ConceptionEntity2 = _ConceptionKind02.newEntity(conceptionEntityValue,false);

        Calendar eventCalendar=Calendar.getInstance();
        eventCalendar.set(Calendar.YEAR,2004);
        eventCalendar.set(Calendar.MONTH,4);
        eventCalendar.set(Calendar.DAY_OF_MONTH,16);
        eventCalendar.set(Calendar.HOUR_OF_DAY,22);
        eventCalendar.set(Calendar.MINUTE,55);

        Map<String,Object> timeEventDataMap = new HashMap<>();
        timeEventDataMap.put("K1",Long.parseLong("112"));
        timeEventDataMap.put("K2",Double.parseDouble("2344"));

        TimeScaleEvent timeScaleEvent1 = _ConceptionEntity.attachTimeScaleEvent(eventCalendar.getTimeInMillis(),"timeEventRelation",timeEventDataMap, TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertNotNull(timeScaleEvent1);
        Assert.assertNotNull(timeScaleEvent1.getTimeScaleEventUID());
        Assert.assertEquals(timeScaleEvent1.getEventComment(),"timeEventRelation");
        Assert.assertEquals(timeScaleEvent1.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(timeScaleEvent1.getReferTime().getYear(),2004);
        Assert.assertEquals(timeScaleEvent1.getReferTime().getMonth().getValue(),5);
        Assert.assertEquals(timeScaleEvent1.getReferTime().getDayOfMonth(),16);
        Assert.assertEquals(timeScaleEvent1.getReferTime().getHour(),22);
        Assert.assertEquals(timeScaleEvent1.getReferTime().getMinute(),55);

        Assert.assertEquals(timeScaleEvent1.getTimeFlowName(),"DefaultTimeFlow");
        Assert.assertEquals(timeScaleEvent1.getAttribute("K1").getAttributeValue(),Long.parseLong("112"));
        Assert.assertEquals(timeScaleEvent1.getAttribute("K2").getAttributeValue(),Double.parseDouble("2344"));

        Classification _Classification01 = coreRealm.getClassification("classificationNameA");
        if(_Classification01 == null) {
            coreRealm.createClassification("classificationNameA", "classificationNameA" + "Desc");
        }
        RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
        relationAttachInfo.setRelationKind("relationTypeForClassificationTest01");
        relationAttachInfo.setRelationDirection(RelationDirection.FROM);
        timeScaleEvent1.attachClassification(relationAttachInfo,"classificationNameA");

        Assert.assertEquals(timeScaleEvent1.getReferTimeScaleEntity().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        Assert.assertEquals(timeScaleEvent1.getReferTimeScaleEntity().getEntityValue(),55);
        Assert.assertEquals(timeScaleEvent1.getAttachConceptionEntity().getConceptionKindName(),"TimeFlowEventTest");
        Assert.assertEquals(timeScaleEvent1.getAttachConceptionEntity().getConceptionEntityUID(),_ConceptionEntity.getConceptionEntityUID());

        _ConceptionEntity.attachFromRelation(_ConceptionEntity2.getConceptionEntityUID(),"timeEventRelation2",null,true);

        List<TimeScaleEvent> attachedTimeScaleEventsList = _ConceptionEntity.getAttachedTimeScaleEvents();
        if(attachedTimeScaleEventsList.size()>0) {
            Assert.assertEquals(attachedTimeScaleEventsList.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
            Assert.assertEquals(attachedTimeScaleEventsList.get(0).getTimeFlowName(),"DefaultTimeFlow");
            Assert.assertNotNull(attachedTimeScaleEventsList.get(0).getReferTime());
            Assert.assertNotNull(attachedTimeScaleEventsList.get(0).getTimeScaleEventUID());
            Assert.assertEquals(attachedTimeScaleEventsList.get(0).getEventComment(),"timeEventRelation");
            Assert.assertNotNull(attachedTimeScaleEventsList.get(0).getAttachConceptionEntity());
            Assert.assertNotNull(attachedTimeScaleEventsList.get(0).getReferTimeScaleEntity());
        }

        List<TimeScaleEntity> attachedTimeScaleEntityList = _ConceptionEntity.getAttachedTimeScaleEntities();
        if(attachedTimeScaleEntityList.size()>0) {
            Assert.assertEquals(attachedTimeScaleEntityList.get(0).getEntityValue(),55);
            Assert.assertEquals(attachedTimeScaleEntityList.get(0).getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        }

        List<TimeScaleDataPair> timeScaleDataPairList = _ConceptionEntity.getAttachedTimeScaleDataPairs();
        if(timeScaleDataPairList.size()>0) {
            Assert.assertNotNull(timeScaleDataPairList.get(0).getTimeScaleEvent().getTimeScaleEventUID());
            Assert.assertEquals(timeScaleDataPairList.get(0).getTimeScaleEntity().getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MINUTE);
        }

        boolean detachTimeScaleEventRes = _ConceptionEntity.detachTimeScaleEvent(attachedTimeScaleEventsList.get(0).getTimeScaleEventUID());
        Assert.assertTrue(detachTimeScaleEventRes);

        AttributesParameters attributesParameters = new AttributesParameters();
        TimeScaleEntity yearEntity = defaultTimeFlow.getYearEntity(2004);

        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.OFFSPRING).longValue(),Long.parseLong("0"));
        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(attributesParameters,false,TimeScaleEntity.TimeScaleLevel.SELF).longValue(),Long.parseLong("0"));

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        ConceptionKind _ConceptionKind = coreRealm.getConceptionKind("TimeFlowEventTest");
        ConceptionEntity targetConceptionEntity = _ConceptionKind.getEntities(queryParameters).getConceptionEntities().get(0);
        Assert.assertEquals(targetConceptionEntity.getAttachedTimeScaleEvents().size(),0);
        LocalDateTime dt = LocalDateTime.parse("2004-06-28T08:41:00");
        TimeScaleEvent resultTimeScaleEvent = targetConceptionEntity.attachTimeScaleEvent(dt,"comment",null, TimeFlow.TimeScaleGrade.MONTH);

        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.OFFSPRING).longValue(),Long.parseLong("1"));
        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(null,true,TimeScaleEntity.TimeScaleLevel.OFFSPRING).longValue(),Long.parseLong("1"));
        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.CHILD).longValue(),Long.parseLong("1"));
        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(null,true,TimeScaleEntity.TimeScaleLevel.CHILD).longValue(),Long.parseLong("1"));
        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.SELF).longValue(),Long.parseLong("0"));
        Assert.assertEquals(yearEntity.countAttachedTimeScaleEvents(null,true,TimeScaleEntity.TimeScaleLevel.SELF).longValue(),Long.parseLong("0"));

        TimeScaleEntity monthEntity = defaultTimeFlow.getMonthEntity(2004,6);
        Assert.assertEquals(monthEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.OFFSPRING).longValue(),Long.parseLong("0"));
        Assert.assertEquals(monthEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.SELF).longValue(),Long.parseLong("1"));
        Assert.assertEquals(monthEntity.countAttachedTimeScaleEvents(null,true,TimeScaleEntity.TimeScaleLevel.SELF).longValue(),Long.parseLong("1"));

        TimeScaleEntity dayEntity = defaultTimeFlow.getDayEntity(2004,6,28);
        Assert.assertEquals(dayEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.OFFSPRING).longValue(),Long.parseLong("0"));
        Assert.assertEquals(dayEntity.countAttachedTimeScaleEvents(attributesParameters,true,TimeScaleEntity.TimeScaleLevel.SELF).longValue(),Long.parseLong("0"));
        Assert.assertEquals(dayEntity.countAttachedTimeScaleEvents(null,true,TimeScaleEntity.TimeScaleLevel.SELF).longValue(),Long.parseLong("0"));

        Assert.assertEquals(resultTimeScaleEvent.getAliasConceptionKindNames().size(),0);
        resultTimeScaleEvent.joinConceptionKinds(new String[]{"conceptionKD01","conceptionKD02"});
        Assert.assertEquals(resultTimeScaleEvent.getAliasConceptionKindNames().size(),2);
        resultTimeScaleEvent.retreatFromConceptionKind("conceptionKD01");
        Assert.assertEquals(resultTimeScaleEvent.getAliasConceptionKindNames().size(),1);

        TimeScaleEventsRetrieveResult timeScaleEventsRetrieveResult = yearEntity.getAttachedTimeScaleEvents(queryParameters,TimeScaleEntity.TimeScaleLevel.OFFSPRING);
        Assert.assertEquals(timeScaleEventsRetrieveResult.getTimeScaleEvents().size(),1);

        for(TimeScaleEvent currentTimeScaleEvent:timeScaleEventsRetrieveResult.getTimeScaleEvents()){
            Assert.assertNotNull(currentTimeScaleEvent.getTimeScaleEventUID());
            Assert.assertEquals(currentTimeScaleEvent.getTimeScaleGrade(),TimeFlow.TimeScaleGrade.MONTH);
            Assert.assertNotNull(currentTimeScaleEvent.getAttachConceptionEntity());
            Assert.assertNotNull(currentTimeScaleEvent.getReferTimeScaleEntity());
        }

        Assert.assertNotNull(timeScaleEventsRetrieveResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(timeScaleEventsRetrieveResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(timeScaleEventsRetrieveResult.getOperationStatistics().getResultEntitiesCount());
        Assert.assertNotNull(timeScaleEventsRetrieveResult.getOperationStatistics().getQueryParameters());

        long removeRefersEventsResult = defaultTimeFlow.removeRefersTimeScaleEvents();
        Assert.assertTrue(removeRefersEventsResult > 0);
    }
}
