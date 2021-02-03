package com.viewfunction.docg.dataCube.consoleApplication;


import com.viewfunction.docg.dataCube.consoleApplication.feature.BaseApplication;

public class ApplicationCapacityRegistry {
    public static BaseApplication createConsoleApplication(String applicationFunctionName){
        if(applicationFunctionName.equals("relationExtraction")){
            //return new RelationExtractionApplication();
        }
        if(applicationFunctionName.equals("entityExtraction")){
            //return new EntityExtractionApplication();
        }
        if(applicationFunctionName.equals("entityFusion")){
            //return new EntityFusionApplication();
        }
        if(applicationFunctionName.equals("entityDisambiguation")){
            //return new EntityDisambiguationApplication();
        }
        return null;
    }
}
