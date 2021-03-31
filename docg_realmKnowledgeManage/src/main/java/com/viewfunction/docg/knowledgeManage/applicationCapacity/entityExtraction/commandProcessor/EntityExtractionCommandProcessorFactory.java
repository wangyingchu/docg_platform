package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.commandProcessor;

import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;

import java.util.Map;

public class EntityExtractionCommandProcessorFactory {

    public static BaseCommandProcessor getCommandProcessor(String command, Map<Object,Object> commandContextDataMap){
        if(command.equalsIgnoreCase("help")){
            HelpCommandProcessor helpCommandProcessor = new HelpCommandProcessor();
            return helpCommandProcessor;
        }
        return null;
    }
}
