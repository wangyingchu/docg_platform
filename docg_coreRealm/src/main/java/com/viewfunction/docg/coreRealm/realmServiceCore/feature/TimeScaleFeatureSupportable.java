package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.Map;

public interface TimeScaleFeatureSupportable {

    public enum TimeScaleGrade {YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,WEEK}

    public TimeScaleEvent attachTimeScaleEvent(long dateTime, String relationType, RelationDirection relationDirection,
                                               Map<String, Object> eventData, TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException;

}
