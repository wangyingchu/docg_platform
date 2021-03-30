package applicationServiceTest;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.AvroMessageHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.AvroMessageReceiver;
import org.apache.avro.generic.GenericRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AvroMessageReceiverTest {

    public static void main(String[] args) throws ConfigurationErrorException{

        AvroMessageHandler avroMessageHandler = new AvroMessageHandler() {
            @Override
            protected void operateRecord(Object recordKey, GenericRecord recordValue, long recordOffset) {
                System.out.println(recordKey+" "+recordValue+" "+recordOffset);
            }
        };

        final AvroMessageReceiver avroMessageReceiver = new AvroMessageReceiver(avroMessageHandler);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            public void run() {
                try {
                    avroMessageReceiver.startMessageReceive(new String[]{"AvroTopic"});
                } catch (ConfigurationErrorException e) {
                    e.printStackTrace();
                } catch (MessageHandleErrorException e) {
                    e.printStackTrace();
                }

            }
        });

        //Thread.sleep(5000);
        //executor.shutdown();
    }
}