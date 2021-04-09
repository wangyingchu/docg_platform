package com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization;

import com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.commandProcessor.DataSlicesSyncCommandProcessorFactory;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.dataSlicesSync.GeneralDataSliceEntityValueOperationsMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.CommonObjectsMessageReceiver;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseApplication;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;
import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSlicesSynchronizationApplication implements BaseApplication {

    public static final String APPLICATION_START_TIME = "APPLICATION_START_TIME";
    public static final String SYNC_LISTENING_START_TIME = "SYNC_LISTENING_START_TIME";

    private ExecutorService executorService;
    private Map<Object,Object> commandContextDataMap;
    private CommonObjectsMessageReceiver commonObjectsMessageReceiver;

    @Override
    public boolean isDaemonApplication() {
        return true;
    }

    @Override
    public void executeDaemonLogic() {
        //Batch load data into data slices by per defined rules
        this.commandContextDataMap.put(SYNC_LISTENING_START_TIME,new Date());
        //
        GeneralDataSliceEntityValueOperationsMessageHandler generalDataSliceEntityValueOperationsMessageHandler = new GeneralDataSliceEntityValueOperationsMessageHandler(this.commandContextDataMap);
        try {
            commonObjectsMessageReceiver = new CommonObjectsMessageReceiver(generalDataSliceEntityValueOperationsMessageHandler);
        } catch (ConfigurationErrorException e) {
            e.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    String defaultMessageReceiverTopicName = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.MessageReceiver.defaultTopicName");
                    commonObjectsMessageReceiver.startMessageReceive(new String[]{defaultMessageReceiverTopicName});
                } catch (ConfigurationErrorException e) {
                    e.printStackTrace();
                } catch (MessageHandleErrorException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApplicationLauncherUtil.printApplicationConsoleBanner();
    }

    @Override
    public boolean initApplication() {
        this.commandContextDataMap = new ConcurrentHashMap<>();
        this.commandContextDataMap.put(APPLICATION_START_TIME,new Date());
        return true;
    }

    @Override
    public boolean shutdownApplication() {
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
                    BaseCommandProcessor commandProcessor = DataSlicesSyncCommandProcessorFactory.getCommandProcessor(command,this.commandContextDataMap);
                    if(commandProcessor!=null){
                        commandProcessor.processCommand(command,options);
                    }
                }
            }
        }
    }
}
