package com.viewfunction.docg.realmExample.internalTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.realmExample.tools.SHP_DataSourceImport;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;

public class SHP_DataSourceImportTest {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        String shpFileLocation = "/home/wangychu/Desktop/GYD_GYW_GIS_DATA/GYD-GIS_WGS84/gyd-building.shp";
        String conceptionKindName = "GYD_Building";
        doSHPImport(shpFileLocation,conceptionKindName);
    }

    private static void doSHPImport(String shpFileLocation,String conceptionKindName) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        String pathName = shpFileLocation;
        File file = new File(pathName);
        SHP_DataSourceImport.importSHPDataToConceptionKind(conceptionKindName,true,file,null);
    }
}
