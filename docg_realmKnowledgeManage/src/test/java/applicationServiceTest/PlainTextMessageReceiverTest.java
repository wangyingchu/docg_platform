package applicationServiceTest;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.PlainTextMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.PlainTextMessageReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlainTextMessageReceiverTest {

    public static void main(String[] args) throws ConfigurationErrorException, InterruptedException {

        PlainTextMessageHandler plainTextMessageHandler = new PlainTextMessageHandler() {
            @Override
            protected void operateRecord(Object recordKey, String recordValue, long recordOffset) {
                System.out.println(recordKey+" "+recordValue+" "+recordOffset);
            }
        };

        final PlainTextMessageReceiver plainTextMessageReceiver = new PlainTextMessageReceiver(plainTextMessageHandler);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            public void run() {
                try {
                    plainTextMessageReceiver.startMessageReceive(new String[]{"PlainTextTopic"});
                } catch (ConfigurationErrorException e) {
                    e.printStackTrace();
                } catch (MessageHandleErrorException e) {
                    e.printStackTrace();
                }
            }
        });

        //Thread.sleep(5000);
        //plainTextMessageReceiver.stopMessageReceive();
        //executor.shutdown();
    }
}
