package com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction.commandProcessor;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class RelationExtractionCommandProcessorFactory {

    public static BaseCommandProcessor getCommandProcessor(String command, CoreRealm coreRealm, ExecutorService executor, Map<Object,Object> commandContextDataMap){
        if(command.equalsIgnoreCase("re")){
            return new RelationExtractProcessor(coreRealm,executor,commandContextDataMap);
        }
        return null;
    }
}
