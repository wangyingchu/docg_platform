package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimesSeriesEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.Map;

public interface TimeScaleFeatureSupportable {

    public enum TimesSeriesGrade{YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,WEEK}

    public TimesSeriesEvent attachTimeSeries(long dateTime, String relationType, RelationDirection relationDirection,
                                             Map<String, Object> relationData, TimesSeriesGrade timesSeriesGrade);

}
