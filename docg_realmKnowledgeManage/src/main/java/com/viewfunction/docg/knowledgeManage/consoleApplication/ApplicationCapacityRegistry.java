package com.viewfunction.docg.knowledgeManage.consoleApplication;

import com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.DataSlicesSynchronizationApplication;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityDisambiguation.EntityDisambiguationApplication;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.EntityExtractionApplication;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityFusion.EntityFusionApplication;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction.RelationExtractionApplication;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseApplication;

public class ApplicationCapacityRegistry {
    public static BaseApplication createConsoleApplication(String applicationFunctionName){
        if(applicationFunctionName.equals("relationExtraction")){
            return new RelationExtractionApplication();
        }
        if(applicationFunctionName.equals("entityExtraction")){
            return new EntityExtractionApplication();
        }
        if(applicationFunctionName.equals("entityFusion")){
            return new EntityFusionApplication();
        }
        if(applicationFunctionName.equals("entityDisambiguation")){
            return new EntityDisambiguationApplication();
        }
        if(applicationFunctionName.equals("dataSlicesSynchronization")){
            return new DataSlicesSynchronizationApplication();
        }
        return null;
    }
}
