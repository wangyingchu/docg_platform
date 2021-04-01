package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.commandProcessor;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageReceiver;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;

import java.util.Map;

public class EntityExtractionCommandProcessorFactory {

    public static BaseCommandProcessor getCommandProcessor(String command, ConceptionEntityValueOperationsMessageReceiver conceptionEntityValueOperationsMessageReceiver,Map<Object, Object> commandContextDataMap){
        if(command.equalsIgnoreCase("help")){
            HelpCommandProcessor helpCommandProcessor = new HelpCommandProcessor();
            return helpCommandProcessor;
        }if(command.equalsIgnoreCase("appinf")){
            AppInfCommandProcessor appInfCommandProcessor = new AppInfCommandProcessor(conceptionEntityValueOperationsMessageReceiver,commandContextDataMap);
            return appInfCommandProcessor;
        }if(command.equalsIgnoreCase("clear")){
            ClearCommandProcessor clearCommandProcessor = new ClearCommandProcessor();
            return clearCommandProcessor;
        }
        return null;
    }
}
