package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;

import java.util.List;
import java.util.Map;

public interface TimeScaleFeatureSupportable {

    public TimeScaleEvent attachTimeScaleEvent(long dateTime, String eventComment, Map<String, Object> eventData,
                                               TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException;

    public TimeScaleEvent attachTimeScaleEvent(String timeFlowName,long dateTime, String eventComment,
                                               Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException;

    public boolean detachTimeScaleEvent(String timeScaleEventUID) throws CoreRealmServiceRuntimeException;

    public List<TimeScaleEvent> getAttachedTimeScaleEvents();

    public List<TimeScaleEntity> getAttachedTimeScaleEntities();

    public List<TimeScaleDataPair> getAttachedTimeScaleDataPairs();
}
