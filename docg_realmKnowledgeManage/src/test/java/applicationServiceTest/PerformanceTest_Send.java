package applicationServiceTest;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.MessageSentEventHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.UniversalMessageSender;

import java.util.UUID;

public class PerformanceTest_Send {

    public static void main(String[] args) throws ConfigurationErrorException, InterruptedException, MessageHandleErrorException, MessageFormatErrorException {

        MessageSentEventHandler messageSentEventHandler = new MessageSentEventHandler() {
            @Override
            public void operateMetaData(long offset, long timestamp, String topic, int partition) {
                System.out.println(offset+" "+timestamp+" "+topic+" "+partition);
            }
        };

        UniversalMessageSender universalMessageSender =new UniversalMessageSender(messageSentEventHandler);
        universalMessageSender.setKeyDeserializer("org.apache.kafka.common.serialization.StringSerializer");
        universalMessageSender.setValueDeserializer("org.apache.kafka.common.serialization.StringSerializer");

        universalMessageSender.beginMessageSendBatch();
        for(int i =0;i<10000000;i++){
            universalMessageSender.sendUniversalMessage("PerformanceTestTopic","keyA","value-"+UUID.randomUUID().toString());
        }
        universalMessageSender.finishMessageSendBatch();
    }
}
