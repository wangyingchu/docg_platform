package com.viewfunction.docg.dataCompute.consoleApplication;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.DataComputeApplication;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseApplication;

public class ApplicationCapacityRegistry {
    public static BaseApplication createConsoleApplication(String applicationFunctionName){
        if(applicationFunctionName.equals("dataComputeApplication")){
            return new DataComputeApplication();
        }
        return null;
    }
}
