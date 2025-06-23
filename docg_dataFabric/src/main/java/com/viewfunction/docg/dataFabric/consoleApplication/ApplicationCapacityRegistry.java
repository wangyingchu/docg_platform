package com.viewfunction.docg.dataFabric.consoleApplication;

import com.viewfunction.docg.dataFabric.applicationCapacity.dataChangeDetect.DataChangeDetectApplication;
import com.viewfunction.docg.dataFabric.consoleApplication.feature.BaseApplication;

public class ApplicationCapacityRegistry {

    public static BaseApplication createConsoleApplication(String applicationFunctionName){
        if(applicationFunctionName.equals("DataChangeDetect")){
            return new DataChangeDetectApplication();
        }
        return null;
    }
}
