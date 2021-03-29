package applicationServiceTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.AvroMessageSender;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender.MessageSentEventHandler;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.PayloadMetaInfo;

public class AvroMessageSenderTest {

    public static void main(String[] args) throws ConfigurationErrorException, MessageHandleErrorException, MessageFormatErrorException, SchemaFormatErrorException{
        MessageSentEventHandler messageSentEventHandler = new MessageSentEventHandler() {
            @Override
            public void operateMetaData(long offset, long timestamp, String topic, int partition) {
                System.out.println(offset+" "+timestamp+" "+topic+" "+partition);
            }
        };

        AvroMessageSender avroMessageSender =new AvroMessageSender(messageSentEventHandler);
        avroMessageSender.beginMessageSendBatch();

        PayloadMetaInfo pmi=new PayloadMetaInfo();
        pmi.setDestinationTopic("AvroTopic");
        //pmi.setPayloadSchema("com.navteq.avro.FacebookUser");
        pmi.setSchemaDefinitionLocation("testresource/facebookUser.avro");
        pmi.setDestinationTopic("AvroTopic");
        pmi.setPayloadKey("TESTKey");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payloadJsonNode=mapper.createObjectNode();
        payloadJsonNode.put("name","wangychu02");
        payloadJsonNode.put("num_likes",152);
        payloadJsonNode.put("num_photos",222);
        payloadJsonNode.put("num_groups",772);

        for(int i=0;i<10;i++) {
            payloadJsonNode.put("name","wangychu"+i);
            pmi.setPayloadKey("TESTKey"+i);
            avroMessageSender.sendAvroMessage(pmi, payloadJsonNode);
        }
        avroMessageSender.finishMessageSendBatch();
    }
}