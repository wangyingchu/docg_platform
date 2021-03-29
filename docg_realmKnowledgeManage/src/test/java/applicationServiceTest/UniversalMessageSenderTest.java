package applicationServiceTest;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.MessageSentEventHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.UniversalMessageSender;

public class UniversalMessageSenderTest {

    public static void main(String[] args) throws ConfigurationErrorException, MessageHandleErrorException {

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
        universalMessageSender.sendUniversalMessage("UniversalTopic","keyA","valueA");
        universalMessageSender.sendUniversalMessage("UniversalTopic","keyB","valueB");
        universalMessageSender.sendUniversalMessage("UniversalTopic","keyC","valueC");
        universalMessageSender.sendUniversalMessage("UniversalTopic","keyD","valueD");

        //Thread.sleep(1000);
        /*
        for(int i =0;i<10;i++){
            Thread.sleep(1000);
            universalMessageSender.sendMessage("testTopic001","key"+i,"value"+i);
        }
        */

        universalMessageSender.finishMessageSendBatch();
    }
}
