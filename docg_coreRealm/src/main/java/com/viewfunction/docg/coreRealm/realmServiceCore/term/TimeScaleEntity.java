package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.LinkedList;

public interface TimeScaleEntity {

    public enum TimeScaleLevel {SELF, CHILD,OFFSPRING}

    public TimeFlow.TimeScaleGrade getTimeScaleGrade();
    public int getEntityValue();
    public TimeScaleEntity getParentEntity();
    public TimeScaleEntity getNextSameScaleEntity();
    public TimeScaleEntity getPreviousSameScaleEntity();
    public LinkedList<TimeScaleEntity> getFellowEntities();
    public LinkedList<TimeScaleEntity> getChildEntities();
    public TimeScaleEntity getFirstChildEntity();
    public TimeScaleEntity getLastChildEntity();
    public InheritanceTree<TimeScaleEntity> getOffspringEntities();

    public Long countAttachedTimeScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, TimeScaleLevel timeScaleLevel);
    public TimeScaleEventsRetrieveResult getAttachedTimeScaleEvents(QueryParameters queryParameters, TimeScaleLevel timeScaleLevel);

    public Long countAttachedConceptionEntities(TimeScaleLevel timeScaleLevel);

    public Long countAttachedConceptionEntities(AttributesParameters attributesParameters, boolean isDistinctMode, TimeScaleLevel timeScaleLevel);
    public ConceptionEntitiesRetrieveResult getAttachedConceptionEntities(QueryParameters queryParameters,TimeScaleLevel timeScaleLevel);

}
