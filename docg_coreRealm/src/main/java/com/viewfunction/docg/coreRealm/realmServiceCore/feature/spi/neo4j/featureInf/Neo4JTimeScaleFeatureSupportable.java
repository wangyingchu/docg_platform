package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.TimeScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimesSeriesEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface Neo4JTimeScaleFeatureSupportable extends TimeScaleFeatureSupportable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JTimeScaleFeatureSupportable.class);

    public default TimesSeriesEvent attachTimeSeries(long dateTime, String relationType, RelationDirection relationDirection,
                                             Map<String, Object> relationData, TimesSeriesGrade timesSeriesGrade){
        return null;
    }
}
