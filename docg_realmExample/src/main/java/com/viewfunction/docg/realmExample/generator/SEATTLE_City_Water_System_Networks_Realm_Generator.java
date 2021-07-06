package com.viewfunction.docg.realmExample.generator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.realmExample.tools.SHP_DataSourceImport;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;

public class SEATTLE_City_Water_System_Networks_Realm_Generator {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        doSHPImport("shpFileLocation",
                "conceptionKindName");
    }

    private static void doSHPImport(String shpFileLocation,String conceptionKindName) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        String pathName = shpFileLocation;
        File file = new File(pathName);
        SHP_DataSourceImport.importSHPDataToConceptionKind(conceptionKindName,true,file,null);
    }
}
