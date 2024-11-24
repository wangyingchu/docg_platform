package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import org.neo4j.driver.Result;

public class GetSingleTimeScaleEventEntirePathTransformer implements DataTransformer<Object>{

    @Override
    public Object transformResult(Result result) {
        if (result.hasNext()) {
            //RETURN attachToTimeScale ,timeScaleEvent,conceptionEntity,timeReferTo,timeScaleEntity"
            return Boolean.getBoolean("true");
        }else{
            return Boolean.getBoolean("False");
        }
    }
}
