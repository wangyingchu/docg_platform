package com.viewfunction.docg.relationManage.consoleApplication;

import com.viewfunction.docg.relationManage.applicationCapacity.relationManager.RelationManagerApplication;
import com.viewfunction.docg.relationManage.consoleApplication.feature.BaseApplication;

public class ApplicationCapacityRegistry {
    public static BaseApplication createConsoleApplication(String applicationFunctionName){
        if(applicationFunctionName.equals("relationManager")){
            return new RelationManagerApplication();
        }
        return null;
    }
}
