package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction;

import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.commandProcessor.EntityExtractionCommandProcessorFactory;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.conceptionEntitiesExtract.GeneralConceptionEntityValueOperationsMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageReceiver;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseApplication;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntityExtractionApplication implements BaseApplication {

    private ConceptionEntityValueOperationsMessageReceiver conceptionEntityValueOperationsMessageReceiver;
    private ExecutorService executorService;
    private Map<Object,Object> commandContextDataMap;

    @Override
    public boolean isDaemonApplication() {
        return true;
    }

    @Override
    public void executeDaemonLogic() {
        GeneralConceptionEntityValueOperationsMessageHandler generalConceptionEntityValueOperationsMessageHandler = new GeneralConceptionEntityValueOperationsMessageHandler();
        try {
            conceptionEntityValueOperationsMessageReceiver = new ConceptionEntityValueOperationsMessageReceiver(generalConceptionEntityValueOperationsMessageHandler);

        } catch (ConfigurationErrorException e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    conceptionEntityValueOperationsMessageReceiver.startMessageReceive(new String[]{"DefaultCoreRealm"});
                } catch (ConfigurationErrorException e) {
                    e.printStackTrace();
                } catch (MessageHandleErrorException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean initApplication() {
        this.commandContextDataMap = new ConcurrentHashMap<>();
        return true;
    }

    @Override
    public boolean shutdownApplication() {
        if(conceptionEntityValueOperationsMessageReceiver != null){
            conceptionEntityValueOperationsMessageReceiver.stopMessageReceive();
        }
        if(executorService != null){
            executorService.shutdown();
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
                    BaseCommandProcessor commandProcessor = EntityExtractionCommandProcessorFactory.getCommandProcessor(command,this.commandContextDataMap);
                    if(commandProcessor!=null){
                        commandProcessor.processCommand(command,options);
                    }
                }
            }
        }
    }
}
