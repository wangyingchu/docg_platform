package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.commandProcessor.EntityExtractionCommandProcessorFactory;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationPayload;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageReceiver;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseApplication;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;

import java.util.Arrays;
import java.util.List;

public class EntityExtractionApplication implements BaseApplication {

    private ConceptionEntityValueOperationsMessageReceiver conceptionEntityValueOperationsMessageReceiver;

    @Override
    public boolean isDaemonApplication() {
        return true;
    }

    @Override
    public void executeDaemonLogic() {




        ConceptionEntityValueOperationsMessageHandler conceptionEntityValueOperationsMessageHandler = new ConceptionEntityValueOperationsMessageHandler() {
            long totalHandledNum = 0;
            @Override
            public void handleConceptionEntityOperationContents(List<? extends ConceptionEntityValueOperationPayload> infoObjectValueOperationPayloads) {
                for(ConceptionEntityValueOperationPayload currentConceptionEntityValueOperationPayload:infoObjectValueOperationPayloads){

                    ConceptionEntityValueOperationContent conceptionEntityValueOperationContent = currentConceptionEntityValueOperationPayload.getConceptionEntityValueOperationContent();
                    ConceptionEntityValue conceptionEntityValue = currentConceptionEntityValueOperationPayload.getConceptionEntityValue();

                    System.out.println(currentConceptionEntityValueOperationPayload.getPayloadOffset());
                    System.out.println(currentConceptionEntityValueOperationPayload.getPayloadKey());
                    System.out.println(conceptionEntityValue.getEntityAttributesValue());
                    System.out.println(conceptionEntityValue.getConceptionEntityUID());

                    System.out.println(conceptionEntityValueOperationContent.getConceptionEntityUID());
                    System.out.println(conceptionEntityValueOperationContent.getEntityAttributesValue());
                    System.out.println(conceptionEntityValueOperationContent.getOperationType());
                    System.out.println(conceptionEntityValueOperationContent.getConceptionKindName());
                    System.out.println(conceptionEntityValueOperationContent.getCoreRealmName());
                    System.out.println(conceptionEntityValueOperationContent.getSenderId());
                    System.out.println(conceptionEntityValueOperationContent.getSenderIP());
                    System.out.println(conceptionEntityValueOperationContent.getSendTime());
                    System.out.println(conceptionEntityValueOperationContent.isAddPerDefinedRelation());

                    System.out.println("=----------------------------------=");
                    totalHandledNum++;
                }
                System.out.println("totalHandledNum = "+totalHandledNum);
            }
        };

        try {
            conceptionEntityValueOperationsMessageReceiver = new ConceptionEntityValueOperationsMessageReceiver(conceptionEntityValueOperationsMessageHandler);
            conceptionEntityValueOperationsMessageReceiver.startMessageReceive(new String[]{"DefaultCoreRealm"});
        } catch (ConfigurationErrorException | MessageHandleErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean initApplication() {
        return true;
    }

    @Override
    public boolean shutdownApplication() {
        if(conceptionEntityValueOperationsMessageReceiver != null){
            conceptionEntityValueOperationsMessageReceiver.stopMessageReceive();
        }
        return true;
    }

    @Override
    public void executeConsoleCommand(String consoleCommand) {
        if(consoleCommand != null){
            String[] commandOptions = consoleCommand.split(" ");
            if(commandOptions.length>0){
                String command = commandOptions[0];
                if(command.startsWith("-")||command.startsWith("--")){
                    System.out.println("Please input valid command and options");
                }else{
                    String[] options = Arrays.copyOfRange(commandOptions,1,commandOptions.length);
                    BaseCommandProcessor commandProcessor = EntityExtractionCommandProcessorFactory.getCommandProcessor(command,null,null,null);
                    if(commandProcessor!=null){
                        commandProcessor.processCommand(command,options);
                    }
                }
            }
        }
    }
}
