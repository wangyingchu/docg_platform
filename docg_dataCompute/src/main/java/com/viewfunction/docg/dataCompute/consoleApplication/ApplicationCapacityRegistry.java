package com.viewfunction.docg.dataCompute.consoleApplication;

import com.viewfunction.docg.dataCompute.applicationCapacity.DataCubeApplication;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseApplication;

public class ApplicationCapacityRegistry {
    public static BaseApplication createConsoleApplication(String applicationFunctionName){
        if(applicationFunctionName.equals("dataCubeApplication")){
            return new DataCubeApplication();
        }
        return null;
    }
}
