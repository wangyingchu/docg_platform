package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.commandProcessor;

import com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.EntityExtractionApplication;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageReceiver;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.EventStreamingServicePropertiesHandler;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;
import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.*;

public class AppInfCommandProcessor implements BaseCommandProcessor {

    private ConceptionEntityValueOperationsMessageReceiver conceptionEntityValueOperationsMessageReceiver;
    private Map<Object,Object> commandContextDataMap;

    public AppInfCommandProcessor(ConceptionEntityValueOperationsMessageReceiver conceptionEntityValueOperationsMessageReceiver,
                                  Map<Object,Object> commandContextDataMap){
        this.conceptionEntityValueOperationsMessageReceiver = conceptionEntityValueOperationsMessageReceiver;
        this.commandContextDataMap = commandContextDataMap;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        Properties receiverConfig = this.conceptionEntityValueOperationsMessageReceiver.getReceiverConfig();
        String defaultMessageReceiverTopicName = ApplicationLauncherUtil
                .getApplicationInfoPropertyValue("EntityExtraction.MessageReceiver.defaultTopicName");
        StringBuffer appInfoMessageStringBuffer=new StringBuffer();
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("================================================================");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("Application started at:  "+ this.commandContextDataMap.get(EntityExtractionApplication.APPLICATION_START_TIME)).toString();
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("Receiver Topics:         "+ defaultMessageReceiverTopicName);
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("-------------------------------------------------------------");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("Kafka Consumer Configuration: ");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("-------------------------------------------------------------");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("BOOTSTRAP_SERVERS_CONFIG:          " + receiverConfig.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("SCHEMA_REGISTRY_URL:               " + receiverConfig.get(EventStreamingServicePropertiesHandler.SCHEMA_REGISTRY_URL));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("GROUP_ID_CONFIG:                   " + receiverConfig.get(ConsumerConfig.GROUP_ID_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("CONSUMER_ENABLE_AUTO_COMMIT:       "+ receiverConfig.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("AUTO_COMMIT_INTERVAL_MS_CONFIG:    "+ receiverConfig.get(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("SESSION_TIMEOUT_MS_CONFIG:         "+ receiverConfig.get(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("MAX_PARTITION_FETCH_BYTES_CONFIG:  "+ receiverConfig.get(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("-------------------------------------------------------------");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("================================================================");

        System.out.println(appInfoMessageStringBuffer.toString());
    }
}
