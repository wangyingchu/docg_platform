package applicationServiceTest;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.MessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.PlainTextMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.UniversalMessageReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UniversalMessageReceiverTest {

    public static void main(String[] args) throws ConfigurationErrorException, InterruptedException {

        MessageHandler plainTextMessageHandler = new PlainTextMessageHandler() {
            @Override
            protected void operateRecord(Object recordKey, String recordValue, long recordOffset) {
                System.out.println(recordKey+" "+recordValue+" "+recordOffset);
            }
        };

        final UniversalMessageReceiver universalMessageReceiver =new UniversalMessageReceiver(plainTextMessageHandler);
        universalMessageReceiver.setKeyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        universalMessageReceiver.setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            public void run() {
                try {
                    universalMessageReceiver.startMessageReceive(new String[]{"UniversalTopic"});
                } catch (ConfigurationErrorException e) {
                    e.printStackTrace();
                } catch (MessageHandleErrorException e) {
                    e.printStackTrace();
                }
            }
        });

        //Thread.sleep(10000);
        //universalMessageReceiver.stopMessageReceive();
        //executor.shutdown();
    }
}
