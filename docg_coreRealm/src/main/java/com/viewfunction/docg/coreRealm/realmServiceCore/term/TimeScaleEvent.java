package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;

public interface TimeScaleEvent extends AttributesMeasurable, ClassificationAttachable {

    public String getTimeFlowName();
    public long getReferTime();
    public TimeFlow.TimeScaleGrade getTimeScaleGrade();
    public String getTimeScaleEventUID();
    public String getEventComment();

}
