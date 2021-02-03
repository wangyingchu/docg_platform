package com.viewfunction.docg.dataCube.consoleApplication;

import com.viewfunction.docg.dataCube.applicationCapacity.DataCubeApplication;
import com.viewfunction.docg.dataCube.consoleApplication.feature.BaseApplication;

public class ApplicationCapacityRegistry {
    public static BaseApplication createConsoleApplication(String applicationFunctionName){
        if(applicationFunctionName.equals("dataCubeApplication")){
            return new DataCubeApplication();
        }
        return null;
    }
}
