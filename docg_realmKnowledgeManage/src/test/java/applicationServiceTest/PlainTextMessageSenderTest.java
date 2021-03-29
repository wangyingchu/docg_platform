package applicationServiceTest;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.MessageSentEventHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.PlainTextMessageSender;

public class PlainTextMessageSenderTest {

    public static void main(String[] args) throws ConfigurationErrorException, MessageHandleErrorException {

        MessageSentEventHandler messageSentEventHandler = new MessageSentEventHandler() {
            @Override
            public void operateMetaData(long offset, long timestamp, String topic, int partition) {
                System.out.println(offset+" "+timestamp+" "+topic+" "+partition);
            }
        };

        PlainTextMessageSender plainTextMessageSender =new PlainTextMessageSender(messageSentEventHandler);

        plainTextMessageSender.beginMessageSendBatch();
        plainTextMessageSender.sendTextMessage("PlainTextTopic","keyA","valueA");
        plainTextMessageSender.sendTextMessage("PlainTextTopic","keyB","valueB");
        plainTextMessageSender.sendTextMessage("PlainTextTopic","keyC","valueC");
        plainTextMessageSender.sendTextMessage("PlainTextTopic","keyD","valueD");

        plainTextMessageSender.finishMessageSendBatch();
    }
}
