package com.viewfunction.docg.sceneDataPrepare;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.realmExample.generator.RoadWeatherInformationStationsRecords_Realm_Generator;
import com.viewfunction.docg.realmExample.generator.SeattleRealTimeFire911Calls_Realm_Generator;

public interface TimeAndGeoSceneDataGenerator {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //load data
        SeattleRealTimeFire911Calls_Realm_Generator.main(null);
        RoadWeatherInformationStationsRecords_Realm_Generator.main(null);

    }
}
