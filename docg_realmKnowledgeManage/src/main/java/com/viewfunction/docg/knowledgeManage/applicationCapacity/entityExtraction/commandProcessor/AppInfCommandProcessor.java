package com.viewfunction.docg.knowledgeManage.applicationCapacity.entityExtraction.commandProcessor;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.ConceptionEntityValueOperationsMessageReceiver;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.EventStreamingServicePropertiesHandler;
import com.viewfunction.docg.knowledgeManage.consoleApplication.feature.BaseCommandProcessor;
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
        System.out.println(receiverConfig);

      /*
        int autoCommitIntervalMs=Integer.parseInt(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_AUTO_COMMIT_INTERVAL_MS));
        this.configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,autoCommitIntervalMs);
        int sessionTimeoutMs=Integer.parseInt(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_SESSION_TIMEOUT_MS));
        this.configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,sessionTimeoutMs);
        int maxPartitionFetchBytes=Integer.parseInt(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_MAX_PARTITION_FETCH_BYTES));
        this.configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,maxPartitionFetchBytes);
*/

        StringBuffer appInfoMessageStringBuffer=new StringBuffer();
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("================================================================");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("Kafka Consumer Configuration: ");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("-------------------------------------------------------------");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("BOOTSTRAP_SERVERS_CONFIG:                " + receiverConfig.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("SCHEMA_REGISTRY_URL:               " + receiverConfig.getProperty(EventStreamingServicePropertiesHandler.SCHEMA_REGISTRY_URL));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("GROUP_ID_CONFIG:         " + receiverConfig.getProperty(ConsumerConfig.GROUP_ID_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("CONSUMER_ENABLE_AUTO_COMMIT:"+ receiverConfig.getProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("-------------------------------------------------------------");
        appInfoMessageStringBuffer.append("\n\r");
        appInfoMessageStringBuffer.append("================================================================");

        System.out.println(appInfoMessageStringBuffer.toString());
    }
}
