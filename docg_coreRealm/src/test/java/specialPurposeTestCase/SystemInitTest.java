package specialPurposeTestCase;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

public class SystemInitTest {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        GeospatialRegion geospatialRegion = coreRealm.getOrCreateGeospatialRegion();
        geospatialRegion.createGeospatialScaleEntities();
        TimeFlow timeFlow = coreRealm.getOrCreateTimeFlow();
        timeFlow.createTimeSpanEntities(2021,false);
    }

}
