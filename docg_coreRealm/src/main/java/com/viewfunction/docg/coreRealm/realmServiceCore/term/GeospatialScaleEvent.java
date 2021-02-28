package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;

public interface GeospatialScaleEvent  extends AttributesMeasurable, ClassificationAttachable {

    public String getGeospatialRegionName();
    //public long getReferTime();
    public TimeFlow.TimeScaleGrade getGeospatialScaleGrade();
    public String getGeospatialScaleEventUID();
    public String getEventComment();
    public GeospatialScaleEntity getReferGeospatialScaleEntity();
    public ConceptionEntity getAttachConceptionEntity();

}
